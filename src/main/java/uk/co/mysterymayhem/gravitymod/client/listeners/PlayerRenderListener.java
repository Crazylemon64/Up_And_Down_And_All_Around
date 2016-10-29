package uk.co.mysterymayhem.gravitymod.client.listeners;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uk.co.mysterymayhem.gravitymod.api.EnumGravityDirection;
import uk.co.mysterymayhem.gravitymod.common.capabilities.gravitydirection.GravityDirectionCapability;
import uk.co.mysterymayhem.gravitymod.common.capabilities.gravitydirection.IGravityDirectionCapability;
import uk.co.mysterymayhem.gravitymod.common.util.Vec3dHelper;

/**
 * Created by Mysteryem on 2016-08-07.
 */
@SideOnly(Side.CLIENT)
public class PlayerRenderListener {

    private static final double rotationSpeed = 2;
    private static final double rotationLength = GravityDirectionCapability.DEFAULT_TIMEOUT/rotationSpeed;
    private static final double rotationEnd = GravityDirectionCapability.DEFAULT_TIMEOUT - rotationLength;

    private boolean needToPop = false;

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onRender(RenderPlayerEvent.Pre event) {
//        if (event.getEntity() instanceof EntityPlayer) {
//            EntityPlayer playerEntity = (EntityPlayer)event.getEntity();
//            float partialTicks = Minecraft.getMinecraft().getRenderPartialTicks();
////            double interpolatedPitch = (playerEntity.prevRotationPitch + (playerEntity.rotationPitch - playerEntity.prevRotationPitch) * partialTicks);
////            double interpolatedYaw = (playerEntity.prevRotationYaw + (playerEntity.rotationYaw - playerEntity.prevRotationYaw) * partialTicks);
//            double interpolatedYaw = playerEntity.rotationYaw;
//
//            GlStateManager.pushMatrix();
//            //7. Set position back to normal
//            GlStateManager.translate(event.getX(), event.getY(), event.getZ());
//            //6. Redo yaw rotation
//            GlStateManager.rotate((float)-interpolatedYaw, 0, 1, 0);
//            //5. Move back to (0, 0, 0)
//            GlStateManager.translate(0, playerEntity.height/2f, 0);
//            //4. Rotate about x, so player leans over forward (z direction is forwards)
//            GlStateManager.rotate(70, 1, 0, 0);
//            //3. So we rotate around the centre of the player instead of the bottom of the player
//            GlStateManager.translate(0, -playerEntity.height/2f, 0);
//            //2. Undo yaw rotation (this will make the player face +z (south)
//            GlStateManager.rotate((float)interpolatedYaw, 0, 1, 0);
//            //1. Set position to (0, 0, 0)
//            GlStateManager.translate(-event.getX(), -event.getY(), -event.getZ());
//            this.needToPop = true;
//            return;
//        }

        EntityPlayer player = event.getEntityPlayer();
        // This is a way of detecting if the player being rendered is the one in the inventory, unfortunately, just returning
        // doesn't make things work, as the rendered player no longer looks at the mouse if under non-downwards gravity direction
//        if (entityPlayer instanceof EntityPlayerSP
//                && event.getX() == 0
//                && event.getY() == 0
//                && event.getZ() == 0
//                && event.getPartialRenderTick() == 1f) {
//            // This is the player being rendered in the inventory screen (or very very unlikely to be the player at (0, 0, 0) )
//            //if we could get the yaw argument (it's not passed to the event), we would check if it was zero
//            //if we could get the boolean argument (it's not passed to the event), we would check if it was false
//            return;
//        }
        float partialTicks = event.getPartialRenderTick();
//        double interpolatedPitch = (player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * partialTicks);
        double interpolatedYaw = (player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * partialTicks);

        IGravityDirectionCapability capability = GravityDirectionCapability.getGravityCapability(player);
        EnumGravityDirection gravityDirection = capability.getDirection();
        float transitionRollAmount = 0;
        int timeoutTicks = capability.getTimeoutTicks();
        double effectiveTimeoutTicks = timeoutTicks - (1 * event.getPartialRenderTick());

        double multiplier = 0;
        double rotationAngle = 0;

        if (timeoutTicks != 0 && effectiveTimeoutTicks > rotationEnd) {



            if (!capability.hasTransitionAngle()) {
                double yaw = player.rotationYaw;
                double pitch = player.rotationPitch;

                // Get the absolute look vector
                Vec3d absoluteLookVec = Vec3dHelper.getPreciseVectorForRotation(pitch, yaw);

                // Get the relative look vector for the current gravity direction
                Vec3d relativeCurrentLookVector = gravityDirection.getInverseAdjustmentFromDOWNDirection().adjustLookVec(absoluteLookVec);
                // Get the pitch and yaw from the relative look vector
                double[] pitchAndYawRelativeCurrentLook = Vec3dHelper.getPrecisePitchAndYawFromVector(relativeCurrentLookVector);
                // Pitch - 90, -90 changes it from the forwards direction to the upwards direction
                double relativeCurrentPitch = pitchAndYawRelativeCurrentLook[Vec3dHelper.PITCH] - 90;
                // Yaw
                double relativeCurrentYaw = pitchAndYawRelativeCurrentLook[Vec3dHelper.YAW];
                // Get the relative upwards vector
                Vec3d relativeCurrentUpVector = Vec3dHelper.getPreciseVectorForRotation(
                        relativeCurrentPitch, relativeCurrentYaw);
                // Get the absolute vector for the relative upwards vector
                Vec3d absoluteCurrentUpVector = gravityDirection.adjustLookVec(relativeCurrentUpVector);

                // Get the relative look vector for the previous gravity direction
                Vec3d relativePrevLookVector = capability.getPrevDirection().getInverseAdjustmentFromDOWNDirection().adjustLookVec(absoluteLookVec);
                // Get the pitch and yaw from the relative look vector
                double[] pitchAndYawRelativePrevLook = Vec3dHelper.getPrecisePitchAndYawFromVector(relativePrevLookVector);
                // Pitch - 90, -90 changes it from the forwards direction to the upwards direction
                double relativePrevPitch = pitchAndYawRelativePrevLook[Vec3dHelper.PITCH] - 90;
                // Yaw
                double relativePrevYaw = pitchAndYawRelativePrevLook[Vec3dHelper.YAW];
                // Get the relative upwards vector
                Vec3d relativePrevUpVector = Vec3dHelper.getPreciseVectorForRotation(
                        relativePrevPitch, relativePrevYaw);
                // Get the absolute vector for the relative upwards vector
                Vec3d absolutePrevUpVector = capability.getPrevDirection().adjustLookVec(relativePrevUpVector);

//                    FMLLog.info("\n" + absoluteCurrentUpVector + ", " + absolutePrevUpVector + "\n");
//                    FMLLog.info("Angle between up vectors: " + (180d/Math.PI)*Math.acos(absoluteCurrentUpVector.dotProduct(absolutePrevUpVector)));
                double radAngleBetweenUpVecs = Math.acos(MathHelper.clamp_double(absoluteCurrentUpVector.dotProduct(absolutePrevUpVector), -1d, 1d));
                Vec3d crossProductOfAbsoluteUpVecs = absoluteCurrentUpVector.crossProduct(absolutePrevUpVector);
                double signDecider = absoluteLookVec.dotProduct(crossProductOfAbsoluteUpVecs);
                if (signDecider < 0) {
                    if (radAngleBetweenUpVecs > 0) {
                        radAngleBetweenUpVecs = -radAngleBetweenUpVecs;
                    }
                }
                rotationAngle = radAngleBetweenUpVecs * (180d/Math.PI);

                capability.setTransitionAngle(rotationAngle);
            }
            else {
                rotationAngle = capability.getTransitionAngle();
            }
            // Get relative upwards vector
            // Adjust for previous direction
            // Adjust for current direction
            // Calculate angle between them

//                Vec3d fastUpwardsVector = Vec3dHelper.getFastUpwardsVector((float) interpolatedPitch, (float) interpolatedYaw);

            double numerator = GravityDirectionCapability.DEFAULT_TIMEOUT - effectiveTimeoutTicks;
            double denominator = rotationLength;
//
            multiplier = 1 - numerator/denominator;

            transitionRollAmount = (float)(rotationAngle * multiplier);
//                transitionRollAmount = (float)(-rotationAngle);

//                FMLLog.info("Angle: " + (float)(rotationAngle));
//                FMLLog.info("Current up Vec: " + currentDirectionUpVec);
//                FMLLog.info("Prev up Vec: " + prevDirectionUpVec + ", " + capability.getPrevDirection().name());
//                FMLLog.info("Current up Vec: " + currentDirectionUpVec + ", " + gravityDirection.name());
//                FMLLog.info("Angle: " + transitionRollAmount);
//                FMLLog.info("Multiplier: " + multiplier);
//                transitionRollAmount = 0;
        }




            GlStateManager.pushMatrix();
            GlStateManager.translate(event.getX(), event.getY(), event.getZ());
            gravityDirection.applyOtherPlayerRenderTransformations(player);
//            gravityDirection.runCameraTransformation();

//            double interpolatedPitch = (player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * event.getRenderPartialTicks());
//            double interpolatedYaw = (player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * event.getRenderPartialTicks());
//            float interpolatedYawHead = (float)(player.prevRotationYawHead + (player.rotationYawHead - player.prevRotationYawHead) * event.getRenderPartialTicks());
//
//            double[] relativeYawAndPitch = Hooks.getRelativeYawAndPitch(interpolatedYaw, interpolatedPitch, player);
//            double relativeInterpolatedPitch = relativeYawAndPitch[Hooks.PITCH];
//            double relativeInterpolatedYaw = relativeYawAndPitch[Hooks.YAW];
//
//            // Read these in reverse order
////            GlStateManager.rotate((float)interpolatedPitch, 1, 0, 0);
////            GlStateManager.rotate((float)interpolatedYaw, 0, 1, 0);
//            // 5: Rotate by the relative player's pitch, this, combined with the camera transformation set the correct camera pitch
//            GlStateManager.rotate((float)relativeInterpolatedPitch, 1, 0, 0);
//            // 4: Rotate by the relative player's yaw, this, combined with the camera transformation sets the correct camera yaw
//            GlStateManager.rotate((float)relativeInterpolatedYaw, 0, 1, 0);
//
//            // 3: Now that our look direction is effectively 0 yaw and 0 pitch, perform the rotation specific for this gravity
//            int timeoutTicks = capability.getTimeoutTicks();
//            final double rotationSpeed = 2;
//            final double rotationLength = GravityDirectionCapability.DEFAULT_TIMEOUT/rotationSpeed;
//            final double rotationEnd = GravityDirectionCapability.DEFAULT_TIMEOUT - rotationLength;
//            double effectiveTimeoutTicks = timeoutTicks;// - (1 * event.getRenderPartialTicks());
//            if (timeoutTicks != 0 && effectiveTimeoutTicks > rotationEnd) {
//                double numerator = GravityDirectionCapability.DEFAULT_TIMEOUT - effectiveTimeoutTicks;
//                double denominator = rotationLength;
////
//                double multiplier = numerator/denominator;
//
//                Vec3i prevVars = capability.getPrevDirection().getCameraTransformVars();
//                Vec3i currentVars = gravityDirection.getCameraTransformVars();
////                float xRot = (float)(currentVars.getX() + (prevVars.getX() - currentVars.getX()) * multiplier);
////                float yRot = (float)(currentVars.getY() + (prevVars.getY() - currentVars.getY()) * multiplier);
////                float zRot = (float)(currentVars.getZ() + (prevVars.getZ() - currentVars.getZ()) * multiplier);
//
//                float xRot = (float)(prevVars.getX() + (currentVars.getX() - prevVars.getX()) * multiplier);
//                float yRot = (float)(prevVars.getY() + (currentVars.getY() - prevVars.getY()) * multiplier);
//                float zRot = (float)(prevVars.getZ() + (currentVars.getZ() - prevVars.getZ()) * multiplier);
//
//                if (xRot != 0) {
//                    GlStateManager.rotate(xRot, 1, 0, 0);
//                }
//                if (yRot != 0) {
//                    GlStateManager.rotate(yRot, 0, 1, 0);
//                }
//                if (zRot != 0) {
//                    GlStateManager.rotate(zRot, 0, 0, 1);
//                }
//            }
//            else {
//                gravityDirection.runCameraTransformation();
//            }
            gravityDirection.runCameraTransformation();
//            GlStateManager.rotate((float)-relativeInterpolatedYaw, 0, 1, 0);
//            GlStateManager.rotate((float)-relativeInterpolatedPitch, 1, 0, 0);
//            // 2: Undo the absolute yaw rotation of the player
//            GlStateManager.rotate((float)-interpolatedYaw, 0, 1, 0);
//            // 1: Undo the absolute pitch rotation of the player
//            GlStateManager.rotate((float)-interpolatedPitch, 1, 0, 0);

            //6. Redo yaw rotation
            GlStateManager.rotate((float)interpolatedYaw, 0, 1, 0);
            //5. Move back to (0, 0, 0)
            GlStateManager.translate(0, player.height/2f, 0);

            GlStateManager.rotate(transitionRollAmount, 0, 0, 1);


            //3. So we rotate around the centre of the player instead of the bottom of the player
            GlStateManager.translate(0, -player.height/2f, 0);
            //2. Undo yaw rotation (this will make the player face +z (south)
            GlStateManager.rotate((float)-interpolatedYaw, 0, 1, 0);

            GlStateManager.translate(-event.getX(), -event.getY(), -event.getZ());
            this.needToPop = true;

//            if (GravityCapability.getGravityDirection(entityPlayer) == EnumGravityDirection.UP) {
//                this.rotationNeedsUndo = true;
//
//                GlStateManager.pushMatrix();
//                GlStateManager.translate(event.getX(), event.getY(), event.getZ());
//                EnumGravityDirection.UP.applyRenderTransformations(entityPlayer);
////                GlStateManager.rotate(180, 1, 0, 0);
////                GlStateManager.rotate(180, 0, 1, 0);
//                GlStateManager.translate(0, -entityPlayer.height, 0);
//                GlStateManager.translate(-event.getX(), -event.getY(), -event.getZ());
//
//                this.player = entityPlayer;
////                player.rotationYawHead *= -1;
////                player.rotationYaw *= -1;
////                player.rotationPitch *= -1;
////                player.prevRotationPitch *= -1;
////                player.prevRotationYaw *= -1;
////                player.renderYawOffset *= -1;
////                player.prevRenderYawOffset *= -1;
////                player.prevRotationYawHead *= -1;
//            }

            //if (entityPlayer == Minecraft.getMinecraft().thePlayer)


            //entityPlayer.cameraYaw += 180;
            //entityPlayer.cameraPitch += 180;
            //entityPlayer.prevCameraYaw += 180;
            //entityPlayer.prevCameraPitch += 180;

    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public void onRender(RenderPlayerEvent.Post event) {
//        if (this.rotationNeedsUndo) {
//            this.rotationNeedsUndo = false;
//            player.rotationYawHead *= -1;
//            player.rotationYaw *= -1;
//            player.rotationPitch *= -1;
//            player.prevRotationPitch *= -1;
//            player.prevRotationYaw *= -1;
//            player.renderYawOffset *= -1;
//            player.prevRenderYawOffset *= -1;
//            player.prevRotationYawHead *= -1;
//
//        }
        if (this.needToPop) {
            this.needToPop = false;
            GlStateManager.popMatrix();
        }
        //player.rotationYawHead -= 180;
        //player.rotationYaw -= 180;
        //player.rotationPitch -= 180;
        //player.cameraYaw -= 180;
        //player.cameraPitch -= 180;
        //player.prevCameraYaw -= 180;
        //player.prevCameraPitch -= 180;
        //player.prevRotationPitch -= 180;
        //player.prevRotationYaw -= 180;
        //player.prevRotationYawHead -= 180;
    }
}
