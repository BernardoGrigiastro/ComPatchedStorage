package shadows.compatched.client.gui;

import java.util.ArrayList;

import com.mojang.blaze3d.systems.RenderSystem;

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
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.gui.GuiUtils;
import shadows.compatched.ComPatchedStorage;
import shadows.compatched.client.gui.elements.GuiButtonExt;
import shadows.compatched.client.gui.elements.GuiSlider;
import shadows.compatched.client.gui.elements.GuiSliderHue;
import shadows.compatched.inventory.ContainerChestBuilder;
import shadows.compatched.packet.MessageCraftChest;
import shadows.compatched.packet.MessageUpdateBuilder;
import shadows.compatched.tileentity.TileEntityChestBuilder;
import shadows.compatched.util.RenderUtil;
import shadows.compatched.util.StorageInfo;

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

		buttonSubmit = new GuiButtonExt(guiLeft + 5, guiTop + 8 + 108 - 14, xSize - 31, 20, "Build", button -> ComPatchedStorage.CHANNEL.sendToServer(new MessageCraftChest(pos, builder.getInfo())));
		addButton(buttonSubmit);

		int offsetY = 18;

		columnSlider = new GuiSlider(guiLeft + 5, guiTop + offsetY + 22, 150, 20, "", " " + I18n.format("compatchedstorage.text.columns"), 1, 24, builder.getInfo().getSizeX(), false, true, b -> {
		}, s -> {
			builder.getInfo().setSizeX(s.getValueInt());
			ComPatchedStorage.CHANNEL.sendToServer(new MessageUpdateBuilder(builder.getInfo()));
		});
		columnSlider.setWidth(xSize / 2 - 7);
		addButton(columnSlider);

		rowSlider = new GuiSlider(guiLeft + xSize / 2 + 3, guiTop + offsetY + 22, 150, 20, "", " " + I18n.format("compatchedstorage.text.rows"), 1f, 12f, builder.getInfo().getSizeY(), false, true, b -> {
		}, s -> {
			builder.getInfo().setSizeY(s.getValueInt());
			ComPatchedStorage.CHANNEL.sendToServer(new MessageUpdateBuilder(builder.getInfo()));
		});
		rowSlider.setWidth(xSize / 2 - 7);
		addButton(rowSlider);

		hueSlider = new GuiSliderHue(guiLeft + 5, guiTop + offsetY, I18n.format("compatchedstorage.text.hue") + " ", -1f, 360f, builder.getInfo().getHue(), s -> {
			builder.getInfo().setHue(s.getValueInt());
			ComPatchedStorage.CHANNEL.sendToServer(new MessageUpdateBuilder(builder.getInfo()));
		});
		hueSlider.setWidth(xSize - 10);
		addButton(hueSlider);
	}

	@Override
	public boolean mouseClicked(double x, double y, int button) {
		hueSlider.mouseMoved(x, y);

		for (int t = 0; t < StorageInfo.Type.values().length; t++) {
			StorageInfo.Type type = StorageInfo.Type.values()[t];

			int startX = guiLeft + 26 * t;
			int startY = guiTop - 26;

			int endX = startX + 26;
			int endY = startY + 26;

			if (x >= startX && x <= endX) {
				if (y >= startY && y <= endY) {
					StorageInfo info = new StorageInfo(builder.getInfo().getSizeX(), builder.getInfo().getSizeY(), builder.getInfo().getHue(), type);
					ComPatchedStorage.CHANNEL.sendToServer(new MessageUpdateBuilder(info));
				}
			}
		}
		return super.mouseClicked(x, y, button);
	}

	@Override
	public boolean mouseReleased(double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_) {
		hueSlider.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_);
		columnSlider.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_);
		rowSlider.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_);
		return super.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_);
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

					int startX = guiLeft + xSize / 2 - 36 + x * 18;
					int startY = guiTop + 62;

					int endX = startX + 18;
					int endY = startY + 18;

					if (mouseX >= startX && mouseX <= endX) {
						if (mouseY >= startY && mouseY <= endY) {
							ArrayList<String> toolList = new ArrayList<>();
							toolList.add(stack.getDisplayName().getFormattedText());
							toolList.add(TextFormatting.AQUA + I18n.format("compatchedstorage.text.amountreq", stack.getCount()));
							GuiUtils.drawHoveringText(toolList, mouseX, mouseY, width, height, -1, font);
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

					int startX = guiLeft + 26 * t;
					int startY = guiTop - 26;

					int endX = startX + 26;
					int endY = startY + 26;

					if (mouseX >= startX && mouseX <= endX) {
						if (mouseY >= startY && mouseY <= endY) {
							ArrayList<String> toolList = new ArrayList<String>();
							toolList.add(I18n.format(type.name));

							GuiUtils.drawHoveringText(toolList, mouseX, mouseY, width, height, -1, font);
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
				drawTab(type, type.getAsStack()); //TODO: CACHEME
			}
		}

		RenderHelper.disableStandardItemLighting();
		RenderSystem.color3f(1, 1, 1);

		blit(guiLeft, guiTop, 0, 0, 7, 7);

		RenderUtil.renderBackground(this, guiLeft, guiTop, 162, 14 + 15 + 15 + 15 + 36);

		int slotX = guiLeft + xSize / 2 - 9 * 18 / 2;
		int slotY = guiTop + 7 + 108 + 10;

		RenderUtil.renderSlots(slotX, slotY, 9, 3);

		slotY = slotY + 3 * 18 + 4;

		RenderUtil.renderSlots(slotX, slotY, 9, 1);

		slotY = guiTop + 50 + 12;
		slotX = guiLeft + xSize / 2 - 36;

		RenderUtil.renderSlots(slotX, slotY, 4, 1);

		slotY = slotY + 20;

		RenderUtil.renderSlots(slotX, slotY, 4, 1);

		RenderUtil.renderSlots(guiLeft + 5 + xSize - 30, guiTop + 8 + 108 - 13, 1, 1);

		RenderSystem.color3f(1, 1, 1);

		StorageInfo info = builder.getInfo();

		if (info == null) { return; }

		slotY = guiTop + 50 + 12;
		slotX = guiLeft + xSize / 2 - 36;

		for (int x = 0; x < info.getMaterialCost().size(); x++) {
			ItemStack stack = info.getMaterialCost().get(x);
			Minecraft.getInstance().getItemRenderer().renderItemIntoGUI(stack, slotX + 1 + x * 18, slotY + 1);
		}
		font.drawString(this.getTitle().getFormattedText(), guiLeft + 7, guiTop + 7, 0x404040);
		drawTab(builder.getInfo().getType(), builder.getInfo().getType().getAsStack());
	}

	/**
	 * Draws the given tab and its background, deciding whether to highlight the tab or not based off of the selected
	 * index.
	 */
	private void drawTab(StorageInfo.Type type, ItemStack stack) {
		boolean active = type.ordinal() == builder.getInfo().getType().ordinal();
		int i = type.ordinal();
		int j = i * 28;
		int k = 0;
		int l = this.guiLeft + 26 * i;
		int i1 = this.guiTop;

		if (active) {
			k += 32;
		}

		if (i == 5) {
			l = this.guiLeft + this.xSize - 28;
		} else if (i > 0) {
			l += i;
		}

		i1 -= 28;

		Minecraft.getInstance().getTextureManager().bindTexture(CREATIVE_INVENTORY_TABS);
		RenderSystem.disableLighting();
		RenderSystem.color3f(1F, 1F, 1F); //Forge: Reset color in case Items change it.
		RenderSystem.enableBlend(); //Forge: Make sure blend is enabled else tabs show a white border.
		this.blit(l, i1, j, k, 28, 32);
		this.setBlitOffset(100);
		this.itemRenderer.zLevel = 100.0F;
		l = l + 6;
		i1 = i1 + 8;
		RenderSystem.enableRescaleNormal();
		this.itemRenderer.renderItemAndEffectIntoGUI(stack, l, i1);
		this.itemRenderer.renderItemOverlays(this.font, stack, l, i1);
		this.itemRenderer.zLevel = 0.0F;
		this.setBlitOffset(0);
	}
}
