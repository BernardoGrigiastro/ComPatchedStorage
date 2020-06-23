package shadows.compatched.block;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.AbstractChestBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMerger.ICallbackWrapper;
import net.minecraft.tileentity.TileEntityMerger.ICallbackWrapper.Single;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
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
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.network.NetworkHooks;
import shadows.compatched.CompactRegistry;
import shadows.compatched.inventory.ContainerChest;
import shadows.compatched.tileentity.CompatchedChestTileEntity;
import shadows.compatched.util.EntityUtil;

public class CompatchedChestBlock extends AbstractChestBlock<CompatchedChestTileEntity> {

	public static final VoxelShape SHAPE = VoxelShapes.create(new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 0.875D, 0.9375D));

	public CompatchedChestBlock() {
		super(Block.Properties.create(Material.WOOD).sound(SoundType.WOOD).hardnessAndResistance(2).harvestLevel(1).harvestTool(ToolType.AXE), () -> CompactRegistry.CHEST_TILE);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
		return Blocks.CHEST.getDefaultState().getShape(world, pos);
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(HorizontalBlock.HORIZONTAL_FACING, ChestBlock.TYPE);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return getDefaultState().with(HorizontalBlock.HORIZONTAL_FACING, EntityUtil.get2dOrientation(context.getPlayer()).getOpposite());
	}

	@Override
	public ActionResultType onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		if (!world.isRemote) {
			TileEntity te = world.getTileEntity(pos);
			if (te instanceof CompatchedChestTileEntity) {
				CompatchedChestTileEntity chest = (CompatchedChestTileEntity) te;
				if (!player.isSneaking()) {
					NetworkHooks.openGui((ServerPlayerEntity) player, chest, buf -> ContainerChest.writeChest(buf, chest).writeBlockPos(pos));
					return ActionResultType.SUCCESS;
				} else {
					ItemStack held = player.getHeldItem(hand);
					if (!chest.isRetaining() && !held.isEmpty() && held.getItem().isIn(Tags.Items.GEMS_DIAMOND)) {
						chest.setRetaining(true);
						held.shrink(1);
						player.sendMessage(new TranslationTextComponent("compatchedstorage.msg.retain").setStyle(new Style().setColor(TextFormatting.AQUA)));
						world.playSound(null, pos, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.AMBIENT, 1, 1);
					}
				}
			}
		}
		return player.isSneaking() ? ActionResultType.PASS : ActionResultType.SUCCESS;
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new CompatchedChestTileEntity();
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.INVISIBLE;
	}

	@Override
	@Deprecated
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
		if (newState.getBlock() != this) {
			super.onReplaced(state, world, pos, newState, isMoving);
		}
	}

	@Override
	public ItemStack getItem(IBlockReader world, BlockPos pos, BlockState state) {
		ItemStack stack = new ItemStack(this);
		CompatchedChestTileEntity chest = (CompatchedChestTileEntity) world.getTileEntity(pos);
		chest.write(stack.getOrCreateChildTag("BlockEntityTag"));
		return stack;
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, Builder builder) {
		List<ItemStack> stacks = new ArrayList<>();
		ItemStack stack = new ItemStack(CompactRegistry.CHEST);
		TileEntity te = builder.get(LootParameters.BLOCK_ENTITY);

		CompatchedChestTileEntity chest = te instanceof CompatchedChestTileEntity ? (CompatchedChestTileEntity) te : new CompatchedChestTileEntity();
		if (!chest.isRetaining()) {
			for (int slot = 0; slot < chest.getItemHandler().getSlots(); slot++) {
				ItemStack s = chest.getItemHandler().getStackInSlot(slot);
				if (!s.isEmpty()) stacks.add(s);
			}
			chest.write(stack.getOrCreateChildTag("BlockEntityTag")).remove("items");
		} else chest.write(stack.getOrCreateChildTag("BlockEntityTag"));
		stacks.add(stack);
		return stacks;
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader p_196283_1_) {
		return new CompatchedChestTileEntity();
	}

	@Override
	public ICallbackWrapper<? extends ChestTileEntity> getBlockEntitySource(BlockState state, World world, BlockPos pos, boolean something) {
		return new Single<>((ChestTileEntity) world.getTileEntity(pos));
	}

}
