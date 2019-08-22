package shadows.compatched.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.SlotItemHandler;
import shadows.compatched.CompactRegistry;
import shadows.compatched.inventory.slot.SlotImmovable;
import shadows.compatched.tileentity.TileEntityChest;

public class ContainerChest extends Container {

	public World world;
	public PlayerEntity player;
	public BlockPos pos;
	public IChest chest;
	public int invX;
	public int invY;
	public int lastId;
	public int backpackSlot;
	public int xSize;
	public int ySize;

	public ContainerChest(int id, World world, IChest chest, PlayerEntity player, BlockPos pos) {
		super(CompactRegistry.CHEST_CONTAINER, id);
		this.world = world;
		this.player = player;
		this.pos = pos;
		this.chest = chest;
		chest.onOpened(player);
		backpackSlot = -1;
		if (chest instanceof InventoryBackpack) {
			backpackSlot = ((InventoryBackpack) chest).slot;
		}
		this.invX = chest.getInvX();
		this.invY = chest.getInvY();
		this.xSize = 7 + (invX < 9 ? (9 * 18) : (invX * 18)) + 7;
		this.ySize = 15 + (invY * 18) + 13 + 54 + 4 + 18 + 7;
		setupSlots();
	}

	public ContainerChest(int id, PlayerInventory inv, PacketBuffer buf) {
		this(id, inv.player.world, readChest(buf, inv), inv.player, buf.readBlockPos());
	}

	public static IChest readChest(PacketBuffer buf, PlayerInventory inv) {
		boolean chest = buf.readBoolean();
		if (chest) return (IChest) inv.player.world.getTileEntity(buf.readBlockPos());
		else {
			int slot = buf.readInt();
			return new InventoryBackpack(inv.getStackInSlot(slot), slot);
		}
	}

	public static PacketBuffer writeChest(PacketBuffer buf, IChest chest) {
		buf.writeBoolean(chest instanceof TileEntityChest);
		if (chest instanceof TileEntityChest) buf.writeBlockPos(((TileEntityChest) chest).getPos());
		else buf.writeInt(((InventoryBackpack) chest).slot);
		return buf;
	}

	@Override
	public boolean canInteractWith(PlayerEntity player) {
		return true;
	}

	private void setupSlots() {
		int slotX = (xSize / 2) - (invX * 18 / 2) + 1;
		int slotY = 18;

		int lastId = 0;

		for (int y = 0; y < invY; y++) {
			for (int x = 0; x < invX; x++) {
				addSlot(new SlotItemHandler(chest.getItems(), lastId++, slotX + (x * 18), slotY + (y * 18)));
			}
		}

		this.lastId = lastId;

		slotX = (xSize / 2) - ((9 * 18) / 2) + 1;
		slotY = slotY + (invY * 18) + 13;

		for (int x = 0; x < 9; x++) {
			for (int y = 0; y < 3; y++) {
				Slot slot = new Slot(player.inventory, x + y * 9 + 9, slotX + (x * 18), slotY + (y * 18));
				addSlot(slot);
			}
		}

		slotY = slotY + (3 * 18) + 4;

		for (int x = 0; x < 9; x++) {
			boolean immovable = false;

			if (backpackSlot != -1 && backpackSlot == x) {
				immovable = true;
			}

			SlotImmovable slot = new SlotImmovable(player.inventory, x, slotX + (x * 18), slotY, immovable);
			addSlot(slot);
		}
	}

	@Override

	public ItemStack transferStackInSlot(PlayerEntity player, int slotIndex) {
		try {
			Slot slot = inventorySlots.get(slotIndex);

			if (slot != null && slot.getHasStack()) {
				ItemStack itemStack1 = slot.getStack();
				ItemStack itemStack = itemStack1.copy();

				if (slotIndex < lastId) {
					if (!this.mergeItemStack(itemStack1, lastId, lastId + 36, false)) { return ItemStack.EMPTY; }
				} else if (!this.mergeItemStack(itemStack1, 0, lastId, false)) { return ItemStack.EMPTY; }

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

	public int getInvX() {
		return invX;
	}

	public int getInvY() {
		return invY;
	}

	@Override
	public void onContainerClosed(PlayerEntity player) {
		chest.onClosed(player);

		if (!world.isRemote) {
			boolean isChest = chest instanceof TileEntityChest;

			if (!isChest) {
				world.playSound(null, pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5d, SoundEvents.BLOCK_WOOL_STEP, SoundCategory.BLOCKS, 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
			}
		}

		super.onContainerClosed(player);
	}

	@Override

	public ItemStack slotClick(int slot, int button, ClickType flag, PlayerEntity player) {
		if (chest instanceof InventoryBackpack) {
			if (slot >= 0 && getSlot(slot).getStack() == player.getHeldItem(Hand.MAIN_HAND)) { return ItemStack.EMPTY; }
		}

		return super.slotClick(slot, button, flag, player);
	}

}
