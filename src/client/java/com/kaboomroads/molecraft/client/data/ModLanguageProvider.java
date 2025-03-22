package com.kaboomroads.molecraft.client.data;

import com.kaboomroads.molecraft.init.ModAttributes;
import com.kaboomroads.molecraft.init.ModBlocks;
import com.kaboomroads.molecraft.init.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public class ModLanguageProvider extends FabricLanguageProvider {
    protected ModLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, registryLookup);
    }

    protected ModLanguageProvider(FabricDataOutput dataOutput, String languageCode, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, languageCode, registryLookup);
    }

    @Override
    public void generateTranslations(HolderLookup.Provider provider, TranslationBuilder builder) {
        builder.add(ModAttributes.MAX_HEALTH, "Max Health");
        builder.add(ModAttributes.HEALTH_REGEN, "Health Regen");
        builder.add(ModAttributes.DEFENSE, "Defense");
        builder.add(ModAttributes.DAMAGE, "Damage");
        builder.add(ModAttributes.CRIT_DAMAGE, "Crit Damage");
        builder.add(ModAttributes.CRIT_CHANCE, "Crit Chance");
        builder.add(ModAttributes.SPELL_DAMAGE, "Spell Damage");
        builder.add(ModAttributes.MAX_MANA, "Max Mana");
        builder.add(ModAttributes.MANA_REGEN, "Mana Regen");
        builder.add(ModAttributes.BREAKING_POWER, "Breaking Power");
        builder.add(ModAttributes.MINING_SPEED, "Mining Speed");
        builder.add(ModAttributes.MINING_FORTUNE, "Mining Fortune");

        builder.add(ModItems.CREATION, "Creation");
        
        builder.add(ModBlocks.NOISE_PERMAFROST, "Noise Permafrost");
    }
}
