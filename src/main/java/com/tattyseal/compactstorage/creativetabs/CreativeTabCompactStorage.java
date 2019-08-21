package com.tattyseal.compactstorage.creativetabs;

import javax.annotation.Nonnull;

import com.tattyseal.compactstorage.CompactRegistry;
import com.tattyseal.compactstorage.tileentity.TileEntityChest;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class CreativeTabCompactStorage extends ItemGroup {

	public CreativeTabCompactStorage() {
		super("compactStorage");
	}

	@Override
	@Nonnull
	public ItemStack createIcon() {
		ItemStack stack = new ItemStack(CompactRegistry.CHEST);
		new TileEntityChest().write(stack.getOrCreateChildTag("BlockEntityTag"));
		return stack;
	}
}
