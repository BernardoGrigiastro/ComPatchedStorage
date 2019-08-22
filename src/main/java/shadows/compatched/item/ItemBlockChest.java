package shadows.compatched.item;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import shadows.compatched.ComPatchedStorage;
import shadows.compatched.CompactRegistry;
import shadows.compatched.tileentity.TileEntityChest;
import shadows.compatched.util.StorageInfo;
import shadows.compatched.util.StorageInfo.Type;

public class ItemBlockChest extends BlockItem {

	public ItemBlockChest() {
		super(CompactRegistry.CHEST, new Item.Properties().group(ComPatchedStorage.TAB).maxStackSize(1));
	}

	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
		if (this.isInGroup(group)) {
			ItemStack stack = new ItemStack(this);
			new TileEntityChest().write(stack.getOrCreateChildTag("BlockEntityTag"));
			items.add(stack);
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		if (stack.hasTag()) {
			StorageInfo info = new StorageInfo(0, 0, 0, Type.CHEST);
			info.deserialize(stack.getOrCreateChildTag("BlockEntityTag").getCompound("info"));
			tooltip.add(new TranslationTextComponent("compatchedstorage.text.slots", info.getSizeX() * info.getSizeY()).setStyle(new Style().setColor(TextFormatting.GREEN)));
			int hue = info.getHue();

			if (hue != -1) {
				tooltip.add(new TranslationTextComponent("compatchedstorage.text.hue2", hue).setStyle(new Style().setColor(TextFormatting.AQUA)));
			} else {
				tooltip.add(new TranslationTextComponent("compatchedstorage.text.white").setStyle(new Style().setColor(TextFormatting.AQUA)));
			}

			if (stack.getOrCreateChildTag("BlockEntityTag").getBoolean("retaining")) {
				tooltip.add(new TranslationTextComponent("compatchedstorage.text.retaining").setStyle(new Style().setColor(TextFormatting.AQUA).setItalic(true)));
			} else {
				tooltip.add(new TranslationTextComponent("compatchedstorage.text.nonretaining").setStyle(new Style().setColor(TextFormatting.RED).setItalic(true)));
			}
		}
	}

	@Override
	public CompoundNBT getShareTag(ItemStack stack) {
		CompoundNBT tag = super.getShareTag(stack).copy();
		tag.getCompound("BlockEntityTag").remove("items");
		return tag;
	}
}
