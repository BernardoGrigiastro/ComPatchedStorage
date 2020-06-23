package shadows.compatched.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;
import shadows.compatched.util.StorageInfo;
import shadows.compatched.util.StorageInfo.Type;

public class InventoryBackpack implements ICompatchedInventory, INamedContainerProvider {

	protected ItemStack backpack;
	protected int slot;
	protected StorageInfo info = new StorageInfo(0, 0, 0, Type.BACKPACK);
	protected InfoItemHandler items = new InfoItemHandler(info);

	public InventoryBackpack(ItemStack stack, int slot) {
		this.backpack = stack;
		read(this.backpack.getOrCreateChildTag("BlockEntityTag"));
		this.slot = slot;
	}

	@Override
	public StorageInfo getInfo() {
		return info;
	}

	@Override
	public void onOpen(PlayerEntity player) {
	}

	@Override
	public void onClose(PlayerEntity player) {
		write(backpack.getOrCreateChildTag("BlockEntityTag"));
		World world = player.world;
		BlockPos pos = player.getPosition();
		if (!world.isRemote) world.playSound(null, pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5d, SoundEvents.BLOCK_WOOL_STEP, SoundCategory.BLOCKS, 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
	}

	@Override
	public int getHue() {
		return info.getHue();
	}

	@Override
	public void setHue(int hue) {
		info.setHue(hue);
	}

	@Override
	public ItemStackHandler getItemHandler() {
		return items;
	}

	private void write(CompoundNBT tag) {
		tag.put("info", info.serialize());
		tag.put("items", items.serializeNBT());
	}

	private void read(CompoundNBT tag) {
		this.info.deserialize(tag.getCompound("info"));
		this.items.deserializeNBT(tag.getCompound("items"));
	}

	@Override
	public Container createMenu(int id, PlayerInventory inv, PlayerEntity player) {
		return new ContainerChest(id, player.world, this, player, player.getPosition());
	}

	@Override
	public ITextComponent getDisplayName() {
		return backpack.getDisplayName();
	}
}
