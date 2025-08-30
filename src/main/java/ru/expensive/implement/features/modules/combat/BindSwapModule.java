package ru.expensive.implement.features.modules.combat;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Formatting;
import ru.expensive.api.event.EventHandler;
import ru.expensive.api.feature.module.Module;
import ru.expensive.api.feature.module.ModuleCategory;
import ru.expensive.api.feature.module.setting.implement.BindSetting;
import ru.expensive.api.feature.module.setting.implement.SelectSetting;
import ru.expensive.common.util.player.InventoryHandler;
import ru.expensive.common.util.player.MovingUtil;
import ru.expensive.implement.events.keyboard.KeyEvent;
import ru.expensive.implement.events.player.MovementInputEvent;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class BindSwapModule extends Module {
    final SelectSetting firstItemSetting = new SelectSetting("First item", "Select first swap item.")
            .value("Shield", "Sphere", "Totem", "GApple");

    final SelectSetting secondItemSetting = new SelectSetting("Second item", "Select second swap item.")
            .value("Shield", "Sphere", "Totem", "GApple");

    final BindSetting bindSetting = new BindSetting("Item use key", "Uses item when pressed");

    int swapTicks;
    boolean swapping;

    public BindSwapModule() {
        super("BindSwap", "Bind Swap", ModuleCategory.COMBAT);
        setup(firstItemSetting, secondItemSetting, bindSetting);
    }

    @EventHandler
    public void onKey(KeyEvent keyEvent) {
        if (!isWorldLoaded()) {
            return;
        }

        if (keyEvent.isKeyDown(bindSetting.getKey())) {
            swapping = true;
        }
    }

    @EventHandler
    public void onInput(MovementInputEvent event) {
        if (!isWorldLoaded()) {
            return;
        }

        if (swapping) {
            if (swapTicks > 1) {
                swap();
                swapTicks = 0;
                swapping = false;
            }

            event.setDirectionalInput(MovingUtil.DirectionalInput.NONE);

            swapTicks++;
        }
    }

    private void swap() {
        ClientPlayerEntity localPlayer = mc.player;

        if (localPlayer == null) {
            return;
        }

        Item swapFromItem = getItemByName(firstItemSetting.getSelected());
        Item swapToItem = getItemByName(secondItemSetting.getSelected());

        Item itemToSwap = computeItem(localPlayer, swapFromItem, swapToItem);

        if (itemToSwap == null) {
            return;
        }

        int slot = InventoryHandler.findItemSlot(itemToSwap);

        if (slot == -1) {
            logDirect(Formatting.RED + "Предмет не найден");
            return;
        }

        ClientPlayerInteractionManager interactionManager = mc.interactionManager;

        if (interactionManager == null) {
            return;
        }

        int syncId = localPlayer.currentScreenHandler.syncId;

        mc.interactionManager.clickSlot(syncId, slot, 40, SlotActionType.SWAP, localPlayer);
    }

    private boolean isWorldLoaded() {
        return mc.world != null && mc.player != null && mc.interactionManager != null;
    }

    private Item computeItem(ClientPlayerEntity localPlayer, Item firstItem, Item secondItem) {
        return localPlayer.getOffHandStack().getItem() == firstItem ? secondItem : firstItem;
    }

    private Item getItemByName(String name) {
        return switch (name) {
            case "Shield" -> Items.SHIELD;
            case "Sphere" -> Items.PLAYER_HEAD;
            case "Totem" -> Items.TOTEM_OF_UNDYING;
            case "GApple" -> Items.GOLDEN_APPLE;
            default -> null;
        };
    }
}
