package shadows.compatched.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.items.ItemStackHandler;
import shadows.compatched.util.StorageInfo;

public interface ICompatchedInventory {

	StorageInfo getInfo();

	int getHue();

	void setHue(int hue);

	ItemStackHandler getItemHandler();

	void onOpen(PlayerEntity player);

	void onClose(PlayerEntity player);
}
