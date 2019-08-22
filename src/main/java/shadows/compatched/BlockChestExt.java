package shadows.compatched;

import com.tattyseal.compactstorage.block.BlockChest;
import com.tattyseal.compactstorage.item.ItemBlockChest;

import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockChestExt extends BlockChest {

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileChestExt();
	}

	@Override
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
		super.getSubBlocks(itemIn, items);
		items.removeIf(s -> s.getItem() instanceof ItemBlockChest && !s.hasTagCompound());
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
		return BlockFaceShape.UNDEFINED;
	}

}
