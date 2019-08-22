package shadows.compatched;

import com.tattyseal.compactstorage.CompactStorage;
import com.tattyseal.compactstorage.item.ItemBlockChest;
import com.tattyseal.compactstorage.util.LogHelper;

import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

@Mod(modid = ComPatchedStorage.MODID, name = ComPatchedStorage.MODNAME, version = ComPatchedStorage.VERSION, dependencies = "required-after:compactstorage@[3.1,4.0)", acceptableRemoteVersions = "[1.6.0,)")
public class ComPatchedStorage {

	public static final String MODID = "compatched";
	public static final String MODNAME = "ComPatchedStorage";
	public static final String VERSION = "1.6.0";

	@EventHandler
	public void preInit(FMLPreInitializationEvent e) throws Exception {
		MinecraftForge.EVENT_BUS.register(this);
		EnumHelper.setFailsafeFieldValue(LogHelper.class.getField("logger"), null, new DummyLogger());

	}

	@SubscribeEvent
	public void caps(AttachCapabilitiesEvent<TileEntity> e) {
		TileEntity te = e.getObject();
		if (te instanceof TileChestExt) {
			e.addCapability(new ResourceLocation(MODID, "invwrapper"), new InvWrappingCap((IInventory) te));
		}
	}

	@SubscribeEvent
	public void blocks(Register<Block> e) {
		ModContainer us = Loader.instance().activeModContainer();
		Loader.instance().setActiveModContainer(Loader.instance().getModList().stream().filter(mc -> mc.getModId().equals(CompactStorage.ID)).findFirst().get());
		CompactStorage.ModBlocks.chest = new BlockChestExt();
		CompactStorage.ModBlocks.chestBuilder = new BlockBuilderExt();
		Loader.instance().setActiveModContainer(us);
		e.getRegistry().register(CompactStorage.ModBlocks.chest);
		e.getRegistry().register(CompactStorage.ModBlocks.chestBuilder);
		GameRegistry.registerTileEntity(TileChestExt.class, new ResourceLocation("tileChest"));
		GameRegistry.registerTileEntity(TileBuilderExt.class, new ResourceLocation("tileChestBuilder"));
	}

	@SubscribeEvent
	public void items(Register<Item> e) {
		CompactStorage.ModItems.backpack = new ItemBackpackExt().setRegistryName(CompactStorage.ID, "backpack");
		e.getRegistry().register(CompactStorage.ModItems.backpack);

		CompactStorage.ModItems.ibChest = new ItemBlockChest(CompactStorage.ModBlocks.chest) {
			@Override
			public String getCreatorModId(net.minecraft.item.ItemStack itemStack) {
				return MODID;
			}
		};
		CompactStorage.ModItems.ibChest.setRegistryName(CompactStorage.ID, "compactChest");
		e.getRegistry().register(CompactStorage.ModItems.ibChest);

		Item ibChestBuilder = new ItemBlock(CompactStorage.ModBlocks.chestBuilder) {
			@Override
			public String getCreatorModId(ItemStack itemStack) {
				return MODID;
			}
		}.setRegistryName(CompactStorage.ID, "chestBuilder").setCreativeTab(CompactStorage.tabCS);
		e.getRegistry().register(ibChestBuilder);
	}

	private static class InvWrappingCap implements ICapabilityProvider {

		InvWrapper wrapped;

		InvWrappingCap(IInventory toWrap) {
			wrapped = new InvWrapper(toWrap);
		}

		@Override
		public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
			return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
		}

		@Override
		public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
			if (this.hasCapability(capability, facing)) return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(wrapped);
			return null;
		}

	}

}
