package shadows.compatched.client.render;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import shadows.compatched.block.BlockBarrel;
import shadows.compatched.tileentity.TileEntityBarrel;

public class TileEntityBarrelRenderer extends TileEntityRenderer<TileEntityBarrel> {
	public TextureManager textureManager;

	@Override
	public void render(TileEntityBarrel te, double x, double y, double z, float partialTicks, int destroyStage) {
		if (te == null) return;
		renderText(te, x, y, z, 0.01f);
		renderItem(te, x, y, z, 1f, 0.5f);
	}

	public void renderText(TileEntityBarrel tileEntity, double coordX, double coordY, double coordZ, float scale) {
		Direction facing = tileEntity.getWorld().getBlockState(tileEntity.getPos()).get(BlockBarrel.HORIZONTAL_FACING);

		RenderSystem.pushMatrix();
		RenderSystem.translatef((float) coordX + 0.5f, (float) coordY + 0.5f, (float) coordZ + 0.5f);

		rotateElement(facing);

		RenderSystem.translatef(0f, -0.225f, -0.44f);

		RenderSystem.scalef(scale, scale, scale);

		FontRenderer fontrenderer = this.getFontRenderer();
		byte b0 = 0;

		String s = tileEntity.getText();

		fontrenderer.drawString(s, -fontrenderer.getStringWidth(s) / 2, 0, b0);

		RenderSystem.popMatrix();
	}

	public void rotateElement(Direction facing) {
		switch (facing) {
		case WEST: {
			RenderSystem.rotatef(180f, 1F, 0.0F, 0f);
			RenderSystem.rotatef(270f, 0F, 1F, 0f);
			break;
		}
		case EAST: {
			RenderSystem.rotatef(180f, 1F, 0.0F, 0f);
			RenderSystem.rotatef(90f, 0F, 1F, 0f);
			break;
		}
		case SOUTH: {
			RenderSystem.rotatef(180f, 1F, 0.0F, 0f);
			RenderSystem.rotatef(180f, 0F, 1F, 0f);
			break;
		}
		case NORTH: {
			RenderSystem.rotatef(180f, 1F, 0.0F, 0f);
			RenderSystem.rotatef(0f, 0F, 1F, 0f);
			break;
		}
		default: {
			RenderSystem.rotatef(180F, -1F, 0.0F, 3F);
			break;
		}
		}
	}

	public void renderItem(TileEntityBarrel tileEntity, double coordX, double coordY, double coordZ, float scale, float size) {
		Direction facing = tileEntity.getWorld().getBlockState(tileEntity.getPos()).get(BlockBarrel.HORIZONTAL_FACING);
		ItemStack stack = tileEntity.getBarrelStack();

		if (!stack.isEmpty()) {

			if (stack.isEmpty()) { return; }

			stack.setCount(1);

			RenderSystem.pushMatrix();

			RenderHelper.enableStandardItemLighting();

			RenderSystem.translatef((float) coordX + 0.5f, (float) coordY + 0.5f, (float) coordZ + 0.5f);
			rotateElement(facing);
			RenderSystem.translatef(-(size / 3), -0.1f, -0.55f);
			RenderSystem.scalef(size / 24, size / 24, 0.001f);

			RenderSystem.enableRescaleNormal();
			RenderHelper.enableGUIStandardItemLighting();
			int i = getWorld().getCombinedLight(tileEntity.getPos(), 0);
			GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, i % 65536, i / 65536);
			Minecraft.getInstance().getItemRenderer().renderItemIntoGUI(stack, 0, 0);
			RenderHelper.disableStandardItemLighting();
			RenderSystem.disableRescaleNormal();
			RenderSystem.disableBlend();
			RenderSystem.popMatrix();
		}
	}
}
