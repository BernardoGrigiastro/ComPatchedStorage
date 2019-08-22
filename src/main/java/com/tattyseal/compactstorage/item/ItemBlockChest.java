package com.tattyseal.compactstorage.item;

import java.util.List;

import com.tattyseal.compactstorage.CompactRegistry;
import com.tattyseal.compactstorage.CompactStorage;
import com.tattyseal.compactstorage.tileentity.TileEntityChest;
import com.tattyseal.compactstorage.util.StorageInfo;
import com.tattyseal.compactstorage.util.StorageInfo.Type;

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

/**
 * Created by Toby on 06/11/2014.
 */
public class ItemBlockChest extends BlockItem {

	public ItemBlockChest() {
		super(CompactRegistry.CHEST, new Item.Properties().group(CompactStorage.TAB).maxStackSize(1));
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
			tooltip.add(new TranslationTextComponent("compactstorage.text.slots", info.getSizeX() * info.getSizeY()).setStyle(new Style().setColor(TextFormatting.GREEN)));
			int hue = info.getHue();

			if (hue != -1) {
				tooltip.add(new TranslationTextComponent("compactstorage.text.hue2", hue).setStyle(new Style().setColor(TextFormatting.AQUA)));
			} else {
				tooltip.add(new TranslationTextComponent("compactstorage.text.white").setStyle(new Style().setColor(TextFormatting.AQUA)));
			}

			if (stack.getOrCreateChildTag("BlockEntityTag").getBoolean("retaining")) {
				tooltip.add(new TranslationTextComponent("compactstorage.text.retaining").setStyle(new Style().setColor(TextFormatting.AQUA).setItalic(true)));
			} else {
				tooltip.add(new TranslationTextComponent("compactstorage.text.nonretaining").setStyle(new Style().setColor(TextFormatting.RED).setItalic(true)));
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
