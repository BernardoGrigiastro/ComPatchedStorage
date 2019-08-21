package com.tattyseal.compactstorage.block;

import com.tattyseal.compactstorage.tileentity.IBarrel;
import com.tattyseal.compactstorage.tileentity.TileEntityBarrel;
import com.tattyseal.compactstorage.util.EntityUtil;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

public class BlockBarrel extends HorizontalBlock {

	public BlockBarrel() {
		super(Block.Properties.create(Material.IRON).hardnessAndResistance(5).harvestLevel(2).harvestTool(ToolType.PICKAXE));
	}

	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		builder.add(HORIZONTAL_FACING);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return getDefaultState().with(HORIZONTAL_FACING, EntityUtil.get2dOrientation(context.getPlayer()));
	}

	@Override
	@Deprecated
	public void onBlockClicked(BlockState state, World world, BlockPos pos, PlayerEntity player) {
		super.onBlockClicked(state, world, pos, player);

		if (!world.isRemote) {
			IBarrel barrel = (IBarrel) world.getTileEntity(pos);

			if (barrel != null) {
				ItemStack stack = barrel.giveItems(player);

				if (!stack.isEmpty()) {
					ItemEntity item = new ItemEntity(world, player.posX, player.posY, player.posZ);
					item.setItem(stack);

					world.addEntity(item);
				}
			}
		}
	}

	@Override
	public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		if (player.isSneaking()) return false;

		if (!world.isRemote) {
			IBarrel barrel = (IBarrel) world.getTileEntity(pos);

			if (barrel != null) {
				if (player.getHeldItem(Hand.MAIN_HAND).isEmpty()) {
					ItemStack stack = barrel.giveItems(player);

					if (!stack.isEmpty()) {
						ItemEntity item = new ItemEntity(world, player.posX, player.posY, player.posZ);
						item.setItem(stack);

						world.addEntity(item);
					}

				} else {
					player.setHeldItem(Hand.MAIN_HAND, barrel.takeItems(player.getHeldItem(Hand.MAIN_HAND), player));
					return true;
				}
			}
		}

		return true;
	}

	@Override
	@Deprecated
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
		if (newState.getBlock() != this) {
			TileEntityBarrel barrel = (TileEntityBarrel) world.getTileEntity(pos);
			if (!world.isRemote && barrel != null) {
				int count = barrel.getCount();
				ItemStack bStack = barrel.getBarrelStack();
				while (count > 0) {
					ItemStack s = bStack.copy();
					s.setCount(Math.min(s.getMaxStackSize(), count));
					count -= s.getCount();
					Block.spawnAsEntity(world, pos, s);
				}
			}
			super.onReplaced(state, world, pos, newState, isMoving);
		}
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new TileEntityBarrel();
	}
}
