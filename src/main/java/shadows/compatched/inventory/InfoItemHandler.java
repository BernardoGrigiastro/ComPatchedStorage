package shadows.compatched.inventory;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.items.ItemStackHandler;
import shadows.compatched.util.StorageInfo;

public class InfoItemHandler extends ItemStackHandler {

	protected StorageInfo info;

	public InfoItemHandler(StorageInfo info) {
		super(info.getSizeX() * info.getSizeY());
		this.info = info;
	}

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT tag = super.serializeNBT();
		tag.remove("Size");
		return tag;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		setSize(info.getSizeX() * info.getSizeY());
		super.deserializeNBT(nbt);
	}

}
