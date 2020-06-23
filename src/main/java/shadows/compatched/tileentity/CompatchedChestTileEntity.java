package shadows.compatched.tileentity;

import java.awt.Color;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import shadows.compatched.CompactRegistry;
import shadows.compatched.inventory.ContainerChest;
import shadows.compatched.inventory.ICompatchedInventory;
import shadows.compatched.inventory.InfoItemHandler;
import shadows.compatched.util.StorageInfo;
import shadows.placebo.recipe.VanillaPacketDispatcher;

public class CompatchedChestTileEntity extends ChestTileEntity implements ICompatchedInventory, INamedContainerProvider {

	private Color color = Color.WHITE;
	protected StorageInfo info;
	protected boolean retaining = false;
	private InfoItemHandler items;

	public CompatchedChestTileEntity(StorageInfo info) {
		super(CompactRegistry.CHEST_TILE);
		this.info = info;
		this.items = new InfoItemHandler(info);
	}

	public CompatchedChestTileEntity() {
		this(new StorageInfo(9, 3, 180, StorageInfo.Type.CHEST));
	}

	@Override
	@Nonnull
	public CompoundNBT write(CompoundNBT tag) {
		super.write(tag);
		tag.put("info", info.serialize());
		tag.putBoolean("retaining", retaining);
		tag.put("items", getItemHandler().serializeNBT());
		return tag;
	}

	@Override
	public void read(CompoundNBT tag) {
		super.read(tag);
		this.retaining = tag.getBoolean("retaining");
		this.info.deserialize(tag.getCompound("info"));
		this.getItemHandler().deserializeNBT(tag.getCompound("items"));
		this.color = getHue() == -1 ? Color.white : Color.getHSBColor(info.getHue() / 360f, 0.5f, 0.5f);
	}

	@Override
	@Nonnull
	public CompoundNBT getUpdateTag() {
		CompoundNBT tag = super.getUpdateTag();
		tag.put("info", info.serialize());
		tag.putBoolean("retaining", retaining);
		return tag;
	}

	@Override
	public void handleUpdateTag(CompoundNBT tag) {
		this.retaining = tag.getBoolean("retaining");
		this.info.deserialize(tag.getCompound("info"));
		this.getItemHandler().setSize(info.getSizeX() * info.getSizeY());
		this.color = getHue() == -1 ? Color.white : Color.getHSBColor(info.getHue() / 360f, 0.5f, 0.5f);
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(pos, -1, getUpdateTag());
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		handleUpdateTag(pkt.getNbtCompound());
	}

	@Override
	public StorageInfo getInfo() {
		return info;
	}

	@Override
	public int getHue() {
		return info.getHue();
	}

	@Override
	public void setHue(int hue) {
		info.setHue(hue);
	}

	LazyOptional<IItemHandler> itemOpt = LazyOptional.of(() -> items);

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return itemOpt.cast();
		return super.getCapability(cap, side);
	}

	public Color getColor() {
		return color;
	}

	@Override
	public ItemStackHandler getItemHandler() {
		return items;
	}

	public boolean isRetaining() {
		return this.retaining;
	}

	public void setRetaining(boolean retain) {
		this.retaining = retain;
		VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
		markDirty();
	}

	@Override
	public Container createMenu(int id, PlayerInventory inv, PlayerEntity player) {
		return new ContainerChest(id, world, this, player, pos);
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent(CompactRegistry.CHEST.getTranslationKey());
	}

	@Override
	public void onOpen(PlayerEntity player) {
		this.openInventory(player);
	}

	@Override
	public void onClose(PlayerEntity player) {
		this.closeInventory(player);
	}

	@Override
	public TileEntityType<?> getType() {
		return CompactRegistry.CHEST_TILE;
	}
}
