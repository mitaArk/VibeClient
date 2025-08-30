package ru.expensive.implement.features.modules.movement;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.Setter;
import net.minecraft.entity.effect.StatusEffects;
import ru.expensive.api.event.EventHandler;
import ru.expensive.api.feature.module.setting.implement.BooleanSetting;
import ru.expensive.implement.events.player.KeepSprintEvent;
import ru.expensive.implement.events.player.TickEvent;
import ru.expensive.api.feature.module.Module;
import ru.expensive.api.feature.module.ModuleCategory;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AutoSprintModule extends Module {

    @Setter
    @NonFinal
    boolean emergencyStop = false;

    BooleanSetting keepSprintSetting = new BooleanSetting("Keep Sprint", "Keep sprint even before impact, thus not slowing you down")
            .setValue(true);
    BooleanSetting ignoreBlindnessSetting = new BooleanSetting("Ignore Blindness", "Allows sprinting even with blindness effect")
            .setValue(false);

    boolean wasSprinting = false;

    public AutoSprintModule() {
        super("AutoSprint", "Auto Sprint", ModuleCategory.MOVEMENT);
        setup(keepSprintSetting, ignoreBlindnessSetting);
    }

    @EventHandler
    public void onTick(TickEvent tickEvent) {
        boolean canSprint = canStartSprinting();

        // Запускаем спринт только в случае необходимости
        if (canSprint && !mc.player.horizontalCollision) {
            if (!mc.player.isSprinting()) {
                mc.player.setSprinting(true);
            }
        } else if (!canSprint || emergencyStop) {
            if (mc.player.isSprinting()) {
                mc.player.setSprinting(false);
            }
        }

        // Отменяем emergencyStop на следующем тике
        emergencyStop = false;
    }

    @EventHandler
    public void onKeepSprint(KeepSprintEvent keepSprintEvent) {
        if (keepSprintSetting.isValue() && !mc.player.horizontalCollision && !mc.player.isSprinting()) {
            mc.player.setSprinting(true);
        }
    }

    @Override
    public void deactivate() {
        if (mc.player.isSprinting()) {
            mc.player.setSprinting(false);
        }
        super.deactivate();
    }

    private boolean canStartSprinting() {
        // Игрок может начать спринт, если он движется вперёд и его состояние позволяет
        return (mc.player.input.movementForward > 0 || mc.player.isSwimming()) &&
                (ignoreBlindnessSetting.isValue() || !mc.player.hasStatusEffect(StatusEffects.BLINDNESS)) &&
                !mc.player.isFallFlying() &&
                !mc.player.hasVehicle() &&
                mc.player.getHungerManager().getFoodLevel() > 6;
    }

    private boolean canSprint() {
        // Игрок может продолжить спринт, если у него достаточно еды или если он в воде или в воздухе
        return mc.player.getHungerManager().getFoodLevel() > 6 || mc.player.isSwimming() || mc.player.getAbilities().allowFlying;
    }
}
