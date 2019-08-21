package com.tattyseal.compactstorage.tileentity;

import javax.annotation.Nullable;

import com.tattyseal.compactstorage.CompactRegistry;
import com.tattyseal.compactstorage.inventory.BarrelItemHandler;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class TileEntityBarrel extends TileEntity implements IBarrel {

	protected ItemStack item = ItemStack.EMPTY;
	protected int count = 0;
	protected BarrelItemHandler handler = new BarrelItemHandler(this);

	public int hue = 0;

	public TileEntityBarrel() {
		super(CompactRegistry.BARREL_TILE);
		hue = 128;
	}

	@Override
	public ItemStack giveItems(PlayerEntity player) {
		ItemStack stack = tryTakeStack(player, item.getMaxStackSize(), false);
		return stack;
	}

	@Override
	public ItemStack takeItems(ItemStack stack, PlayerEntity player) {
		return insertItems(stack, player, false);
	}

	public ItemStack tryTakeStack(PlayerEntity player, int amount, boolean simulate) {
		if (count > 0) {
			ItemStack stack = item.copy();
			stack.setCount(Math.min(count, Math.min(stack.getMaxStackSize(), amount)));
			if (!simulate) {
				count -= stack.getCount();
				if (count <= 0) {
					count = 0;
					item = ItemStack.EMPTY;
				}
			}
			markDirty();
			return stack;
		}
		return ItemStack.EMPTY;
	}

	public ItemStack insertItems(ItemStack stack, PlayerEntity player, boolean simulate) {
		ItemStack workingStack = stack.copy();
		if (workingStack.isEmpty()) return ItemStack.EMPTY;

		if (item.isEmpty()) {
			if (!simulate) {
				item = workingStack.copy();
				count = item.getCount();
				item.setCount(1);
				markDirty();
			}
			return ItemStack.EMPTY;
		} else {
			if (item.getItem() == workingStack.getItem() && count < getMaxStorage()) {
				int used = Math.min(workingStack.getCount(), getMaxStorage() - count);
				if (!simulate) {
					count += used;
					markDirty();
				}
				workingStack.shrink(used);
				return workingStack;
			}
		}
		return workingStack;
	}

	@Override
	public int color() {
		return hue;
	}

	public String getText() {
		if (item.isEmpty()) {
			return "Empty";
		} else if (count < item.getMaxStackSize()) {
			return count + "";
		} else {
			int numOfStacks = count / item.getMaxStackSize();

			return numOfStacks + "x" + item.getMaxStackSize();
		}
	}

	public int getMaxStorage() {
		return item.getMaxStackSize() * 64;
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		compound.put("item", item.write(new CompoundNBT()));
		compound.putInt("count", count);
		return super.write(compound);
	}

	@Override
	public void read(CompoundNBT compound) {
		item = ItemStack.read(compound.getCompound("item"));
		count = compound.getInt("count");
		super.read(compound);
	}

	@Override
	public CompoundNBT getUpdateTag() {
		CompoundNBT tag = this.write(new CompoundNBT());
		CompoundNBT item = this.item.getItem().getShareTag(this.item);
		if (this.item.hasTag()) tag.getCompound("item").put("tag", item);
		return tag;
	}

	@Nullable
	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(pos, 0, getUpdateTag());
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		read(pkt.getNbtCompound());
	}

	LazyOptional<IItemHandler> itemOpt = LazyOptional.of(() -> handler);

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return itemOpt.cast();
		return super.getCapability(cap, side);
	}

	public ItemStack getBarrelStack() {
		return this.item;
	}

	public int getCount() {
		return count;
	}
}
