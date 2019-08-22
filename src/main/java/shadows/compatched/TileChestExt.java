package shadows.compatched;

import java.awt.Color;

import com.tattyseal.compactstorage.tileentity.TileEntityChest;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;

public class TileChestExt extends TileEntityChest {

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(pos, -1, getClientData());
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		return getClientData();
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		handleClientData(pkt.getNbtCompound());
	}

	@Override
	public void handleUpdateTag(NBTTagCompound tag) {
		handleClientData(tag);
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

	NBTTagCompound getClientData() {
		NBTTagCompound tag = super.writeToNBT(new NBTTagCompound());
		tag.removeTag("Items");
		return tag;
	}

	void handleClientData(NBTTagCompound tag) {
		if (tag.hasKey("facing")) {
			this.direction = EnumFacing.byIndex(tag.getInteger("facing"));
		}

		this.setRetaining(tag.getBoolean("retaining"));
		if (tag.hasKey("hue")) {
			this.info.setHue(tag.getInteger("hue"));
			this.color = this.getHue() == -1 ? Color.white : Color.getHSBColor(this.info.getHue() / 360.0F, 0.5F, 0.5F);
		} else if (tag.hasKey("color")) {
			String color;
			if (tag.getTag("color") instanceof NBTTagInt) {
				color = String.format("#%06X", 16777215 & tag.getInteger("color"));
			} else {
				color = tag.getString("color");
			}

			if (color.startsWith("0x")) {
				color = "#" + color.substring(2, color.length());
			}

			if (!color.isEmpty()) {
				float[] hsbVals = new float[3];
				this.color = Color.decode(color);
				hsbVals = Color.RGBtoHSB(this.color.getRed(), this.color.getGreen(), this.color.getBlue(), hsbVals);
				this.info.setHue((int) (hsbVals[0] * 360.0F));
				tag.removeTag("color");
			} else {
				this.color = Color.white;
				this.info.setHue(180);
				tag.removeTag("color");
			}
		}

		this.invX = tag.getInteger("invX");
		this.invY = tag.getInteger("invY");
		if (this.items.length != this.getSizeInventory()) this.items = new ItemStack[this.getSizeInventory()];

		if (tag.hasKey("Name", 8)) {
			this.setCustomName(tag.getString("Name"));
		}

	}

}