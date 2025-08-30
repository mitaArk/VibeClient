package ru.expensive.implement.features.modules.combat.killaura.rotation.angle;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import ru.expensive.common.util.math.MathUtil;
import ru.expensive.implement.features.modules.combat.killaura.rotation.Angle;
import ru.expensive.implement.features.modules.combat.killaura.rotation.AngleUtil;

public class AdaptiveSmoothMode extends AngleSmoothMode {
    public AdaptiveSmoothMode() {
        super("Adaptive");
    }

    @Override
    public Angle limitAngleChange(Angle currentAngle, Angle targetAngle, Vec3d vec3d, Entity entity) {
        Angle angleDelta = AngleUtil.calculateDelta(currentAngle, targetAngle);
        float yawDelta = angleDelta.getYaw();
        float pitchDelta = angleDelta.getPitch();

        float rotationDifference = (float) Math.hypot(Math.abs(yawDelta), Math.abs(pitchDelta));

        float straightLineYaw = Math.abs(yawDelta / rotationDifference) * 180;
        float straightLinePitch = Math.abs(pitchDelta / rotationDifference) * 180;

        float moveYaw = Math.min(Math.max(yawDelta, -straightLineYaw), straightLineYaw);
        float movePitch = Math.min(Math.max(pitchDelta, -straightLinePitch), straightLinePitch);

        float newYaw = currentAngle.getYaw() + moveYaw;
        float newPitch = currentAngle.getPitch() + movePitch;

        return new Angle(newYaw, newPitch);
    }

    @Override
    public Vec3d randomValue() {
        return new Vec3d(0.05F, 0.0F, 0.05F);
    }
}
