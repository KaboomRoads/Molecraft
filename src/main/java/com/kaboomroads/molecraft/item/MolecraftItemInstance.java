package com.kaboomroads.molecraft.item;

import net.minecraft.nbt.CompoundTag;

import java.util.Optional;

public record MolecraftItemInstance(Optional<CompoundTag> tag, int count) {
}
