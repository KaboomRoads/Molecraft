package com.kaboomroads.molecraft.entity;

import com.kaboomroads.molecraft.entity.custom.*;
import com.kaboomroads.molecraft.item.Rarity;
import net.minecraft.network.chat.Component;

import java.util.HashMap;

public class MolecraftEntities {
    public static HashMap<String, MolecraftEntity> ENTITIES = new HashMap<>();
    public static final MolecraftEntity HEALTH_DISPLAY = register(new MolecraftEntity("health_display", Component.literal("Health Display"), HealthDisplay::new, Rarity.NONE, false, 0));
    public static final MolecraftEntity DAMAGE_INDICATOR = register(new MolecraftEntity("damage_indicator", Component.literal("Damage Indicator"), DamageIndicator::new, Rarity.NONE, false, 0));
    public static final MolecraftEntity SPAWNER = register(new MolecraftEntity("spawner", Component.literal("Spawner"), Spawner::new, Rarity.NONE, false, 0));
    public static final MolecraftEntity MASTER_MODE_FLOOR_FOUR = register(new MolecraftEntity("master_mode_floor_four", Component.literal("Master Mode Floor Four"), MasterModeFloorFour::new, Rarity.ADMIN, true, 1000));
    public static final MolecraftEntity LIVING_MUD = register(new MolecraftEntity("living_mud", Component.literal("Living Mud"), LivingMud::new, Rarity.COMMON, true, 10));
    public static final MolecraftEntity LIVING_SLUDGE = register(new MolecraftEntity("living_sludge", Component.literal("Living Sludge"), LivingSludge::new, Rarity.UNCOMMON, true, 15));
    public static final MolecraftEntity SHROOMER = register(new MolecraftEntity("shroomer", Component.literal("Shroomer"), Shroomer::new, Rarity.UNCOMMON, true, 15));
    public static final MolecraftEntity MOOSHROOMER = register(new MolecraftEntity("mooshroomer", Component.literal("Mooshroomer"), Mooshroomer::new, Rarity.COMMON, false, 10));
    public static final MolecraftEntity CREAKING = register(new MolecraftEntity("creaking", Component.literal("Creaking"), MolecraftCreaking::new, Rarity.UNCOMMON, false, 30));
    public static final MolecraftEntity BOOM_SHROOMER = register(new MolecraftEntity("boom_shroomer", Component.literal("Boom Shroomer"), BoomShroomer::new, Rarity.UNCOMMON, false, 0));

    public static MolecraftEntity register(MolecraftEntity molecraftEntity) {
        ENTITIES.put(molecraftEntity.id, molecraftEntity);
        return molecraftEntity;
    }
}
