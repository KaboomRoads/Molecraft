package com.kaboomroads.molecraft.trading;

import com.kaboomroads.molecraft.item.MolecraftData;
import com.kaboomroads.molecraft.item.MolecraftItem;
import com.kaboomroads.molecraft.mixinimpl.ModItemEntity;
import com.kaboomroads.molecraft.mixinimpl.ModPlayer;
import com.kaboomroads.molecraft.util.ItemUtils;
import com.kaboomroads.molecraft.util.SignGui;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.network.protocol.game.ServerboundSignUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.NonInteractiveResultSlot;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.entity.SignText;
import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.util.Optional;
import java.util.function.Consumer;

public class TradeMenu extends AbstractContainerMenu {
    private final boolean isSender;
    @NotNull
    public final TradeData tradeData;
    public final Container trueContainer;
    public static final ItemStack TRADE_GLASS = createTradeGlass();
    public static final ItemStack COIN_TRANSACTION = createCoinTransaction();

    public TradeMenu(
            int id,
            Inventory inventory,
            @NotNull TradeData tradeData,
            boolean isSender
    ) {
        super(MenuType.GENERIC_9x6, id);
        this.tradeData = tradeData;
        this.isSender = isSender;
        tradeData.nextSeconds = getTradeSeconds();
        SimpleContainer self;
        SimpleContainer other;
        boolean selfConfirm;
        boolean otherConfirm;
        if (isSender) {
            tradeData.senderMenu = this;
            self = tradeData.senderContainer;
            other = tradeData.receiverContainer;
            selfConfirm = tradeData.senderConfirm;
            otherConfirm = tradeData.receiverConfirm;
        } else {
            tradeData.receiverMenu = this;
            self = tradeData.receiverContainer;
            other = tradeData.senderContainer;
            selfConfirm = tradeData.receiverConfirm;
            otherConfirm = tradeData.senderConfirm;
        }
        checkContainerSize(self, 20);
        checkContainerSize(other, 20);
        trueContainer = new SimpleContainer(54);
        for (int y = 0; y < 6; ++y) {
            for (int x = 0; x < 9; ++x) {
                boolean inFiller = x == 4 || y == 5;
                boolean inSelf = !inFiller && x < 4;
                boolean inOther = !inFiller && x > 4;
                int i = x + y * 9;
                if (inSelf) addSlot(new TradeSelfSideSlot(trueContainer, i, 0, 0));
                else if (inOther) addSlot(new NonInteractiveResultSlot(trueContainer, i, 0, 0));
                else {
                    if (y == 5) {
                        switch (x) {
                            case 0:
                                addSlot(new ButtonSlot(trueContainer, i, 0, 0, COIN_TRANSACTION, slot -> {
                                    ServerPlayer player = getPlayer();
                                    Component[] messages = new Component[]{
                                            Component.empty(),
                                            Component.literal("^^^^^^^^^^^^^^^"),
                                            Component.literal("Enter amount"),
                                            Component.empty()
                                    };
                                    SignGui.openSignGui(player, new SignText(messages, messages, DyeColor.BLACK, false), new OnCoinSignGuiClose(this));
                                }));
                                break;
                            case 3:
                                addSlot(new ButtonSlot(trueContainer, i, 0, 0, createConfirmTrade(selfConfirm ? TradeButtonState.CONFIRMED : tradeData.tradeTime <= 0 ? TradeButtonState.READY : TradeButtonState.NOT_READY), slot -> {
                                    if (tradeData.tradeTime <= 0) {
                                        if (isSender) {
                                            tradeData.senderConfirm = true;
                                            tradeData.senderMenu.confirm();
                                            tradeData.receiverMenu.setOtherConfirm(true);
                                        } else {
                                            tradeData.receiverConfirm = true;
                                            tradeData.receiverMenu.confirm();
                                            tradeData.senderMenu.setOtherConfirm(true);
                                        }
                                    }
                                }));
                                break;
                            case 4:
                                addSlot(new DisplaySlot(trueContainer, i, 0, 0, createTradeTimer(tradeData.nextSeconds)));
                                break;
                            case 5:
                                addSlot(new DisplaySlot(trueContainer, i, 0, 0, createOtherConfirm(otherConfirm)));
                                break;
                            default:
                                addSlot(new DisplaySlot(trueContainer, i, 0, 0, TRADE_GLASS));
                                break;
                        }
                    } else addSlot(new DisplaySlot(trueContainer, i, 0, 0, TRADE_GLASS));
                }
            }
        }
        addStandardInventorySlots(inventory, 0, 0);
    }

    public enum TradeButtonState {
        NOT_READY,
        READY,
        CONFIRMED
    }

    public void setTradeTimer(int seconds) {
        ((DisplaySlot) getSlot(49)).display = createTradeTimer(seconds);
    }

    public void setReady(boolean ready) {
        ((DisplaySlot) getSlot(48)).display = createConfirmTrade(ready ? TradeButtonState.READY : TradeButtonState.NOT_READY);
    }

    public void confirm() {
        ((DisplaySlot) getSlot(48)).display = createConfirmTrade(TradeButtonState.CONFIRMED);
    }

    public void setOtherConfirm(boolean confirm) {
        ((DisplaySlot) getSlot(50)).display = createOtherConfirm(confirm);
    }

    public static ItemStack createTradeGlass() {
        ItemStack itemStack = new ItemStack(Items.GRAY_STAINED_GLASS_PANE);
        itemStack.set(DataComponents.ITEM_NAME, CommonComponents.EMPTY);
        return itemStack;
    }

    public static ItemStack createTradeTimer(int seconds) {
        ItemStack itemStack = new ItemStack(Items.PAPER, Math.max(1, seconds));
        itemStack.set(DataComponents.ITEM_NAME, Component.literal("Trade timer: " + seconds));
        return itemStack;
    }

    public static ItemStack createConfirmTrade(TradeButtonState buttonState) {
        ItemStack itemStack = new ItemStack(switch (buttonState) {
            case NOT_READY -> Items.RED_TERRACOTTA;
            case READY -> Items.YELLOW_TERRACOTTA;
            case CONFIRMED -> Items.LIME_TERRACOTTA;
        });
        itemStack.set(DataComponents.ITEM_NAME, Component.literal(buttonState == TradeButtonState.CONFIRMED ? "Trade confirmed" : "Confirm trade"));
        return itemStack;
    }

    public static ItemStack createOtherConfirm(boolean confirmed) {
        ItemStack itemStack = new ItemStack((confirmed ? Items.LIME_DYE : Items.GRAY_DYE));
        itemStack.set(DataComponents.ITEM_NAME, Component.literal(confirmed ? "Trade confirmed" : "Waiting for other side to confirm"));
        return itemStack;
    }

    public static ItemStack createCoinTransaction() {
        ItemStack itemStack = ItemUtils.createPlayerHeadFromBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjMxMWU4NDIyNDk3NDUxMTMzMzM4NzhmZDVhOGQ4ZWIyMjgwYmNiYWI3Njg5ZTQ3M2YxNjY5NjIzYTExYzk5NyJ9fX0=");
        itemStack.set(DataComponents.ITEM_NAME, Component.literal("Coin transaction").withStyle(ChatFormatting.WHITE));
        return itemStack;
    }

    public static ItemStack createCoinAmount(long amount) {
        ItemStack itemStack = ItemUtils.createPlayerHeadFromBase64(
                amount >= 1000000 ? "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjMxMWU4NDIyNDk3NDUxMTMzMzM4NzhmZDVhOGQ4ZWIyMjgwYmNiYWI3Njg5ZTQ3M2YxNjY5NjIzYTExYzk5NyJ9fX0="
                        : amount >= 100000 ? "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjAxNDExMmMzN2QxNTEwMTI4NTdkOWYyYTUzMzI4OTg3MzFhMDE0NzNiNGY0YmM2OTA5ZDIzY2YyNjRkYmVlYiJ9fX0="
                        : "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWRiOTZmYTMwNjNiNzkwNWFiYzA5YjRjOTAzODllYWRmYTFiZTFiZGVhMDZiMGVjNGFlOGQwZDA1ZTliZmRiMSJ9fX0="
        );
        itemStack.set(DataComponents.ITEM_NAME, Component.literal(NumberFormat.getInstance().format(amount) + " Coins").withStyle(ChatFormatting.GOLD));
        CompoundTag tag = new CompoundTag();
        tag.putLong("coins", amount);
        itemStack.set(DataComponents.CUSTOM_DATA, new CustomData(tag));
        return itemStack;
    }

    public static class DisplaySlot extends NonInteractiveResultSlot {
        public ItemStack display;

        public DisplaySlot(Container container, int i, int j, int k, ItemStack display) {
            super(container, i, j, k);
            this.display = display;
        }

        @NotNull
        @Override
        public ItemStack getItem() {
            return display;
        }
    }

    public static class ButtonSlot extends DisplaySlot {
        public final Consumer<ButtonSlot> callback;

        public ButtonSlot(Container container, int i, int j, int k, ItemStack display, Consumer<ButtonSlot> callback) {
            super(container, i, j, k, display);
            this.callback = callback;
        }

        @Override
        public boolean mayPickup(Player player) {
            return true;
        }

        @NotNull
        @Override
        public Optional<ItemStack> tryRemove(int count, int decrement, Player player) {
            callback.accept(this);
            return super.tryRemove(count, decrement, player);
        }
    }

    private class TradePlayerInventorySlot extends NonInteractiveResultSlot {
        public TradePlayerInventorySlot(Container container, int i, int j, int k) {
            super(container, i, j, k);
        }

        @Override
        public boolean mayPickup(Player player) {
            return true;
        }

        @NotNull
        @Override
        public Optional<ItemStack> tryRemove(int count, int decrement, Player player) {
            ItemStack itemStack = getItem();
            if (mayTrade(itemStack)) {
                resetTradeTimer();
                SimpleContainer self = isSender ? tradeData.senderContainer : tradeData.receiverContainer;
                Player otherPlayer = getOther();
                self.addItem(itemStack);
                set(ItemStack.EMPTY);
                repaint(true, isSender);
                if (otherPlayer.containerMenu instanceof TradeMenu tradeMenu) tradeMenu.repaint(false, isSender);
            }
            return super.tryRemove(count, decrement, player);
        }
    }

    private class TradeSelfSideSlot extends NonInteractiveResultSlot {
        public TradeSelfSideSlot(Container container, int i, int j, int k) {
            super(container, i, j, k);
        }

        @Override
        public boolean mayPickup(Player player) {
            return true;
        }

        @NotNull
        @Override
        public Optional<ItemStack> tryRemove(int count, int decrement, Player player) {
            ItemStack itemStack = getItem();
            resetTradeTimer();
            Player otherPlayer = getOther();
            player.getInventory().add(itemStack);
            SimpleContainer self = isSender ? tradeData.senderContainer : tradeData.receiverContainer;
            self.setItem(tradeData.fromLeft(getContainerSlot()), ItemStack.EMPTY);
            repaint(true, isSender);
            if (otherPlayer.containerMenu instanceof TradeMenu tradeMenu) tradeMenu.repaint(false, isSender);
            return super.tryRemove(count, decrement, player);
        }
    }

    @Override
    protected void addInventoryHotbarSlots(Container container, int x, int y) {
        for (int i = 0; i < 9; i++) addSlot(new TradePlayerInventorySlot(container, i, x + i * 18, y));
    }

    @Override
    protected void addInventoryExtendedSlots(Container container, int x, int y) {
        for (int i = 0; i < 3; i++) for (int j = 0; j < 9; j++) addSlot(new TradePlayerInventorySlot(container, j + (i + 1) * 9, x + j * 18, y + i * 18));
    }

    public boolean mayTrade(ItemStack stack) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData != null) {
            MolecraftData molecraftData = MolecraftData.parse(customData.getUnsafe());
            if (molecraftData != null) {
                MolecraftItem molecraftItem = molecraftData.getItem();
                if (molecraftItem != null) return !molecraftItem.soulbound;
            }
        }
        return true;
    }

    public void repaint(boolean side, boolean whoSelf) {
        for (int i = 0; i < 20; i++) {
            Container the = whoSelf ? tradeData.senderContainer : tradeData.receiverContainer;
            int trueIndex = side ? tradeData.left(i) : tradeData.right(i);
            trueContainer.setItem(trueIndex, the.getItem(i));
        }
    }

    public void resetTradeTimer() {
        tradeData.senderConfirm = false;
        tradeData.receiverConfirm = false;
        tradeData.tradeTime = tradeData.getMaxTradeTime();
        tradeData.nextSeconds = getTradeSeconds() - 1;
        resetSelf();
        (isSender ? tradeData.receiverMenu : tradeData.senderMenu).resetSelf();
    }

    public void resetSelf() {
        setOtherConfirm(false);
        setReady(false);
        setTradeTimer(getTradeSeconds());
    }

    public int getTradeSeconds() {
        return Mth.positiveCeilDiv(tradeData.tradeTime, 20);
    }

    @Override
    public boolean clickMenuButton(Player player, int i) {
        if (i == 0) {
            if (isSender) tradeData.senderConfirm = true;
            else tradeData.receiverConfirm = true;
            return true;
        }
        return super.clickMenuButton(player, i);
    }

    @NotNull
    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player p) {
        Player tradeulous = getOther();
        Player player = getPlayer();
        if (tradeulous == null) return true;
        if (tradeulous.containerMenu instanceof TradeMenu tradeMenu) {
            Player otherTradeulous = tradeMenu.getOther();
            if (otherTradeulous != null) return otherTradeulous.equals(player);
        }
        return false;
    }

    private ServerPlayer getOther() {
        return isSender ? tradeData.receiver : tradeData.sender;
    }

    private ServerPlayer getPlayer() {
        return isSender ? tradeData.sender : tradeData.receiver;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (!tradeData.traded && player instanceof ServerPlayer) addOrDropBound(isSender ? tradeData.senderContainer : tradeData.receiverContainer, player);
    }

    public static void addOrDropBound(Container container, Player player) {
        if (player instanceof ServerPlayer) for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack itemStack = container.getItem(i);
            if (!player.addItem(itemStack)) {
                ItemEntity itemEntity = new ItemEntity(player.level(), player.getX(), player.getY(), player.getZ(), itemStack);
                itemEntity.setTarget(player.getUUID());
                itemEntity.setNoPickUpDelay();
                ((ModItemEntity) itemEntity).molecraft$setTelekinetic(true);
                itemEntity.setUnlimitedLifetime();
                player.level().addFreshEntity(itemEntity);
            }
        }
    }

    public static class OnCoinSignGuiClose implements Consumer<ServerboundSignUpdatePacket> {
        private final TradeMenu tradeMenu;

        private OnCoinSignGuiClose(TradeMenu tradeMenu) {
            this.tradeMenu = tradeMenu;
        }

        @Override
        public void accept(ServerboundSignUpdatePacket packet) {
            ServerPlayer player = tradeMenu.getPlayer();
            player.connection.send(new ClientboundOpenScreenPacket(tradeMenu.containerId, tradeMenu.getType(), Component.literal("Trade with ").append(tradeMenu.getOther().getDisplayName())));
            tradeMenu.sendAllDataToRemote();
            String firstLine = packet.getLines()[0];
            try {
                long amount = Long.parseLong(firstLine);
                if (amount > 0 && ((ModPlayer) player).molecraft$getCoins() >= amount) {
                    tradeMenu.resetTradeTimer();
                    SimpleContainer self = tradeMenu.isSender ? tradeMenu.tradeData.senderContainer : tradeMenu.tradeData.receiverContainer;
                    Player otherPlayer = tradeMenu.getOther();
                    ((ModPlayer) player).molecraft$setCoins(((ModPlayer) player).molecraft$getCoins() - amount);
                    self.addItem(createCoinAmount(amount));
                    tradeMenu.repaint(true, tradeMenu.isSender);
                    if (otherPlayer.containerMenu instanceof TradeMenu otherMenu) otherMenu.repaint(false, tradeMenu.isSender);
                } else player.displayClientMessage(Component.literal("Wrong number, better luck next time.").withStyle(ChatFormatting.RED), false);
            } catch (NumberFormatException ignore) {
                player.displayClientMessage(Component.literal("Could not parse number value.").withStyle(ChatFormatting.RED), false);
            }
        }
    }
}