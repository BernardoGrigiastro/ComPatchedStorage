package shadows.compatched.client.render;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.BiomeColors;
import net.minecraftforge.fluids.FluidStack;
import shadows.compatched.block.BlockBarrel;
import shadows.compatched.tileentity.TileEntityBarrelFluid;

public class TileEntityBarrelFluidRenderer extends TileEntityRenderer<TileEntityBarrelFluid> {
	public static final ResourceLocation blockSheet = new ResourceLocation("textures/atlas/blocks.png");

	@Override
	public void render(TileEntityBarrelFluid te, double x, double y, double z, float partialTicks, int destroyStage) {
		if (te == null) return;

		FluidStack stack = te.tank.getFluid();

		if (!stack.isEmpty()) {
			TextureAtlasSprite tex = Minecraft.getInstance().getTextureMap().getAtlasSprite(stack.getFluid().getAttributes().getStillTexture().toString());

			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder builder = tessellator.getBuffer();

			bindTexture(blockSheet);

			RenderSystem.pushMatrix();

			RenderSystem.translated(x, y, z);

			RenderSystem.disableLighting();

			int color = stack.getFluid().getAttributes().getColor(te.getWorld(), te.getPos());
			if (stack.getFluid() == Fluids.WATER) color = BiomeColors.getWaterColor(te.getWorld(), te.getPos());
			if (color != -1) {
				float[] colors = new float[3];
				colors[0] = (float) (color >> 0x10 & 0xFF) / 0xFF;
				colors[1] = (float) (color >> 0x8 & 0xFF) / 0xFF;
				colors[2] = (float) (color & 0xFF) / 0xFF;
				RenderSystem.color3f(colors[0], colors[1], colors[2]);
			}

			double px = 1D / 16;

			double minXZ = px * 3;
			double maxXZ = px * 13;

			double baseHeight = px;
			double height = ((px * 14) / te.tank.getCapacity()) * te.tank.getFluidAmount() + baseHeight;

			double minU = tex.getInterpolatedU(3D);
			double maxU = tex.getInterpolatedU(13D);
			double minV = tex.getInterpolatedV(3D);
			double maxV = tex.getInterpolatedV(13D);

			double minV_side = tex.getInterpolatedV(0D);
			double maxV_side = tex.getInterpolatedV(te.tank.getFluidAmount() * 16D / (te.tank.getCapacity()));

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

			RenderSystem.popMatrix();

			float scale = 0.01f;

			RenderSystem.pushMatrix();
			RenderSystem.translatef((float) x + 0.5f, (float) y + 0.5f, (float) z + 0.5f);

			Direction facing = te.getWorld().getBlockState(te.getPos()).get(BlockBarrel.HORIZONTAL_FACING);

			RenderSystem.translatef(0f, 0.5001f, 0f);
			RenderSystem.rotatef(180f, 0, 1f, 0f);
			RenderSystem.rotatef(90f, 1, 0, 0);

			RenderSystem.rotatef(facing.getHorizontalAngle(), 0, 0, 1f);

			RenderSystem.scalef(scale, scale, scale);

			FontRenderer fontrenderer = this.getFontRenderer();
			byte b0 = 0;

			String s = te.getText();

			fontrenderer.drawString(s, -fontrenderer.getStringWidth(s) / 2, -5, b0);

			RenderSystem.popMatrix();
		}
	}
}
