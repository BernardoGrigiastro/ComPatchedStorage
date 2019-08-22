package shadows.compatched.inventory;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import shadows.compatched.tileentity.TileEntityBarrel;

public class BarrelItemHandler implements IItemHandler {

	TileEntityBarrel barrel;

	public BarrelItemHandler(TileEntityBarrel barrel) {
		this.barrel = barrel;
	}

	@Override
	public int getSlots() {
		return 1;
	}

	@Nonnull
	@Override
	public ItemStack getStackInSlot(int slot) {
		return barrel.getBarrelStack();
	}

	@Nonnull
	@Override
	public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
		return barrel.insertItems(stack, null, simulate);
	}

	@Nonnull
	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		return barrel.tryTakeStack(null, amount, simulate);
	}

	@Override
	public int getSlotLimit(int slot) {
		return barrel.getMaxStorage();
	}

	@Override
	public boolean isItemValid(int slot, ItemStack stack) {
		return true;
	}
}
