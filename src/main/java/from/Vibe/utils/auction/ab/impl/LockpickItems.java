package from.Vibe.utils.auction.ab.impl;

import from.Vibe.utils.auction.ab.ABItems;
import lombok.experimental.UtilityClass;
import net.minecraft.item.ItemStack;

@UtilityClass
public class LockpickItems {

    public ItemStack getOtmichkaArmor() {
        return ABItems.otmichkaArmor();
    }

    public ItemStack getOtmichkaResources() {
        return ABItems.otmichkaResources();
    }

    public ItemStack getOtmichkaSpheres() {
        return ABItems.otmichkaSpheres();
    }

    public ItemStack getOtmichkaTools() {
        return ABItems.otmichkaTools();
    }

    public ItemStack getOtmichkaWeapons() {
        return ABItems.otmichkaWeapons();
    }
}