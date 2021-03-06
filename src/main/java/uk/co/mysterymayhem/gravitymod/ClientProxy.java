package uk.co.mysterymayhem.gravitymod;

import com.google.common.collect.Lists;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uk.co.mysterymayhem.gravitymod.client.listeners.*;

import java.util.Collection;

/**
 * Created by Mysteryem on 2016-08-04.
 */
@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    @Override
    public void postInit() {
        super.postInit();
        this.postInitClient();
    }

    @Override
    public void preInit() {
        super.preInit();
        this.preInitClient();
    }

    @Override
    public void init() {
        super.init();
        this.initClient();
    }

    @Override
    public void registerGravityManager() {
        this.gravityManagerCommon = new GravityManagerClient();
    }

    @Override
    public void registerListeners() {
        super.registerListeners();
        MinecraftForge.EVENT_BUS.register(PlayerCameraListener.class);
        MinecraftForge.EVENT_BUS.register(EntityRenderListener.class);
        MinecraftForge.EVENT_BUS.register(ItemTooltipListener.class);
        MinecraftForge.EVENT_BUS.register(FallOutOfWorldUpwardsListenerClient.class);
    }

    @Override
    public Collection<?> createSidedEventListeners() {
        return Lists.newArrayList(new FallOutOfWorldUpwardsListenerClient());
    }
}
