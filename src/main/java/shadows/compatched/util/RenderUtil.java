package shadows.compatched.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import shadows.compatched.ComPatchedStorage;

public class RenderUtil {
	public static final ResourceLocation slotTexture = new ResourceLocation(ComPatchedStorage.MODID, "textures/gui/chestslots.png");
	public static final ResourceLocation backgroundTexture = new ResourceLocation(ComPatchedStorage.MODID, "textures/gui/chest.png");

	private static float slotTextureWidth = 432F;
	private static float slotTextureHeight = 216F;
	private static float chestTextureSize = 15F;

	private static final Minecraft mc = Minecraft.getInstance();

	public static void renderSlots(int x, int y, int width, int height) {
		mc.getTextureManager().bindTexture(slotTexture);

		int realWidth = width * 18;
		int realHeight = height * 18;

		float ux = 1F / slotTextureWidth * realWidth;
		float uz = 1F / slotTextureHeight * realHeight;

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder worldRenderer = tessellator.getBuffer();

		worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
		worldRenderer.vertex(x, y + realHeight, 0).texture(0, uz).endVertex();
		worldRenderer.vertex(x + realWidth, y + realHeight, 0).texture(ux, uz).endVertex();
		worldRenderer.vertex(x + realWidth, y, 0).texture(ux, 0).endVertex();
		worldRenderer.vertex(x, y, 0).texture(0, 0).endVertex();
		tessellator.draw();
	}

	public static void renderChestBackground(ContainerScreen<?> gui, int x, int y, int width, int height) {
		renderBackground(gui, x, y, Math.max(9, width) * 18, height * 18);
	}

	public static void renderBackground(ContainerScreen<?> gui, int x, int y, int width, int height) {
		mc.getTextureManager().bindTexture(backgroundTexture);

		int realWidth = 7 + width + 7;
		int realHeight = 15 + height + 13 + 54 + 4 + 18 + 7;

		int by = y + realHeight - 7;

		renderPartBackground(x, y, 0, 0, 7, 7, 7, 7);
		renderPartBackground(x + 7, y, 8, 0, 8, 7, width, 7);
		renderPartBackground(x + 7 + width, y, 9, 0, 15, 7, 7, 7);

		renderPartBackground(x, by, 0, 8, 7, 15, 7, 7);
		renderPartBackground(x + 7, by, 8, 8, 7, 15, width, 7);
		renderPartBackground(x + 7 + width, by, 9, 8, 15, 15, 7, 7);

		renderPartBackground(x, y + 7, 0, 7, 7, 7, 7, realHeight - 14);
		renderPartBackground(x + realWidth - 8, y + 7, 8, 7, 15, 7, 8, realHeight - 14);

		renderPartBackground(x + 7, y + 7, 8, 8, 8, 8, width, realHeight - 14);
	}

	private static void renderPartBackground(int x, int y, int startX, int startY, int endX, int endY, int width, int height) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder worldRenderer = tessellator.getBuffer();
		worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);

		worldRenderer.vertex(x, (double) y + height, 0).texture(getEnd(chestTextureSize, startX), getEnd(chestTextureSize, endY)).endVertex();
		worldRenderer.vertex((double) x + width, (double) y + height, 0).texture(getEnd(chestTextureSize, endX), getEnd(chestTextureSize, endY)).endVertex();
		worldRenderer.vertex((double) x + width, (double) y + 0, 0).texture(getEnd(chestTextureSize, endX), getEnd(chestTextureSize, startY)).endVertex();
		worldRenderer.vertex(x, y, 0).texture(getEnd(chestTextureSize, startX), getEnd(chestTextureSize, startY)).endVertex();

		tessellator.draw();
	}

	private static float getEnd(float width, float other) {
		return 1F / width * other;
	}
}
