package com.tattyseal.compactstorage.client.render;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;
import com.tattyseal.compactstorage.tileentity.TileEntityBarrelFluid;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

public class TileEntityBarrelFluidRenderer extends TileEntityRenderer<TileEntityBarrelFluid> {
	public static final ResourceLocation blockSheet = new ResourceLocation("textures/atlas/blocks.png");

	@Override
	public void render(TileEntityBarrelFluid te, double x, double y, double z, float partialTicks, int destroyStage) {
		if (te == null) return;

		FluidStack stack = te.tank.getFluid();

		if (stack != null) {
			TextureAtlasSprite tex = Minecraft.getInstance().getTextureMap().getAtlasSprite(stack.getFluid().getStill().toString());

			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder builder = tessellator.getBuffer();

			bindTexture(blockSheet);

			GlStateManager.pushMatrix();

			GlStateManager.translated(x, y, z);

			GlStateManager.disableLighting();

			float increments = (1f / 16f);

			float minXZ = increments * 3f;
			float maxXZ = increments * 13f;

			float baseHeight = increments;
			float height = ((increments * 14f) / te.tank.getCapacity()) * te.tank.getFluidAmount() + baseHeight;

			double minU = tex.getInterpolatedU(3D);
			double maxU = tex.getInterpolatedU(13D);
			double minV = tex.getInterpolatedV(3D);
			double maxV = tex.getInterpolatedV(13D);

			double minV_side = tex.getInterpolatedV(0D);
			double maxV_side = tex.getInterpolatedV((te.tank.getFluidAmount() - 0) * (16 - 0) / (te.tank.getCapacity() - 0) + 0);

			builder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			builder.pos(maxXZ, height, maxXZ).tex(maxU, maxV).endVertex();
			builder.pos(maxXZ, height, minXZ).tex(maxU, minV).endVertex();
			builder.pos(minXZ, height, minXZ).tex(minU, minV).endVertex();
			builder.pos(minXZ, height, maxXZ).tex(minU, maxV).endVertex();

			builder.pos(minXZ, baseHeight, maxXZ).tex(maxU, maxV_side).endVertex();
			builder.pos(minXZ, height, maxXZ).tex(maxU, minV_side).endVertex();
			builder.pos(minXZ, height, minXZ).tex(minU, minV_side).endVertex();
			builder.pos(minXZ, baseHeight, minXZ).tex(minU, maxV_side).endVertex();

			builder.pos(maxXZ, baseHeight, minXZ).tex(maxU, maxV_side).endVertex();
			builder.pos(maxXZ, height, minXZ).tex(maxU, minV_side).endVertex();
			builder.pos(maxXZ, height, maxXZ).tex(minU, minV_side).endVertex();
			builder.pos(maxXZ, baseHeight, maxXZ).tex(minU, maxV_side).endVertex();

			builder.pos(minXZ, baseHeight, minXZ).tex(maxU, maxV_side).endVertex();
			builder.pos(minXZ, height, minXZ).tex(maxU, minV_side).endVertex();
			builder.pos(maxXZ, height, minXZ).tex(minU, minV_side).endVertex();
			builder.pos(maxXZ, baseHeight, minXZ).tex(minU, maxV_side).endVertex();

			builder.pos(maxXZ, baseHeight, maxXZ).tex(maxU, maxV_side).endVertex();
			builder.pos(maxXZ, height, maxXZ).tex(maxU, minV_side).endVertex();
			builder.pos(minXZ, height, maxXZ).tex(minU, minV_side).endVertex();
			builder.pos(minXZ, baseHeight, maxXZ).tex(minU, maxV_side).endVertex();

			tessellator.draw();

			GlStateManager.popMatrix();

			float scale = 0.01f;

			/**
			 * Draw the text
			 */

			GlStateManager.pushMatrix();
			GlStateManager.translatef((float) x + 0.5f, (float) y + 0.5f, (float) z + 0.5f);

			float angle = Minecraft.getInstance().player.rotationYaw; //- (float) Math.toDegrees(Math.atan2(Minecraft.getMinecraft().player.posX - te.getPos().getX(), Minecraft.getMinecraft().player.posZ - te.getPos().getZ()));
			//LogHelper.dump("Angle:" + angle);

			GlStateManager.translatef(0f, 0.5001f, 0f);
			GlStateManager.rotatef(180f, 0, 1f, 0f);
			GlStateManager.rotatef(90f, 1, 0, 0);

			GlStateManager.rotatef(angle, 0, 0, 1f);

			GlStateManager.scalef(scale, scale, scale);

			FontRenderer fontrenderer = this.getFontRenderer();
			byte b0 = 0;

			String s = te.getText();

			fontrenderer.drawString(s, -fontrenderer.getStringWidth(s) / 2, -5, b0);

			GlStateManager.popMatrix();
		}
	}
}
