package com.tattyseal.compactstorage.block;

import java.util.Collections;
import java.util.List;

import com.tattyseal.compactstorage.CompactRegistry;
import com.tattyseal.compactstorage.inventory.ContainerChest;
import com.tattyseal.compactstorage.tileentity.TileEntityChest;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootContext.Builder;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.network.NetworkHooks;

public class BlockChest extends Block {

	public static final VoxelShape SHAPE = VoxelShapes.create(new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 0.875D, 0.9375D));

	public BlockChest() {
		super(Block.Properties.create(Material.WOOD).hardnessAndResistance(2).harvestLevel(1).harvestTool(ToolType.AXE));
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return SHAPE;
	}

	@Override
	public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		if (!world.isRemote) {
			TileEntity te = world.getTileEntity(pos);
			if (te instanceof TileEntityChest) {
				TileEntityChest chest = (TileEntityChest) te;
				if (!player.isSneaking()) {
					NetworkHooks.openGui((ServerPlayerEntity) player, chest, buf -> ContainerChest.writeChest(buf, chest).writeBlockPos(pos));
					return true;
				} else {
					ItemStack held = player.getHeldItem(hand);
					if (!chest.isRetaining() && !held.isEmpty() && held.getItem() == Items.DIAMOND) {
						chest.setRetaining(true);
						held.setCount(held.getCount() - 1);
						player.sendMessage(new TranslationTextComponent("Chest will now retain items when broken!").setStyle(new Style().setColor(TextFormatting.AQUA)));
						world.playSound(null, pos, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.AMBIENT, 1, 1);
						chest.updateBlock();
					}
				}
			}
		}
		return !player.isSneaking();
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new TileEntityChest();
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.INVISIBLE;
	}

	@Override
	@Deprecated
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
		if (newState.getBlock() != this) {
			TileEntityChest chest = (TileEntityChest) world.getTileEntity(pos);

			if (!world.isRemote && chest != null) {
				ItemStack stack = new ItemStack(CompactRegistry.CHEST);

				if (chest.isRetaining()) {
					chest.write(stack.getOrCreateChildTag("BlockEntityTag"));
				} else {
					for (int slot = 0; slot < chest.getItems().getSlots(); slot++) {
						Block.spawnAsEntity(world, pos, chest.getItems().getStackInSlot(slot));
					}
					chest.write(stack.getOrCreateChildTag("BlockEntityTag")).remove("items");
				}

				world.addEntity(new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack));
			}

			super.onReplaced(state, world, pos, newState, isMoving);
		}
	}

	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
		super.fillItemGroup(group, items);
	}

	@Override
	public ItemStack getItem(IBlockReader world, BlockPos pos, BlockState state) {
		ItemStack stack = new ItemStack(this);
		TileEntityChest chest = (TileEntityChest) world.getTileEntity(pos);
		chest.write(stack.getOrCreateChildTag("BlockEntityTag"));
		return stack;
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, Builder builder) {
		return Collections.emptyList();
	}

}
