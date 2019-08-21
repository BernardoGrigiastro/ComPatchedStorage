package com.tattyseal.compactstorage.item;

import java.util.List;

import com.tattyseal.compactstorage.CompactStorage;
import com.tattyseal.compactstorage.tileentity.TileEntityChest;
import com.tattyseal.compactstorage.util.StorageInfo;
import com.tattyseal.compactstorage.util.StorageInfo.Type;

import net.minecraft.block.Block;
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

/**
 * Created by Toby on 06/11/2014.
 */
public class ItemBlockChest extends BlockItem {

	public ItemBlockChest(Block block) {
		super(block, new Item.Properties().group(CompactStorage.TAB).maxStackSize(1));
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
	public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> list, ITooltipFlag flagIn) {
		if (stack.hasTag()) {
			StorageInfo info = new StorageInfo(0, 0, 0, Type.CHEST);
			info.deserialize(stack.getOrCreateChildTag("BlockEntityTag").getCompound("info"));
			list.add(new TranslationTextComponent("Slots: " + info.getSizeX() * info.getSizeY()).setStyle(new Style().setColor(TextFormatting.GREEN)));
			int hue = info.getHue();

			if (hue != -1) {
				list.add(new TranslationTextComponent("Hue: " + hue).setStyle(new Style().setColor(TextFormatting.AQUA)));
			} else {
				list.add(new TranslationTextComponent("White").setStyle(new Style().setColor(TextFormatting.AQUA)));
			}

			if (stack.getOrCreateChildTag("BlockEntityTag").getBoolean("retaining")) {
				list.add(new TranslationTextComponent("Retaining").setStyle(new Style().setColor(TextFormatting.AQUA).setItalic(true)));
			} else {
				list.add(new TranslationTextComponent("Non-Retaining").setStyle(new Style().setColor(TextFormatting.RED).setItalic(true)));
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
