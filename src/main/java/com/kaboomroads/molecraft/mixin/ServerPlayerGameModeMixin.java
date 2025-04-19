package com.kaboomroads.molecraft.mixin;

import com.kaboomroads.molecraft.entity.StatType;
import com.kaboomroads.molecraft.entity.StatsMap;
import com.kaboomroads.molecraft.mining.BlockType;
import com.kaboomroads.molecraft.mining.Mining;
import com.kaboomroads.molecraft.mining.MiningArea;
import com.kaboomroads.molecraft.mining.Octree;
import com.kaboomroads.molecraft.mixinimpl.ModLivingEntity;
import com.kaboomroads.molecraft.mixinimpl.ModServerLevel;
import com.kaboomroads.molecraft.mixinimpl.ModServerLevelData;
import com.kaboomroads.molecraft.util.MiningPlayer;
import com.kaboomroads.molecraft.util.SoundInstance;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerGameMode.class)
public abstract class ServerPlayerGameModeMixin {
    @Shadow
    @Final
    protected ServerPlayer player;
    @Shadow
    protected ServerLevel level;

    @Shadow
    private BlockPos destroyPos;
    @Unique
    private long nextMiningHit = Long.MIN_VALUE;
    @Unique
    private BlockPos lastHitBlock = null;

    @WrapMethod(method = "destroyBlock")
    private boolean wrap_destroyBlock(BlockPos pos, Operation<Boolean> original) {
        return player.getAbilities().instabuild ? original.call(pos) : false;
    }

    @Inject(method = "handleBlockBreakAction", at = @At(value = "FIELD", target = "Lnet/minecraft/server/level/ServerPlayerGameMode;gameTicks:I", ordinal = 0), cancellable = true)
    private void blockDamage(BlockPos pos, ServerboundPlayerActionPacket.Action action, Direction face, int maxBuildHeight, int sequence, CallbackInfo ci) {
        Mining mining = ((ModServerLevelData) level.getLevelData()).molecraft$getMining();
        Octree octree = mining.areaOctree;
        if (octree.rootNode == null) {
            player.sendSystemMessage(Component.literal("Unbuilt Octree Error").withStyle(ChatFormatting.RED));
            return;
        }
        MiningArea area = octree.get(pos.getX(), pos.getY(), pos.getZ());
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();
        BlockType type = mining.getBlockType(area, block);
        if (type == null) return;
        int breakingPower = type.breakingPower();
        double blockHealth = type.blockHealth();
        int resonantFrequency = type.resonantFrequency();
        long gameTime = level.getGameTime();
        boolean newBlock = !pos.equals(lastHitBlock);
        long delta = nextMiningHit - gameTime;
        boolean onTime = Math.abs(delta) <= 1;
        boolean fresh = nextMiningHit == Long.MIN_VALUE;
        if (fresh || newBlock || onTime) {
            MiningPlayer miningPlayer = mining.currentlyMining.computeIfAbsent(pos, k -> new MiningPlayer(player, blockHealth));
            if (!miningPlayer.player.equals(player)) return;
            if (newBlock && lastHitBlock != null) mining.currentlyMining.remove(lastHitBlock);
            StatsMap stats = ((ModLivingEntity) player).molecraft$getStats();
            double playerBreakingPower = stats.get(StatType.BREAKING_POWER).cachedValue;
            double miningStrength = stats.get(StatType.MINING_STRENGTH).cachedValue;
            if (playerBreakingPower + 0.000001D < breakingPower) return;
            lastHitBlock = pos;
            nextMiningHit = gameTime + resonantFrequency;
            miningPlayer.blockHealth -= miningStrength;
            boolean done = miningPlayer.blockHealth <= 0;
            int progress = done ? 10 : (int) Math.round((1 - miningPlayer.blockHealth / type.blockHealth()) * 10 - 1);
            blockDestruction(pos, progress);
            if (done) {
                mining.currentlyMining.remove(pos);
                lastHitBlock = null;
                BlockState bedrock = Blocks.BEDROCK.defaultBlockState();
                level.setBlock(pos, bedrock, 3);
                SoundInstance soundInstance = type.breakSound();
                ClientboundSoundPacket packet = new ClientboundSoundPacket(soundInstance.sound(), SoundSource.BLOCKS, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, (soundInstance.volume() + 1.0F) / 2.0F, soundInstance.pitch() * 0.8F, level.random.nextLong());
                player.connection.send(packet);
                ((ModServerLevel) level).molecraft$schedule(100, serverLevel -> serverLevel.setBlock(pos, state, 3));
            } else {
                ((ModServerLevel) level).molecraft$schedule(resonantFrequency + (onTime ? delta : 0), serverLevel ->
                {
                    ClientboundSoundPacket packet = new ClientboundSoundPacket(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.COPPER_GRATE_BREAK), SoundSource.BLOCKS, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 1.5F, 0.75F, level.random.nextLong());
                    player.connection.send(packet);
                });
            }
        } else {
            mining.currentlyMining.remove(pos);
            blockDestruction(pos, 10);
            lastHitBlock = null;
        }
        destroyPos = pos.immutable();
        ci.cancel();
    }

    @Unique
    private void blockDestruction(BlockPos pos, int progress) {
        for (ServerPlayer serverPlayer : level.getServer().getPlayerList().getPlayers()) {
            if (serverPlayer != null && serverPlayer.level() == level) {
                double d = pos.getX() - serverPlayer.getX();
                double e = pos.getY() - serverPlayer.getY();
                double f = pos.getZ() - serverPlayer.getZ();
                if (d * d + e * e + f * f < 1024.0) serverPlayer.connection.send(new ClientboundBlockDestructionPacket(~player.getId(), pos, progress));
            }
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void molecraftTick(CallbackInfo ci) {
        long gameTime = level.getGameTime();
        if (gameTime - nextMiningHit > 1 && lastHitBlock != null) {
            Mining mining = ((ModServerLevelData) level.getLevelData()).molecraft$getMining();
            MiningPlayer miningPlayer = mining.currentlyMining.get(lastHitBlock);
            if (miningPlayer == null || miningPlayer.player.equals(player)) {
                mining.currentlyMining.remove(lastHitBlock);
                blockDestruction(lastHitBlock, 10);
                lastHitBlock = null;
            }
        }
    }
}
