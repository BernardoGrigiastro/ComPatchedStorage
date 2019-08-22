package shadows.compatched;

import com.tattyseal.compactstorage.item.ItemBackpack;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ItemBackpackExt extends ItemBackpack {

	@Override
	public NBTTagCompound getNBTShareTag(ItemStack stack) {
		NBTTagCompound tag = super.getNBTShareTag(stack);
		if (tag == null) return tag;
		tag = tag.copy();
		tag.removeTag("Items");
		return tag;
	}

	@Override
	public String getCreatorModId(ItemStack itemStack) {
		return ComPatchedStorage.MODID;
	}

}
