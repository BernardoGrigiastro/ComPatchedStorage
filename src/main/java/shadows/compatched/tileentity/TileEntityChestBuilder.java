package shadows.compatched.tileentity;

import java.util.List;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import shadows.compatched.CompactRegistry;
import shadows.compatched.inventory.ContainerChestBuilder;
import shadows.compatched.util.StorageInfo;

public class TileEntityChestBuilder extends TileEntity implements INamedContainerProvider {

	protected StorageInfo info = new StorageInfo(9, 3, 180, StorageInfo.Type.CHEST);
	protected ItemHandler items = new ItemHandler(5);

	public TileEntityChestBuilder() {
		super(CompactRegistry.BUILDER_TILE);
	}

	@Override
	public CompoundNBT write(CompoundNBT tag) {
		super.write(tag);
		tag.put("info", info.serialize());
		tag.put("items", items.serializeNBT());
		return tag;
	}

	@Override
	public void read(CompoundNBT tag) {
		super.read(tag);
		info.deserialize(tag.getCompound("info"));
		items.deserializeNBT(tag.getCompound("items"));
	}

	public StorageInfo getInfo() {
		return info;
	}

	public ItemHandler getItems() {
		return items;
	}

	LazyOptional<IItemHandler> itemOpt = LazyOptional.of(() -> items);

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap) {
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return itemOpt.cast();
		return super.getCapability(cap);
	}

	public class ItemHandler extends ItemStackHandler {

		public ItemHandler(int size) {
			super(size);
		}

		@Override
		public boolean isItemValid(int slot, ItemStack stack) {
			if (info == null) return false;
			List<ItemStack> cost = info.getMaterialCost();
			return cost.size() > slot && stack.getItem() == cost.get(slot).getItem();
		}
	}

	@Override
	public Container createMenu(int id, PlayerInventory inv, PlayerEntity player) {
		return new ContainerChestBuilder(id, world, player, this);
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent(CompactRegistry.CHEST_BUILDER.getTranslationKey());
	}
}
