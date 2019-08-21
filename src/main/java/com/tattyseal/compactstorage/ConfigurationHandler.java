package com.tattyseal.compactstorage;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import shadows.placebo.config.Configuration;

public class ConfigurationHandler {

	public static Configuration configuration;

	public static ItemStack storage;
	public static ItemStack storageBackpack;

	public static ItemStack[] primary;
	public static ItemStack[] secondary;

	public static ItemStack binder;
	public static ItemStack binderBackpack;

	public static float storageModifier;
	public static float primaryModifier;
	public static float secondaryModifier;
	public static float binderModifier;

	public static void init() {

		storage = getItemFromConfig(configuration, "chestStorage", "builder", "minecraft:chest", "This is used as the first component in the Builder when building a CHEST.");
		storageBackpack = getItemFromConfig(configuration, "backpackStorage", "builder", "minecraft:white_wool", "This is used as the first component in the Builder when building a BACKPACK.");

		primary = getItemsFromConfig(configuration, "primaryItem", "builder", new String[] { "minecraft:iron_ingot" }, "These values are used for the first material cost in the chest builder, you can add as many values as you like, it will configure itself to use all of them.");
		secondary = getItemsFromConfig(configuration, "secondaryItem", "builder", new String[] { "minecraft:iron_bars" }, "These values are used for the second material cost in the chest builder, you can add as many values as you like, it will configure itself to use all of them.");

		binder = getItemFromConfig(configuration, "chestBinder", "builder", "minecraft:clay_ball", "This is used as the binder material when making a CHEST.");
		binderBackpack = getItemFromConfig(configuration, "backpackBinder", "builder", "minecraft:string", "This is used as the binder material when making a BACKPACK.");

		storageModifier = configuration.getFloat("storage_modifier", "builder", 1F, 0F, 1F, "This determines how much of the item is required.");
		primaryModifier = configuration.getFloat("primary_modifier", "builder", 1F, 0F, 1F, "This determines how much of the item is required.");
		secondaryModifier = configuration.getFloat("secondary_modifier", "builder", 1F, 0F, 1F, "This determines how much of the item is required.");
		binderModifier = configuration.getFloat("binder_modifier", "builder", 1F, 0F, 1F, "This determines how much of the item is required.");

		configuration.setCategoryComment("builder", "Format for item names is modid:name");

		if (configuration.hasChanged()) configuration.save();

	}

	public static ItemStack getItemFromConfig(Configuration config, String key, String category, String defaultString, String comment) {
		String itemName = config.getString(key, category, defaultString, comment);

		String modId = itemName.contains(":") ? itemName.split(":", 2)[0] : "minecraft";
		String itemId = itemName.contains(":") ? itemName.split(":", 2)[1] : itemName;

		Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(modId, itemId));

		if (item == null) {
			CompactStorage.LOGGER.error("Invalid configuration entry {} in {}", itemName, key);
		}

		return new ItemStack(item);
	}

	public static ItemStack[] getItemsFromConfig(Configuration config, String key, String category, String[] defaultItems, String comment) {
		List<ItemStack> items = Lists.newArrayList();
		String[] itemNames = config.getStringList(key, category, defaultItems, comment);
		for (String itemName : itemNames) {
			String modId = itemName.contains(":") ? itemName.split(":", 2)[0] : "minecraft";
			String itemId = itemName.contains(":") ? itemName.split(":", 2)[1] : itemName;

			Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(modId, itemId));

			if (item == null) {
				CompactStorage.LOGGER.error("Invalid configuration entry {} in {}", itemName, key);
			}
			items.add(new ItemStack(item));
		}
		return items.toArray(new ItemStack[items.size()]);
	}
}
