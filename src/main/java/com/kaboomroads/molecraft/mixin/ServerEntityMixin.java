package com.kaboomroads.molecraft.mixin;

import com.kaboomroads.molecraft.entity.PlayerEntity;
import com.kaboomroads.molecraft.mixinimpl.ModServerLevel;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.authlib.GameProfile;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

@Mixin(ServerEntity.class)
public abstract class ServerEntityMixin {
    @Shadow
    @Final
    private Entity entity;

    @Shadow
    @Final
    private ServerLevel level;

    @Shadow
    private @Nullable List<SynchedEntityData.DataValue<?>> trackedDataValues;

    @Shadow
    @Final
    private Consumer<Packet<?>> broadcast;
    @Unique
    private ServerPlayer serverPlayer = null;

    @WrapMethod(method = "sendPairingData")
    private void hideItemEntities(ServerPlayer player, Consumer<Packet<ClientGamePacketListener>> consumer, Operation<Void> original) {
        if (entity instanceof ItemEntity itemEntity) {
            if (itemEntity.target == null || itemEntity.target.equals(player.getUUID())) original.call(player, consumer);
        } else original.call(player, consumer);
    }

    @WrapOperation(method = "sendPairingData", at = @At(value = "INVOKE", target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V", ordinal = 0))
    private <T> void molecraftPlayerEntity(Consumer<T> instance, T t, Operation<Void> original) {
        if (entity instanceof PlayerEntity playerEntity) {
            int entityId = entity.getId();
            GameProfile profile = playerEntity.createGameProfile(entity);
            serverPlayer = FakePlayerInvoker.init(level, profile);
            serverPlayer.setId(entityId);
            ClientboundPlayerInfoUpdatePacket infoPacket = new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, serverPlayer);
            Packet<ClientGamePacketListener> spawnPacket = serverPlayer.getAddEntityPacket((ServerEntity) (Object) this);
            ClientboundRotateHeadPacket roatatePacket = new ClientboundRotateHeadPacket(serverPlayer, (byte) ((entity.getYHeadRot() / 360) * 256));
            Scoreboard scoreboard = new Scoreboard();
            String name = profile.getName();
            PlayerTeam team = new PlayerTeam(scoreboard, name);
            team.setNameTagVisibility(Team.Visibility.NEVER);
            team.getPlayers().add(name);
            ClientboundSetPlayerTeamPacket teamPacket = ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(team, true);
            SynchedEntityData.DataItem<Byte> skinLayerDataItem = new SynchedEntityData.DataItem<>(new EntityDataAccessor<>(17, EntityDataSerializers.BYTE), (byte) 127);
            ClientboundSetEntityDataPacket playerDataPacket = new ClientboundSetEntityDataPacket(entityId, List.of(skinLayerDataItem.value()));
            SynchedEntityData entityData = entity.getEntityData();
            SynchedEntityData playerData = serverPlayer.getEntityData();
            trackedDataValues = filterDataValues(entityData.getNonDefaultValues(), playerData);

            original.call(instance, infoPacket);
            original.call(instance, spawnPacket);
            original.call(instance, roatatePacket);
            original.call(instance, teamPacket);
            original.call(instance, playerDataPacket);

            remove(level.getServer().getPlayerList(), serverPlayer);
            ((ModServerLevel) level).molecraft$schedule(40, serverLevel -> broadcast.accept(new ClientboundPlayerInfoRemovePacket(List.of(serverPlayer.getUUID()))));
        } else {
            original.call(instance, t);
        }
    }

    @Unique
    private void remove(PlayerList playerList, ServerPlayer player) {
        player.getAdvancements().stopListening();
        playerList.getPlayers().remove(player);
        UUID uuid = player.getUUID();
        if (playerList.playersByUUID.get(uuid) == player) playerList.playersByUUID.remove(uuid);
        if (playerList.stats.get(uuid) == player.getStats()) playerList.stats.remove(uuid);
        if (playerList.advancements.get(uuid) == player.getAdvancements()) playerList.advancements.remove(uuid);
    }

    @ModifyExpressionValue(method = "sendDirtyEntityData", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/syncher/SynchedEntityData;packDirty()Ljava/util/List;"))
    private List<SynchedEntityData.DataValue<?>> filterDirty(@Nullable List<SynchedEntityData.DataValue<?>> original) {
        if (serverPlayer != null && original != null) return filterDataValues(original, serverPlayer.getEntityData());
        return original;
    }

    @ModifyExpressionValue(method = "sendDirtyEntityData", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/syncher/SynchedEntityData;getNonDefaultValues()Ljava/util/List;"))
    private List<SynchedEntityData.DataValue<?>> filterNonDefaultValues(@Nullable List<SynchedEntityData.DataValue<?>> original) {
        if (serverPlayer != null) return filterDataValues(original, serverPlayer.getEntityData());
        return original;
    }

    @Unique
    @NotNull
    private List<SynchedEntityData.DataValue<?>> filterDataValues(List<SynchedEntityData.DataValue<?>> entityNonDefault, SynchedEntityData playerData) {
        return entityNonDefault.stream().filter(dataValue -> Objects.equals(dataValue.serializer(), playerData.itemsById[dataValue.id()].accessor.serializer())).toList();
    }
}
