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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import shadows.compatched.CompactRegistry;

public class TileEntityBarrelFluid extends TileEntity implements IBarrel, ITickableTileEntity {
	public static final int CAPACITY = 32000;

	public FluidTank tank;
	public int hue;

	public int lastAmount;

	public TileEntityBarrelFluid() {
		super(CompactRegistry.FLUID_BARREL_TILE);
		tank = new FluidTank(CAPACITY);
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

		markDirty();

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
		tank = new FluidTank(CAPACITY);
		tank.readFromNBT(compound.getCompound("fluid"));
		hue = compound.getInt("hue");
		super.read(compound);
	}

	@Override
	public CompoundNBT getUpdateTag() {
		CompoundNBT tag = super.getUpdateTag();
		write(tag);
		return tag;
	}

	@Nullable
	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(pos, 0, write(new CompoundNBT()));
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		read(pkt.getNbtCompound());
	}

	LazyOptional<IFluidHandler> fluidOpt = LazyOptional.of(() -> tank);

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) return fluidOpt.cast();
		return super.getCapability(cap, side);
	}

	@Override
	public void tick() {
		if (tank != null) {
			if (tank.getFluid() != null && lastAmount != tank.getFluidAmount()) {
				markDirty();
			}

			lastAmount = tank.getFluid() == null ? 0 : tank.getFluidAmount();
		}
	}

	@OnlyIn(Dist.CLIENT)
	public String getText() {
		if (tank == null || tank.getFluid() == null || tank.getFluidAmount() == 0) {
			return I18n.format("compatchedstorage.text.empty");
		} else {
			return I18n.format("compatchedstorage.text.fluidformat", tank.getFluidAmount(), CAPACITY);
		}
	}
}
