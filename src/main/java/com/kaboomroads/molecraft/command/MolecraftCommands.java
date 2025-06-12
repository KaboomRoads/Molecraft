package com.kaboomroads.molecraft.command;

import com.kaboomroads.molecraft.entity.MolecraftEntities;
import com.kaboomroads.molecraft.entity.MolecraftEntity;
import com.kaboomroads.molecraft.entity.SkillLevel;
import com.kaboomroads.molecraft.entity.SkillType;
import com.kaboomroads.molecraft.item.DataPropertyMap;
import com.kaboomroads.molecraft.item.DataPropertyTypes;
import com.kaboomroads.molecraft.item.MolecraftData;
import com.kaboomroads.molecraft.item.MolecraftItems;
import com.kaboomroads.molecraft.item.ability.core.MolecraftEnchant;
import com.kaboomroads.molecraft.item.ability.core.MolecraftEnchants;
import com.kaboomroads.molecraft.mixinimpl.ModPlayer;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.CompoundTagArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.TreeMap;

public class MolecraftCommands {
    public static final SuggestionProvider<CommandSourceStack> SUGGEST_MOLECRAFT_ITEM = (commandContext, suggestionsBuilder) -> SharedSuggestionProvider.suggest(
            MolecraftItems.ITEMS.keySet(), suggestionsBuilder
    );
    public static final SuggestionProvider<CommandSourceStack> SUGGEST_MOLECRAFT_ENCHANT = (commandContext, suggestionsBuilder) -> SharedSuggestionProvider.suggest(
            MolecraftEnchants.ENCHANTS.keySet(), suggestionsBuilder
    );
    public static final SuggestionProvider<CommandSourceStack> SUGGEST_MOLECRAFT_ENTITY = (commandContext, suggestionsBuilder) -> SharedSuggestionProvider.suggest(
            MolecraftEntities.ENTITIES.keySet(), suggestionsBuilder
    );
    public static final SuggestionProvider<CommandSourceStack> SUGGEST_MOLECRAFT_SKILL = (commandContext, suggestionsBuilder) -> SharedSuggestionProvider.suggest(
            Arrays.stream(SkillType.values()).map(type -> type.id), suggestionsBuilder
    );

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("mcitem")
                        .requires(commandSourceStack -> commandSourceStack.hasPermission(2))
                        .then(
                                Commands.argument("targets", EntityArgument.players())
                                        .then(
                                                Commands.argument("item", StringArgumentType.string()).suggests(SUGGEST_MOLECRAFT_ITEM)
                                                        .executes(
                                                                commandContext -> giveItem(
                                                                        commandContext.getSource(), StringArgumentType.getString(commandContext, "item"), EntityArgument.getPlayers(commandContext, "targets"), 1
                                                                )
                                                        )
                                                        .then(
                                                                Commands.argument("count", IntegerArgumentType.integer(1))
                                                                        .executes(
                                                                                commandContext -> giveItem(
                                                                                        commandContext.getSource(),
                                                                                        StringArgumentType.getString(commandContext, "item"),
                                                                                        EntityArgument.getPlayers(commandContext, "targets"),
                                                                                        IntegerArgumentType.getInteger(commandContext, "count")
                                                                                )
                                                                        )
                                                        )
                                        )
                        )
        );
        dispatcher.register(
                Commands.literal("mcenchant")
                        .requires(commandSourceStack -> commandSourceStack.hasPermission(2))
                        .then(
                                Commands.argument("targets", EntityArgument.players())
                                        .then(
                                                Commands.argument("enchant", StringArgumentType.string()).suggests(SUGGEST_MOLECRAFT_ENCHANT)
                                                        .executes(
                                                                commandContext -> enchantHandItem(
                                                                        commandContext.getSource(), StringArgumentType.getString(commandContext, "enchant"), EntityArgument.getPlayers(commandContext, "targets"), 1
                                                                )
                                                        )
                                                        .then(
                                                                Commands.argument("level", IntegerArgumentType.integer(0))
                                                                        .executes(
                                                                                commandContext -> enchantHandItem(
                                                                                        commandContext.getSource(),
                                                                                        StringArgumentType.getString(commandContext, "enchant"),
                                                                                        EntityArgument.getPlayers(commandContext, "targets"),
                                                                                        IntegerArgumentType.getInteger(commandContext, "level")
                                                                                )
                                                                        )
                                                        )
                                        )
                        )
        );
        dispatcher.register(
                Commands.literal("mcentity")
                        .requires(commandSourceStack -> commandSourceStack.hasPermission(2))
                        .then(
                                Commands.argument("entity", StringArgumentType.string()).suggests(SUGGEST_MOLECRAFT_ENTITY)
                                        .executes(
                                                commandContext -> spawnEntity(
                                                        commandContext.getSource(),
                                                        StringArgumentType.getString(commandContext, "entity"),
                                                        commandContext.getSource().getPosition(),
                                                        null
                                                )
                                        )
                                        .then(
                                                Commands.argument("pos", Vec3Argument.vec3())
                                                        .executes(
                                                                commandContext -> spawnEntity(
                                                                        commandContext.getSource(),
                                                                        StringArgumentType.getString(commandContext, "entity"),
                                                                        Vec3Argument.getVec3(commandContext, "pos"),
                                                                        null
                                                                )
                                                        )
                                                        .then(
                                                                Commands.argument("nbt", CompoundTagArgument.compoundTag())
                                                                        .executes(
                                                                                commandContext -> spawnEntity(
                                                                                        commandContext.getSource(),
                                                                                        StringArgumentType.getString(commandContext, "entity"),
                                                                                        Vec3Argument.getVec3(commandContext, "pos"),
                                                                                        CompoundTagArgument.getCompoundTag(commandContext, "nbt")
                                                                                )
                                                                        )
                                                        )
                                        )
                        )
        );
        dispatcher.register(
                Commands.literal("mccoins")
                        .requires(commandSourceStack -> commandSourceStack.hasPermission(2))
                        .then(
                                Commands.argument("targets", EntityArgument.players())
                                        .then(Commands.literal("add")
                                                .then(
                                                        Commands.argument("amount", LongArgumentType.longArg())
                                                                .executes(
                                                                        context -> {
                                                                            ServerPlayer player = context.getSource().getPlayer();
                                                                            if (player != null) {
                                                                                ((ModPlayer) player).molecraft$setCoins(((ModPlayer) player).molecraft$getCoins() + LongArgumentType.getLong(context, "amount"));
                                                                                return 1;
                                                                            }
                                                                            return 0;
                                                                        }
                                                                )
                                                )
                                        )
                                        .then(Commands.literal("set")
                                                .then(
                                                        Commands.argument("amount", LongArgumentType.longArg())
                                                                .executes(
                                                                        context -> {
                                                                            ServerPlayer player = context.getSource().getPlayer();
                                                                            if (player != null) {
                                                                                ((ModPlayer) player).molecraft$setCoins(LongArgumentType.getLong(context, "amount"));
                                                                                return 1;
                                                                            }
                                                                            return 0;
                                                                        }
                                                                )
                                                )
                                        )
                        )
        );
        dispatcher.register(
                Commands.literal("mcxp")
                        .requires(commandSourceStack -> commandSourceStack.hasPermission(2))
                        .then(Commands.argument("targets", EntityArgument.players())
                                .then(Commands.argument("skill", StringArgumentType.string()).suggests(SUGGEST_MOLECRAFT_SKILL)
                                        .then(Commands.literal("add")
                                                .then(Commands.argument("amount", IntegerArgumentType.integer())
                                                        .executes(
                                                                context -> {
                                                                    ServerPlayer player = context.getSource().getPlayer();
                                                                    if (player == null) return 0;
                                                                    SkillType skill = SkillType.BY_ID.get(StringArgumentType.getString(context, "skill"));
                                                                    if (skill == null) return 0;
                                                                    ((ModPlayer) player).molecraft$getSkills().addXp(skill, IntegerArgumentType.getInteger(context, "amount"));
                                                                    return 1;
                                                                }
                                                        )
                                                )
                                        )
                                        .then(Commands.literal("set")
                                                .then(Commands.argument("level", IntegerArgumentType.integer())
                                                        .then(Commands.argument("xp", IntegerArgumentType.integer())
                                                                .executes(
                                                                        context -> {
                                                                            ServerPlayer player = context.getSource().getPlayer();
                                                                            if (player == null) return 0;
                                                                            SkillType skill = SkillType.BY_ID.get(StringArgumentType.getString(context, "skill"));
                                                                            if (skill == null) return 0;
                                                                            int skillLevel = IntegerArgumentType.getInteger(context, "level");
                                                                            int xp = IntegerArgumentType.getInteger(context, "xp");
                                                                            ((ModPlayer) player).molecraft$getSkills().skillLevels.put(skill, new SkillLevel(skillLevel, xp));
                                                                            skill.onLevelChange.accept(skillLevel, player);
                                                                            context.getSource().sendSuccess(() -> Component.literal("Set ").append(skill.name.copy().withStyle(ChatFormatting.GOLD)).append(" level to §a" + skillLevel + "§f and xp to §a" + xp + "§f for player ").append(player.getDisplayName().copy().withStyle(ChatFormatting.BLUE)), false);
                                                                            return 1;
                                                                        }
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
        );
//        dispatcher.register(
//                Commands.literal("ce")
//                        .requires(commandSourceStack -> commandSourceStack.hasPermission(2))
//                        .executes(context -> {
//                            Commands commands = context.getSource().getServer().getCommands();
//                            String command = "kill @e[operation=!minecraft:player]";
//                            commands.performCommand(commands.getDispatcher().parse(command, context.getSource()), command);
//                            return 1;
//                        })
//        );
    }

    private static int giveItem(CommandSourceStack source, String itemId, Collection<ServerPlayer> targets, int count) {
        if (!MolecraftItems.ITEMS.containsKey(itemId)) return 0;
        MolecraftData molecraftData = new MolecraftData(itemId);
        ItemStack itemStack = molecraftData.construct(count, source.registryAccess());
        int i = itemStack.getMaxStackSize();
        int j = i * 100;
        if (count > j) {
            source.sendFailure(Component.translatable("commands.give.failed.toomanyitems", j, itemStack.getDisplayName()));
            return 0;
        } else {
            for (ServerPlayer serverPlayer : targets) {
                int k = count;
                while (k > 0) {
                    int l = Math.min(i, k);
                    k -= l;
                    ItemStack itemStack2 = molecraftData.construct(l, source.registryAccess());
                    boolean bl = serverPlayer.getInventory().add(itemStack2);
                    if (bl && itemStack2.isEmpty()) {
                        ItemEntity itemEntity = serverPlayer.drop(itemStack, false);
                        if (itemEntity != null) itemEntity.makeFakeItem();
                        serverPlayer.level()
                                .playSound(
                                        null,
                                        serverPlayer.getX(),
                                        serverPlayer.getY(),
                                        serverPlayer.getZ(),
                                        SoundEvents.ITEM_PICKUP,
                                        SoundSource.PLAYERS,
                                        0.2F,
                                        ((serverPlayer.getRandom().nextFloat() - serverPlayer.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F
                                );
                        serverPlayer.containerMenu.broadcastChanges();
                    } else {
                        ItemEntity itemEntity = serverPlayer.drop(itemStack2, false);
                        if (itemEntity != null) {
                            itemEntity.setNoPickUpDelay();
                            itemEntity.setTarget(serverPlayer.getUUID());
                        }
                    }
                }
            }
            if (targets.size() == 1) {
                source.sendSuccess(
                        () -> Component.translatable("commands.give.success.single", count, itemStack.getDisplayName(), targets.iterator().next().getDisplayName()),
                        true
                );
            } else {
                source.sendSuccess(() -> Component.translatable("commands.give.success.single", count, itemStack.getDisplayName(), targets.size()), true);
            }
            return targets.size();
        }
    }

    private static int enchantHandItem(CommandSourceStack source, String enchantId, Collection<ServerPlayer> targets, int enchantLevel) {
        for (ServerPlayer player : targets) {
            ItemStack itemStack = player.getMainHandItem();
            CustomData customData = itemStack.get(DataComponents.CUSTOM_DATA);
            if (customData != null) {
                CompoundTag tag = customData.getUnsafe();
                MolecraftData molecraftData = MolecraftData.parse(tag);
                if (molecraftData != null) {
                    DataPropertyMap propertyMap = molecraftData.propertyMap();
                    TreeMap<MolecraftEnchant<?, ?>, Integer> enchants = propertyMap.get(DataPropertyTypes.ENCHANTS);
                    if (enchants == null) enchants = new TreeMap<>();
                    MolecraftEnchant<?, ?> enchant = MolecraftEnchants.ENCHANTS.get(enchantId);
                    if (enchant != null) {
                        if (enchantLevel <= 0) enchants.remove(enchant);
                        else enchants.put(enchant, enchantLevel);
                        propertyMap.set(DataPropertyTypes.ENCHANTS, enchants);
                        molecraftData = new MolecraftData(molecraftData.id(), propertyMap);
                        player.setItemInHand(InteractionHand.MAIN_HAND, molecraftData.construct(itemStack.getCount(), source.registryAccess()));
                        return 1;
                    }
                }
            }
        }
        return 0;
    }

    private static int spawnEntity(CommandSourceStack source, String id, Vec3 pos, @Nullable CompoundTag tag) throws CommandSyntaxException {
        Entity entity = createEntity(source, id, pos, tag);
        source.sendSuccess(() -> Component.translatable("commands.summon.success", entity.getDisplayName()), true);
        return 1;
    }

    private static final SimpleCommandExceptionType ERROR_DUPLICATE_UUID = new SimpleCommandExceptionType(Component.translatable("commands.summon.failed.uuid"));
    private static final SimpleCommandExceptionType INVALID_POSITION = new SimpleCommandExceptionType(Component.translatable("commands.summon.invalidPosition"));

    public static Entity createEntity(CommandSourceStack source, String id, Vec3 pos, @Nullable CompoundTag tag) throws CommandSyntaxException {
        BlockPos blockPos = BlockPos.containing(pos);
        if (!Level.isInSpawnableBounds(blockPos)) {
            throw INVALID_POSITION.create();
        } else {
            MolecraftEntity molecraftEntity = MolecraftEntities.ENTITIES.get(id);
            ServerLevel serverLevel = source.getLevel();
            Entity entity = molecraftEntity.construct(serverLevel);
            if (tag != null) {
                CompoundTag merged = new CompoundTag();
                entity.save(merged);
                merged.merge(tag);
                entity.load(merged);
            }
            entity.moveTo(pos.x, pos.y, pos.z);
            if (!serverLevel.tryAddFreshEntityWithPassengers(entity)) throw ERROR_DUPLICATE_UUID.create();
            else return entity;
        }
    }
}
