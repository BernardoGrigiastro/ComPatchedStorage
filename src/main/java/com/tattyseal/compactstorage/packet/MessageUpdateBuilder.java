package com.tattyseal.compactstorage.packet;

import java.util.function.Supplier;

import com.tattyseal.compactstorage.inventory.ContainerChestBuilder;
import com.tattyseal.compactstorage.tileentity.TileEntityChestBuilder;
import com.tattyseal.compactstorage.util.StorageInfo;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import shadows.placebo.util.NetworkUtils;
import shadows.placebo.util.NetworkUtils.MessageProvider;

public class MessageUpdateBuilder extends MessageProvider<MessageUpdateBuilder> {

	protected StorageInfo info;

	public MessageUpdateBuilder(StorageInfo info) {
		this.info = info;
	}

	public MessageUpdateBuilder() {
	}

	@Override
	public Class<MessageUpdateBuilder> getMsgClass() {
		return MessageUpdateBuilder.class;
	}

	@Override
	public MessageUpdateBuilder read(PacketBuffer buf) {
		return new MessageUpdateBuilder(new StorageInfo(buf.readInt(), buf.readInt(), buf.readInt(), StorageInfo.Type.values()[buf.readInt()]));
	}

	@Override
	public void write(MessageUpdateBuilder msg, PacketBuffer buf) {
		buf.writeInt(msg.info.getSizeX());
		buf.writeInt(msg.info.getSizeY());
		buf.writeInt(msg.info.getHue());
		buf.writeInt(msg.info.getType().ordinal());
	}

	@Override
	public void handle(MessageUpdateBuilder msg, Supplier<Context> ctx) {
		NetworkUtils.handlePacket(() -> () -> {
			ServerPlayerEntity player = ctx.get().getSender();
			TileEntityChestBuilder builder = null;
			if (player.openContainer instanceof ContainerChestBuilder) builder = ((ContainerChestBuilder) player.openContainer).builder;
			if (builder != null) builder.getInfo().deserialize(msg.info.serialize());
		}, ctx.get());
	}
}
