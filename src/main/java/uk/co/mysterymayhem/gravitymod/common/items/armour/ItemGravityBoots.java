package uk.co.mysterymayhem.gravitymod.common.items.armour;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import uk.co.mysterymayhem.gravitymod.api.IWeakGravityEnabler;
import uk.co.mysterymayhem.gravitymod.common.items.materials.ItemGravityIngot;
import uk.co.mysterymayhem.gravitymod.common.registries.IGravityModItem;
import uk.co.mysterymayhem.gravitymod.common.registries.StaticRegistry;

/**
 * Created by Mysteryem on 2016-08-08.
 */
public class ItemGravityBoots extends ItemArmor implements IGravityModItem<ItemGravityBoots>, IWeakGravityEnabler {

    public ItemGravityBoots() {
        super(ItemGravityIngot.ARMOUR_MATERIAL, 2, EntityEquipmentSlot.FEET);
    }

    @Override
    public String getName() {
        return "gravityboots";
    }

    @Override
    public void postInit() {
        GameRegistry.addRecipe(
                new ItemStack(this),
                "I I",
                "I I",
                'I', StaticRegistry.gravityIngot);
    }
}
