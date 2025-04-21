package com.kaboomroads.molecraft.mining;

import com.kaboomroads.molecraft.util.SoundInstance;
import org.jetbrains.annotations.Nullable;

public record BlockType(int breakingPower, double blockHealth, int resonantFrequency, SoundInstance breakSound, @Nullable String loot) {
}
