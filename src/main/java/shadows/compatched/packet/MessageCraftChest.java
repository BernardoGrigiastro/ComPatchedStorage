package shadows.compatched.packet;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import shadows.compatched.inventory.ContainerChestBuilder;
import shadows.compatched.tileentity.TileEntityChest;
import shadows.compatched.tileentity.TileEntityChestBuilder;
import shadows.compatched.util.StorageInfo;
import shadows.placebo.util.NetworkUtils;
import shadows.placebo.util.NetworkUtils.MessageProvider;

public class MessageCraftChest extends MessageProvider<MessageCraftChest> {

	protected int x;
	protected int y;
	protected int z;
	protected StorageInfo info;

	public MessageCraftChest(int x, int y, int z, StorageInfo info) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.info = info;
	}

	public MessageCraftChest(BlockPos pos, StorageInfo info) {
		this(pos.getX(), pos.getY(), pos.getZ(), info);
	}

	public MessageCraftChest() {
	}

	@Override
	public Class<MessageCraftChest> getMsgClass() {
		return MessageCraftChest.class;
	}

	@Override
	public MessageCraftChest read(PacketBuffer buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		info = new StorageInfo(buf.readInt(), buf.readInt(), buf.readInt(), StorageInfo.Type.values()[buf.readInt()]);
		return new MessageCraftChest(x, y, z, info);
	}

	@Override
	public void write(MessageCraftChest msg, PacketBuffer buf) {
		buf.writeInt(msg.x);
		buf.writeInt(msg.y);
		buf.writeInt(msg.z);
		buf.writeInt(msg.info.getSizeX());
		buf.writeInt(msg.info.getSizeY());
		buf.writeInt(msg.info.getHue());
		buf.writeInt(msg.info.getType().ordinal());
	}

	@Override
	public void handle(MessageCraftChest msg, Supplier<Context> ctx) {
		NetworkUtils.handlePacket(() -> () -> {
			ServerPlayerEntity player = ctx.get().getSender();
			TileEntityChestBuilder builder = null;
			if (player.openContainer instanceof ContainerChestBuilder) builder = ((ContainerChestBuilder) player.openContainer).builder;
			if (builder == null) return;

			List<ItemStack> items = new ArrayList<>();
			for (int i = 0; i < 4; i++) {
				items.add(builder.getItems().getStackInSlot(i));
			}
			List<ItemStack> requiredItems = builder.getInfo().getMaterialCost();

			boolean hasRequiredMaterials = true;

			for (int slot = 0; slot < items.size(); slot++) {
				ItemStack stack = items.get(slot);

				if (stack != null && slot < requiredItems.size() && requiredItems.get(slot) != null) {
					if (requiredItems.get(slot).getItem() == stack.getItem() && stack.getCount() >= requiredItems.get(slot).getCount()) {
						hasRequiredMaterials = true;
					} else {
						hasRequiredMaterials = requiredItems.get(slot) != null && requiredItems.get(slot).getCount() == 0;
						break;
					}
				} else {
					hasRequiredMaterials = false;
					break;
				}
			}

			if (hasRequiredMaterials && builder.getItems().getStackInSlot(4).isEmpty()) {
				ItemStack stack = msg.info.getType().display.copy();
				TileEntityChest chest = new TileEntityChest(msg.info);
				chest.write(stack.getOrCreateChildTag("BlockEntityTag"));
				builder.getItems().setStackInSlot(4, stack);

				for (int x = 0; x < requiredItems.size(); x++) {
					builder.getItems().getStackInSlot(x).shrink(requiredItems.get(x).getCount());
				}

				player.world.playSound(null, builder.getPos(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.AMBIENT, 1, 1);
			} else {
				player.world.playSound(null, builder.getPos(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.AMBIENT, 1, 0);
			}
		}, ctx.get());
	}

}
