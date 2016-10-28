package uk.co.mysterymayhem.gravitymod.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import uk.co.mysterymayhem.gravitymod.GravityMod;
import uk.co.mysterymayhem.gravitymod.asm.EntityPlayerWithGravity;
import uk.co.mysterymayhem.gravitymod.common.capabilities.gravitydirection.GravityDirectionCapability;
import uk.co.mysterymayhem.gravitymod.common.capabilities.gravitydirection.IGravityDirectionCapability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 * General API class that other mods should use if they intend to interact with the mod (also used internally)
 * Created by Mysteryem on 2016-08-26.
 */
@SuppressWarnings("unused")
public class API {

    public static final int X = 0;
    public static final int Y = 1;
    public static final int Z = 2;

    /**
     * Call this every tick that a player should have the gravity direction you specify.
     * At the beginning of the player's next tick, the highest priority 'prepared gravity transition' will be chosen.
     * @param newGravity The gravity direction the player should have during the next tick.
     * @param player The player to apply the gravity to.
     * @param priority The priority of this 'prepared gravity transition'
     */
    public static void setPlayerGravity(@Nonnull EnumGravityDirection newGravity, EntityPlayerMP player, int priority) {
        GravityMod.proxy.getGravityManager().prepareGravityTransition(Objects.requireNonNull(newGravity), player, priority);
    }

    /**
     * Forcefully set a new gravity direction for a player this tick, this wall fire (post) the relevant events, perform client sync etc.
     * Even if the player in question currently has a timeout preventing normal changes to their gravity, this will bypass it
     * @param newGravity New gravity direction for the player.
     * @param player Server player whose gravity you want to change.
     * @param noTimeout False to set a default timeout, preventing normal changes to gravity.
     *                  True to specifically set a zero timeout, normal changes will be able to take place immediately.
     */
    public static void forceSetPlayerGravity(EnumGravityDirection newGravity, EntityPlayerMP player, boolean noTimeout) {
        GravityMod.proxy.getGravityManager().doGravityTransition(Objects.requireNonNull(newGravity), player, noTimeout);
    }



    /**
     * Get the gravity direction capability for a specific player
     * @param player The player whose gravity direction capability you are after
     * @return The specified player's gravity direction capability
     */
    public static IGravityDirectionCapability getGravityDirectionCapability(EntityPlayer player) {
        return GravityDirectionCapability.getGravityCapability(player);
    }

    /**
     * Get the current gravity direction of a player.
     * @param player Player to look up, must not be NULL, can be either a client or server player.
     * @return The gravity direction of the player.
     * (will return DOWN if the player does not have the GravityCapability, e.g., while the EntityPlayer object is constructing)
     */
    public static EnumGravityDirection getGravityDirection(@Nonnull EntityPlayer player) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }
        return GravityDirectionCapability.getGravityDirection(player);
    }

    /**
     * Get the current gravity direction of a player.
     * @param player Player to look up, can be either a client or server player.
     * @return The gravity direction of the player.
     * (will return DOWN if the player does not have the GravityCapability, e.g., while the EntityPlayer object is
     * constructing or if the passed player object is NULL)
     */
    public static EnumGravityDirection getGravityDirectionUnsafe(@Nullable EntityPlayer player) {
        if (player == null) {
            return GravityDirectionCapability.DEFAULT_GRAVITY;
        }
        return GravityDirectionCapability.getGravityDirection(player);
    }

    /**
     * Get the eye height the player would have if their gravity direction was currently DOWN.
     * This mod overrides getEyeHeight() so a ton of vanilla stuff works/mostly works. You can use this method to get
     * what getEyeHeight() would normally return if it hadn't been overwritten.
     * @param player Player whose 'standard' eye height you want
     * @return The player's eye height as if their current gravity direction is DOWN.
     */
    public static float getStandardEyeHeight(@Nonnull EntityPlayer player) {
        // Should pretty much always be the case
        if (player instanceof EntityPlayerWithGravity) {
            return ((EntityPlayerWithGravity)player).super_getEyeHeight();
        }
        else {
            // This probably shouldn't ever happen
            return player.getEyeHeight();
        }
    }
}
