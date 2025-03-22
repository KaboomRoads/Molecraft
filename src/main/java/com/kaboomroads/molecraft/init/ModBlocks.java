package com.kaboomroads.molecraft.init;

import com.kaboomroads.molecraft.ModConstants;
import com.kaboomroads.molecraft.block.MineableBlock;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

import java.util.function.Function;

public class ModBlocks {
    public static final Block NOISE_PERMAFROST = register(
            "noise_permafrost",
            properties -> new MineableBlock(properties, 10, 100.0),
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .requiresCorrectToolForDrops()
                    .strength(1.0F)
    );

    public static Block register(ResourceKey<Block> resourceKey, Function<BlockBehaviour.Properties, Block> function, BlockBehaviour.Properties properties) {
        Block block = function.apply(properties.setId(resourceKey));
        Block registered = Registry.register(BuiltInRegistries.BLOCK, resourceKey, block);
        for (BlockState blockState : block.getStateDefinition().getPossibleStates()) {
            Block.BLOCK_STATE_REGISTRY.add(blockState);
            blockState.initCache();
        }
        block.getLootTable();
        return registered;
    }

    public static Block register(ResourceKey<Block> resourceKey, BlockBehaviour.Properties properties) {
        return register(resourceKey, Block::new, properties);
    }

    private static ResourceKey<Block> blockId(String name) {
        return ResourceKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, name));
    }

    private static Block register(String name, Function<BlockBehaviour.Properties, Block> function, BlockBehaviour.Properties properties) {
        return register(blockId(name), function, properties);
    }

    private static Block register(String name, BlockBehaviour.Properties properties) {
        return register(name, Block::new, properties);
    }

    public static void init() {
    }
}
