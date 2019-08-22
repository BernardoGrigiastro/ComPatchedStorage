package shadows.compatched;

import com.google.common.collect.ImmutableSet;

import net.minecraft.block.Block;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.network.IContainerFactory;
import net.minecraftforge.registries.ObjectHolder;
import shadows.compatched.block.BlockBarrel;
import shadows.compatched.block.BlockChest;
import shadows.compatched.block.BlockChestBuilder;
import shadows.compatched.block.BlockFluidBarrel;
import shadows.compatched.inventory.ContainerChest;
import shadows.compatched.inventory.ContainerChestBuilder;
import shadows.compatched.item.ItemBackpack;
import shadows.compatched.item.ItemBlockChest;
import shadows.compatched.tileentity.TileEntityBarrel;
import shadows.compatched.tileentity.TileEntityBarrelFluid;
import shadows.compatched.tileentity.TileEntityChest;
import shadows.compatched.tileentity.TileEntityChestBuilder;

@ObjectHolder(ComPatchedStorage.MODID)
@EventBusSubscriber(modid = ComPatchedStorage.MODID, bus = Bus.MOD)
public class CompactRegistry {

	public static final BlockChest CHEST = null;
	public static final BlockChestBuilder CHEST_BUILDER = null;
	public static final BlockBarrel BARREL = null;
	public static final BlockFluidBarrel FLUID_BARREL = null;
	public static final ItemBackpack BACKPACK = null;
	public static final TileEntityType<TileEntityChest> CHEST_TILE = null;
	public static final TileEntityType<TileEntityChestBuilder> BUILDER_TILE = null;
	public static final TileEntityType<TileEntityBarrel> BARREL_TILE = null;
	public static final TileEntityType<TileEntityBarrelFluid> FLUID_BARREL_TILE = null;
	public static final ContainerType<ContainerChest> CHEST_CONTAINER = null;
	public static final ContainerType<ContainerChestBuilder> BUILDER_CONTAINER = null;

	@SubscribeEvent
	public static void blocks(Register<Block> e) {
		e.getRegistry().registerAll(new BlockChest().setRegistryName("chest"), new BlockChestBuilder().setRegistryName("chest_builder"), new BlockBarrel().setRegistryName("barrel"), new BlockFluidBarrel().setRegistryName("fluid_barrel"));
	}

	@SubscribeEvent
	public static void items(Register<Item> e) {
		Item i = new ItemBlockChest().setRegistryName(CHEST.getRegistryName());
		e.getRegistry().register(i);

		i = new BlockItem(CHEST_BUILDER, new Item.Properties().group(ComPatchedStorage.TAB));
		i.setRegistryName(CHEST_BUILDER.getRegistryName());
		e.getRegistry().register(i);

		i = new ItemBackpack().setRegistryName("backpack");
		e.getRegistry().register(i);

		i = new BlockItem(BARREL, new Item.Properties().group(ComPatchedStorage.TAB));
		i.setRegistryName(BARREL.getRegistryName());
		e.getRegistry().register(i);

		i = new BlockItem(FLUID_BARREL, new Item.Properties().group(ComPatchedStorage.TAB));
		i.setRegistryName(FLUID_BARREL.getRegistryName());
		e.getRegistry().register(i);
	}

	@SubscribeEvent
	public static void tiles(Register<TileEntityType<?>> e) {
		e.getRegistry().register(new TileEntityType<>(TileEntityChest::new, ImmutableSet.of(CHEST), null).setRegistryName("chest_tile"));
		e.getRegistry().register(new TileEntityType<>(TileEntityChestBuilder::new, ImmutableSet.of(CHEST_BUILDER), null).setRegistryName("builder_tile"));
		e.getRegistry().register(new TileEntityType<>(TileEntityBarrel::new, ImmutableSet.of(BARREL), null).setRegistryName("barrel_tile"));
		e.getRegistry().register(new TileEntityType<>(TileEntityBarrelFluid::new, ImmutableSet.of(FLUID_BARREL), null).setRegistryName("fluid_barrel_tile"));
	}

	@SubscribeEvent
	public static void containers(Register<ContainerType<?>> e) {
		e.getRegistry().register(new ContainerType<>(cf(ContainerChest::new)).setRegistryName("chest_container"));
		e.getRegistry().register(new ContainerType<>(cf(ContainerChestBuilder::new)).setRegistryName("builder_container"));
	}

	static <T extends Container> IContainerFactory<T> cf(IContainerFactory<T> fac) {
		return fac;
	}

}
