package com.kaboomroads.molecraft.mixin;

import com.kaboomroads.molecraft.loot.LootManager;
import com.kaboomroads.molecraft.mining.Mining;
import com.kaboomroads.molecraft.mixinimpl.ModServerLevelData;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.serialization.Dynamic;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.PrimaryLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(PrimaryLevelData.class)
public abstract class PrimaryLevelDataMixin implements ModServerLevelData {
    @Unique
    private Mining mining = new Mining();
    @Unique
    private LootManager lootManager = new LootManager();

    @Override
    public Mining molecraft$getMining() {
        return mining;
    }

    @Override
    public void molecraft$setMining(Mining mining) {
        this.mining = mining;
    }

    @Override
    public LootManager molecraft$getLootManager() {
        return lootManager;
    }

    @Override
    public void molecraft$setLootManager(LootManager lootManager) {
        this.lootManager = lootManager;
    }

    @Inject(method = "setTagData", at = @At("TAIL"))
    private void saveMolecraft(RegistryAccess registry, CompoundTag tag, CompoundTag playerNBT, CallbackInfo ci) {
        tag.put("molecraft_mining", mining.save());
        tag.put("molecraft_loot_manager", lootManager.save());
    }

    @ModifyReturnValue(method = "parse", at = @At("RETURN"))
    private static <T> PrimaryLevelData loadMolecraft(PrimaryLevelData original, @Local(ordinal = 0, argsOnly = true) Dynamic<T> tag) {
        Optional<CompoundTag> miningTag = tag.get("molecraft_mining").flatMap(CompoundTag.CODEC::parse).result();
        if (miningTag.isPresent()) {
            Optional<HolderGetter<Block>> blockLookup = ((RegistryOps<T>) tag.getOps()).getter(Registries.BLOCK);
            Optional<HolderGetter<SoundEvent>> soundEventLookup = ((RegistryOps<T>) tag.getOps()).getter(Registries.SOUND_EVENT);
            ((ModServerLevelData) original).molecraft$setMining(Mining.load(miningTag.get(), blockLookup.get(), soundEventLookup.get()));
        }
        Optional<CompoundTag> lootManagerTag = tag.get("molecraft_loot_manager").flatMap(CompoundTag.CODEC::parse).result();
        lootManagerTag.ifPresent(compoundTag -> ((ModServerLevelData) original).molecraft$setLootManager(LootManager.load(compoundTag)));
        return original;
    }
}
