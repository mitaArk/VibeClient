package ru.expensive.implement.features.modules.misc;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import ru.expensive.api.event.EventHandler;
import ru.expensive.api.feature.module.Module;
import ru.expensive.api.feature.module.ModuleCategory;
import ru.expensive.api.feature.module.setting.implement.BindSetting;
import ru.expensive.common.util.player.InventoryHandler;
import ru.expensive.common.util.player.MovingUtil;
import ru.expensive.implement.events.keyboard.KeyEvent;
import ru.expensive.implement.events.player.MovementInputEvent;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class ElytraHelperModule extends Module {
    final BindSetting swapKey = new BindSetting("Swap bind", "Swaps Elytra with Chestplate on key press");
    final BindSetting fireworkKey = new BindSetting("Firework bind", "Activates Firework when pressed");

    int swapTicks;
    boolean swapping;

    public ElytraHelperModule() {
        super("ElytraHelperModule", "Elytra Helper", ModuleCategory.PLAYER);
        setup(swapKey, fireworkKey);
    }

    @EventHandler
    public void onKey(KeyEvent keyEvent) {
        if (!isWorldLoaded()) {
            return;
        }

        if (keyEvent.isKeyDown(swapKey.getKey())) {
            swapping = true;
        }

        if (keyEvent.isKeyDown(fireworkKey.getKey())) {
            useFirework();
        }
    }

    @EventHandler
    public void onInput(MovementInputEvent event) {
        if (!isWorldLoaded()) {
            return;
        }

        if (swapping) {
            if (swapTicks > 1) {
                swapElytra();
                swapTicks = 0;
                swapping = false;
            }

            event.setDirectionalInput(MovingUtil.DirectionalInput.NONE);

            swapTicks++;
        }
    }

    private void swapElytra() {
        ClientPlayerEntity localPlayer = mc.player;

        if (localPlayer == null) {
            return;
        }

        ItemStack currentChestItemStack = localPlayer.getInventory().armor.get(2);
        Item currentChestItem = currentChestItemStack.getItem();
        boolean isWearingElytra = currentChestItem == Items.ELYTRA;

        int targetSlot = isWearingElytra
                ? InventoryHandler.getChestplate()
                : InventoryHandler.getElytra();

        if (targetSlot == -1) {
            logDirect(Formatting.RED + (isWearingElytra ? "Chestplate не найден." : "Elytra не найдена."));
            return;
        }

        ClientPlayerInteractionManager interactionManager = mc.interactionManager;

        if (interactionManager == null) {
            return;
        }

        int syncId = localPlayer.currentScreenHandler.syncId;

        int armorSlotIndex = 38;

        mc.interactionManager.clickSlot(syncId, targetSlot, armorSlotIndex, SlotActionType.SWAP, localPlayer);

        String itemName = currentChestItemStack.getName().getString();

        logDirect(Formatting.WHITE + "Свапнул на " + Formatting.RED + itemName);
    }

    private void useFirework() {
        ClientPlayerEntity localPlayer = mc.player;

        if (localPlayer == null) {
            return;
        }

        int previousSlot = mc.player.getInventory().selectedSlot;

        int fireworkSlot = findFireworkSlot();

        if (fireworkSlot == -1) {
            logDirect(Formatting.RED + "Фейерверк не найден.");
            return;
        }

        mc.player.getInventory().selectedSlot = fireworkSlot;

        mc.interactionManager.interactItem(localPlayer, Hand.MAIN_HAND);

        mc.player.getInventory().selectedSlot = previousSlot;
    }

    private int findFireworkSlot() {
        for (int i = 0; i < mc.player.getInventory().size(); i++) {
            if (mc.player.getInventory().getStack(i).getItem() == Items.FIREWORK_ROCKET) {
                return i;
            }
        }
        return -1;
    }

    private boolean isWorldLoaded() {
        return mc.world != null && mc.player != null && mc.interactionManager != null;
    }
}