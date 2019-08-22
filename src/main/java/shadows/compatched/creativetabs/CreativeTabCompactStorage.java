package shadows.compatched.creativetabs;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import shadows.compatched.CompactRegistry;
import shadows.compatched.tileentity.TileEntityChest;

public class CreativeTabCompactStorage extends ItemGroup {

	public CreativeTabCompactStorage() {
		super("compactStorage");
	}

	@Override
	@Nonnull
	public ItemStack createIcon() {
		ItemStack stack = new ItemStack(CompactRegistry.CHEST);
		new TileEntityChest().write(stack.getOrCreateChildTag("BlockEntityTag"));
		return stack;
	}
}
