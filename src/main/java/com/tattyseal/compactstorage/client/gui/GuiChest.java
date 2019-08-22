package com.tattyseal.compactstorage.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.tattyseal.compactstorage.inventory.ContainerChest;
import com.tattyseal.compactstorage.inventory.IChest;
import com.tattyseal.compactstorage.inventory.InventoryBackpack;
import com.tattyseal.compactstorage.util.RenderUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

public class GuiChest extends ContainerScreen<ContainerChest> {
	public World world;
	public PlayerEntity player;
	public BlockPos pos;

	private int invX;
	private int invY;

	public IChest chest;

	private KeyBinding[] hotbar;
	private int backpackSlot;

	public GuiChest(ContainerChest container, PlayerInventory inv, ITextComponent title) {
		super(container, inv, title);

		this.world = container.world;
		this.player = container.player;
		this.pos = container.pos;

		this.chest = container.chest;
		this.hotbar = Minecraft.getInstance().gameSettings.keyBindsHotbar;

		backpackSlot = -1;
		if (chest instanceof InventoryBackpack) {
			backpackSlot = player.inventory.currentItem;
		}

		this.invX = chest.getInvX();
		this.invY = chest.getInvY();

		this.xSize = 7 + (Math.max(9, invX) * 18) + 7;
		this.ySize = 15 + (invY * 18) + 13 + 54 + 4 + 18 + 7;
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		this.renderBackground();
		super.render(mouseX, mouseY, partialTicks);
		this.renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	public void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		this.font.drawString(I18n.format("compactstorage.chest.inv", invX, invY), 8, 6, 4210752);
		this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8, 15 + (invY * 18) + 5, 4210752);
	}

	@Override
	public void drawGuiContainerBackgroundLayer(float i, int j, int k) {
		GlStateManager.pushMatrix();

		GlStateManager.disableLighting();
		GlStateManager.color3f(1, 1, 1);

		RenderUtil.renderChestBackground(this, guiLeft, guiTop, invX, invY);

		RenderUtil.renderSlots(guiLeft + 7 + ((Math.max(9, invX) * 18) / 2) - (invX * 18) / 2, guiTop + 17, invX, invY);
		RenderUtil.renderSlots(guiLeft + 7 + ((((Math.max(9, invX)) * 18) / 2) - ((9 * 18) / 2)), guiTop + 17 + (invY * 18) + 13, 9, 3);
		RenderUtil.renderSlots(guiLeft + 7 + ((((Math.max(9, invX)) * 18) / 2) - ((9 * 18) / 2)), guiTop + 17 + (invY * 18) + 13 + 54 + 4, 9, 1);

		GlStateManager.popMatrix();
	}

	@Override
	public boolean charTyped(char c, int id) {
		if (backpackSlot != -1 && hotbar[backpackSlot].getKey().getKeyCode() == id) return false;
		return super.charTyped(c, id);
	}

}
