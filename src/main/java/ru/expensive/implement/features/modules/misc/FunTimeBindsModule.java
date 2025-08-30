package ru.expensive.implement.features.modules.misc;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import org.lwjgl.glfw.GLFW;
import ru.expensive.api.event.EventHandler;
import ru.expensive.api.feature.module.Module;
import ru.expensive.api.feature.module.ModuleCategory;
import ru.expensive.api.feature.module.setting.Setting;
import ru.expensive.api.feature.module.setting.implement.BindSetting;
import ru.expensive.api.feature.module.setting.implement.GroupSetting;
import ru.expensive.common.util.math.Counter;
import ru.expensive.implement.events.player.TickEvent;

import java.util.HashMap;
import java.util.Map;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class FunTimeBindsModule extends Module {

    Counter counter = Counter.create();
    Runnable itemUseAction;
    Integer initialSlot = null;
    Action action = Action.START;

    GroupSetting bindGroup = new GroupSetting("Use by bind", "Uses server items by bind")
            .settings(
                    new BindSetting("«Дезориентация»", "Bind to use «Дезориентация» item"),
                    new BindSetting("«Трапка»", "Bind to use «Трапка» item"),
                    new BindSetting("«Явная пыль»", "Bind to use «Явная пыль» item"),
                    new BindSetting("«Божья аура»", "Bind to use «Божья аура» item"),
                    new BindSetting("«Пласт»", "Bind to use «Пласт» item"),
                    new BindSetting("«Снежок заморозка»", "Bind to use «Снежок заморозка» item")
            );

    String[] itemKeywords = {"Дезориентация", "Трапка", "Явная пыль", "Божья аура", "Пласт", "Снежок заморозка"};
    Map<BindSetting, Boolean> keyState = new HashMap<>();

    public FunTimeBindsModule() {
        super("FunTimeBinds", "FunTime Binds", ModuleCategory.MISC);
        setup(bindGroup);
        for (Setting setting : bindGroup.getSubSettings()) {
            if (setting instanceof BindSetting bind) {
                keyState.put(bind, false);
            }
        }
    }

    @EventHandler
    public void onTick(TickEvent tickEvent) {
        if (mc.currentScreen != null || !bindGroup.isValue()) {
            return;
        }

        if (itemUseAction != null) {
            itemUseAction.run();
        } else {
            int index = 0;
            for (Setting setting : bindGroup.getSubSettings()) {
                if (setting instanceof BindSetting bind) {
                    boolean isPressed = isKeyPressed(bind);
                    if (isPressed && !keyState.get(bind)) {
                        startItemUse(itemKeywords[index]);
                    }
                    keyState.put(bind, isPressed);
                    index++;
                }
            }
        }
    }

    private void startItemUse(String itemKeyword) {
        action = Action.START;
        itemUseAction = () -> performItemUse(itemKeyword);
        counter.resetCounter();
    }

    private void performItemUse(String itemKeyword) {
        Integer itemSlot = findItemSlotByKeyword(itemKeyword);
        if (itemSlot != null) {
            switch (action) {
                case START -> {
                    initialSlot = mc.player.getInventory().selectedSlot;
                    switchSlotIfNeeded(itemSlot);
                    action = Action.WAIT;
                }
                case WAIT -> action = Action.USE_ITEM;
                case USE_ITEM -> {
                    interactWithItem(Hand.MAIN_HAND);
                    switchSlotIfNeeded(initialSlot);
                    itemUseAction = null;
                    action = Action.START;
                }
            }
        } else {
            logDirect("«" + itemKeyword + "» не найден в хотбаре.", Formatting.RED);
            itemUseAction = null;
        }
    }

    private Integer findItemSlotByKeyword(String keyword) {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.getName().getString().toLowerCase().contains(keyword.toLowerCase())) return i;
        }
        return null;
    }

    private void switchSlotIfNeeded(int slot) {
        if (slot != mc.player.getInventory().selectedSlot) {
            mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(slot));
        }
    }

    private void interactWithItem(Hand hand) {
        if (!mc.player.getItemCooldownManager().isCoolingDown(mc.player.getMainHandStack().getItem())) {
            mc.interactionManager.sendSequencedPacket(mc.world, sequence -> new PlayerInteractItemC2SPacket(hand, sequence));
            mc.player.swingHand(hand);
        }
    }

    private boolean isKeyPressed(BindSetting bindSetting) {
        return GLFW.glfwGetKey(mc.getWindow().getHandle(), bindSetting.getKey()) == GLFW.GLFW_PRESS;
    }

    public enum Action { START, WAIT, USE_ITEM }
}
