package com.tattyseal.compactstorage.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.tattyseal.compactstorage.block.BlockBarrel;
import com.tattyseal.compactstorage.tileentity.TileEntityBarrel;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;

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

		GlStateManager.pushMatrix();
		GlStateManager.translatef((float) coordX + 0.5f, (float) coordY + 0.5f, (float) coordZ + 0.5f);

		rotateElement(facing);

		GlStateManager.translatef(0f, -0.225f, -0.44f);

		GlStateManager.scalef(scale, scale, scale);

		FontRenderer fontrenderer = this.getFontRenderer();
		byte b0 = 0;

		String s = tileEntity.getText();

		fontrenderer.drawString(s, -fontrenderer.getStringWidth(s) / 2, 0, b0);

		GlStateManager.popMatrix();
	}

	public void rotateElement(Direction facing) {
		switch (facing) {
		case WEST: {
			GlStateManager.rotatef(180f, 1F, 0.0F, 0f);
			GlStateManager.rotatef(270f, 0F, 1F, 0f);
			break;
		}
		case EAST: {
			GlStateManager.rotatef(180f, 1F, 0.0F, 0f);
			GlStateManager.rotatef(90f, 0F, 1F, 0f);
			break;
		}
		case SOUTH: {
			GlStateManager.rotatef(180f, 1F, 0.0F, 0f);
			GlStateManager.rotatef(180f, 0F, 1F, 0f);
			break;
		}
		case NORTH: {
			GlStateManager.rotatef(180f, 1F, 0.0F, 0f);
			GlStateManager.rotatef(0f, 0F, 1F, 0f);
			break;
		}
		default: {
			GlStateManager.rotatef(180F, -1F, 0.0F, 3F);
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

			GlStateManager.pushMatrix();

			RenderHelper.enableStandardItemLighting();

			GlStateManager.translatef((float) coordX + 0.5f, (float) coordY + 0.5f, (float) coordZ + 0.5f);
			rotateElement(facing);
			//GlStateManager.glRotatef(180f, 0, 0, 0);
			GlStateManager.translatef(-(size / 3), -0.1f, -0.55f);
			GlStateManager.scalef(size / 24, size / 24, 0.001f);

			Minecraft.getInstance().getItemRenderer().renderItemIntoGUI(stack, 0, 0);
			GlStateManager.popMatrix();
		}
	}
}
