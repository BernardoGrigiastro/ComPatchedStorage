package shadows.compatched.block;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;
import shadows.compatched.tileentity.TileEntityBarrelFluid;

public class BlockFluidBarrel extends BlockBarrel {

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader reader) {
		return new TileEntityBarrelFluid();
	}

}
