package shadows.compatched.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import shadows.compatched.tileentity.CompatchedChestTileEntity;

public class CompatchedChestItemRenderer extends ItemStackTileEntityRenderer {

	CompatchedChestTileEntity tileentity = new CompatchedChestTileEntity();

	@Override
	public void render(ItemStack stack, MatrixStack p_228364_2_, IRenderTypeBuffer p_228364_3_, int p_228364_4_, int p_228364_5_) {
		CompoundNBT tag = stack.getOrCreateChildTag("BlockEntityTag").getCompound("info");
		if (tag != null && tag.contains("data")) tileentity.setHue(tag.getIntArray("data")[2]);
		TileEntityRendererDispatcher.instance.renderEntity(tileentity, p_228364_2_, p_228364_3_, p_228364_4_, p_228364_5_);
	}

}
