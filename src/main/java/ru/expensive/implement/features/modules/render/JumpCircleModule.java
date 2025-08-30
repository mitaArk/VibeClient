package ru.expensive.implement.features.modules.render;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import ru.expensive.api.event.EventHandler;
import ru.expensive.api.feature.module.Module;
import ru.expensive.api.feature.module.ModuleCategory;
import ru.expensive.api.feature.module.setting.implement.ValueSetting;
import ru.expensive.api.system.shape.ShapeProperties;
import ru.expensive.api.system.shape.implement.Image;
import ru.expensive.common.QuickImports;
import ru.expensive.common.util.math.ProjectionUtil;
import ru.expensive.implement.events.player.TickEvent;
import ru.expensive.implement.events.render.DrawEvent;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class JumpCircleModule extends Module {

    private final ValueSetting maxSizeSetting = new ValueSetting("Max Size", "Макс. размер круга")
            .setValue(32.0f)
            .range(8.0f, 128.0f);

    private final ValueSetting durationMsSetting = new ValueSetting("Duration", "Длительность анимации (мс)")
            .setValue(600f)
            .range(200f, 2000f);

    private final List<Circle> circles = new LinkedList<>();
    private boolean wasOnGround = true;

    public JumpCircleModule() {
        super("JumpCircles", "JumpCircles", ModuleCategory.RENDER);
        setup(maxSizeSetting, durationMsSetting);
    }

    @EventHandler
    public void onTick(TickEvent event) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return;

        boolean onGround = player.isOnGround();
        // детект прыжка: переход с onGround -> в воздух с положительной вертикальной скоростью
        if (wasOnGround && !onGround && player.getVelocity().y > 0.0) {
            Vec3d pos = player.getPos();
            double yFeet = pos.y;
            circles.add(new Circle(new Vec3d(pos.x, yFeet, pos.z), System.currentTimeMillis()));
        }
        wasOnGround = onGround;
    }

    @EventHandler
    public void onDraw(DrawEvent event) {
        if (circles.isEmpty()) return;

        MatrixStack stack = event.getDrawContext().getMatrices();
        Matrix4f matrix = stack.peek().getPositionMatrix();
        Image image = QuickImports.image.setMatrixStack(stack);

        long now = System.currentTimeMillis();
        float durationMs = durationMsSetting.getValue();
        float maxSize = maxSizeSetting.getValue();

        Iterator<Circle> iterator = circles.iterator();
        while (iterator.hasNext()) {
            Circle c = iterator.next();
            float t = (now - c.startTime) / durationMs;
            if (t >= 1.0f) {
                iterator.remove();
                continue;
            }

            // Этапы: 0..0.7 рост от 0.25 до 1.0; 0.7..1.0 лёгкая усадка до 0.85 и исчезновение
            float scale;
            if (t < 0.7f) {
                float k = t / 0.7f;
                scale = 0.25f + k * (1.0f - 0.25f);
            } else {
                float k = (t - 0.7f) / 0.3f;
                scale = 1.0f + (0.85f - 1.0f) * k;
            }

            float alpha = 1.0f - t; // к концу исчезает
            float size = maxSize * scale;

            Vector2f screen = ProjectionUtil.project(c.worldPos.x, c.worldPos.y, c.worldPos.z);
            if (screen.equals(new Vector2f(Float.MAX_VALUE, Float.MAX_VALUE))) continue;

            float x = screen.getX() - size / 2.0f;
            float y = screen.getY() - size / 2.0f;

            int argb = (Math.round(alpha * 255) << 24) | 0x00FFFFFF;

            image.setTexture("textures/circle.png").render(
                    ShapeProperties.create(matrix, x, y, size, size)
                            .color(argb)
                            .build()
            );
        }
    }

    private static class Circle {
        final Vec3d worldPos;
        final long startTime;

        private Circle(Vec3d worldPos, long startTime) {
            this.worldPos = worldPos;
            this.startTime = startTime;
        }
    }
}

