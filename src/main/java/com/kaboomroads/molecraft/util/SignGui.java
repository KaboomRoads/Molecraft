package com.kaboomroads.molecraft.util;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.MessageToMessageDecoder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.network.protocol.game.ClientboundOpenSignEditorPacket;
import net.minecraft.network.protocol.game.ServerboundSignUpdatePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class SignGui {
    public static void openSignGui(ServerPlayer player, SignText text, Consumer<ServerboundSignUpdatePacket> onDone) {
        Channel channel = player.connection.connection.channel;
        ChannelPipeline pipeline = channel.pipeline();
        if (pipeline.names().contains("MCSGUIMD")) return;
        if (pipeline.context("decoder") == null) return;
        ServerLevel level = player.serverLevel();
        SignBlockEntity blockEntity = new SignBlockEntity(player.blockPosition(), Blocks.OAK_SIGN.defaultBlockState());
        blockEntity.setLevel(level);
        UUID uuid = player.getUUID();
        blockEntity.setAllowedPlayerEditor(new UUID(~uuid.getMostSignificantBits(), ~uuid.getLeastSignificantBits()));
        blockEntity.frontText = text;
        BlockPos blockPos = blockEntity.getBlockPos();
        player.connection.send(new ClientboundBundlePacket(List.of(
                new ClientboundBlockUpdatePacket(blockPos, blockEntity.getBlockState()),
                blockEntity.getUpdatePacket(),
                new ClientboundOpenSignEditorPacket(blockPos, true)
        )));
        pipeline.addAfter("decoder", "MCSGUIMD", new MessageToMessageDecoder<>() {
            @Override
            protected void decode(ChannelHandlerContext ctx, Object msg, List<Object> out) {
                try {
                    if (msg instanceof ServerboundSignUpdatePacket updateSign) {
                        if (updateSign.getPos().equals(blockPos)) {
                            player.level().getServer().execute(() -> {
                                onDone.accept(updateSign);
                                player.connection.send(new ClientboundBlockUpdatePacket(level, blockPos));
                            });
                            uninject(player);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                out.add(msg);
            }
        });

    }

    public static void uninject(ServerPlayer player) {
        Channel channel = player.connection.connection.channel;
        ChannelPipeline pipeline = channel.pipeline();
        if (!pipeline.names().contains("MCSGUIMD")) return;
        pipeline.remove("MCSGUIMD");
    }
}
