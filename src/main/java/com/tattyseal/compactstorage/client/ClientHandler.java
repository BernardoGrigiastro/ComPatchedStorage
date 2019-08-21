package com.tattyseal.compactstorage.client;

import com.tattyseal.compactstorage.CompactRegistry;
import com.tattyseal.compactstorage.CompactStorage;
import com.tattyseal.compactstorage.client.gui.GuiChest;
import com.tattyseal.compactstorage.client.gui.GuiChestBuilder;
import com.tattyseal.compactstorage.client.render.TileEntityBarrelFluidRenderer;
import com.tattyseal.compactstorage.client.render.TileEntityBarrelRenderer;
import com.tattyseal.compactstorage.client.render.TileEntityChestRenderer;
import com.tattyseal.compactstorage.tileentity.TileEntityBarrel;
import com.tattyseal.compactstorage.tileentity.TileEntityBarrelFluid;
import com.tattyseal.compactstorage.tileentity.TileEntityChest;

import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@EventBusSubscriber(value = Dist.CLIENT, modid = CompactStorage.MODID, bus = Bus.MOD)
public class ClientHandler {

	@SubscribeEvent
	public static void registerModels(FMLCommonSetupEvent e) {
		DeferredWorkQueue.runLater(() -> {
			ClientRegistry.bindTileEntitySpecialRenderer(TileEntityChest.class, new TileEntityChestRenderer());
			ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBarrel.class, new TileEntityBarrelRenderer());
			ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBarrelFluid.class, new TileEntityBarrelFluidRenderer());
			ScreenManager.registerFactory(CompactRegistry.CHEST_CONTAINER, GuiChest::new);
			ScreenManager.registerFactory(CompactRegistry.BUILDER_CONTAINER, GuiChestBuilder::new);
		});
	}

	@SubscribeEvent
	public static void colors(ColorHandlerEvent.Item e) {
		e.getItemColors().register((stack, color) -> CompactStorage.getColorFromNBT(stack), CompactRegistry.BACKPACK, CompactRegistry.CHEST);
	}

}
