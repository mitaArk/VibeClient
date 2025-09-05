package from.Vibe.utils.auction.ab;

import from.Vibe.Vibe;
import from.Vibe.utils.Wrapper;
import from.Vibe.utils.auction.nbt.NbtUtils;
import lombok.experimental.UtilityClass;
import net.minecraft.item.ItemStack;

@UtilityClass
public class ABItems implements Wrapper {

    //Пиздак блять)))) круш блядский
    public ItemStack krushHelmet() {
        return NbtUtils.loadItemStack("krushHelmet", Vibe.getInstance().getAbItemsDir(), mc.getNetworkHandler().getRegistryManager());
    }

    public ItemStack krushChestplate() {
        return NbtUtils.loadItemStack("krushChestplate", Vibe.getInstance().getAbItemsDir(), mc.getNetworkHandler().getRegistryManager());
    }

    public ItemStack krushLeggings() {
        return NbtUtils.loadItemStack("krushLeggings", Vibe.getInstance().getAbItemsDir(), mc.getNetworkHandler().getRegistryManager());
    }

    public ItemStack krushBoots() {
        return NbtUtils.loadItemStack("krushBoots", Vibe.getInstance().getAbItemsDir(), mc.getNetworkHandler().getRegistryManager());
    }

    public ItemStack krushSword() {
        return NbtUtils.loadItemStack("krushSword", Vibe.getInstance().getAbItemsDir(), mc.getNetworkHandler().getRegistryManager());
    }

    public ItemStack krushPickaxe() {
        return NbtUtils.loadItemStack("krushPickaxe", Vibe.getInstance().getAbItemsDir(), mc.getNetworkHandler().getRegistryManager());
    }

    public ItemStack krushTrident() {
        return NbtUtils.loadItemStack("krushTrident", Vibe.getInstance().getAbItemsDir(), mc.getNetworkHandler().getRegistryManager());
    }

    public ItemStack krushCrossbow() {
        return NbtUtils.loadItemStack("krushCrossbow", Vibe.getInstance().getAbItemsDir(), mc.getNetworkHandler().getRegistryManager());
    }

    //Бафы
    public ItemStack serka() {
        return NbtUtils.loadItemStack("serka", Vibe.getInstance().getAbItemsDir(), mc.getNetworkHandler().getRegistryManager());
    }

    public ItemStack agentka() {
        return NbtUtils.loadItemStack("agentka", Vibe.getInstance().getAbItemsDir(), mc.getNetworkHandler().getRegistryManager());
    }

    public ItemStack killerka() {
        return NbtUtils.loadItemStack("killerka", Vibe.getInstance().getAbItemsDir(), mc.getNetworkHandler().getRegistryManager());
    }

    public ItemStack otrizhka() {
        return NbtUtils.loadItemStack("otrizhka", Vibe.getInstance().getAbItemsDir(), mc.getNetworkHandler().getRegistryManager());
    }

    public ItemStack medika() {
        return NbtUtils.loadItemStack("medika", Vibe.getInstance().getAbItemsDir(), mc.getNetworkHandler().getRegistryManager());
    }

    public ItemStack pobedilka() {
        return NbtUtils.loadItemStack("pobedilka", Vibe.getInstance().getAbItemsDir(), mc.getNetworkHandler().getRegistryManager());
    }

    //Стрелы
    public ItemStack proklyatayaStrela() {
        return NbtUtils.loadItemStack("proklyatayaStrela", Vibe.getInstance().getAbItemsDir(), mc.getNetworkHandler().getRegistryManager());
    }

    public ItemStack adskayaStrela() {
        return NbtUtils.loadItemStack("adskayaStrela", Vibe.getInstance().getAbItemsDir(), mc.getNetworkHandler().getRegistryManager());
    }

    public ItemStack paranoiaStrela() {
        return NbtUtils.loadItemStack("paranoiaStrela", Vibe.getInstance().getAbItemsDir(), mc.getNetworkHandler().getRegistryManager());
    }

    public ItemStack snezhnayaStrela() {
        return NbtUtils.loadItemStack("snezhnayaStrela", Vibe.getInstance().getAbItemsDir(), mc.getNetworkHandler().getRegistryManager());
    }

    //Отмычки
    public ItemStack otmichkaArmor() {
        return NbtUtils.loadItemStack("otmichkaArmor", Vibe.getInstance().getAbItemsDir(), mc.getNetworkHandler().getRegistryManager());
    }

    public ItemStack otmichkaResources() {
        return NbtUtils.loadItemStack("otmichkaResources", Vibe.getInstance().getAbItemsDir(), mc.getNetworkHandler().getRegistryManager());
    }

    public ItemStack otmichkaSpheres() {
        return NbtUtils.loadItemStack("otmichkaSpheres", Vibe.getInstance().getAbItemsDir(), mc.getNetworkHandler().getRegistryManager());
    }

    public ItemStack otmichkaTools() {
        return NbtUtils.loadItemStack("otmichkaTools", Vibe.getInstance().getAbItemsDir(), mc.getNetworkHandler().getRegistryManager());
    }

    public ItemStack otmichkaWeapons() {
        return NbtUtils.loadItemStack("otmichkaWeapons", Vibe.getInstance().getAbItemsDir(), mc.getNetworkHandler().getRegistryManager());
    }

    //Динамиты
    public ItemStack tierBlack() {
        return NbtUtils.loadItemStack("tierBlack", Vibe.getInstance().getAbItemsDir(), mc.getNetworkHandler().getRegistryManager());
    }

    public ItemStack tierWhite() {
        return NbtUtils.loadItemStack("tierWhite", Vibe.getInstance().getAbItemsDir(), mc.getNetworkHandler().getRegistryManager());
    }

    //Расходники
    public ItemStack desor() {
        return NbtUtils.loadItemStack("desor", Vibe.getInstance().getAbItemsDir(), mc.getNetworkHandler().getRegistryManager());
    }

    public ItemStack plast() {
        return NbtUtils.loadItemStack("plast", Vibe.getInstance().getAbItemsDir(), mc.getNetworkHandler().getRegistryManager());
    }

    public ItemStack bozhka() {
        return NbtUtils.loadItemStack("bozhka", Vibe.getInstance().getAbItemsDir(), mc.getNetworkHandler().getRegistryManager());
    }

    public ItemStack snezhok() {
        return NbtUtils.loadItemStack("snezhok", Vibe.getInstance().getAbItemsDir(), mc.getNetworkHandler().getRegistryManager());
    }

    public ItemStack trapka() {
        return NbtUtils.loadItemStack("trapka", Vibe.getInstance().getAbItemsDir(), mc.getNetworkHandler().getRegistryManager());
    }

    public ItemStack yavka() {
        return NbtUtils.loadItemStack("yavka", Vibe.getInstance().getAbItemsDir(), mc.getNetworkHandler().getRegistryManager());
    }

    //Талики
    public ItemStack dedalaTier1() {
        return NbtUtils.loadItemStack("dedalaTier1", Vibe.getInstance().getAbItemsDir(), mc.getNetworkHandler().getRegistryManager());
    }

    public ItemStack dedalaTier2() {
        return NbtUtils.loadItemStack("dedalaTier2", Vibe.getInstance().getAbItemsDir(), mc.getNetworkHandler().getRegistryManager());
    }

    public ItemStack dedalaTier3() {
        return NbtUtils.loadItemStack("dedalaTier3", Vibe.getInstance().getAbItemsDir(), mc.getNetworkHandler().getRegistryManager());
    }

    public ItemStack exidnaTier1() {
        return NbtUtils.loadItemStack("exidnaTier1", Vibe.getInstance().getAbItemsDir(), mc.getNetworkHandler().getRegistryManager());
    }

    public ItemStack exidnaTier2() {
        return NbtUtils.loadItemStack("exidnaTier2", Vibe.getInstance().getAbItemsDir(), mc.getNetworkHandler().getRegistryManager());
    }

    public ItemStack exidnaTier3() {
        return NbtUtils.loadItemStack("exidnaTier3", Vibe.getInstance().getAbItemsDir(), mc.getNetworkHandler().getRegistryManager());
    }

    public ItemStack garmoniiTier1() {
        return NbtUtils.loadItemStack("garmoniiTier1", Vibe.getInstance().getAbItemsDir(), mc.getNetworkHandler().getRegistryManager());
    }

    public ItemStack garmoniiTier2() {
        return NbtUtils.loadItemStack("garmoniiTier2", Vibe.getInstance().getAbItemsDir(), mc.getNetworkHandler().getRegistryManager());
    }

    public ItemStack garmoniiTier3() {
        return NbtUtils.loadItemStack("garmoniiTier3", Vibe.getInstance().getAbItemsDir(), mc.getNetworkHandler().getRegistryManager());
    }

    public ItemStack graniTier1() {
        return NbtUtils.loadItemStack("graniTier1", Vibe.getInstance().getAbItemsDir(), mc.getNetworkHandler().getRegistryManager());
    }

    public ItemStack graniTier2() {
        return NbtUtils.loadItemStack("graniTier2", Vibe.getInstance().getAbItemsDir(), mc.getNetworkHandler().getRegistryManager());
    }

    public ItemStack graniTier3() {
        return NbtUtils.loadItemStack("graniTier3", Vibe.getInstance().getAbItemsDir(), mc.getNetworkHandler().getRegistryManager());
    }

    public ItemStack haronTier1() {
        return NbtUtils.loadItemStack("haronTier1", Vibe.getInstance().getAbItemsDir(), mc.getNetworkHandler().getRegistryManager());
    }

    public ItemStack haronTier2() {
        return NbtUtils.loadItemStack("haronTier2", Vibe.getInstance().getAbItemsDir(), mc.getNetworkHandler().getRegistryManager());
    }

    public ItemStack haronTier3() {
        return NbtUtils.loadItemStack("haronTier3", Vibe.getInstance().getAbItemsDir(), mc.getNetworkHandler().getRegistryManager());
    }

    public ItemStack phoenixTier1() {
        return NbtUtils.loadItemStack("phoenixTier1", Vibe.getInstance().getAbItemsDir(), mc.getNetworkHandler().getRegistryManager());
    }

    public ItemStack phoenixTier2() {
        return NbtUtils.loadItemStack("phoenixTier2", Vibe.getInstance().getAbItemsDir(), mc.getNetworkHandler().getRegistryManager());
    }

    public ItemStack phoenixTier3() {
        return NbtUtils.loadItemStack("phoenixTier3", Vibe.getInstance().getAbItemsDir(), mc.getNetworkHandler().getRegistryManager());
    }

    public ItemStack tritonTier1() {
        return NbtUtils.loadItemStack("tritonTier1", Vibe.getInstance().getAbItemsDir(), mc.getNetworkHandler().getRegistryManager());
    }

    public ItemStack tritonTier2() {
        return NbtUtils.loadItemStack("tritonTier2", Vibe.getInstance().getAbItemsDir(), mc.getNetworkHandler().getRegistryManager());
    }

    public ItemStack tritonTier3() {
        return NbtUtils.loadItemStack("tritonTier3", Vibe.getInstance().getAbItemsDir(), mc.getNetworkHandler().getRegistryManager());
    }

    public ItemStack krush() {
        return NbtUtils.loadItemStack("krush", Vibe.getInstance().getAbItemsDir(), mc.getNetworkHandler().getRegistryManager());
    }

    public ItemStack karatel() {
        return NbtUtils.loadItemStack("karatel", Vibe.getInstance().getAbItemsDir(), mc.getNetworkHandler().getRegistryManager());
    }

    //Сферы
    public ItemStack andromedaTier1() {
        return NbtUtils.loadItemStack("andromedaTier1", Vibe.getInstance().getAbItemsDir(), mc.getNetworkHandler().getRegistryManager());
    }

    public ItemStack andromedaTier2() {
        return NbtUtils.loadItemStack("andromedaTier2", Vibe.getInstance().getAbItemsDir(), mc.getNetworkHandler().getRegistryManager());
    }

    public ItemStack andromedaTier3() {
        return NbtUtils.loadItemStack("andromedaTier3", Vibe.getInstance().getAbItemsDir(), mc.getNetworkHandler().getRegistryManager());
    }

    public ItemStack apollonaTier1() {
        return NbtUtils.loadItemStack("apollonaTier1", Vibe.getInstance().getAbItemsDir(), mc.getNetworkHandler().getRegistryManager());
    }

    public ItemStack apollonaTier2() {
        return NbtUtils.loadItemStack("apollonaTier2", Vibe.getInstance().getAbItemsDir(), mc.getNetworkHandler().getRegistryManager());
    }

    public ItemStack apollonaTier3() {
        return NbtUtils.loadItemStack("apollonaTier3", Vibe.getInstance().getAbItemsDir(), mc.getNetworkHandler().getRegistryManager());
    }

    public ItemStack astreaTier1() {
        return NbtUtils.loadItemStack("astreaTier1", Vibe.getInstance().getAbItemsDir(), mc.getNetworkHandler().getRegistryManager());
    }

    public ItemStack astreaTier2() {
        return NbtUtils.loadItemStack("astreaTier2", Vibe.getInstance().getAbItemsDir(), mc.getNetworkHandler().getRegistryManager());
    }

    public ItemStack astreaTier3() {
        return NbtUtils.loadItemStack("astreaTier3", Vibe.getInstance().getAbItemsDir(), mc.getNetworkHandler().getRegistryManager());
    }

    public ItemStack himeraTier1() {
        return NbtUtils.loadItemStack("himeraTier1", Vibe.getInstance().getAbItemsDir(), mc.getNetworkHandler().getRegistryManager());
    }

    public ItemStack himeraTier2() {
        return NbtUtils.loadItemStack("himeraTier2", Vibe.getInstance().getAbItemsDir(), mc.getNetworkHandler().getRegistryManager());
    }

    public ItemStack himeraTier3() {
        return NbtUtils.loadItemStack("himeraTier3", Vibe.getInstance().getAbItemsDir(), mc.getNetworkHandler().getRegistryManager());
    }

    public ItemStack osirisaTier1() {
        return NbtUtils.loadItemStack("osirisaTier1", Vibe.getInstance().getAbItemsDir(), mc.getNetworkHandler().getRegistryManager());
    }

    public ItemStack osirisaTier2() {
        return NbtUtils.loadItemStack("osirisaTier2", Vibe.getInstance().getAbItemsDir(), mc.getNetworkHandler().getRegistryManager());
    }

    public ItemStack osirisaTier3() {
        return NbtUtils.loadItemStack("osirisaTier3", Vibe.getInstance().getAbItemsDir(), mc.getNetworkHandler().getRegistryManager());
    }

    public ItemStack pandoraTier1() {
        return NbtUtils.loadItemStack("pandoraTier1", Vibe.getInstance().getAbItemsDir(), mc.getNetworkHandler().getRegistryManager());
    }

    public ItemStack pandoraTier2() {
        return NbtUtils.loadItemStack("pandoraTier2", Vibe.getInstance().getAbItemsDir(), mc.getNetworkHandler().getRegistryManager());
    }

    public ItemStack pandoraTier3() {
        return NbtUtils.loadItemStack("pandoraTier3", Vibe.getInstance().getAbItemsDir(), mc.getNetworkHandler().getRegistryManager());
    }

    public ItemStack titanTier1() {
        return NbtUtils.loadItemStack("titanTier1", Vibe.getInstance().getAbItemsDir(), mc.getNetworkHandler().getRegistryManager());
    }

    public ItemStack titanTier2() {
        return NbtUtils.loadItemStack("titanTier2", Vibe.getInstance().getAbItemsDir(), mc.getNetworkHandler().getRegistryManager());
    }

    public ItemStack titanTier3() {
        return NbtUtils.loadItemStack("titanTier3", Vibe.getInstance().getAbItemsDir(), mc.getNetworkHandler().getRegistryManager());
    }
}