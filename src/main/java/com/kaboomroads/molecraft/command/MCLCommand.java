package com.kaboomroads.molecraft.command;

import com.kaboomroads.molecraft.item.MolecraftData;
import com.kaboomroads.molecraft.loot.Loot;
import com.kaboomroads.molecraft.loot.LootManager;
import com.kaboomroads.molecraft.mixinimpl.ModServerLevelData;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.random.WeightedEntry;

import java.util.HashMap;
import java.util.Map;

public class MCLCommand {
    public static final SuggestionProvider<CommandSourceStack> SUGGEST_LOOT = (context, builder) -> {
        LootManager lootManager = getLootManager(context);
        return SharedSuggestionProvider.suggest(lootManager.lootMap.keySet(), builder);
    };
    public static final SuggestionProvider<CommandSourceStack> SUGGEST_ROLLS = (context, builder) -> {
        LootManager lootManager = getLootManager(context);
        return SharedSuggestionProvider.suggest(lootManager.get(StringArgumentType.getString(context, "id")).rolls.keySet(), builder);
    };

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("mcl")
                        .requires(commandSourceStack -> commandSourceStack.hasPermission(2))
                        .then(Commands.literal("list").executes(MCLCommand::listLoot))
                        .then(Commands.literal("remove")
                                .then(Commands.argument("id", StringArgumentType.string()).suggests(SUGGEST_LOOT)
                                        .executes(context -> removeLoot(
                                                context,
                                                StringArgumentType.getString(context, "id")
                                        ))
                                )
                        )
                        .then(Commands.literal("add")
                                .then(Commands.argument("id", StringArgumentType.string())
                                        .executes(context -> addLoot(
                                                context,
                                                StringArgumentType.getString(context, "id")
                                        ))
                                )
                        )
                        .then(Commands.literal("select")
                                .then(Commands.argument("id", StringArgumentType.string()).suggests(SUGGEST_LOOT)
                                        .then(Commands.literal("rename")
                                                .then(Commands.argument("newName", StringArgumentType.string())
                                                        .executes(context -> renameLoot(
                                                                context,
                                                                StringArgumentType.getString(context, "id"),
                                                                StringArgumentType.getString(context, "newName")
                                                        ))
                                                )
                                        )
                                        .then(Commands.literal("add")
                                                .then(Commands.argument("name", StringArgumentType.string()).suggests(SUGGEST_ROLLS)
                                                        .executes(context -> addRoll(
                                                                context,
                                                                StringArgumentType.getString(context, "id"),
                                                                StringArgumentType.getString(context, "name")
                                                        ))
                                                )
                                        )
                                        .then(Commands.literal("remove")
                                                .then(Commands.argument("name", StringArgumentType.string()).suggests(SUGGEST_ROLLS)
                                                        .executes(context -> removeRoll(
                                                                context,
                                                                StringArgumentType.getString(context, "id"),
                                                                StringArgumentType.getString(context, "name")
                                                        ))
                                                )
                                        )
                                        .then(Commands.literal("clear")
                                                .executes(context -> clearRolls(
                                                        context,
                                                        StringArgumentType.getString(context, "id")
                                                ))
                                        )
                                        .then(Commands.literal("list")
                                                .executes(context -> listRolls(
                                                        context,
                                                        StringArgumentType.getString(context, "id")
                                                ))
                                        )
                                        .then(Commands.literal("select")
                                                .then(Commands.argument("name", StringArgumentType.string()).suggests(SUGGEST_ROLLS)
                                                        .then(Commands.literal("add")
                                                                .then(Commands.argument("molecraft_id", StringArgumentType.string()).suggests(MolecraftCommands.SUGGEST_MOLECRAFT_ITEM)
                                                                        .then(Commands.argument("chance", FloatArgumentType.floatArg(0))
                                                                                .then(Commands.argument("weight", IntegerArgumentType.integer(0))
                                                                                        .executes(context -> rollSelectAdd(
                                                                                                context,
                                                                                                StringArgumentType.getString(context, "id"),
                                                                                                StringArgumentType.getString(context, "name"),
                                                                                                new MolecraftData(StringArgumentType.getString(context, "molecraft_id")),
                                                                                                FloatArgumentType.getFloat(context, "chance"),
                                                                                                IntegerArgumentType.getInteger(context, "weight")
                                                                                        ))
                                                                                )
                                                                        )
                                                                )
                                                        )
                                                        .then(Commands.literal("remove")
                                                                .then(Commands.argument("molecraft_id", StringArgumentType.string()).suggests(MolecraftCommands.SUGGEST_MOLECRAFT_ITEM)
                                                                        .then(Commands.argument("chance", FloatArgumentType.floatArg(0))
                                                                                .then(Commands.argument("weight", IntegerArgumentType.integer(0))
                                                                                        .executes(context -> rollSelectRemove(
                                                                                                context,
                                                                                                StringArgumentType.getString(context, "id"),
                                                                                                StringArgumentType.getString(context, "name"),
                                                                                                new MolecraftData(StringArgumentType.getString(context, "molecraft_id")),
                                                                                                FloatArgumentType.getFloat(context, "chance"),
                                                                                                IntegerArgumentType.getInteger(context, "weight")
                                                                                        ))
                                                                                )
                                                                        )
                                                                )
                                                        )
                                                        .then(Commands.literal("clear")
                                                                .executes(context -> rollSelectClear(
                                                                        context,
                                                                        StringArgumentType.getString(context, "id"),
                                                                        StringArgumentType.getString(context, "name")
                                                                ))
                                                        )
                                                        .then(Commands.literal("list")
                                                                .executes(context -> rollSelectList(
                                                                        context,
                                                                        StringArgumentType.getString(context, "id"),
                                                                        StringArgumentType.getString(context, "name")
                                                                ))
                                                        )
                                                        .then(Commands.literal("rename")
                                                                .then(Commands.argument("newName", StringArgumentType.string())
                                                                        .executes(context -> renameRoll(
                                                                                context,
                                                                                StringArgumentType.getString(context, "id"),
                                                                                StringArgumentType.getString(context, "name"),
                                                                                StringArgumentType.getString(context, "newName")
                                                                        ))
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
        );
    }

    private static LootManager getLootManager(CommandContext<CommandSourceStack> context) {
        return ((ModServerLevelData) context.getSource().getLevel().getLevelData()).molecraft$getLootManager();
    }

    public static boolean lootInvalid(String id, CommandSourceStack source, LootManager lootManager) {
        if (!lootManager.lootMap.containsKey(id)) {
            source.sendSystemMessage(Component.literal("Couldn't find loot §d" + id).withStyle(ChatFormatting.RED));
            return true;
        }
        return false;
    }

    public static boolean rollInvalid(String id, Loot loot, String name, CommandSourceStack source) {
        if (!loot.rolls.containsKey(name)) {
            source.sendSystemMessage(Component.literal("Couldn't find roll §6" + name + "§f in loot §d" + id).withStyle(ChatFormatting.RED));
            return true;
        }
        return false;
    }

    private static int listLoot(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        LootManager lootManager = getLootManager(context);
        if (lootManager.lootMap.isEmpty())
            source.sendSystemMessage(Component.literal("No current loot").withStyle(ChatFormatting.RED));
        else for (String area : lootManager.lootMap.keySet()) source.sendSystemMessage(Component.literal(area));
        return 1;
    }

    private static int removeLoot(CommandContext<CommandSourceStack> context, String id) {
        CommandSourceStack source = context.getSource();
        LootManager lootManager = getLootManager(context);
        if (lootInvalid(id, source, lootManager)) return 0;
        lootManager.lootMap.remove(id);
        source.sendSystemMessage(Component.literal("Removed loot §d" + id));
        return 1;
    }

    private static int addLoot(CommandContext<CommandSourceStack> context, String id) {
        CommandSourceStack source = context.getSource();
        LootManager lootManager = getLootManager(context);
        lootManager.lootMap.put(id, new Loot());
        source.sendSystemMessage(Component.literal("Added loot §d" + id));
        return 1;
    }

    private static int renameLoot(CommandContext<CommandSourceStack> context, String id, String newName) {
        CommandSourceStack source = context.getSource();
        LootManager lootManager = getLootManager(context);
        if (lootInvalid(id, source, lootManager)) return 0;
        Loot loot = lootManager.get(id);
        lootManager.lootMap.remove(id);
        lootManager.lootMap.put(newName, loot);
        source.sendSystemMessage(Component.literal("Renamed loot §d" + id + "§f to §d" + newName));
        return 1;
    }

    private static int addRoll(CommandContext<CommandSourceStack> context, String id, String name) {
        CommandSourceStack source = context.getSource();
        LootManager lootManager = getLootManager(context);
        if (lootInvalid(id, source, lootManager)) return 0;
        Loot loot = lootManager.get(id);
        if (loot.rolls.containsKey(name)) {
            source.sendSystemMessage(Component.literal("§cLoot §d" + id + "§c already contains roll §6" + name));
            return 0;
        }
        loot.rolls.put(name, new HashMap<>());
        source.sendSystemMessage(Component.literal("Added roll §6" + name + "§f to loot §d" + id));
        return 1;
    }

    private static int removeRoll(CommandContext<CommandSourceStack> context, String id, String name) {
        CommandSourceStack source = context.getSource();
        LootManager lootManager = getLootManager(context);
        if (lootInvalid(id, source, lootManager)) return 0;
        Loot loot = lootManager.get(id);
        if (!loot.rolls.containsKey(name)) {
            source.sendSystemMessage(Component.literal("§cCouldn't find roll §6" + name + "§c in loot §d" + id));
            return 0;
        }
        loot.rolls.remove(name);
        source.sendSystemMessage(Component.literal("Removed roll §6" + name + "§f from loot §d" + id));
        return 1;
    }

    private static int clearRolls(CommandContext<CommandSourceStack> context, String id) {
        CommandSourceStack source = context.getSource();
        LootManager lootManager = getLootManager(context);
        if (lootInvalid(id, source, lootManager)) return 0;
        Loot loot = lootManager.get(id);
        loot.rolls.clear();
        source.sendSystemMessage(Component.literal("Cleared rolls in loot §d" + id));
        return 1;
    }

    private static int listRolls(CommandContext<CommandSourceStack> context, String id) {
        CommandSourceStack source = context.getSource();
        LootManager lootManager = getLootManager(context);
        if (lootInvalid(id, source, lootManager)) return 0;
        Loot loot = lootManager.get(id);
        if (loot.rolls == null) {
            source.sendSystemMessage(Component.literal("§cThere are no rolls in loot §d" + id));
            return 0;
        }
        source.sendSystemMessage(Component.literal("Rolls in loot §d" + id + "§f:"));
        for (String preset : loot.rolls.keySet()) source.sendSystemMessage(Component.literal(preset).withStyle(ChatFormatting.GOLD));
        return 1;
    }

    private static int rollSelectAdd(CommandContext<CommandSourceStack> context, String id, String name, MolecraftData molecraftData, float chance, int weight) {
        CommandSourceStack source = context.getSource();
        LootManager lootManager = getLootManager(context);
        if (lootInvalid(id, source, lootManager)) return 0;
        Loot loot = lootManager.get(id);
        if (rollInvalid(id, loot, name, source)) return 0;
        HashMap<Float, SimpleWeightedRandomList<MolecraftData>> roll = loot.rolls.get(name);
        SimpleWeightedRandomList<MolecraftData> list = roll.get(chance);
        SimpleWeightedRandomList.Builder<MolecraftData> builder = new SimpleWeightedRandomList.Builder<>();
        if (list != null) for (WeightedEntry.Wrapper<MolecraftData> wrapper : list.unwrap()) builder.add(wrapper.data(), wrapper.weight().asInt());
        builder.add(molecraftData);
        roll.put(chance, builder.build());
        source.sendSystemMessage(Component.literal("Added drop §9" + molecraftData.id() + "§f with chance §a" + chance + "§f and weight §a" + weight + "§f to roll §6" + name + "§f in loot §d" + id));
        return 1;
    }

    private static int rollSelectRemove(CommandContext<CommandSourceStack> context, String id, String name, MolecraftData molecraftData, float chance, int weight) {
        CommandSourceStack source = context.getSource();
        LootManager lootManager = getLootManager(context);
        if (lootInvalid(id, source, lootManager)) return 0;
        Loot loot = lootManager.get(id);
        if (rollInvalid(id, loot, name, source)) return 0;
        HashMap<Float, SimpleWeightedRandomList<MolecraftData>> roll = loot.rolls.get(name);
        SimpleWeightedRandomList<MolecraftData> list = roll.get(chance);
        if (list == null || list.unwrap().stream().noneMatch(wrapper -> molecraftData.is(wrapper.data()) && weight == wrapper.weight().asInt())) {
            source.sendSystemMessage(Component.literal("§cCouldn't find drop §9" + molecraftData.id() + "§c with chance §a" + chance + "§c and weight §a" + weight + "§c in roll §6" + name + "§c in loot §d" + id));
            return 0;
        }
        SimpleWeightedRandomList.Builder<MolecraftData> builder = new SimpleWeightedRandomList.Builder<>();
        for (WeightedEntry.Wrapper<MolecraftData> wrapper : list.unwrap())
            if (!molecraftData.is(wrapper.data()) && weight != wrapper.weight().asInt())
                builder.add(wrapper.data(), wrapper.weight().asInt());
        SimpleWeightedRandomList<MolecraftData> newList = builder.build();
        if (newList.isEmpty()) roll.remove(chance);
        else roll.put(chance, newList);
        source.sendSystemMessage(Component.literal("Removed drop §9" + molecraftData.id() + "§f with chance §a" + chance + "§f and weight §a" + weight + "§f from roll §6" + name + "§f in loot §d" + id));
        return 1;
    }

    private static int rollSelectClear(CommandContext<CommandSourceStack> context, String id, String name) {
        CommandSourceStack source = context.getSource();
        LootManager lootManager = getLootManager(context);
        if (lootInvalid(id, source, lootManager)) return 0;
        Loot loot = lootManager.get(id);
        if (rollInvalid(id, loot, name, source)) return 0;
        loot.rolls.get(name).clear();
        source.sendSystemMessage(Component.literal("Cleared roll §6" + name));
        return 1;
    }

    private static int rollSelectList(CommandContext<CommandSourceStack> context, String id, String name) {
        CommandSourceStack source = context.getSource();
        LootManager lootManager = getLootManager(context);
        if (lootInvalid(id, source, lootManager)) return 0;
        Loot loot = lootManager.get(id);
        if (rollInvalid(id, loot, name, source)) return 0;
        source.sendSystemMessage(Component.literal("Contents of roll §6" + name + "§f:"));
        for (Map.Entry<Float, SimpleWeightedRandomList<MolecraftData>> entry : loot.rolls.get(name).entrySet()) {
            float chance = entry.getKey();
            SimpleWeightedRandomList<MolecraftData> drops = entry.getValue();
            for (WeightedEntry.Wrapper<MolecraftData> wrapper : drops.unwrap()) source.sendSystemMessage(Component.literal("§9" + wrapper.data().id() + "§f with chance §a" + chance + "§f and weight §a" + wrapper.weight().asInt()));
        }
        return 1;
    }

    private static int renameRoll(CommandContext<CommandSourceStack> context, String id, String name, String newName) {
        CommandSourceStack source = context.getSource();
        LootManager lootManager = getLootManager(context);
        if (lootInvalid(id, source, lootManager)) return 0;
        Loot loot = lootManager.get(id);
        if (rollInvalid(id, loot, name, source)) return 0;
        HashMap<Float, SimpleWeightedRandomList<MolecraftData>> roll = loot.rolls.get(name);
        loot.rolls.remove(name);
        loot.rolls.put(newName, roll);
        source.sendSystemMessage(Component.literal("Renamed roll §6" + name + "§f in loot §d" + id + "§f to §6" + newName));
        return 1;
    }
}
