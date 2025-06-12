package com.kaboomroads.molecraft.util;

import net.minecraft.resources.ResourceLocation;

public record SoundInstance(ResourceLocation sound, float volume, float pitch) {
}
