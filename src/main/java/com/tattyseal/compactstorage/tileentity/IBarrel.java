package com.tattyseal.compactstorage.tileentity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public interface IBarrel {

	public ItemStack giveItems(PlayerEntity player);

	public ItemStack takeItems(ItemStack stack, PlayerEntity player);

	public int color();
}
