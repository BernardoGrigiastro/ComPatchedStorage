package shadows.compatched;

import java.util.List;

import com.tattyseal.compactstorage.tileentity.TileEntityChestBuilder;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;

public class TileBuilderExt extends TileEntityChestBuilder {

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		if (info != null) {
			List<ItemStack> list = info.getMaterialCost();
			return list.size() > slot && stack.getItem() == list.get(slot).getItem();
		}
		return false;
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return null;
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		return new NBTTagCompound();
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
	}

	@Override
	public void handleUpdateTag(NBTTagCompound tag) {
	};

	NBTTagCompound tileData;

	@Override
	public NBTTagCompound getTileData() {
		return tileData == null ? tileData = new NBTTagCompound() : tileData;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		if (this.tileData != null) tag.setTag("ForgeData", this.tileData);
		return super.writeToNBT(tag);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		if (tag.hasKey("ForgeData")) this.tileData = tag.getCompoundTag("ForgeData");
	}

}
