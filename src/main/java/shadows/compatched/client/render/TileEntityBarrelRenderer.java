package shadows.compatched.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import shadows.compatched.block.BlockBarrel;
import shadows.compatched.tileentity.TileEntityBarrel;

public class TileEntityBarrelRenderer extends TileEntityRenderer<TileEntityBarrel> {

	public TileEntityBarrelRenderer(TileEntityRendererDispatcher terd) {
		super(terd);
	}

	@Override
	public void render(TileEntityBarrel te, float partial, MatrixStack stack, IRenderTypeBuffer buf, int p_225616_5_, int p_225616_6_) {
		if (te == null) return;
		renderText(te, stack, buf, 0.01F);
		renderItem(te, stack, buf, 1, 0.5F);
	}

	public void renderText(TileEntityBarrel tileEntity, MatrixStack stack, IRenderTypeBuffer buf, float scale) {
		Direction facing = tileEntity.getWorld().getBlockState(tileEntity.getPos()).get(BlockBarrel.HORIZONTAL_FACING);
		stack.push();
		stack.translate(0.0D, -0.225, -0.44);
		rotateElement(stack, facing);
		stack.scale(-0.025F, -0.025F, 0.025F);
		Matrix4f matrix4f = stack.peek().getModel();
		String name = tileEntity.getText();
		FontRenderer fontrenderer = Minecraft.getInstance().fontRenderer;
		float f2 = (float) (-fontrenderer.getStringWidth(name) / 2);
		fontrenderer.draw(name, f2, 0, -1, false, matrix4f, buf, false, 0, 0);
		stack.pop();
	}

	public void rotateElement(MatrixStack stack, Direction facing) {
		switch (facing) {
		case WEST: {
			stack.multiply(new Quaternion(180f, 1F, 0.0F, 0f));
			stack.multiply(new Quaternion(270f, 0F, 1F, 0f));
			break;
		}
		case EAST: {
			stack.multiply(new Quaternion(180f, 1F, 0.0F, 0f));
			stack.multiply(new Quaternion(90f, 0F, 1F, 0f));
			break;
		}
		case SOUTH: {
			stack.multiply(new Quaternion(180f, 1F, 0.0F, 0f));
			stack.multiply(new Quaternion(180f, 0F, 1F, 0f));
			break;
		}
		case NORTH: {
			stack.multiply(new Quaternion(180f, 1F, 0.0F, 0f));
			stack.multiply(new Quaternion(0f, 0F, 1F, 0f));
			break;
		}
		default:
		}
	}

	public void renderItem(TileEntityBarrel tileEntity, MatrixStack stack, IRenderTypeBuffer buf, float scale, float size) {
		Direction facing = tileEntity.getWorld().getBlockState(tileEntity.getPos()).get(BlockBarrel.HORIZONTAL_FACING);
		ItemStack item = tileEntity.getBarrelStack();
		if (!item.isEmpty()) {
			item.setCount(1);
			stack.push();
			rotateElement(stack, facing);
			stack.translate(-(size / 3), -0.1f, -0.55f);
			stack.scale(size / 24, size / 24, 0.001f);
			RenderSystem.enableRescaleNormal();
			Minecraft.getInstance().getItemRenderer().renderItemIntoGUI(item, 0, 0);
			RenderHelper.disableStandardItemLighting();
			RenderSystem.disableRescaleNormal();
			RenderSystem.disableBlend();
			stack.pop();
		}
	}
}
