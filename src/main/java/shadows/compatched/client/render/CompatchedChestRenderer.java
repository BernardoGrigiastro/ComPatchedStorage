package shadows.compatched.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.Material;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.ChestTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.DualBrightnessCallback;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.state.properties.ChestType;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.IChestLid;
import net.minecraft.tileentity.TileEntityMerger;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import shadows.compatched.ComPatchedStorage;
import shadows.compatched.client.ClientHandler;
import shadows.compatched.tileentity.CompatchedChestTileEntity;

public class CompatchedChestRenderer extends ChestTileEntityRenderer<CompatchedChestTileEntity> {

	private final ModelRenderer chestLid;
	private final ModelRenderer field_228863_c_;
	private final ModelRenderer field_228864_d_;

	public CompatchedChestRenderer(TileEntityRendererDispatcher p_i226008_1_) {
		super(p_i226008_1_);
		this.field_228863_c_ = new ModelRenderer(64, 64, 0, 19);
		this.field_228863_c_.addCuboid(1.0F, 0.0F, 1.0F, 14.0F, 10.0F, 14.0F, 0.0F);
		this.chestLid = new ModelRenderer(64, 64, 0, 0);
		this.chestLid.addCuboid(1.0F, 0.0F, 0.0F, 14.0F, 5.0F, 14.0F, 0.0F);
		this.chestLid.rotationPointY = 9.0F;
		this.chestLid.rotationPointZ = 1.0F;
		this.field_228864_d_ = new ModelRenderer(64, 64, 0, 0);
		this.field_228864_d_.addCuboid(7.0F, -1.0F, 15.0F, 2.0F, 4.0F, 1.0F, 0.0F);
		this.field_228864_d_.rotationPointY = 8.0F;
	}

	public void render(CompatchedChestTileEntity p_225616_1_, float p_225616_2_, MatrixStack p_225616_3_, IRenderTypeBuffer p_225616_4_, int p_225616_5_, int p_225616_6_) {
		World world = p_225616_1_.getWorld();
		boolean flag = world != null;
		BlockState blockstate = flag ? p_225616_1_.getBlockState() : Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, Direction.SOUTH);
		ChestType chesttype = blockstate.has(ChestBlock.TYPE) ? blockstate.get(ChestBlock.TYPE) : ChestType.SINGLE;
		p_225616_3_.push();
		float f = blockstate.get(ChestBlock.FACING).getHorizontalAngle();
		p_225616_3_.translate(0.5D, 0.5D, 0.5D);
		p_225616_3_.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-f));
		p_225616_3_.translate(-0.5D, -0.5D, -0.5D);
		TileEntityMerger.ICallbackWrapper<? extends ChestTileEntity> icallbackwrapper = TileEntityMerger.ICallback::getFallback;
		float f1 = icallbackwrapper.apply(ChestBlock.getAnimationProgressRetriever((IChestLid) p_225616_1_)).get(p_225616_2_);
		f1 = 1.0F - f1;
		f1 = 1.0F - f1 * f1 * f1;
		int i = icallbackwrapper.apply(new DualBrightnessCallback<>()).applyAsInt(p_225616_5_);
		Material material = this.getMaterial(p_225616_1_, chesttype);
		IVertexBuilder ivertexbuilder = material.getVertexConsumer(p_225616_4_, RenderType::getEntityCutout);
		this.func_228871_a_(p_225616_3_, ivertexbuilder, this.chestLid, this.field_228864_d_, this.field_228863_c_, f1, i, p_225616_6_, ComPatchedStorage.getColorFromHue(p_225616_1_.getHue()));
		p_225616_3_.pop();
	}

	private void func_228871_a_(MatrixStack p_228871_1_, IVertexBuilder p_228871_2_, ModelRenderer p_228871_3_, ModelRenderer p_228871_4_, ModelRenderer p_228871_5_, float p_228871_6_, int p_228871_7_, int p_228871_8_, int color) {
		p_228871_3_.rotateAngleX = -(p_228871_6_ * ((float) Math.PI / 2F));
		p_228871_4_.rotateAngleX = p_228871_3_.rotateAngleX;
		p_228871_3_.render(p_228871_1_, p_228871_2_, p_228871_7_, p_228871_8_, (color >> 16) / 255F, (color >> 8) / 255F, color / 255F, 1);
		p_228871_4_.render(p_228871_1_, p_228871_2_, p_228871_7_, p_228871_8_);
		p_228871_5_.render(p_228871_1_, p_228871_2_, p_228871_7_, p_228871_8_, (color >> 16) / 255F, (color >> 8) / 255F, color / 255F, 1);
	}

	@Override
	protected Material getMaterial(CompatchedChestTileEntity tileEntity, ChestType chestType) {
		return ClientHandler.COMPATCHED_CHEST;
	}

}