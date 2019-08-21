package com.tattyseal.compactstorage.inventory;

import com.tattyseal.compactstorage.util.StorageInfo;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.items.ItemStackHandler;

public interface IChest {

	int getInvX();

	int getInvY();

	StorageInfo getInfo();

	int getHue();

	void setHue(int hue);

	void onOpened(PlayerEntity player);

	void onClosed(PlayerEntity player);

	ItemStackHandler getItems();
}
