package com.kaboomroads.molecraft.item;

import net.minecraft.nbt.CompoundTag;

import java.util.Optional;

public record SavedMolecraftItem(Optional<CompoundTag> tag, int count) {
}
