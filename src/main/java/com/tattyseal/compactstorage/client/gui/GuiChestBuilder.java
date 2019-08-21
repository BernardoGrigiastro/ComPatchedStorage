package com.tattyseal.compactstorage.client.gui;

import java.util.ArrayList;

import com.mojang.blaze3d.platform.GlStateManager;
import com.tattyseal.compactstorage.CompactStorage;
import com.tattyseal.compactstorage.client.gui.elements.GuiSliderHue;
import com.tattyseal.compactstorage.inventory.ContainerChestBuilder;
import com.tattyseal.compactstorage.packet.MessageCraftChest;
import com.tattyseal.compactstorage.packet.MessageUpdateBuilder;
import com.tattyseal.compactstorage.tileentity.TileEntityChestBuilder;
import com.tattyseal.compactstorage.util.RenderUtil;
import com.tattyseal.compactstorage.util.StorageInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.client.config.GuiSlider;

public class GuiChestBuilder extends ContainerScreen<ContainerChestBuilder> {

	public World world;
	public PlayerEntity player;
	public BlockPos pos;

	private Button buttonSubmit;

	private GuiSlider hueSlider;
	private GuiSlider columnSlider;
	private GuiSlider rowSlider;

	public TileEntityChestBuilder builder;

	private static final ResourceLocation CREATIVE_INVENTORY_TABS = new ResourceLocation("textures/gui/container/creative_inventory/tabs.png");

	public GuiChestBuilder(ContainerChestBuilder container, PlayerInventory inv, ITextComponent title) {
		super(container, inv, title);
		this.world = container.world;
		this.player = container.player;
		this.pos = container.pos;
		this.builder = container.builder;
		this.xSize = 7 + 162 + 7;
		this.ySize = 7 + 108 + 13 + 54 + 4 + 18 + 7;
	}

	@Override
	public void init() {
		super.init();

		buttonSubmit = new GuiButtonExt(guiLeft + 5, guiTop + 8 + 108 - 14, xSize - 31, 20, "Build", button -> CompactStorage.CHANNEL.sendToServer(new MessageCraftChest(pos, builder.getInfo())));
		buttons.add(buttonSubmit);

		int offsetY = 18;

		columnSlider = new GuiSlider(guiLeft + 5, guiTop + offsetY + 22, 150, 20, "Columns", "", 1f, 24f, 9, true, true, b -> {
		}, s -> {
			builder.getInfo().setSizeX((int) MathHelper.clamp(s.sliderValue, 1, 24));
			CompactStorage.CHANNEL.sendToServer(new MessageUpdateBuilder(pos, builder.getInfo()));
		});
		columnSlider.setWidth((xSize / 2) - 7);
		columnSlider.sliderValue = builder.getInfo().getSizeX();
		buttons.add(columnSlider);

		rowSlider = new GuiSlider(guiLeft + ((xSize / 2)) + 3, guiTop + offsetY + 22, 150, 20, "Rows", "", 1f, 12f, 3, true, true, b -> {
		}, s -> {
			builder.getInfo().setSizeY((int) MathHelper.clamp(s.sliderValue, 1, 12));
			CompactStorage.CHANNEL.sendToServer(new MessageUpdateBuilder(pos, builder.getInfo()));
		});
		rowSlider.setWidth((xSize / 2) - 7);
		rowSlider.sliderValue = builder.getInfo().getSizeY();
		buttons.add(rowSlider);

		hueSlider = new GuiSliderHue(guiLeft + 5, guiTop + offsetY, "Hue", -1f, 360f, 180, s -> {
			builder.getInfo().setHue((int) MathHelper.clamp(s.sliderValue, -1, 360));
			CompactStorage.CHANNEL.sendToServer(new MessageUpdateBuilder(pos, builder.getInfo()));
		});
		hueSlider.setWidth(xSize - 10);
		hueSlider.sliderValue = builder.getInfo().getHue();
		buttons.add(hueSlider);
	}

	@Override
	public boolean mouseClicked(double x, double y, int button) {
		hueSlider.mouseMoved(x, y);

		for (int t = 0; t < StorageInfo.Type.values().length; t++) {
			StorageInfo.Type type = StorageInfo.Type.values()[t];

			int startX = guiLeft + (26 * t);
			int startY = guiTop - 26;

			int endX = startX + 26;
			int endY = startY + 26;

			if (x >= startX && x <= endX) {
				if (y >= startY && y <= endY) {
					StorageInfo info = new StorageInfo(builder.getInfo().getSizeX(), builder.getInfo().getSizeY(), builder.getInfo().getHue(), type);
					CompactStorage.CHANNEL.sendToServer(new MessageUpdateBuilder(pos, info));
				}
			}
		}
		return super.mouseClicked(x, y, button);
	}

	@Override
	public void render(int mouseX, int mouseY, float k) {
		this.renderBackground();
		super.render(mouseX, mouseY, k);

		if (builder != null) {
			boolean hoverTooltip = false;

			for (int x = 0; x < 4; x++) {
				if (x < builder.getInfo().getMaterialCost().size() && builder.getInfo().getMaterialCost().get(x) != null) {
					ItemStack stack = builder.getInfo().getMaterialCost().get(x);

					int startX = guiLeft + ((xSize / 2) - 36) + (x * 18);
					int startY = guiTop + 62;

					int endX = startX + 18;
					int endY = startY + 18;

					if (mouseX >= startX && mouseX <= endX) {
						if (mouseY >= startY && mouseY <= endY) {
							ArrayList<String> toolList = new ArrayList<>();
							toolList.add(stack.getDisplayName().getFormattedText());
							toolList.add(TextFormatting.AQUA + "Amount Required: " + stack.getCount());
							toolList.forEach(s -> font.drawString(s, mouseX, mouseY, 0));
							hoverTooltip = true;
							break;
						}
					}

					RenderHelper.disableStandardItemLighting();
				}
			}

			if (!hoverTooltip) {
				for (int t = 0; t < StorageInfo.Type.values().length; t++) {
					StorageInfo.Type type = StorageInfo.Type.values()[t];

					int startX = guiLeft + (26 * t);
					int startY = guiTop - 26;

					int endX = startX + 26;
					int endY = startY + 26;

					if (mouseX >= startX && mouseX <= endX) {
						if (mouseY >= startY && mouseY <= endY) {
							ArrayList<String> toolList = new ArrayList<String>();
							toolList.add(type.name);

							toolList.forEach(s -> font.drawString(s, mouseX, mouseY, 0));
							hoverTooltip = true;
							break;
						}
					}
				}
			}

			if (!hoverTooltip) {
				this.renderHoveredToolTip(mouseX, mouseY);
			}
		}
	}

	@Override
	public void drawGuiContainerBackgroundLayer(float i, int j, int k) {
		super.drawGuiContainerForegroundLayer(j, k);

		for (StorageInfo.Type type : StorageInfo.Type.values()) {
			if (!type.equals(builder.getInfo().getType())) {
				drawTab(type, type.display);
			}
		}

		RenderHelper.disableStandardItemLighting();
		GlStateManager.color3f(1, 1, 1);

		blit(guiLeft, guiTop, 0, 0, 7, 7);

		RenderUtil.renderBackground(this, guiLeft, guiTop, 162, 14 + 15 + 15 + 15 + 36);

		int slotX = guiLeft + (xSize / 2) - ((9 * 18) / 2);
		int slotY = guiTop + 7 + 108 + 10;

		RenderUtil.renderSlots(slotX, slotY, 9, 3);

		slotY = slotY + (3 * 18) + 4;

		RenderUtil.renderSlots(slotX, slotY, 9, 1);

		slotY = guiTop + 50 + 12;
		slotX = guiLeft + ((xSize / 2) - 36);

		RenderUtil.renderSlots(slotX, slotY, 4, 1);

		slotY = slotY + 20;

		RenderUtil.renderSlots(slotX, slotY, 4, 1);

		RenderUtil.renderSlots(guiLeft + 5 + xSize - 30, guiTop + 8 + 108 - 13, 1, 1);

		GlStateManager.color3f(1, 1, 1);

		StorageInfo info = builder.getInfo();

		if (info == null) { return; }

		slotY = guiTop + 50 + 12;
		slotX = guiLeft + ((xSize / 2) - 36);

		for (int x = 0; x < info.getMaterialCost().size(); x++) {
			ItemStack stack = info.getMaterialCost().get(x);

			RenderHelper.enableGUIStandardItemLighting();
			Minecraft.getInstance().getItemRenderer().renderItemIntoGUI(stack, slotX + 1 + (x * 18), slotY + 1);

			RenderHelper.disableStandardItemLighting();
		}
		font.drawString(I18n.format("tile.chestBuilder.name"), guiLeft + 7, guiTop + 7, 0x404040);
		drawTab(builder.getInfo().getType(), builder.getInfo().getType().display);
	}

	/**
	 * Draws the given tab and its background, deciding whether to highlight the tab or not based off of the selected
	 * index.
	 */
	private void drawTab(StorageInfo.Type type, ItemStack stack) {
		boolean flag = type.ordinal() == builder.getInfo().getType().ordinal();
		int i = type.ordinal();
		int j = i * 28;
		int k = 0;
		int l = this.guiLeft + 26 * i;
		int i1 = this.guiTop;

		if (flag) {
			k += 32;
		}

		if (i == 5) {
			l = this.guiLeft + this.xSize - 28;
		} else if (i > 0) {
			l += i;
		}

		i1 -= 28;

		Minecraft.getInstance().getTextureManager().bindTexture(CREATIVE_INVENTORY_TABS);
		GlStateManager.disableLighting();
		GlStateManager.color3f(1F, 1F, 1F); //Forge: Reset color in case Items change it.
		GlStateManager.enableBlend(); //Forge: Make sure blend is enabled else tabs show a white border.
		this.blit(l, i1, j, k, 28, 32);
		this.blitOffset = 100;
		this.itemRenderer.zLevel = 100.0F;
		l = l + 6;
		i1 = i1 + 8;
		GlStateManager.enableLighting();
		GlStateManager.enableRescaleNormal();
		this.itemRenderer.renderItemAndEffectIntoGUI(stack, l, i1);
		this.itemRenderer.renderItemOverlays(this.font, stack, l, i1);
		GlStateManager.disableLighting();
		this.itemRenderer.zLevel = 0.0F;
		this.blitOffset = 0;
	}
}
