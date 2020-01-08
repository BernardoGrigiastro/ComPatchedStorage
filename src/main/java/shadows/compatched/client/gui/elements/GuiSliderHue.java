package shadows.compatched.client.gui.elements;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.client.config.GuiSlider;
import net.minecraftforge.fml.client.config.GuiUtils;

public class GuiSliderHue extends GuiSlider {

	public GuiSliderHue(int x, int y, String nameIn, float minIn, float maxIn, float defaultValue, ISlider slider) {
		super(x, y, 150, 20, nameIn, "", minIn, maxIn, defaultValue, false, true, b -> {
		}, slider);
	}

	@Override
	protected void renderBg(Minecraft par1Minecraft, int par2, int par3) {
		super.renderBg(par1Minecraft, par2, par3);
		if (this.visible) {
			Color color = this.sliderValue == 0f ? Color.WHITE : Color.getHSBColor((float) sliderValue, 1, 1);
			RenderSystem.color3f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f);

			Minecraft.getInstance().getTextureManager().bindTexture(WIDGETS_LOCATION);

			drawContinuousTexturedBox(this.x + (int) (this.sliderValue * (this.width - 8)), this.y, 0, 66, 8, this.height, 200, 20, 2, 3, 2, 2, this.getBlitOffset());
		}
	}

	@Override
	public void updateSlider() {
		super.updateSlider();
		if (drawString) {
			String val = Integer.toString(getValueInt());
			setMessage(dispString + val + suffix);
		}
	}

	@Override
	public void onRelease(double mouseX, double mouseY) {
		this.dragging = false;
	}

	@Override
	public int getValueInt() {
		return (int) (this.minValue + (this.maxValue - this.minValue) * this.sliderValue);
	}

	@Override
	public double getValue() {
		return sliderValue * (maxValue - minValue) + minValue;
	}

	public static void drawContinuousTexturedBox(int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight, int topBorder, int bottomBorder, int leftBorder, int rightBorder, float zLevel) {
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);

		int fillerWidth = textureWidth - leftBorder - rightBorder;
		int fillerHeight = textureHeight - topBorder - bottomBorder;
		int canvasWidth = width - leftBorder - rightBorder;
		int canvasHeight = height - topBorder - bottomBorder;
		int xPasses = canvasWidth / fillerWidth;
		int remainderWidth = canvasWidth % fillerWidth;
		int yPasses = canvasHeight / fillerHeight;
		int remainderHeight = canvasHeight % fillerHeight;

		// Draw Border
		// Top Left
		GuiUtils.drawTexturedModalRect(x, y, u, v, leftBorder, topBorder, zLevel);
		// Top Right
		GuiUtils.drawTexturedModalRect(x + leftBorder + canvasWidth, y, u + leftBorder + fillerWidth, v, rightBorder, topBorder, zLevel);
		// Bottom Left
		GuiUtils.drawTexturedModalRect(x, y + topBorder + canvasHeight, u, v + topBorder + fillerHeight, leftBorder, bottomBorder, zLevel);
		// Bottom Right
		GuiUtils.drawTexturedModalRect(x + leftBorder + canvasWidth, y + topBorder + canvasHeight, u + leftBorder + fillerWidth, v + topBorder + fillerHeight, rightBorder, bottomBorder, zLevel);

		for (int i = 0; i < xPasses + (remainderWidth > 0 ? 1 : 0); i++) {
			// Top Border
			GuiUtils.drawTexturedModalRect(x + leftBorder + i * fillerWidth, y, u + leftBorder, v, i == xPasses ? remainderWidth : fillerWidth, topBorder, zLevel);
			// Bottom Border
			GuiUtils.drawTexturedModalRect(x + leftBorder + i * fillerWidth, y + topBorder + canvasHeight, u + leftBorder, v + topBorder + fillerHeight, i == xPasses ? remainderWidth : fillerWidth, bottomBorder, zLevel);

			// Throw in some filler for good measure
			for (int j = 0; j < yPasses + (remainderHeight > 0 ? 1 : 0); j++)
				GuiUtils.drawTexturedModalRect(x + leftBorder + i * fillerWidth, y + topBorder + j * fillerHeight, u + leftBorder, v + topBorder, i == xPasses ? remainderWidth : fillerWidth, j == yPasses ? remainderHeight : fillerHeight, zLevel);
		}

		// Side Borders
		for (int j = 0; j < yPasses + (remainderHeight > 0 ? 1 : 0); j++) {
			// Left Border
			GuiUtils.drawTexturedModalRect(x, y + topBorder + j * fillerHeight, u, v + topBorder, leftBorder, j == yPasses ? remainderHeight : fillerHeight, zLevel);
			// Right Border
			GuiUtils.drawTexturedModalRect(x + leftBorder + canvasWidth, y + topBorder + j * fillerHeight, u + leftBorder + fillerWidth, v + topBorder, rightBorder, j == yPasses ? remainderHeight : fillerHeight, zLevel);
		}
	}
}
