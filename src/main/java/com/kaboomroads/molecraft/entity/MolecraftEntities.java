package com.kaboomroads.molecraft.entity;

import com.kaboomroads.molecraft.entity.custom.HealthDisplay;
import com.kaboomroads.molecraft.entity.custom.MasterModeFloorFour;
import com.kaboomroads.molecraft.item.Rarity;
import net.minecraft.network.chat.Component;

import java.util.HashMap;

public class MolecraftEntities {
    public static HashMap<String, MolecraftEntity> ENTITIES = new HashMap<>();
    public static final MolecraftEntity MASTER_MODE_FLOOR_FOUR = register(new MolecraftEntity("master_mode_floor_four", Component.literal("Master Mode Floor Four"), MasterModeFloorFour::new, Rarity.ADMIN, true));
    public static final MolecraftEntity HEALTH_DISPLAY = register(new MolecraftEntity("health_display", Component.literal("Health Display"), HealthDisplay::new, Rarity.NONE, false));

    public static MolecraftEntity register(MolecraftEntity molecraftEntity) {
        ENTITIES.put(molecraftEntity.id(), molecraftEntity);
        return molecraftEntity;
    }
}
