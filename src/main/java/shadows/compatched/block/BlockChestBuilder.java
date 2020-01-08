package shadows.compatched.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.ItemStackHandler;
import shadows.compatched.tileentity.TileEntityChestBuilder;

public class BlockChestBuilder extends Block {

	public BlockChestBuilder() {
		super(Block.Properties.create(Material.IRON).sound(SoundType.METAL).hardnessAndResistance(2).harvestLevel(1).harvestTool(ToolType.PICKAXE));
	}

	@Override
	public ActionResultType onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		TileEntity te = world.getTileEntity(pos);
		if (!world.isRemote && te instanceof TileEntityChestBuilder) {
			NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) te, buf -> buf.writeBlockPos(pos));
		}
		return ActionResultType.SUCCESS;
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
