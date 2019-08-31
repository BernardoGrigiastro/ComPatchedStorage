package shadows.compatched.tileentity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import shadows.compatched.CompactRegistry;
import shadows.placebo.recipe.VanillaPacketDispatcher;

public class TileEntityBarrelFluid extends TileEntity implements IBarrel, ITickableTileEntity {

	public FluidTank tank = new FluidTank(32000);
	public int hue;

	public int lastAmount;

	public TileEntityBarrelFluid() {
		super(CompactRegistry.FLUID_BARREL_TILE);
		hue = 128;
	}

	@Override
	public ItemStack giveItems(PlayerEntity player) {
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack takeItems(@Nonnull ItemStack stack, PlayerEntity player) {
		FluidActionResult res = FluidUtil.tryEmptyContainerAndStow(stack, tank, null, tank.getCapacity(), player, true);

		if (res.isSuccess()) {
			return res.result;
		} else {
			res = FluidUtil.tryFillContainerAndStow(stack, tank, null, tank.getCapacity(), player, true);
			if (res.isSuccess()) { return res.result; }
		}

		return stack;
	}

	@Override
	public int color() {
		return hue;
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		compound.put("fluid", tank.writeToNBT(new CompoundNBT()));
		compound.putInt("hue", hue);
		return super.write(compound);
	}

	@Override
	public void read(CompoundNBT compound) {
		tank.readFromNBT(compound.getCompound("fluid"));
		hue = compound.getInt("hue");
		super.read(compound);
	}

	@Override
	public CompoundNBT getUpdateTag() {
		return write(new CompoundNBT());
	}

	@Nullable
	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT tag = new CompoundNBT();
		tag.put("fluid", tank.getFluid().writeToNBT(new CompoundNBT()));
		return new SUpdateTileEntityPacket(pos, 0, tag);
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		tank.setFluid(FluidStack.loadFluidStackFromNBT(pkt.getNbtCompound().getCompound("fluid")));
	}

	LazyOptional<IFluidHandler> fluidOpt = LazyOptional.of(() -> tank);

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) return fluidOpt.cast();
		return super.getCapability(cap, side);
	}

	@Override
	public void tick() {
		if (world.isRemote) return;
		if (lastAmount != tank.getFluidAmount()) {
			markDirty();
		}

		lastAmount = tank.getFluid().isEmpty() ? 0 : tank.getFluidAmount();
	}

	public String getText() {
		if (tank.getFluid().isEmpty() || tank.getFluidAmount() == 0) {
			return I18n.format("compatchedstorage.text.empty");
		} else {
			return I18n.format("compatchedstorage.text.fluidformat", tank.getFluidAmount(), 32000);
		}
	}

	@Override
	public void markDirty() {
		super.markDirty();
		VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
	}
}
