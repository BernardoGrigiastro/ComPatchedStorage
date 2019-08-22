package com.tattyseal.compactstorage.block;

import java.util.ArrayList;
import java.util.List;

import com.tattyseal.compactstorage.CompactRegistry;
import com.tattyseal.compactstorage.inventory.ContainerChest;
import com.tattyseal.compactstorage.tileentity.TileEntityChest;
import com.tattyseal.compactstorage.util.EntityUtil;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.StateContainer;
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
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.network.NetworkHooks;

public class BlockChest extends HorizontalBlock {

	public static final VoxelShape SHAPE = VoxelShapes.create(new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 0.875D, 0.9375D));

	public BlockChest() {
		super(Block.Properties.create(Material.WOOD).sound(SoundType.WOOD).hardnessAndResistance(2).harvestLevel(1).harvestTool(ToolType.AXE));
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return SHAPE;
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(HORIZONTAL_FACING);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return getDefaultState().with(HORIZONTAL_FACING, EntityUtil.get2dOrientation(context.getPlayer()));
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
			super.onReplaced(state, world, pos, newState, isMoving);
		}
	}

	@Override
	public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, IFluidState fluid) {
		return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
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
		List<ItemStack> stacks = new ArrayList<>();
		ItemStack stack = new ItemStack(CompactRegistry.CHEST);
		TileEntity te = builder.get(LootParameters.BLOCK_ENTITY);

		TileEntityChest chest = te instanceof TileEntityChest ? (TileEntityChest) te : new TileEntityChest();
		if (!chest.isRetaining()) {
			for (int slot = 0; slot < chest.getItems().getSlots(); slot++) {
				ItemStack s = chest.getItems().getStackInSlot(slot);
				if (!s.isEmpty()) stacks.add(s);
			}
			chest.write(stack.getOrCreateChildTag("BlockEntityTag")).remove("items");
		} else chest.write(stack.getOrCreateChildTag("BlockEntityTag"));
		stacks.add(stack);
		return stacks;
	}

}
