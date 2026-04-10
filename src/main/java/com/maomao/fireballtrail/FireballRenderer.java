package com.maomao.fireballtrail;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class FireballRenderer {

    private final Minecraft mc = Minecraft.getMinecraft();

    private static class ImpactInfo {
        Vec3 pos;
        float seconds;

        ImpactInfo(Vec3 pos, float seconds) {
            this.pos = pos;
            this.seconds = seconds;
        }
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {

        if (mc.theWorld == null || mc.thePlayer == null) return;

        Entity view = mc.getRenderViewEntity();

        double px = view.lastTickPosX + (view.posX - view.lastTickPosX) * event.partialTicks;
        double py = view.lastTickPosY + (view.posY - view.lastTickPosY) * event.partialTicks;
        double pz = view.lastTickPosZ + (view.posZ - view.lastTickPosZ) * event.partialTicks;

        GL11.glPushMatrix();

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glLineWidth(2.5F);

        for (Entity entity : mc.theWorld.loadedEntityList) {

            if (!(entity instanceof EntityFireball)) continue;

            EntityFireball fb = (EntityFireball) entity;

            Vec3 start = new Vec3(fb.posX, fb.posY, fb.posZ);
            Vec3 motion = new Vec3(fb.motionX, fb.motionY, fb.motionZ);

            if (motion.lengthVector() < 0.01) continue;

            ImpactInfo info = null;

            Vec3 pos = start;

            for (int i = 0; i < 120; i++) {

                Vec3 next = pos.addVector(motion.xCoord, motion.yCoord, motion.zCoord);

                MovingObjectPosition mop = fb.worldObj.rayTraceBlocks(pos, next);

                if (mop != null &&
                        mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {

                    float seconds = (i + 1) / 20f;
                    info = new ImpactInfo(mop.hitVec, seconds);
                    break;
                }

                pos = next;
            }

            Vec3 end = predictLine(start, motion, 100);

            GL11.glColor3f(1F, 0F, 0F);

            GL11.glBegin(GL11.GL_LINES);
            GL11.glVertex3d(start.xCoord - px, start.yCoord - py, start.zCoord - pz);
            GL11.glVertex3d(end.xCoord - px, end.yCoord - py, end.zCoord - pz);
            GL11.glEnd();

            if (info != null) {

                Vec3 p = info.pos;

                double x = p.xCoord - px;
                double y = p.yCoord - py + 1.3;
                double z = p.zCoord - pz;

                String text = String.format("%.2fs", info.seconds);

                FontRenderer fr = mc.fontRendererObj;

                GL11.glPushMatrix();
                GL11.glTranslated(x, y, z);

                GL11.glRotatef(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);

                float scale = 0.025F;
                GL11.glScalef(-scale, -scale, scale);

                GL11.glEnable(GL11.GL_TEXTURE_2D);

                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

                GL11.glDisable(GL11.GL_DEPTH_TEST);

                fr.drawStringWithShadow(
                        text,
                        -fr.getStringWidth(text) / 2,
                        0,
                        0xFFFF55
                );

                GL11.glEnable(GL11.GL_DEPTH_TEST);

                GL11.glPopMatrix();
            }
        }

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);

        GL11.glPopMatrix();
    }

    private Vec3 predictLine(Vec3 start, Vec3 motion, int ticks) {

        Vec3 pos = start;

        for (int i = 0; i < ticks; i++) {

            Vec3 next = pos.addVector(motion.xCoord, motion.yCoord, motion.zCoord);

            MovingObjectPosition mop = mc.theWorld.rayTraceBlocks(pos, next);

            if (mop != null) {
                return mop.hitVec;
            }

            pos = next;
        }

        return pos;
    }
}
