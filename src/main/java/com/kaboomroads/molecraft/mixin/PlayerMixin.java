package com.kaboomroads.molecraft.mixin;

import com.kaboomroads.molecraft.command.TradeCommand;
import com.kaboomroads.molecraft.entity.StatType;
import com.kaboomroads.molecraft.entity.StatsMap;
import com.kaboomroads.molecraft.menu.TradeMenu;
import com.kaboomroads.molecraft.mixinimpl.ModPlayer;
import com.kaboomroads.molecraft.util.MolecraftUtil;
import com.kaboomroads.molecraft.util.TradeData;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetScorePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.text.NumberFormat;
import java.util.Optional;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntityMixin implements ModPlayer {
    @Shadow
    public abstract void displayClientMessage(Component chatComponent, boolean actionBar);

    public PlayerMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Unique
    private int regenTick = 0;
    @Unique
    private TradeData tickingTradeData = null;
    @Unique
    @Nullable
    private TradeCommand.PendingTrade sentTrade = null;
    @Unique
    private long coins = 0;
    @Unique
    private long oCoins = Long.MIN_VALUE;

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;tick(Lnet/minecraft/server/level/ServerPlayer;)V"))
    private void molecraftTick(FoodData instance, ServerPlayer player, Operation<Void> original) {
        regenTick++;
        if (regenTick > 40) {
            regenTick = 0;
            StatsMap stats = molecraft$getStats();
            molecraft$setHealth(molecraft$getHealth() + stats.get(StatType.MAX_HEALTH).cachedValue * stats.get(StatType.HEALTH_REGEN).cachedValue * 0.01);
        }
        if (tickCount % 5 == 0) {
            double health = molecraft$getHealth();
            StatsMap stats = molecraft$getStats();
            double maxHealth = stats.get(StatType.MAX_HEALTH).cachedValue;
            double defense = stats.get(StatType.DEFENSE).cachedValue;
            double maxMana = stats.get(StatType.MAX_MANA).cachedValue;
            double mana = molecraft$getMana();
            Component overlayMessageString = StatType.MAX_HEALTH.format(Component.literal(((int) health) + "/" + ((int) maxHealth))).append("    ").append(StatType.DEFENSE.format(Component.literal("" + (int) defense))).append("    ").append(StatType.MAX_MANA.format(Component.literal(((int) mana) + "/" + ((int) maxMana))));
            ClientboundSetActionBarTextPacket packet = new ClientboundSetActionBarTextPacket(overlayMessageString);
            player.connection.send(packet);

            if (coins != oCoins) {
                NumberFormat format = NumberFormat.getInstance();
                ClientboundSetScorePacket scorePacket = new ClientboundSetScorePacket("coins", "molecraft_sidebar", 0, Optional.of(Component.literal("Coins: ").append(Component.literal(format.format(coins)).withStyle(ChatFormatting.GOLD))), Optional.empty());
                player.connection.send(scorePacket);
                oCoins = coins;
            }
        }
    }

    @Inject(method = "serverAiStep", at = @At("TAIL"))
    private void serverAiStep(CallbackInfo ci) {
        if (tickingTradeData != null) {
            if (tickingTradeData.senderMenu != null && tickingTradeData.receiverMenu != null) {
                int seconds = tickingTradeData.senderMenu.getTradeSeconds();
                if (seconds == tickingTradeData.nextSeconds) {
                    TradeMenu senderMenu = tickingTradeData.senderMenu;
                    TradeMenu receiverMenu = tickingTradeData.receiverMenu;
                    senderMenu.setTradeTimer(seconds);
                    receiverMenu.setTradeTimer(seconds);
                    if (seconds <= 0) {
                        senderMenu.setReady(true);
                        receiverMenu.setReady(true);
                    }
                    tickingTradeData.nextSeconds = seconds - 1;
                }
            }
            if (tickingTradeData.tradeTime <= 0 && tickingTradeData.senderConfirm && tickingTradeData.receiverConfirm) {
                tickingTradeData.trade();
                tickingTradeData = null;
            } else if (tickingTradeData.tradeTime > 0) {
                tickingTradeData.tradeTime -= 1;
            }
        }
        if (sentTrade != null) {
            if (sentTrade.expireTime-- <= 0) {
                ServerPlayer receiver = (ServerPlayer) level().getPlayerByUUID(sentTrade.receiver);
                ServerPlayer sender = (ServerPlayer) level().getPlayerByUUID(sentTrade.sender);
                if (sender != null && receiver != null) {
                    displayClientMessage(Component.literal("Your trade with ").append((sentTrade.sender.equals(getUUID()) ? receiver : sender).getDisplayName()).append(" has expired!").withStyle(ChatFormatting.RED), false);
                    ((ModPlayer) sender).molecraft$setSentTrade(null);
                    ((ModPlayer) receiver).molecraft$setSentTrade(null);
                }
                sentTrade = null;
            }
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void saveMolecraft(CompoundTag tag, CallbackInfo ci) {
        tag.putLong("coins", coins);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void loadMolecraft(CompoundTag tag, CallbackInfo ci) {
        if (tag.contains("coins", CompoundTag.TAG_LONG)) coins = tag.getLong("coins");
    }

    @Override
    public long molecraft$getCoins() {
        return coins;
    }

    @Override
    public void molecraft$setCoins(long coins) {
        this.coins = coins;
    }

    @Override
    public TradeData molecraft$getTickingTradeData() {
        return tickingTradeData;
    }

    @Override
    public void molecraft$setTickingTradeData(TradeData tradeData) {
        tickingTradeData = tradeData;
    }

    @Nullable
    @Override
    public TradeCommand.PendingTrade molecraft$getSentTrade() {
        return sentTrade;
    }

    @Override
    public void molecraft$setSentTrade(TradeCommand.PendingTrade sentTrade) {
        this.sentTrade = sentTrade;
    }

    @WrapOperation(method = "addAdditionalSaveData", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V"))
    private void noFood(FoodData instance, CompoundTag compoundTag, Operation<Void> original) {
    }

    @Override
    public StatsMap.Builder molecraft$initStats() {
        return super.molecraft$initStats()
                .stat(StatType.BREAKING_POWER, 0)
                .stat(StatType.MINING_STRENGTH, 10)
                .stat(StatType.BRILLIANCE, 0)
                ;
    }

    @WrapMethod(method = "actuallyHurt")
    private void wrap_actuallyHurt(ServerLevel level, DamageSource damageSource, float amount, Operation<Void> original) {
        MolecraftUtil.dealDamage((LivingEntity) (Object) this, level, damageSource, amount);
    }
}
