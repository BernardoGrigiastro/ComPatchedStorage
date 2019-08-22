package shadows.compatched.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.items.ItemStackHandler;
import shadows.compatched.util.StorageInfo;
import shadows.compatched.util.StorageInfo.Type;

public class InventoryBackpack implements IChest, INamedContainerProvider {

	protected ItemStack backpack;
	protected int slot;
	protected StorageInfo info = new StorageInfo(0, 0, 0, Type.BACKPACK);
	protected InfoItemHandler items = new InfoItemHandler(info);

	public InventoryBackpack(ItemStack stack, int slot) {
		this.backpack = stack;
		read(this.backpack.getOrCreateChildTag("BlockEntityTag"));
		this.slot = slot;
	}

	@Override
	public int getInvX() {
		return info.getSizeX();
	}

	@Override
	public int getInvY() {
		return info.getSizeY();
	}

	@Override
	public StorageInfo getInfo() {
		return info;
	}

	@Override
	public void onOpened(PlayerEntity player) {
	}

	@Override
	public void onClosed(PlayerEntity player) {
		write(backpack.getOrCreateChildTag("BlockEntityTag"));
	}

	@Override
	public int getHue() {
		return info.getHue();
	}

	@Override
	public void setHue(int hue) {
		info.setHue(hue);
	}

	@Override
	public ItemStackHandler getItems() {
		return items;
	}

	private void write(CompoundNBT tag) {
		tag.put("info", info.serialize());
		tag.put("items", items.serializeNBT());
	}

	private void read(CompoundNBT tag) {
		this.info.deserialize(tag.getCompound("info"));
		this.items.deserializeNBT(tag.getCompound("items"));
	}

	@Override
	public Container createMenu(int id, PlayerInventory inv, PlayerEntity player) {
		return new ContainerChest(id, player.world, this, player, player.getPosition());
	}

	@Override
	public ITextComponent getDisplayName() {
		return backpack.getDisplayName();
	}
}
