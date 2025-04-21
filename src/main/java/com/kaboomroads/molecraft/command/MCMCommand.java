package com.kaboomroads.molecraft.command;

import com.kaboomroads.molecraft.mining.BlockType;
import com.kaboomroads.molecraft.mining.IAABB;
import com.kaboomroads.molecraft.mining.Mining;
import com.kaboomroads.molecraft.mining.MiningArea;
import com.kaboomroads.molecraft.mixinimpl.ModServerLevel;
import com.kaboomroads.molecraft.mixinimpl.ModServerLevelData;
import com.kaboomroads.molecraft.util.SoundInstance;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class MCMCommand {
    public static final SuggestionProvider<CommandSourceStack> SUGGEST_AREAS = (context, builder) -> {
        Mining mining = getMining(context);
        return SharedSuggestionProvider.suggest(mining.areas.keySet(), builder);
    };
    public static final SuggestionProvider<CommandSourceStack> SUGGEST_PRESETS = (context, builder) -> {
        Mining mining = getMining(context);
        return SharedSuggestionProvider.suggest(mining.presets.keySet(), builder);
    };

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext) {
        dispatcher.register(
                Commands.literal("mcm")
                        .requires(commandSourceStack -> commandSourceStack.hasPermission(2))
                        .then(Commands.literal("build").executes(MCMCommand::build))
                        .then(Commands.literal("list").executes(MCMCommand::list))
                        .then(Commands.literal("show")
                                .executes(context -> {
                                    context.getSource().sendSystemMessage(Component.literal("Showing all areas"));
                                    Mining mining = getMining(context);
                                    return show(context.getSource().getLevel(), context.getSource().getPlayer(), mining.areas.values());
                                })
                                .then(Commands.argument("id", StringArgumentType.string()).suggests(SUGGEST_AREAS)
                                        .executes(context -> {
                                            String name = StringArgumentType.getString(context, "id");
                                            CommandSourceStack source = context.getSource();
                                            source.sendSystemMessage(Component.literal("Showing area " + name));
                                            Mining mining = getMining(context);
                                            return show(source.getLevel(), source.getPlayer(), List.of(mining.areas.get(name)));
                                        })
                                )
                        )
                        .then(Commands.literal("remove")
                                .then(Commands.argument("id", StringArgumentType.string()).suggests(SUGGEST_AREAS)
                                        .executes(context -> remove(
                                                context,
                                                StringArgumentType.getString(context, "id")
                                        ))
                                )
                        )
                        .then(Commands.literal("add")
                                .then(Commands.argument("id", StringArgumentType.string())
                                        .then(Commands.argument("from", BlockPosArgument.blockPos())
                                                .then(Commands.argument("to", BlockPosArgument.blockPos())
                                                        .executes(context -> add(
                                                                context,
                                                                StringArgumentType.getString(context, "id"),
                                                                BlockPosArgument.getBlockPos(context, "from"),
                                                                BlockPosArgument.getBlockPos(context, "to")
                                                        ))
                                                )
                                        )
                                )
                        )
                        .then(Commands.literal("set")
                                .then(Commands.argument("id", StringArgumentType.string()).suggests(SUGGEST_AREAS)
                                        .then(Commands.argument("from", BlockPosArgument.blockPos())
                                                .then(Commands.argument("to", BlockPosArgument.blockPos())
                                                        .executes(context -> set(
                                                                context,
                                                                StringArgumentType.getString(context, "id"),
                                                                BlockPosArgument.getBlockPos(context, "from"),
                                                                BlockPosArgument.getBlockPos(context, "to")
                                                        ))
                                                )
                                        )
                                )
                        )
                        .then(Commands.literal("select")
                                .then(Commands.argument("id", StringArgumentType.string()).suggests(SUGGEST_AREAS)
                                        .then(Commands.literal("add")
                                                .then(Commands.argument("block", BlockStateArgument.block(buildContext))
                                                        .then(Commands.argument("breakingPower", IntegerArgumentType.integer(0))
                                                                .then(Commands.argument("blockHealth", DoubleArgumentType.doubleArg(0))
                                                                        .then(Commands.argument("resonantFrequency", IntegerArgumentType.integer(0))
                                                                                .then(Commands.argument("sound", ResourceLocationArgument.id()).suggests(SuggestionProviders.AVAILABLE_SOUNDS)
                                                                                        .then(Commands.argument("volume", FloatArgumentType.floatArg(0))
                                                                                                .then(Commands.argument("pitch", FloatArgumentType.floatArg(0))
                                                                                                        .executes(context -> modifyAdd(
                                                                                                                context,
                                                                                                                StringArgumentType.getString(context, "id"),
                                                                                                                BlockStateArgument.getBlock(context, "block").getState().getBlock(),
                                                                                                                IntegerArgumentType.getInteger(context, "breakingPower"),
                                                                                                                DoubleArgumentType.getDouble(context, "blockHealth"),
                                                                                                                IntegerArgumentType.getInteger(context, "resonantFrequency"),
                                                                                                                ResourceLocationArgument.getId(context, "sound"),
                                                                                                                FloatArgumentType.getFloat(context, "volume"),
                                                                                                                FloatArgumentType.getFloat(context, "pitch"),
                                                                                                                null
                                                                                                        ))
                                                                                                        .then(Commands.argument("loot", StringArgumentType.string()).suggests(MCLCommand.SUGGEST_LOOT)
                                                                                                                .executes(context -> modifyAdd(
                                                                                                                        context,
                                                                                                                        StringArgumentType.getString(context, "id"),
                                                                                                                        BlockStateArgument.getBlock(context, "block").getState().getBlock(),
                                                                                                                        IntegerArgumentType.getInteger(context, "breakingPower"),
                                                                                                                        DoubleArgumentType.getDouble(context, "blockHealth"),
                                                                                                                        IntegerArgumentType.getInteger(context, "resonantFrequency"),
                                                                                                                        ResourceLocationArgument.getId(context, "sound"),
                                                                                                                        FloatArgumentType.getFloat(context, "volume"),
                                                                                                                        FloatArgumentType.getFloat(context, "pitch"),
                                                                                                                        StringArgumentType.getString(context, "loot")
                                                                                                                ))
                                                                                                        )
                                                                                                )
                                                                                        )
                                                                                )
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                        .then(Commands.literal("loot")
                                                .then(Commands.argument("block", BlockStateArgument.block(buildContext))
                                                        .then(Commands.literal("set")
                                                                .then(Commands.argument("loot", StringArgumentType.string()).suggests(MCLCommand.SUGGEST_LOOT)
                                                                        .executes(context -> modifyLootSet(
                                                                                context,
                                                                                StringArgumentType.getString(context, "name"),
                                                                                BlockStateArgument.getBlock(context, "block").getState().getBlock(),
                                                                                StringArgumentType.getString(context, "loot")
                                                                        ))
                                                                )
                                                        )
                                                        .then(Commands.literal("remove")
                                                                .executes(context -> modifyLootRemove(
                                                                        context,
                                                                        StringArgumentType.getString(context, "name"),
                                                                        BlockStateArgument.getBlock(context, "block").getState().getBlock()
                                                                ))
                                                        )
                                                )
                                        )
                                        .then(Commands.literal("remove")
                                                .then(Commands.argument("block", BlockStateArgument.block(buildContext))
                                                        .executes(context -> modifyRemove(
                                                                context,
                                                                StringArgumentType.getString(context, "id"),
                                                                BlockStateArgument.getBlock(context, "block").getState().getBlock()
                                                        ))
                                                )
                                        )
                                        .then(Commands.literal("clear")
                                                .executes(context -> modifyClear(
                                                        context,
                                                        StringArgumentType.getString(context, "id")
                                                ))
                                        )
                                        .then(Commands.literal("list")
                                                .executes(context -> modifyList(
                                                        context,
                                                        StringArgumentType.getString(context, "id")
                                                ))
                                        )
                                        .then(Commands.literal("rename")
                                                .then(Commands.argument("newName", StringArgumentType.string())
                                                        .executes(context -> modifyRename(
                                                                context,
                                                                StringArgumentType.getString(context, "id"),
                                                                StringArgumentType.getString(context, "newName")
                                                        ))
                                                )
                                        )
                                        .then(Commands.literal("preset")
                                                .then(Commands.literal("add")
                                                        .then(Commands.argument("name", StringArgumentType.string()).suggests(SUGGEST_PRESETS)
                                                                .executes(context -> modifyPresetAdd(
                                                                        context,
                                                                        StringArgumentType.getString(context, "id"),
                                                                        StringArgumentType.getString(context, "name")
                                                                ))
                                                        )
                                                )
                                                .then(Commands.literal("remove")
                                                        .then(Commands.argument("name", StringArgumentType.string()).suggests(SUGGEST_PRESETS)
                                                                .executes(context -> modifyPresetRemove(
                                                                        context,
                                                                        StringArgumentType.getString(context, "id"),
                                                                        StringArgumentType.getString(context, "name")
                                                                ))
                                                        )
                                                )
                                                .then(Commands.literal("clear")
                                                        .executes(context -> modifyPresetClear(
                                                                context,
                                                                StringArgumentType.getString(context, "id")
                                                        ))
                                                )
                                                .then(Commands.literal("list")
                                                        .executes(context -> modifyPresetList(
                                                                context,
                                                                StringArgumentType.getString(context, "id")
                                                        ))
                                                )
                                        )
                                )
                        )
                        .then(Commands.literal("preset")
                                .then(Commands.literal("add")
                                        .then(Commands.argument("name", StringArgumentType.string())
                                                .executes(context -> presetAdd(
                                                        context,
                                                        StringArgumentType.getString(context, "name")
                                                ))
                                        )
                                )
                                .then(Commands.literal("remove")
                                        .then(Commands.argument("name", StringArgumentType.string()).suggests(SUGGEST_PRESETS)
                                                .executes(context -> presetRemove(
                                                        context,
                                                        StringArgumentType.getString(context, "name")
                                                ))
                                        )
                                )
                                .then(Commands.literal("list")
                                        .executes(MCMCommand::presetList)
                                )
                                .then(Commands.literal("select")
                                        .then(Commands.argument("name", StringArgumentType.string()).suggests(SUGGEST_PRESETS)
                                                .then(Commands.literal("add")
                                                        .then(Commands.argument("block", BlockStateArgument.block(buildContext))
                                                                .then(Commands.argument("breakingPower", IntegerArgumentType.integer(0))
                                                                        .then(Commands.argument("blockHealth", DoubleArgumentType.doubleArg(0))
                                                                                .then(Commands.argument("resonantFrequency", IntegerArgumentType.integer(0))
                                                                                        .then(Commands.argument("sound", ResourceLocationArgument.id()).suggests(SuggestionProviders.AVAILABLE_SOUNDS)
                                                                                                .then(Commands.argument("volume", FloatArgumentType.floatArg(0))
                                                                                                        .then(Commands.argument("pitch", FloatArgumentType.floatArg(0))
                                                                                                                .executes(context -> presetModifyAdd(
                                                                                                                        context,
                                                                                                                        StringArgumentType.getString(context, "name"),
                                                                                                                        BlockStateArgument.getBlock(context, "block").getState().getBlock(),
                                                                                                                        IntegerArgumentType.getInteger(context, "breakingPower"),
                                                                                                                        DoubleArgumentType.getDouble(context, "blockHealth"),
                                                                                                                        IntegerArgumentType.getInteger(context, "resonantFrequency"),
                                                                                                                        ResourceLocationArgument.getId(context, "sound"),
                                                                                                                        FloatArgumentType.getFloat(context, "volume"),
                                                                                                                        FloatArgumentType.getFloat(context, "pitch"),
                                                                                                                        null
                                                                                                                ))
                                                                                                                .then(Commands.argument("loot", StringArgumentType.string()).suggests(MCLCommand.SUGGEST_LOOT)
                                                                                                                        .executes(context -> presetModifyAdd(
                                                                                                                                context,
                                                                                                                                StringArgumentType.getString(context, "name"),
                                                                                                                                BlockStateArgument.getBlock(context, "block").getState().getBlock(),
                                                                                                                                IntegerArgumentType.getInteger(context, "breakingPower"),
                                                                                                                                DoubleArgumentType.getDouble(context, "blockHealth"),
                                                                                                                                IntegerArgumentType.getInteger(context, "resonantFrequency"),
                                                                                                                                ResourceLocationArgument.getId(context, "sound"),
                                                                                                                                FloatArgumentType.getFloat(context, "volume"),
                                                                                                                                FloatArgumentType.getFloat(context, "pitch"),
                                                                                                                                StringArgumentType.getString(context, "loot")
                                                                                                                        ))
                                                                                                                )
                                                                                                        )
                                                                                                )
                                                                                        )
                                                                                )
                                                                        )
                                                                )
                                                        )
                                                )
                                                .then(Commands.literal("remove")
                                                        .then(Commands.argument("block", BlockStateArgument.block(buildContext))
                                                                .executes(context -> presetModifyRemove(
                                                                        context,
                                                                        StringArgumentType.getString(context, "name"),
                                                                        BlockStateArgument.getBlock(context, "block").getState().getBlock()
                                                                ))
                                                        )
                                                )
                                                .then(Commands.literal("loot")
                                                        .then(Commands.argument("block", BlockStateArgument.block(buildContext))
                                                                .then(Commands.literal("set")
                                                                        .then(Commands.argument("loot", StringArgumentType.string()).suggests(MCLCommand.SUGGEST_LOOT)
                                                                                .executes(context -> presetModifyLootSet(
                                                                                        context,
                                                                                        StringArgumentType.getString(context, "name"),
                                                                                        BlockStateArgument.getBlock(context, "block").getState().getBlock(),
                                                                                        StringArgumentType.getString(context, "loot")
                                                                                ))
                                                                        )
                                                                )
                                                                .then(Commands.literal("remove")
                                                                        .executes(context -> presetModifyLootRemove(
                                                                                context,
                                                                                StringArgumentType.getString(context, "name"),
                                                                                BlockStateArgument.getBlock(context, "block").getState().getBlock()
                                                                        ))
                                                                )
                                                        )
                                                )
                                                .then(Commands.literal("clear")
                                                        .executes(context -> presetModifyClear(
                                                                context,
                                                                StringArgumentType.getString(context, "name")
                                                        ))
                                                )
                                                .then(Commands.literal("list")
                                                        .executes(context -> presetModifyList(
                                                                context,
                                                                StringArgumentType.getString(context, "name")
                                                        ))
                                                )
                                                .then(Commands.literal("rename")
                                                        .then(Commands.argument("newName", StringArgumentType.string())
                                                                .executes(context -> presetModifyRename(
                                                                        context,
                                                                        StringArgumentType.getString(context, "name"),
                                                                        StringArgumentType.getString(context, "newName")
                                                                ))
                                                        )
                                                )
                                        )
                                )
                        )
        );
    }

    private static Mining getMining(CommandContext<CommandSourceStack> context) {
        return ((ModServerLevelData) context.getSource().getLevel().getLevelData()).molecraft$getMining();
    }

    public static boolean areaInvalid(String name, CommandSourceStack source, Mining mining) {
        if (!mining.areas.containsKey(name)) {
            source.sendSystemMessage(Component.literal("Couldn't find area §d" + name).withStyle(ChatFormatting.RED));
            return true;
        }
        return false;
    }

    public static boolean presetInvalid(String name, CommandSourceStack source, Mining mining) {
        if (!mining.presets.containsKey(name)) {
            source.sendSystemMessage(Component.literal("Couldn't find preset §6" + name).withStyle(ChatFormatting.RED));
            return true;
        }
        return false;
    }

    public static void updateAreaPresets(String name, Mining mining) {
        for (MiningArea area : mining.areas.values()) if (area.presets != null) area.presets.remove(name);
    }

    public static int build(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        Mining mining = getMining(context);
        mining.buildOctree();
        source.sendSystemMessage(Component.literal("Octree built with encapsulating cube §a" + mining.areaOctree.rootNode.boundingBox));
        return 1;
    }

    public static int list(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        Mining mining = getMining(context);
        if (mining.areas.isEmpty())
            source.sendSystemMessage(Component.literal("No current areas").withStyle(ChatFormatting.RED));
        else for (String area : mining.areas.keySet()) source.sendSystemMessage(Component.literal(area));
        return 1;
    }

    public static int remove(CommandContext<CommandSourceStack> context, String name) {
        CommandSourceStack source = context.getSource();
        Mining mining = getMining(context);
        if (areaInvalid(name, source, mining)) return 0;
        mining.areas.remove(name);
        source.sendSystemMessage(Component.literal("Removed area §d" + name));
        return 1;
    }

    public static int add(CommandContext<CommandSourceStack> context, String name, BlockPos from, BlockPos to) {
        CommandSourceStack source = context.getSource();
        Mining mining = getMining(context);
        mining.areas.put(name, new MiningArea(new HashMap<>(), new IAABB(from.getX(), from.getY(), from.getZ(), to.getX(), to.getY(), to.getZ())));
        source.sendSystemMessage(Component.literal("Added area §d" + name));
        return 1;
    }

    public static int set(CommandContext<CommandSourceStack> context, String name, BlockPos from, BlockPos to) {
        CommandSourceStack source = context.getSource();
        Mining mining = getMining(context);
        if (areaInvalid(name, source, mining)) return 0;
        MiningArea area = mining.areas.get(name);
        area.bounds = new IAABB(from.getX(), from.getY(), from.getZ(), to.getX(), to.getY(), to.getZ());
        source.sendSystemMessage(Component.literal("Set area §d" + name + "§f bounds to §a" + area.bounds));
        return 1;
    }

    public static int modifyAdd(CommandContext<CommandSourceStack> context, String name, Block block, int breakingPower, double blockHealth, int resonantFrequency, ResourceLocation breakSound, float volume, float pitch, @Nullable String loot) {
        CommandSourceStack source = context.getSource();
        Mining mining = getMining(context);
        if (areaInvalid(name, source, mining)) return 0;
        MiningArea area = mining.areas.get(name);
        area.types.put(block, new BlockType(breakingPower, blockHealth, resonantFrequency, new SoundInstance(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvent.createVariableRangeEvent(breakSound)), volume, pitch), loot));
        source.sendSystemMessage(Component.literal("Set breaking power of ").append(block.getName().withStyle(ChatFormatting.BLUE)).append(Component.literal("§f to §a" + breakingPower + "§f, block health to §a" + blockHealth + "§f and resonant frequency to §a" + resonantFrequency + "§f in area §d" + name)));
        return 1;
    }

    public static int modifyLootSet(CommandContext<CommandSourceStack> context, String name, Block block, String loot) {
        CommandSourceStack source = context.getSource();
        Mining mining = getMining(context);
        if (areaInvalid(name, source, mining)) return 0;
        MiningArea area = mining.areas.get(name);
        area.types.computeIfPresent(block, (k, type) -> new BlockType(type.breakingPower(), type.blockHealth(), type.resonantFrequency(), type.breakSound(), loot));
        source.sendSystemMessage(Component.literal("Set loot of ").append(block.getName().withStyle(ChatFormatting.BLUE)).append(Component.literal("§f to §a" + loot + "§f in area §d" + name)));
        return 1;
    }

    public static int modifyLootRemove(CommandContext<CommandSourceStack> context, String name, Block block) {
        CommandSourceStack source = context.getSource();
        Mining mining = getMining(context);
        if (areaInvalid(name, source, mining)) return 0;
        MiningArea area = mining.areas.get(name);
        area.types.computeIfPresent(block, (k, type) -> new BlockType(type.breakingPower(), type.blockHealth(), type.resonantFrequency(), type.breakSound(), null));
        source.sendSystemMessage(Component.literal("Removed loot of ").append(block.getName().withStyle(ChatFormatting.BLUE)).append(Component.literal("§f in area §d" + name)));
        return 1;
    }

    public static int modifyRemove(CommandContext<CommandSourceStack> context, String name, Block block) {
        CommandSourceStack source = context.getSource();
        Mining mining = getMining(context);
        if (areaInvalid(name, source, mining)) return 0;
        MiningArea area = mining.areas.get(name);
        area.types.remove(block);
        source.sendSystemMessage(Component.literal("Removed ").append(block.getName().withStyle(ChatFormatting.BLUE)).append(Component.literal("§f from area §d" + name)));
        return 1;
    }

    public static int modifyClear(CommandContext<CommandSourceStack> context, String name) {
        CommandSourceStack source = context.getSource();
        Mining mining = getMining(context);
        if (areaInvalid(name, source, mining)) return 0;
        MiningArea area = mining.areas.get(name);
        area.types.clear();
        source.sendSystemMessage(Component.literal("Cleared area §d" + name));
        return 1;
    }

    public static int modifyList(CommandContext<CommandSourceStack> context, String name) {
        CommandSourceStack source = context.getSource();
        Mining mining = getMining(context);
        if (areaInvalid(name, source, mining)) return 0;
        MiningArea area = mining.areas.get(name);
        if (area.types.isEmpty()) {
            source.sendSystemMessage(Component.literal("§cThere are no blocks assigned in area §d" + name));
            return 0;
        }
        source.sendSystemMessage(Component.literal("Contents of area §d" + name + "§f:"));
        for (Map.Entry<Block, BlockType> entry : area.types.entrySet()) {
            Block block = entry.getKey();
            BlockType type = entry.getValue();
            source.sendSystemMessage(block.getName().withStyle(ChatFormatting.BLUE).append(Component.literal("§f with breaking power §a" + type.breakingPower() + "§f, block health §a" + type.blockHealth() + "§f and resonant frequency §a" + type.resonantFrequency())));
        }
        return 1;
    }

    public static int modifyRename(CommandContext<CommandSourceStack> context, String name, String newName) {
        CommandSourceStack source = context.getSource();
        Mining mining = getMining(context);
        if (areaInvalid(name, source, mining)) return 0;
        MiningArea area = mining.areas.get(name);
        mining.areas.remove(name);
        mining.areas.put(newName, area);
        source.sendSystemMessage(Component.literal("Renamed area §d" + name + "§f to §d" + newName));
        return 1;
    }

    public static int modifyPresetAdd(CommandContext<CommandSourceStack> context, String areaName, String name) {
        CommandSourceStack source = context.getSource();
        Mining mining = getMining(context);
        if (areaInvalid(areaName, source, mining)) return 0;
        if (presetInvalid(name, source, mining)) return 0;
        MiningArea area = mining.areas.get(areaName);
        if (area.presets == null) area.presets = new HashSet<>();
        else if (area.presets.contains(name)) {
            source.sendSystemMessage(Component.literal("§cArea §d" + areaName + "§c already contains preset §6" + name));
            return 0;
        }
        area.presets.add(name);
        source.sendSystemMessage(Component.literal("Added preset §6" + name + "§f to area §d" + areaName));
        return 1;
    }

    public static int modifyPresetRemove(CommandContext<CommandSourceStack> context, String areaName, String name) {
        CommandSourceStack source = context.getSource();
        Mining mining = getMining(context);
        if (areaInvalid(areaName, source, mining)) return 0;
        if (presetInvalid(name, source, mining)) return 0;
        MiningArea area = mining.areas.get(areaName);
        if (area.presets == null || !area.presets.contains(name)) {
            source.sendSystemMessage(Component.literal("§cCouldn't find preset §6" + name + "§c in area §d" + areaName));
            return 0;
        }
        area.presets.remove(name);
        source.sendSystemMessage(Component.literal("Removed preset §6" + name + "§f from area §d" + areaName));
        return 1;
    }

    public static int modifyPresetList(CommandContext<CommandSourceStack> context, String areaName) {
        CommandSourceStack source = context.getSource();
        Mining mining = getMining(context);
        if (areaInvalid(areaName, source, mining)) return 0;
        MiningArea area = mining.areas.get(areaName);
        if (area.presets == null) {
            source.sendSystemMessage(Component.literal("§cThere are no presets in area §d" + areaName));
            return 0;
        }
        source.sendSystemMessage(Component.literal("Presets in area §d" + areaName + "§f:"));
        for (String preset : area.presets) source.sendSystemMessage(Component.literal(preset).withStyle(ChatFormatting.GOLD));
        return 1;
    }

    public static int modifyPresetClear(CommandContext<CommandSourceStack> context, String areaName) {
        CommandSourceStack source = context.getSource();
        Mining mining = getMining(context);
        if (areaInvalid(areaName, source, mining)) return 0;
        MiningArea area = mining.areas.get(areaName);
        area.presets = null;
        source.sendSystemMessage(Component.literal("Cleared presets in area §d" + areaName));
        return 1;
    }

    public static int presetAdd(CommandContext<CommandSourceStack> context, String name) {
        CommandSourceStack source = context.getSource();
        Mining mining = getMining(context);
        source.sendSystemMessage(Component.literal((mining.presets.containsKey(name) ? "Reset" : "Added") + " preset §6" + name));
        mining.presets.put(name, new HashMap<>());
        return 1;
    }

    public static int presetRemove(CommandContext<CommandSourceStack> context, String name) {
        CommandSourceStack source = context.getSource();
        Mining mining = getMining(context);
        if (presetInvalid(name, source, mining)) return 0;
        mining.presets.remove(name);
        updateAreaPresets(name, mining);
        source.sendSystemMessage(Component.literal("Removed preset §6" + name));
        return 1;
    }

    public static int presetList(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        Mining mining = getMining(context);
        if (mining.presets.isEmpty()) {
            source.sendSystemMessage(Component.literal("No current presets").withStyle(ChatFormatting.RED));
            return 0;
        } else for (String preset : mining.presets.keySet()) source.sendSystemMessage(Component.literal(preset).withStyle(ChatFormatting.GOLD));
        return 1;
    }

    public static int presetModifyAdd(CommandContext<CommandSourceStack> context, String name, Block block, int breakingPower, double blockHealth, int resonantFrequency, ResourceLocation breakSound, float volume, float pitch, @Nullable String loot) {
        CommandSourceStack source = context.getSource();
        Mining mining = getMining(context);
        if (presetInvalid(name, source, mining)) return 0;
        HashMap<Block, BlockType> preset = mining.presets.get(name);
        preset.put(block, new BlockType(breakingPower, blockHealth, resonantFrequency, new SoundInstance(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvent.createVariableRangeEvent(breakSound)), volume, pitch), loot));
        source.sendSystemMessage(Component.literal("Set breaking power of ").append(block.getName().withStyle(ChatFormatting.BLUE)).append(Component.literal("§f to §a" + breakingPower + "§f, block health to §a" + blockHealth + "§f and resonant frequency to §a" + resonantFrequency + "§f in preset §6" + name)));
        return 1;
    }

    public static int presetModifyLootSet(CommandContext<CommandSourceStack> context, String name, Block block, String loot) {
        CommandSourceStack source = context.getSource();
        Mining mining = getMining(context);
        if (presetInvalid(name, source, mining)) return 0;
        HashMap<Block, BlockType> preset = mining.presets.get(name);
        preset.computeIfPresent(block, (k, type) -> new BlockType(type.breakingPower(), type.blockHealth(), type.resonantFrequency(), type.breakSound(), loot));
        source.sendSystemMessage(Component.literal("Set loot of ").append(block.getName().withStyle(ChatFormatting.BLUE)).append(Component.literal("§f to §a" + loot + "§f in preset §6" + name)));
        return 1;
    }

    public static int presetModifyLootRemove(CommandContext<CommandSourceStack> context, String name, Block block) {
        CommandSourceStack source = context.getSource();
        Mining mining = getMining(context);
        if (presetInvalid(name, source, mining)) return 0;
        HashMap<Block, BlockType> preset = mining.presets.get(name);
        preset.computeIfPresent(block, (k, type) -> new BlockType(type.breakingPower(), type.blockHealth(), type.resonantFrequency(), type.breakSound(), null));
        source.sendSystemMessage(Component.literal("Removed loot of ").append(block.getName().withStyle(ChatFormatting.BLUE)).append(Component.literal("§f in preset §6" + name)));
        return 1;
    }

    public static int presetModifyRemove(CommandContext<CommandSourceStack> context, String name, Block block) {
        CommandSourceStack source = context.getSource();
        Mining mining = getMining(context);
        if (presetInvalid(name, source, mining)) return 0;
        HashMap<Block, BlockType> preset = mining.presets.get(name);
        preset.remove(block);
        source.sendSystemMessage(Component.literal("Removed ").append(block.getName().withStyle(ChatFormatting.BLUE)).append(Component.literal("§f from preset §6" + name)));
        return 1;
    }

    public static int presetModifyClear(CommandContext<CommandSourceStack> context, String name) {
        CommandSourceStack source = context.getSource();
        Mining mining = getMining(context);
        if (presetInvalid(name, source, mining)) return 0;
        HashMap<Block, BlockType> preset = mining.presets.get(name);
        preset.clear();
        source.sendSystemMessage(Component.literal("Cleared preset §6" + name));
        return 1;
    }

    public static int presetModifyList(CommandContext<CommandSourceStack> context, String name) {
        CommandSourceStack source = context.getSource();
        Mining mining = getMining(context);
        if (presetInvalid(name, source, mining)) return 0;
        HashMap<Block, BlockType> preset = mining.presets.get(name);
        source.sendSystemMessage(Component.literal("Contents of preset §6" + name + "§f:"));
        for (Map.Entry<Block, BlockType> entry : preset.entrySet()) {
            Block block = entry.getKey();
            BlockType type = entry.getValue();
            source.sendSystemMessage(block.getName().withStyle(ChatFormatting.BLUE).append(Component.literal("§f with breaking power §a" + type.breakingPower() + "§f, block health §a" + type.blockHealth() + "§f and resonant frequency §a" + type.resonantFrequency())));
        }
        return 1;
    }

    public static int presetModifyRename(CommandContext<CommandSourceStack> context, String name, String newName) {
        CommandSourceStack source = context.getSource();
        Mining mining = getMining(context);
        if (presetInvalid(name, source, mining)) return 0;
        HashMap<Block, BlockType> preset = mining.presets.get(name);
        mining.presets.remove(name);
        mining.presets.put(newName, preset);
        source.sendSystemMessage(Component.literal("Renamed preset §6" + name + "§f to §6" + newName));
        return 1;
    }

    public static final Block[] DISPLAY = new Block[]{
            Blocks.WHITE_STAINED_GLASS,
            Blocks.ORANGE_STAINED_GLASS,
            Blocks.MAGENTA_STAINED_GLASS,
            Blocks.LIGHT_BLUE_STAINED_GLASS,
            Blocks.YELLOW_STAINED_GLASS,
            Blocks.LIME_STAINED_GLASS,
            Blocks.PINK_STAINED_GLASS,
            Blocks.GRAY_STAINED_GLASS,
            Blocks.LIGHT_GRAY_STAINED_GLASS,
            Blocks.CYAN_STAINED_GLASS,
            Blocks.PURPLE_STAINED_GLASS,
            Blocks.BLUE_STAINED_GLASS,
            Blocks.BROWN_STAINED_GLASS,
            Blocks.GREEN_STAINED_GLASS,
            Blocks.RED_STAINED_GLASS,
            Blocks.BLACK_STAINED_GLASS,
    };

    public static int show(ServerLevel level, ServerPlayer player, Collection<MiningArea> areas) {
        ArrayList<IAABB> bbs = new ArrayList<>(areas.size());
        for (MiningArea area : areas) {
            IAABB bb = area.bounds;
            bbs.add(bb);
            BlockState displayState = DISPLAY[bb.hashCode() % DISPLAY.length].defaultBlockState();
            if (player == null) return 0;
            for (int x = bb.minX; x <= bb.maxX; x++)
                for (int y = bb.minY; y <= bb.maxY; y++)
                    for (int z = bb.minZ; z <= bb.maxZ; z++)
                        player.connection.send(new ClientboundBlockUpdatePacket(new BlockPos(x, y, z), displayState));
        }
        ((ModServerLevel) level).molecraft$schedule(20, l -> {
            for (IAABB bb : bbs) {
                if (player == null) return;
                for (int x = bb.minX; x <= bb.maxX; x++)
                    for (int y = bb.minY; y <= bb.maxY; y++)
                        for (int z = bb.minZ; z <= bb.maxZ; z++) {
                            if (l != null) {
                                BlockPos pos = new BlockPos(x, y, z);
                                player.connection.send(new ClientboundBlockUpdatePacket(pos, l.getBlockState(pos)));
                            }
                        }
            }
        });
        return 1;
    }
}
