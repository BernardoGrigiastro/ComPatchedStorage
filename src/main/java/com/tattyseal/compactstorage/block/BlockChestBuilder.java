package com.tattyseal.compactstorage.block;

import com.tattyseal.compactstorage.CompactStorage;
import com.tattyseal.compactstorage.tileentity.TileEntityChestBuilder;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.items.ItemStackHandler;

public class BlockChestBuilder extends Block {

	public BlockChestBuilder() {
		super(Block.Properties.create(Material.IRON).hardnessAndResistance(2).harvestLevel(1).harvestTool(ToolType.PICKAXE));
	}

	@Override
	public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		if (!player.isSneaking()) {
			if (!world.isRemote) {
				player.openGui(CompactStorage.instance, 1, world, pos.getX(), pos.getY(), pos.getZ());
			}

			return true;
		}
		return false;
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new TileEntityChestBuilder();
	}

	@Override
	@Deprecated
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
		TileEntity chest = world.getTileEntity(pos);
		if (!(chest instanceof TileEntityChestBuilder)) return;

		ItemStackHandler items = ((TileEntityChestBuilder) chest).getItems();

		for (int slot = 0; slot < items.getSlots(); slot++) {
			Block.spawnAsEntity(world, pos, items.getStackInSlot(slot));
		}

		super.onReplaced(state, world, pos, newState, isMoving);
	}
}
