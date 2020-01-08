package shadows.compatched.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.items.ItemStackHandler;
import shadows.compatched.util.StorageInfo;

public interface IChest {

	int getInvX();

	int getInvY();

	StorageInfo getInfo();

	int getHue();

	void setHue(int hue);

	void onOpened(PlayerEntity player);

	void onClosed(PlayerEntity player);

	ItemStackHandler getItemHandler();
}
