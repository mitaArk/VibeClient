package ru.expensive.implement.features.modules.combat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;
import org.joml.Math;
import ru.expensive.api.event.EventHandler;
import ru.expensive.api.feature.module.Module;
import ru.expensive.api.feature.module.ModuleCategory;
import ru.expensive.api.feature.module.setting.implement.*;
import ru.expensive.common.util.task.TaskPriority;
import ru.expensive.core.Expensive;
import ru.expensive.implement.events.player.PostRotationMovementInputEvent;
import ru.expensive.implement.events.player.TickEvent;
import ru.expensive.implement.features.commands.defaults.DebugCommand;
import ru.expensive.implement.features.modules.combat.killaura.attack.AttackHandler;
import ru.expensive.implement.features.modules.combat.killaura.attack.AttackPerpetrator;
import ru.expensive.implement.features.modules.combat.killaura.attack.ClickScheduler;
import ru.expensive.implement.features.modules.combat.killaura.attack.SprintManager;
import ru.expensive.implement.features.modules.combat.killaura.rotation.*;
import ru.expensive.implement.features.modules.combat.killaura.rotation.angle.*;
import ru.expensive.implement.features.modules.combat.killaura.target.TargetSelector;
import ru.expensive.implement.events.render.DrawEvent;
import ru.expensive.api.feature.module.setting.implement.ColorSetting;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuraModule extends Module implements ru.expensive.common.QuickImports {
    TargetSelector targetSelector = new TargetSelector();
    PointFinder pointFinder = new PointFinder();

    @NonFinal
    LivingEntity target = null;

    ValueSetting maxDistanceSetting = new ValueSetting("Max Distance", "Sets the value of the maximum target search distance")
            .setValue(3.0F).range(1.0F, 6.0F);

    ValueSetting aimDistanceSetting = new ValueSetting("Aim Distance", "Дистанция наводки - за сколько блоков киллаура будет видеть сущности")
            .setValue(180.0F).range(30.0F, 360.0F);

    MultiSelectSetting targetTypeSetting = new MultiSelectSetting("Target Type", "Filters the entire list of targets by type")
            .value("Players", "Mobs", "Animals", "Friends");

    MultiSelectSetting attackSetting = new MultiSelectSetting("Attack setting", "Allows you to customize the attack")
            .value("Only Critical", "Raytrace check", "Dynamic Cooldown", "Break Shield", "Un Press Shield");

    SelectSetting correctionType = new SelectSetting("Correction Type", "Selects the type of correction")
            .value("Free", "Focused");

    GroupSetting correctionGroupSetting = new GroupSetting("Move correction", "Prevents detection by movement sensitive anticheats.")
            .settings(correctionType);

    SelectSetting sprintMode = new SelectSetting("Sprint Mode", "Allows you to select a sprint mod")
            .value("Bypass", "Default", "None", "Legit");

    SelectSetting aimMode = new SelectSetting("Aim Time", "Allows you to select the timing of the rotation")
            .value("Normal", "Snap", "One Tick");

    SelectSetting rotationModeSetting = new SelectSetting("Rotation Mode", "Select the mode for aim rotation correction")
            .value("ReallyWorld", "Adaptive", "FunTime", "SpookyTime", "HolyWorld");

    BooleanSetting legitFovSetting = new BooleanSetting("Legit Fov", "Включить легитный режим Fov").setValue(false);
    ValueSetting legitFovWidthSetting = new ValueSetting("Fov Width", "Ширина Fov").setValue(90f).range(30f, 170f).visible(legitFovSetting::isValue);
    ColorSetting legitFovColorSetting = new ColorSetting("Fov Color", "Цвет круга Fov").value(0xFF8187FF).presets().visible(legitFovSetting::isValue);

    AttackPerpetrator attackPerpetrator = new AttackPerpetrator();

    public AuraModule() {
        super("Aura", ModuleCategory.COMBAT);
        setup(maxDistanceSetting, aimDistanceSetting, targetTypeSetting, attackSetting, correctionGroupSetting, sprintMode, aimMode, rotationModeSetting, legitFovSetting, legitFovWidthSetting, legitFovColorSetting);
    }

    @Override
    public void deactivate() {
        targetSelector.releaseTarget();
        target = null;
        super.deactivate();
    }

    @EventHandler
    public void onPostRotationMovementInput(PostRotationMovementInputEvent postRotationMovementInputEvent) {
        target = updateTarget();
        if (target != null) {
            RotationController rotationController = RotationController.INSTANCE;
            Vec3d attackVector = pointFinder.computeVector(target, maxDistanceSetting.getValue(), rotationController.getRotation(),
                    getSmoothMode().randomValue());
            Angle angle = AngleUtil.fromVec3d(attackVector.subtract(mc.player.getEyePos()));
            rotateToTarget(target, new Angle.VecRotation(angle, attackVector), rotationController);
            // Визуальный headYaw для F5
            if (legitFovSetting.isValue()) {
                double dx = target.getX() - mc.player.getX();
                double dz = target.getZ() - mc.player.getZ();
                float yaw = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0F);
                ru.expensive.core.Expensive.getInstance().killauraHeadYaw = yaw;
            } else {
                ru.expensive.core.Expensive.getInstance().killauraHeadYaw = null;
            }
        } else {
            ru.expensive.core.Expensive.getInstance().killauraHeadYaw = null;
        }
    }

    @EventHandler
    public void onTick(TickEvent tickEvent) {
        if (legitFovSetting.isValue()) {
            // Применяем только легитные фильтры и raytrace
            targetTypeSetting.value("Players");
            attackSetting.value("Raytrace check");
            // Можно добавить другие легитные ограничения
        }
        if (target != null) {
            attackTarget(target, RotationController.INSTANCE.getCurrentAngle());
            TargetSelector.EntityFilter filter = new TargetSelector.EntityFilter(targetTypeSetting.getSelected());

            targetSelector.searchTargets(mc.world.getEntities(), maxDistanceSetting.getValue());
            targetSelector.validateTarget(filter::isValid);
        }
    }

    @EventHandler
    public void onDrawLegitFov(ru.expensive.implement.events.render.DrawEvent event) {
        if (!isState() || !legitFovSetting.isValue()) return;
        float fov = legitFovWidthSetting.getValue();
        int color = legitFovColorSetting.getColor();
        int centerX = event.getDrawContext().getScaledWindowWidth() / 2;
        int centerY = event.getDrawContext().getScaledWindowHeight() / 2;
        int radius = (int) (120 * (fov / 90f)); // масштабируем радиус под FOV
        // Рисуем FOV круг через drawEngine (точки или маленькие квадраты по окружности)
        var matrices = event.getDrawContext().getMatrices();
        for (int i = 0; i < 360; i += 2) {
            double rad = Math.toRadians(i);
            float x = (float) (centerX + Math.cos(rad) * radius);
            float y = (float) (centerY + Math.sin(rad) * radius);
            drawEngine.quad(matrices.peek().getPositionMatrix(), x - 1, y - 1, 2, 2, color);
        }
    }

    private LivingEntity updateTarget() {
        TargetSelector.EntityFilter filter = new TargetSelector.EntityFilter(targetTypeSetting.getSelected());
        targetSelector.searchTargets(mc.world.getEntities(), maxDistanceSetting.getValue());
        // FOV ограничение
        float fov = legitFovSetting.isValue() ? legitFovWidthSetting.getValue() : aimDistanceSetting.getValue();
        Vec3d look = mc.player.getRotationVec(1.0F);
        LivingEntity best = null;
        double bestDist = Double.MAX_VALUE;
        for (var entity : mc.world.getEntities()) {
            if (!(entity instanceof LivingEntity living)) continue;
            if (!filter.isValid(living)) continue;
            Vec3d toTarget = living.getPos().add(0, living.getStandingEyeHeight(), 0).subtract(mc.player.getEyePos()).normalize();
            double angle = Math.toDegrees(Math.acos(look.dotProduct(toTarget)));
            if (angle > fov / 2) continue;
            double dist = mc.player.squaredDistanceTo(living);
            if (dist < bestDist) {
                best = living;
                bestDist = dist;
            }
        }
        targetSelector.currentTarget = best;
        return best;
    }

    private void attackTarget(LivingEntity target, Angle angle) {
        AttackPerpetrator attackPerpetrator = Expensive.getInstance().getAttackPerpetrator();

        AttackPerpetrator.AttackPerpetratorConfigurable configurable = new AttackPerpetrator.AttackPerpetratorConfigurable(
                target,
                RotationController.INSTANCE.getServerAngle(),
                maxDistanceSetting.getValue(),
                attackSetting.getSelected(),
                getSprintMode()
        );
        if (angle != null && aimMode.isSelected("One Tick")) {
            mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.Full(mc.player.getX(), mc.player.getY(), mc.player.getZ(),
                    angle.getYaw(), angle.getPitch(), mc.player.isOnGround()));
        }

        attackPerpetrator.performAttack(configurable);

        if (angle != null && aimMode.isSelected("One Tick")) {
            mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.Full(mc.player.getX(), mc.player.getY(), mc.player.getZ(),
                    mc.player.getYaw(), mc.player.getPitch(), mc.player.isOnGround()));
        }
    }

    private void rotateToTarget(LivingEntity target, Angle.VecRotation rotation, RotationController rotationController) {
        RotationConfig configurable = new RotationConfig(getSmoothMode(),
                DebugCommand.debug,
                correctionGroupSetting.isValue(),
                ((SelectSetting) correctionGroupSetting.getSubSetting("Correction Type")).isSelected("Free")
        );

        AttackHandler attackHandler = Expensive.getInstance().getAttackPerpetrator().getAttackHandler();
        ClickScheduler clickScheduler = attackHandler.getClickScheduler();

        if (aimMode.isSelected("Snap") && clickScheduler.hasTicksElapsedSinceLastClick(2)) {
            return;
        }

        if (aimMode.isSelected("One Tick")) {
            return;
        }

        rotationController.rotateTo(rotation, target, configurable, TaskPriority.HIGH_IMPORTANCE_1, this);
        // Локально крутим голову, чтобы игрок видел наведение
        // if (target != null && legitFovSetting.isValue()) {
        //     mc.player.setYaw((float) rotation.getAngle().getYaw());
        //     mc.player.setPitch((float) rotation.getAngle().getPitch());
        // }
    }

    public SprintManager.Mode getSprintMode() {
        switch (sprintMode.getSelected()) {
            case "Bypass" -> {
                return SprintManager.Mode.BYPASS;
            }
            case "Default" -> {
                return SprintManager.Mode.DEFAULT;
            }
            case "Legit" -> {
                // Максимально легитный режим: не форсирует спринт, не ломает ванильное поведение, не вызывает подозрительных пакетов
                if (mc.player.isSprinting() && mc.player.input.movementForward > 0) {
                    return SprintManager.Mode.DEFAULT;
                } else {
                    return SprintManager.Mode.NONE;
                }
            }
        }
        return SprintManager.Mode.NONE;
    }

    public AngleSmoothMode getSmoothMode() {
        if (!aimMode.isSelected("Snap")) {
            switch (rotationModeSetting.getSelected()) {
                case "Adaptive" -> {
                    return new AdaptiveSmoothMode();
                }
                case "FunTime" -> {
                    return new FunTimeSmoothMode();
                }
                case "ReallyWorld" -> {
                    return new ReallyWorldSmoothMode();
                }
                case "HolyWorld Classic" -> {
                    return new HolyWorldClassicSmoothMode();
                }
                case "HolyWorld Lite" -> {
                    return new HolyWorldLiteSmoothMode();
                }
            }
        } else {
            return new LinearSmoothMode();
        }
        return new ReallyWorldSmoothMode();
    }
}
