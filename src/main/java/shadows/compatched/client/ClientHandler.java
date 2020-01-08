package shadows.compatched.client;

import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import shadows.compatched.ComPatchedStorage;
import shadows.compatched.CompactRegistry;
import shadows.compatched.client.gui.GuiChest;
import shadows.compatched.client.gui.GuiChestBuilder;
import shadows.compatched.client.render.TileEntityBarrelFluidRenderer;
import shadows.compatched.client.render.TileEntityBarrelRenderer;
import shadows.compatched.client.render.TileEntityChestRenderer;

@SuppressWarnings("deprecation")
@EventBusSubscriber(value = Dist.CLIENT, modid = ComPatchedStorage.MODID, bus = Bus.MOD)
public class ClientHandler {

	@SubscribeEvent
	public static void registerModels(FMLCommonSetupEvent e) {
		DeferredWorkQueue.runLater(() -> {
			ClientRegistry.bindTileEntityRenderer(CompactRegistry.CHEST_TILE, TileEntityChestRenderer::new);
			ClientRegistry.bindTileEntityRenderer(CompactRegistry.BARREL_TILE, TileEntityBarrelRenderer::new);
			ClientRegistry.bindTileEntityRenderer(CompactRegistry.FLUID_BARREL_TILE, TileEntityBarrelFluidRenderer::new);
			ScreenManager.registerFactory(CompactRegistry.CHEST_CONTAINER, GuiChest::new);
			ScreenManager.registerFactory(CompactRegistry.BUILDER_CONTAINER, GuiChestBuilder::new);
			RenderTypeLookup.setRenderLayer(CompactRegistry.FLUID_BARREL, RenderType.getCutoutMipped());
		});
	}

	@SubscribeEvent
	public static void colors(ColorHandlerEvent.Item e) {
		DeferredWorkQueue.runLater(() -> e.getItemColors().register((stack, color) -> ComPatchedStorage.getColorFromNBT(stack), CompactRegistry.BACKPACK, CompactRegistry.CHEST));
	}

}
