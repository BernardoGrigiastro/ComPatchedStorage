package com.tattyseal.compactstorage.packet;

import java.util.function.Supplier;

import com.tattyseal.compactstorage.inventory.ContainerChestBuilder;
import com.tattyseal.compactstorage.tileentity.TileEntityChestBuilder;
import com.tattyseal.compactstorage.util.StorageInfo;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import shadows.placebo.util.NetworkUtils;
import shadows.placebo.util.NetworkUtils.MessageProvider;

public class MessageUpdateBuilder extends MessageProvider<MessageUpdateBuilder> {

	protected int x;
	protected int y;
	protected int z;
	protected StorageInfo info;

	public MessageUpdateBuilder(int x, int y, int z, StorageInfo info) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.info = info;
	}

	public MessageUpdateBuilder(BlockPos pos, StorageInfo info) {
		this(pos.getX(), pos.getY(), pos.getZ(), info);
	}

	public MessageUpdateBuilder() {
	}

	@Override
	public Class<MessageUpdateBuilder> getMsgClass() {
		return MessageUpdateBuilder.class;
	}

	@Override
	public MessageUpdateBuilder read(PacketBuffer buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		info = new StorageInfo(buf.readInt(), buf.readInt(), buf.readInt(), StorageInfo.Type.values()[buf.readInt()]);
		return new MessageUpdateBuilder(x, y, z, info);
	}

	@Override
	public void write(MessageUpdateBuilder msg, PacketBuffer buf) {
		buf.writeInt(msg.x);
		buf.writeInt(msg.y);
		buf.writeInt(msg.z);
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
