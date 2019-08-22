package shadows.compatched.inventory.slot;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;

public class SlotImmovable extends Slot {
	private boolean immovable;

	public SlotImmovable(IInventory inventory, int id, int x, int y, boolean immovable) {
		super(inventory, id, x, y);
		this.immovable = immovable;
	}

	@Override
	public boolean canTakeStack(PlayerEntity player) {
		return !immovable;
	}
}
