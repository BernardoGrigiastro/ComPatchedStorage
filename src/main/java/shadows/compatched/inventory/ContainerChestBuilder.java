package shadows.compatched.inventory;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.SlotItemHandler;
import shadows.compatched.CompactRegistry;
import shadows.compatched.inventory.slot.SlotUnplaceable;
import shadows.compatched.tileentity.TileEntityChestBuilder;
import shadows.compatched.util.StorageInfo;

public class ContainerChestBuilder extends Container {

	public World world;
	public PlayerEntity player;
	public BlockPos pos;
	public TileEntityChestBuilder builder;
	public int xSize;

	public ContainerChestBuilder(int id, World world, PlayerEntity player, TileEntityChestBuilder te) {
		super(CompactRegistry.BUILDER_CONTAINER, id);
		this.world = world;
		this.player = player;
		this.pos = te.getPos();
		this.builder = te;
		this.xSize = 7 + 162 + 7;
		setupSlots();
	}

	public ContainerChestBuilder(int id, PlayerInventory inv, PacketBuffer buf) {
		this(id, inv.player.world, inv.player, (TileEntityChestBuilder) inv.player.world.getTileEntity(buf.readBlockPos()));
	}

	@Override
	public boolean canInteractWith(@Nonnull PlayerEntity player) {
		return true;
	}

	private void setupSlots() {
		int slotY = 50 + 12;
		int slotX = xSize / 2 - 36;

		for (int x = 0; x < 4; x++) {
			addSlot(new SlotItemHandler(builder.getItems(), x, slotX + x * 18 + 1, slotY + 21));
		}

		SlotUnplaceable chestSlot = new SlotUnplaceable(builder.getItems(), 4, 5 + xSize - 29, 8 + 108 - 12);
		addSlot(chestSlot);

		slotX = xSize / 2 - 9 * 18 / 2 + 1;
		slotY = 8 + 108 + 10;

		for (int x = 0; x < 9; x++) {
			for (int y = 0; y < 3; y++) {
				Slot slot = new Slot(player.inventory, x + y * 9 + 9, slotX + x * 18, slotY + y * 18);
				addSlot(slot);
			}
		}

		slotY = slotY + 3 * 18 + 4;

		for (int x = 0; x < 9; x++) {
			Slot slot = new Slot(player.inventory, x, slotX + x * 18, slotY);
			addSlot(slot);
		}
	}

	@Override
	@Nonnull
	public ItemStack transferStackInSlot(PlayerEntity player, int slotIndex) {
		try {
			Slot slot = inventorySlots.get(slotIndex);

			if (slot != null && slot.getHasStack()) {
				ItemStack itemStack1 = slot.getStack();
				ItemStack itemStack = itemStack1.copy();

				if (slotIndex < 5) {
					if (!this.mergeItemStack(itemStack1, 5, 5 + 36, false)) { return ItemStack.EMPTY; }
				} else if (!this.mergeItemStack(itemStack1, 0, 5, false)) { return ItemStack.EMPTY; }

				if (itemStack1.getCount() == 0) {
					slot.putStack(ItemStack.EMPTY);
				} else {
					slot.onSlotChanged();
				}
				return itemStack;
			}

			return ItemStack.EMPTY;
		} catch (Exception e) {
			e.printStackTrace();
			return ItemStack.EMPTY;
		}
	}

	@Override
	public void addListener(IContainerListener listener) {
		super.addListener(listener);
		listener.sendWindowProperty(this, 0, 9);
		listener.sendWindowProperty(this, 1, 3);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		for (IContainerListener player : this.listeners) {
			if (builder != null) {
				StorageInfo info = builder.getInfo();
				player.sendWindowProperty(this, 0, info.getSizeX());
				player.sendWindowProperty(this, 1, info.getSizeY());
				player.sendWindowProperty(this, 2, info.getHue());
				player.sendWindowProperty(this, 3, info.getType().ordinal());
			}
		}
	}

	@Override
	public void updateProgressBar(int id, int value) {
		switch (id) {
		case 0:
			builder.getInfo().setSizeX(value);
			break;
		case 1:
			builder.getInfo().setSizeY(value);
			break;
		case 2:
			builder.getInfo().setHue(value);
			break;
		case 3:
			builder.getInfo().setType(StorageInfo.Type.values()[value]);
			break;
		}
	}
}
