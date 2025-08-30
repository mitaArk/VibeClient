package ru.expensive.implement.features.modules.render;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.joml.Matrix4f;
import ru.expensive.api.event.EventHandler;
import ru.expensive.api.feature.module.Module;
import ru.expensive.api.feature.module.ModuleCategory;
import ru.expensive.api.feature.module.setting.implement.ColorSetting;
import ru.expensive.api.feature.module.setting.implement.ValueSetting;
import ru.expensive.api.system.shape.ShapeProperties;
import ru.expensive.api.system.shape.implement.Image;
import ru.expensive.common.QuickImports;
import ru.expensive.common.util.math.ProjectionUtil;
import ru.expensive.implement.events.player.AttackEvent;
import ru.expensive.implement.events.packet.PacketEvent;
import ru.expensive.implement.events.render.DrawEvent;
import io.netty.buffer.Unpooled;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class HitParticlesModule extends Module {

    private final ColorSetting colorSetting = new ColorSetting("Color", "Цвет партиклов")
            .value(0xFF00FFFF)
            .presets(
                    0xFFFF5555, // red
                    0xFFFFAA00, // orange
                    0xFFFFFF55, // yellow
                    0xFF55FF55, // green
                    0xFF55FFFF, // aqua
                    0xFF5555FF, // blue
                    0xFFFF55FF, // magenta
                    0xFFFFFFFF  // white
            );
    private final ValueSetting sizeSetting = new ValueSetting("Size", "Размер кружков").setValue(6.0f).range(2.0f, 20.0f);
    private final ValueSetting intensitySetting = new ValueSetting("Intensity", "Интенсивность появления").setValue(1.0f).range(0.2f, 2.0f);
    private final ValueSetting countSetting = new ValueSetting("Count", "Количество кружков").setValue(10.0f).range(2.0f, 50.0f);
    private final ValueSetting lifetimeMsSetting = new ValueSetting("Lifetime", "Время жизни (мс)").setValue(5000f).range(500f, 10000f);
    private final ValueSetting spreadSetting = new ValueSetting("Spread", "Разлёт (блоки)").setValue(0.6f).range(0.1f, 2.0f);
    private final ValueSetting scatterSetting = new ValueSetting("Scatter", "Рассыпчатость (дрейф)").setValue(0.3f).range(0.0f, 2.0f);

    private final List<Particle> particles = new ArrayList<>();
    private final Random random = new Random();

    public HitParticlesModule() {
        super("HitParticles", "Particles", ModuleCategory.RENDER);
        setup(colorSetting, sizeSetting, intensitySetting, countSetting, lifetimeMsSetting, spreadSetting, scatterSetting);
    }

    @EventHandler
    public void onAttack(AttackEvent event) {
        Entity entity = event.getEntity();
        if (entity == null || MinecraftClient.getInstance().world == null) return;
        spawnForEntity(entity);
    }

    @EventHandler
    public void onPacket(PacketEvent event) {
        if (!event.isSend()) return;
        if (!(event.getPacket() instanceof PlayerInteractEntityC2SPacket packet)) return;

        InteractType interactType = getInteractType(packet);
        if (interactType != InteractType.ATTACK) return;

        Entity entity = getEntity(packet);
        if (entity == null || MinecraftClient.getInstance().world == null) return;
        spawnForEntity(entity);
    }

    private void spawnForEntity(Entity entity) {
        int baseCount = Math.round(countSetting.getValue());
        int spawnCount = Math.max(1, Math.round(baseCount * intensitySetting.getValue()));
        float spread = spreadSetting.getValue();
        long now = System.currentTimeMillis();

        Vec3d base = entity.getPos().add(0, entity.getHeight() * 0.5, 0);
        for (int i = 0; i < spawnCount; i++) {
            // начальная позиция с разбросом по радиусу (контролируется spread)
            double angle = random.nextDouble() * Math.PI * 2.0;
            double radius = (0.2 + random.nextDouble() * 0.8) * spread;
            double ox = Math.cos(angle) * radius;
            double oz = Math.sin(angle) * radius;
            Vec3d origin = base.add(ox, 0, oz);

            // конечная точка в случайном направлении (как в примере)
            double ex = ThreadLocalRandom.current().nextDouble(-3.0, 3.0);
            double ey = ThreadLocalRandom.current().nextDouble(-3.0, 3.0);
            double ez = ThreadLocalRandom.current().nextDouble(-3.0, 3.0);
            Vec3d end = origin.add(ex, ey, ez);

            particles.add(new Particle(origin, end, now));
        }
    }

    @EventHandler
    public void onDraw(DrawEvent event) {
        if (particles.isEmpty()) return;
        MatrixStack stack = event.getDrawContext().getMatrices();
        Matrix4f matrix = stack.peek().getPositionMatrix();
        Image image = QuickImports.image.setMatrixStack(stack);

        int color = colorSetting.getColor();
        float baseSize = sizeSetting.getValue();
        float lifetime = lifetimeMsSetting.getValue();
        long now = System.currentTimeMillis();

        Iterator<Particle> iterator = particles.iterator();
        while (iterator.hasNext()) {
            Particle p = iterator.next();
            float t = (now - p.startTime) / lifetime;
            if (t >= 1.0f) {
                iterator.remove();
                continue;
            }

            if (MinecraftClient.getInstance().player != null) {
                if (MinecraftClient.getInstance().player.getPos().distanceTo(p.pos) > 30.0) {
                    iterator.remove();
                    continue;
                }
            }

            if (isOccluded(p.pos)) {
                iterator.remove();
                continue;
            }

            // плавное нарастание альфы и движение к конечной точке
            p.alpha = lerp(p.alpha, 1.0f, 0.1f);
            p.pos = p.pos.lerp(p.end, 0.05);

            float size = baseSize * (1.0f - t);
            float alpha = (1.0f - t) * p.alpha;

            Vector2f screen = ProjectionUtil.project(p.pos.x, p.pos.y, p.pos.z);
            if (screen.equals(new Vector2f(Float.MAX_VALUE, Float.MAX_VALUE))) continue;

            float x = screen.getX() - size / 2.0f;
            float y = screen.getY() - size / 2.0f;

            int argb = (Math.round(alpha * 255) << 24) | (color & 0x00FFFFFF);

            image.setTexture("textures/circle.png").render(
                    ShapeProperties.create(matrix, x, y, size, size)
                            .color(argb)
                            .build()
            );
        }
    }

    private static class Particle {
        Vec3d pos;
        final Vec3d end;
        final long startTime;
        float alpha;

        private Particle(Vec3d origin, Vec3d end, long startTime) {
            this.pos = origin;
            this.end = end;
            this.startTime = startTime;
            this.alpha = 0.0f;
        }
    }

    private boolean isOccluded(Vec3d point) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null || client.player == null) return false;
        Vec3d eye = client.player.getCameraPosVec(1.0f);
        BlockHitResult result = client.world.raycast(new RaycastContext(
                eye,
                point,
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE,
                client.player
        ));
        return result.getType() != HitResult.Type.MISS;
    }

    private static float lerp(float from, float to, float factor) {
        return from + (to - from) * factor;
    }

    private Entity getEntity(PlayerInteractEntityC2SPacket packet) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        packet.write(buf);
        int id = buf.readVarInt();
        return MinecraftClient.getInstance().world != null
                ? MinecraftClient.getInstance().world.getEntityById(id)
                : null;
    }

    private InteractType getInteractType(PlayerInteractEntityC2SPacket packet) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        packet.write(buf);
        buf.readVarInt();
        return buf.readEnumConstant(InteractType.class);
    }

    private enum InteractType {
        INTERACT, ATTACK, INTERACT_AT
    }
}

