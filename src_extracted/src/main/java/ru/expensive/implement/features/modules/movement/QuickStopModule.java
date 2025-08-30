package ru.expensive.implement.features.modules.movement;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.minecraft.util.math.Vec3d;
import ru.expensive.api.event.EventHandler;
import ru.expensive.api.feature.module.setting.implement.BooleanSetting;
import ru.expensive.implement.events.player.TickEvent;
import ru.expensive.api.feature.module.Module;
import ru.expensive.api.feature.module.ModuleCategory;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QuickStopModule extends Module {

    BooleanSetting onlyOnGroundSetting = new BooleanSetting("Only on Ground", "Only stop movement when on the ground")
            .setValue(true);

    public QuickStopModule() {
        super("QuickStop", "Quick Stop", ModuleCategory.MOVEMENT);
        setup(onlyOnGroundSetting);
    }

    @EventHandler
    public void onTick(TickEvent tickEvent) {
        if (mc.player == null || mc.player.input == null) return;

        boolean isMoving = mc.player.input.movementForward != 0 || mc.player.input.movementSideways != 0;

        if (!isMoving && (mc.player.isOnGround() || !onlyOnGroundSetting.isValue())) {
            stopMovement();
        }
    }

    private void stopMovement() {
        Vec3d velocity = mc.player.getVelocity();
        mc.player.setVelocity(onlyOnGroundSetting.isValue() && !mc.player.isOnGround()
                ? new Vec3d(0, velocity.y, 0)
                : Vec3d.ZERO);
    }
}
