package com.tattyseal.compactstorage.item;

import java.util.List;

import com.tattyseal.compactstorage.CompactStorage;
import com.tattyseal.compactstorage.inventory.ContainerChest;
import com.tattyseal.compactstorage.inventory.InventoryBackpack;
import com.tattyseal.compactstorage.tileentity.TileEntityChest;
import com.tattyseal.compactstorage.util.StorageInfo;
import com.tattyseal.compactstorage.util.StorageInfo.Type;

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
import net.minecraftforge.fml.network.NetworkHooks;

/**
 * Created by Toby on 11/02/2015.
 */
public class ItemBackpack extends Item {

	public ItemBackpack() {
		super(new Item.Properties().group(CompactStorage.TAB).maxStackSize(1));
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
		if (!world.isRemote) {
			int slot = hand == Hand.MAIN_HAND ? player.inventory.currentItem : 40;
			InventoryBackpack inv = new InventoryBackpack(player.getHeldItem(hand), slot);
			NetworkHooks.openGui((ServerPlayerEntity) player, inv, buf -> ContainerChest.writeChest(buf, inv).writeBlockPos(player.getPosition()));
			world.playSound(null, player.posX, player.posY + 1, player.posZ, SoundEvents.BLOCK_WOOL_FALL, SoundCategory.BLOCKS, 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
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
	public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		if (stack.hasTag()) {
			StorageInfo info = new StorageInfo(0, 0, 0, Type.CHEST);
			info.deserialize(stack.getOrCreateChildTag("BlockEntityTag").getCompound("info"));
			tooltip.add(new TranslationTextComponent("Slots: " + info.getSizeX() * info.getSizeY()).setStyle(new Style().setColor(TextFormatting.GREEN)));
			int hue = info.getHue();

			if (hue != -1) {
				tooltip.add(new TranslationTextComponent("Hue: " + hue).setStyle(new Style().setColor(TextFormatting.AQUA)));
			} else {
				tooltip.add(new TranslationTextComponent("White").setStyle(new Style().setColor(TextFormatting.AQUA)));
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
