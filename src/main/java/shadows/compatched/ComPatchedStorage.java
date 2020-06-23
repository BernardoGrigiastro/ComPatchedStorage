package shadows.compatched;

import java.awt.Color;
import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import shadows.compatched.block.CompatchedChestBlock;
import shadows.compatched.creativetabs.CreativeTabCompactStorage;
import shadows.compatched.packet.MessageCraftChest;
import shadows.compatched.packet.MessageUpdateBuilder;
import shadows.compatched.util.StorageInfo;
import shadows.placebo.config.Configuration;
import shadows.placebo.loot.LootSystem;
import shadows.placebo.recipe.RecipeHelper;
import shadows.placebo.util.NetworkUtils;

@Mod(ComPatchedStorage.MODID)
public class ComPatchedStorage {

	public static final String MODID = "compatchedstorage";
	public static final ItemGroup TAB = new CreativeTabCompactStorage();
	public static final Logger LOGGER = LogManager.getLogger(MODID);
	public static final RecipeHelper HELPER = new RecipeHelper(MODID);
	//Formatter::off
    public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(MODID, MODID))
            .clientAcceptedVersions(s->true)
            .serverAcceptedVersions(s->true)
            .networkProtocolVersion(() -> "1.0.0")
            .simpleChannel();
    //Formatter::on

	public ComPatchedStorage() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		MinecraftForge.EVENT_BUS.addListener(this::onPlayerInteract);
	}

	@SubscribeEvent
	public void setup(FMLCommonSetupEvent e) {
		ConfigurationHandler.configuration = new Configuration(new File(FMLPaths.CONFIGDIR.get().toFile(), MODID + ".cfg"));
		NetworkUtils.registerMessage(CHANNEL, 0, new MessageUpdateBuilder());
		NetworkUtils.registerMessage(CHANNEL, 1, new MessageCraftChest());
		HELPER.addShaped(CompactRegistry.CHEST_BUILDER, 3, 3, Items.IRON_INGOT, Blocks.LEVER, Items.IRON_INGOT, Items.IRON_INGOT, Blocks.CHEST, Items.IRON_INGOT, Items.IRON_INGOT, Blocks.LEVER, Items.IRON_INGOT);
		HELPER.addShaped(CompactRegistry.BARREL, 3, 3, Items.IRON_INGOT, Items.IRON_INGOT, Items.IRON_INGOT, Blocks.IRON_BLOCK, Blocks.CHEST, Blocks.IRON_BLOCK, Items.IRON_INGOT, Items.IRON_INGOT, Items.IRON_INGOT);
		HELPER.addShaped(CompactRegistry.FLUID_BARREL, 3, 3, Items.IRON_INGOT, Blocks.GLASS_PANE, Items.IRON_INGOT, Blocks.IRON_BLOCK, Items.IRON_INGOT, Blocks.IRON_BLOCK, Items.IRON_INGOT, Blocks.GLASS_PANE, Items.IRON_INGOT);
		ConfigurationHandler.init();
		LootSystem.defaultBlockTable(CompactRegistry.CHEST_BUILDER);
	}

	@SubscribeEvent
	public void onPlayerInteract(PlayerInteractEvent.RightClickBlock event) {
		BlockState state = event.getWorld().getBlockState(event.getPos());
		Block block = state.getBlock();

		if (block instanceof CompatchedChestBlock && event.getPlayer().getHeldItem(event.getHand()).getItem() == Items.DIAMOND) {
			state.onUse(event.getWorld(), event.getPlayer(), event.getHand(), BlockRayTraceResult.createMiss(null, null, event.getPos()));
			event.setCanceled(true);
			event.setCancellationResult(ActionResultType.SUCCESS);
		}
	}

	public static int getColorFromHue(int hue) {
		Color color = hue == -1 ? Color.white : Color.getHSBColor(hue / 360f, 0.5f, 0.5f).brighter();
		return color.getRGB();
	}

	public static int getColorFromNBT(ItemStack stack) {
		StorageInfo info = new StorageInfo(0, 0, 0, null);
		info.deserialize(stack.getOrCreateChildTag("BlockEntityTag").getCompound("info"));
		return getColorFromHue(info.getHue());
	}
}
