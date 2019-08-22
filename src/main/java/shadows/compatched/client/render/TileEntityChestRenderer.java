package shadows.compatched.client.render;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.model.ChestModel;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import shadows.compatched.ComPatchedStorage;
import shadows.compatched.tileentity.TileEntityChest;

public class TileEntityChestRenderer extends TileEntityRenderer<TileEntityChest> {

	private ChestModel model = new ChestModel();
	private static final ResourceLocation texture = new ResourceLocation(ComPatchedStorage.MODID, "textures/models/chest.png");
	ItemStack stack = new ItemStack(Items.DIAMOND);
	ItemEntity item;

	@Override
	public void render(TileEntityChest tile, double x, double y, double z, float partialTicks, int destroyStage) {
		if (tile == null) return;
		GlStateManager.pushMatrix();

		GlStateManager.translatef((float) x, (float) y + 1.0F, (float) z + 1.0F);
		GlStateManager.scalef(1.0F, -1.0F, -1.0F);

		GlStateManager.translatef(0.5F, 0.5F, 0.5F);

		Direction direction = tile.getDirection();

		switch (direction) {
		case SOUTH:
			GlStateManager.rotatef(180f, 0f, 1f, 0f);
			break;
		case WEST:
			GlStateManager.rotatef(-90f, 0f, 1f, 0f);
			break;
		case EAST:
			GlStateManager.rotatef(90f, 0f, 1f, 0f);
			break;
		default:
			break;
		}

		GlStateManager.translatef(-0.5F, -0.5F, -0.5F);

		Minecraft.getInstance().getTextureManager().bindTexture(texture);

		int color = tile.getColor().brighter().getRGB();

		float r = (color >> 16 & 255) / 255.0F;
		float g = (color >> 8 & 255) / 255.0F;
		float b = (color & 255) / 255.0F;
		GlStateManager.color4f(r, g, b, 1F);

		float f = tile.getPrevLidAngle() + (tile.getLidAngle() - tile.getPrevLidAngle()) * partialTicks;

		f = 1.0F - f;
		f = 1.0F - f * f * f;

		model.getLid().rotateAngleX = -(f * ((float) Math.PI / 2F));
		model.renderAll();

		GlStateManager.color3f(1f, 1f, 1f);

		if (tile.isRetaining()) {
			if (item == null) item = new ItemEntity(tile.getWorld(), 0D, 0D, 0D, stack);
			GlStateManager.rotatef(180, 0, 0, 1);
			GlStateManager.translatef(-0.5f, -1.1f, 0.01f);
			Minecraft.getInstance().getRenderManager().renderEntity(item, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F, false);
		}

		GlStateManager.popMatrix();
	}
}
