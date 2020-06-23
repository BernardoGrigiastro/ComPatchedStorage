package shadows.compatched.creativetabs;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import shadows.compatched.ComPatchedStorage;
import shadows.compatched.CompactRegistry;
import shadows.compatched.tileentity.CompatchedChestTileEntity;

public class CreativeTabCompactStorage extends ItemGroup {

	public CreativeTabCompactStorage() {
		super(ComPatchedStorage.MODID);
	}

	@Override
	@Nonnull
	public ItemStack createIcon() {
		ItemStack stack = new ItemStack(CompactRegistry.CHEST);
		new CompatchedChestTileEntity().write(stack.getOrCreateChildTag("BlockEntityTag"));
		return stack;
	}
}
