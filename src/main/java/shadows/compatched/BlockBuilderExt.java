package shadows.compatched;

import com.tattyseal.compactstorage.block.BlockChestBuilder;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockBuilderExt extends BlockChestBuilder {

	@Override
	public TileEntity createNewTileEntity(World world, int dim) {
		return new TileBuilderExt();
	}

}
