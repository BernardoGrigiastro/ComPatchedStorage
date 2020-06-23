package shadows.compatched.item;

import java.util.List;

import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import shadows.compatched.ComPatchedStorage;
import shadows.compatched.CompactRegistry;
import shadows.compatched.client.ClientHandler;
import shadows.compatched.tileentity.CompatchedChestTileEntity;
import shadows.compatched.util.StorageInfo;
import shadows.compatched.util.StorageInfo.Type;

public class ItemBlockChest extends BlockItem {

	public ItemBlockChest() {
		super(CompactRegistry.CHEST, new Item.Properties().group(ComPatchedStorage.TAB).maxStackSize(1).setISTER(() -> ClientHandler::getCCIR));
	}

	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
		if (this.isInGroup(group)) {
			ItemStack stack = new ItemStack(this);
			new CompatchedChestTileEntity().write(stack.getOrCreateChildTag("BlockEntityTag"));
			items.add(stack);
		}
	}

	@Override
	public ITextComponent getDisplayName(ItemStack stack) {
		if (stack.hasTag()) {
			StorageInfo info = new StorageInfo(0, 0, 0, Type.CHEST);
			info.deserialize(stack.getOrCreateChildTag("BlockEntityTag").getCompound("info"));
			return new TranslationTextComponent("%s (%sx%s)", new TranslationTextComponent(this.getTranslationKey(stack)), info.getSizeX(), info.getSizeY());
		}
		return super.getDisplayName(stack);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		if (stack.hasTag()) {
			if (stack.getOrCreateChildTag("BlockEntityTag").getBoolean("retaining")) {
				tooltip.add(new TranslationTextComponent("compatchedstorage.text.retaining").applyTextStyle(TextFormatting.GRAY));
			}
		}
	}

	@Override
	public CompoundNBT getShareTag(ItemStack stack) {
		CompoundNBT tag = super.getShareTag(stack).copy();
		tag.getCompound("BlockEntityTag").remove("items");
		return tag;
	}

	@Override
	protected boolean onBlockPlaced(BlockPos pos, World world, PlayerEntity player, ItemStack stack, BlockState state) {
		if (world.isRemote) {
			CompoundNBT compoundnbt = stack.getChildTag("BlockEntityTag");
			if (compoundnbt != null) {
				TileEntity tileentity = world.getTileEntity(pos);
				if (tileentity instanceof CompatchedChestTileEntity) {
					((CompatchedChestTileEntity) tileentity).getInfo().deserialize(compoundnbt.getCompound("info"));
				}
			}
		}
		return super.onBlockPlaced(pos, world, player, stack, state);
	}

}
