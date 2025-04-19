package com.kaboomroads.molecraft.mining;

import com.kaboomroads.molecraft.util.SoundInstance;

public record BlockType(int breakingPower, double blockHealth, int resonantFrequency, SoundInstance breakSound) {
    //TODO: DROPS, MCM LOOT ADD PRESET LIKE SYSTEM
}
