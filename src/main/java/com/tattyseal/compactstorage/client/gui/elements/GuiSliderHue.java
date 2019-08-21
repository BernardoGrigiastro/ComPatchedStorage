package com.tattyseal.compactstorage.client.gui.elements;

import java.awt.Color;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.client.config.GuiSlider;

public class GuiSliderHue extends GuiSlider {

	public GuiSliderHue(int x, int y, String nameIn, float minIn, float maxIn, float defaultValue, ISlider slider) {
		super(x, y, 150, 20, nameIn, "", minIn, maxIn, defaultValue, false, true, b -> {
		}, slider);
	}

	@Override
	protected void renderBg(Minecraft par1Minecraft, int par2, int par3) {
		super.renderBg(par1Minecraft, par2, par3);
		if (this.visible) {
			Color color = this.sliderValue == 0f ? Color.white : Color.getHSBColor((float) sliderValue, 1f, 1f);

			GlStateManager.color3f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f);

			this.blit(this.x + (int) (this.sliderValue) * (this.width - 8), this.y, 0, 66, 4, 20);
			this.blit(this.x + (int) (this.sliderValue * (this.width - 8)) + 4, this.y, 196, 66, 4, 20);
		}
	}
}
