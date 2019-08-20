package com.tattyseal.compactstorage.block;

import com.tattyseal.compactstorage.tileentity.TileEntityBarrelFluid;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.world.IBlockReader;

public class BlockFluidBarrel extends BlockBarrel {

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT_MIPPED;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader reader) {
		return new TileEntityBarrelFluid();
	}
}
