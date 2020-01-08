package shadows.compatched.item;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;
import shadows.compatched.ComPatchedStorage;
import shadows.compatched.inventory.ContainerChest;
import shadows.compatched.inventory.InventoryBackpack;
import shadows.compatched.tileentity.TileEntityChest;
import shadows.compatched.util.StorageInfo;
import shadows.compatched.util.StorageInfo.Type;

public class ItemBackpack extends Item {

	public ItemBackpack() {
		super(new Item.Properties().group(ComPatchedStorage.TAB).maxStackSize(1));
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
		if (!world.isRemote) {
			int slot = hand == Hand.MAIN_HAND ? player.inventory.currentItem : 40;
			InventoryBackpack inv = new InventoryBackpack(player.getHeldItem(hand), slot);
			NetworkHooks.openGui((ServerPlayerEntity) player, inv, buf -> ContainerChest.writeChest(buf, inv).writeBlockPos(player.getPosition()));
			world.playSound(null, player.getX(), player.getY() + 1, player.getZ(), SoundEvents.BLOCK_WOOL_FALL, SoundCategory.BLOCKS, 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
		}
		return new ActionResult<ItemStack>(ActionResultType.SUCCESS, player.getHeldItem(hand));
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		return 1;
	}

	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
		if (this.isInGroup(group)) {
			ItemStack stack = new ItemStack(this);
			new TileEntityChest().write(stack.getOrCreateChildTag("BlockEntityTag"));
			items.add(stack);
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		if (stack.hasTag()) {
			StorageInfo info = new StorageInfo(0, 0, 0, Type.CHEST);
			info.deserialize(stack.getOrCreateChildTag("BlockEntityTag").getCompound("info"));
			tooltip.add(new TranslationTextComponent("compatchedstorage.text.slots", info.getSizeX() * info.getSizeY()).setStyle(new Style().setColor(TextFormatting.GREEN)));
			int hue = info.getHue();

			if (hue != -1) {
				tooltip.add(new TranslationTextComponent("compatchedstorage.text.hue2", hue).setStyle(new Style().setColor(TextFormatting.AQUA)));
			} else {
				tooltip.add(new TranslationTextComponent("compatchedstorage.text.white").setStyle(new Style().setColor(TextFormatting.AQUA)));
			}
		}
	}

	@Override
	public CompoundNBT getShareTag(ItemStack stack) {
		CompoundNBT tag = super.getShareTag(stack).copy();
		tag.getCompound("BlockEntityTag").remove("items");
		return tag;
	}
}
