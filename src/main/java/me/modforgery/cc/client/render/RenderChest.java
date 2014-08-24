package me.modforgery.cc.client.render;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Calendar;

import me.modforgery.cc.tileentity.TileEntityChest;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

@SideOnly(Side.CLIENT)
public class RenderChest extends TileEntitySpecialRenderer
{
    private ResourceLocation chest;
    private ModelChest model = new ModelChest();

    public RenderChest(String texture)
    {
        Calendar calendar = Calendar.getInstance();

        chest = new ResourceLocation("compactchests", "textures/models/chest_" + texture + ".png");
    }

    public void renderTileEntityAt(TileEntityChest chest, double x, double y, double z, float p_147500_8_)
    {
        int i;

        if (!chest.hasWorldObj())
        {
            i = 0;
        }
        else
        {
            Block block = chest.getBlockType();
            i = chest.getBlockMetadata();

            if (block instanceof BlockChest && i == 0)
            {
                try
                {
                    ((BlockChest)block).func_149954_e(chest.getWorldObj(), chest.xCoord, chest.yCoord, chest.zCoord);
                }
                catch (ClassCastException e)
                {
                    FMLLog.severe("Attempted to render a chest at %d,  %d, %d that was not a chest", chest.xCoord, chest.yCoord, chest.zCoord);
                }
                i = chest.getBlockMetadata();
            }

            bindTexture(this.chest);

            GL11.glPushMatrix();
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glTranslatef((float)x, (float)y + 1.0F, (float)z + 1.0F);
            GL11.glScalef(1.0F, -1.0F, -1.0F);
            GL11.glTranslatef(0.5F, 0.5F, 0.5F);
            short short1 = 0;

            if (i == 2)
            {
                short1 = 180;
            }

            if (i == 3)
            {
                short1 = 0;
            }

            if (i == 4)
            {
                short1 = 90;
            }

            if (i == 5)
            {
                short1 = -90;
            }

            GL11.glRotatef((float)short1, 0.0F, 1.0F, 0.0F);
            GL11.glTranslatef(-0.5F, -0.5F, -0.5F);

            model.chestLid.rotateAngleX = -chest.lidAngle;

            chest.prevLidAngle = chest.lidAngle;

            model.renderAll();
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            GL11.glPopMatrix();
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    public void renderTileEntityAt(TileEntity p_147500_1_, double p_147500_2_, double p_147500_4_, double p_147500_6_, float p_147500_8_)
    {
        this.renderTileEntityAt((TileEntityChest)p_147500_1_, p_147500_2_, p_147500_4_, p_147500_6_, p_147500_8_);
    }
}