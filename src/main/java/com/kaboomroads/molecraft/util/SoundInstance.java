package com.kaboomroads.molecraft.util;

import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;

public record SoundInstance(Holder<SoundEvent> sound, float volume, float pitch) {
}
