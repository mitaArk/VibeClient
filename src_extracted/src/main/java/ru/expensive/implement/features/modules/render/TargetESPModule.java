package ru.expensive.implement.features.modules.render;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.util.Identifier;
import ru.expensive.api.event.EventHandler;
import ru.expensive.api.feature.module.Module;
import ru.expensive.api.feature.module.ModuleCategory;
import ru.expensive.api.feature.module.setting.implement.BooleanSetting;
import ru.expensive.api.feature.module.setting.implement.ValueSetting;
// removed unused Image/ShapeProperties usage after switching to direct quad render
import ru.expensive.common.QuickImports;
import ru.expensive.common.util.math.ProjectionUtil;
import ru.expensive.core.Expensive;
import ru.expensive.implement.events.render.DrawEvent;
import ru.expensive.implement.events.player.AttackEvent;
import ru.expensive.implement.events.packet.PacketEvent;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import io.netty.buffer.Unpooled;
import net.minecraft.client.MinecraftClient;
import ru.expensive.implement.features.modules.combat.AuraModule;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TargetESPModule extends Module {

    private final ValueSetting imageSizeSetting = new ValueSetting("Image Size", "Размер изображения target")
            .setValue(32.0f)
            .range(16.0f, 64.0f);

    private final BooleanSetting onlyWhenAuraActiveSetting = new BooleanSetting("Only When Aura Active", "Показывать target только когда Aura активен")
            .setValue(true);

    private final BooleanSetting rotateImageSetting = new BooleanSetting("Rotate", "Вращать картинку цели")
            .setValue(false);

    private final ValueSetting rotateSpeedSetting = new ValueSetting("Rotate Speed", "Скорость вращения (град/сек)")
            .setValue(180f)
            .range(30f, 720f);

    private final java.util.Map<Integer, Long> recentHits = new java.util.HashMap<>();

    public TargetESPModule() {
        super("TargetESP", "Target ESP", ModuleCategory.RENDER);
        setup(imageSizeSetting, onlyWhenAuraActiveSetting, rotateImageSetting, rotateSpeedSetting);
    }

    @EventHandler
    public void onAttack(AttackEvent event) {
        if (event.getEntity() instanceof LivingEntity) {
            recentHits.put(event.getEntity().getId(), System.currentTimeMillis());
        }
    }

    // Дополнительно реагируем на исходящие пакеты атаки, чтобы засчитать удар аурой
    @EventHandler
    public void onPacket(PacketEvent event) {
        if (!event.isSend()) return;
        if (!(event.getPacket() instanceof PlayerInteractEntityC2SPacket packet)) return;

        InteractType type = getInteractType(packet);
        if (type != InteractType.ATTACK) return;

        net.minecraft.entity.Entity e = getEntity(packet);
        if (e instanceof LivingEntity) {
            recentHits.put(e.getId(), System.currentTimeMillis());
        }
    }

    @EventHandler
    public void onDraw(DrawEvent drawEvent) {
        // Получаем AuraModule
        Module auraModule = Expensive.getInstance().getModuleProvider().module("Aura");
        if (!(auraModule instanceof AuraModule)) {
            return;
        }

        AuraModule aura = (AuraModule) auraModule;
        
        // Проверяем, активен ли Aura и есть ли цель
        if (onlyWhenAuraActiveSetting.isValue() && (!aura.isState() || aura.getTarget() == null)) {
            return;
        }

        LivingEntity target = aura.getTarget();
        if (target == null) {
            return;
        }

        // Позиция на груди игрока (примерно на уровне груди)
        Vec3d chestPos = new Vec3d(
                target.getX(),
                target.getY() + target.getHeight() * 0.6, // 60% от высоты игрока - примерно грудь
                target.getZ()
        );

        // Проецируем 3D координаты в 2D экранные координаты
        Vector2f projection = ProjectionUtil.project(chestPos.x, chestPos.y, chestPos.z);
        
        // Проверяем, что проекция валидна
        if (projection.equals(new Vector2f(Float.MAX_VALUE, Float.MAX_VALUE))) {
            return;
        }

        // Размер изображения
        float imageSize = imageSizeSetting.getValue();
        float halfSize = imageSize / 2.0f;

        // Позиция для отрисовки (центрируем изображение)
        float posX = projection.getX() - halfSize;
        float posY = projection.getY() - halfSize;

        // Отрисовываем изображение target
        MatrixStack stack = drawEvent.getDrawContext().getMatrices();
        // keep peek for consistency; we render via helper

        float cx = posX + halfSize;
        float cy = posY + halfSize;

        if (rotateImageSetting.isValue()) {
            stack.push();
            stack.translate(cx, cy, 0);
            float seconds = (System.currentTimeMillis() % 100000L) / 1000f;
            float angle = rotateSpeedSetting.getValue() * seconds;
            net.minecraft.util.math.RotationAxis axis = net.minecraft.util.math.RotationAxis.POSITIVE_Z;
            stack.multiply(axis.rotationDegrees(angle));
            stack.translate(-cx, -cy, 0);

            // Red tint for recent hit (<= 1s)
            int color = -1;
            Long last = recentHits.get(target.getId());
            if (last != null && System.currentTimeMillis() - last <= 100L) {
                color = 0xFFFF0000;
            }

            renderTexture(stack, posX, posY, imageSize, imageSize, "textures/target.png", color);

            stack.pop();
        } else {
            int color = -1;
            Long last = recentHits.get(target.getId());
            if (last != null && System.currentTimeMillis() - last <= 100L) {
                color = 0xFFFF0000;
            }
            renderTexture(stack, posX, posY, imageSize, imageSize, "textures/target.png", color);
        }
    }

    private void renderTexture(MatrixStack matrixStack, float x, float y, float width, float height, String texture, int color) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.setShaderTexture(0, new Identifier("expensive/" + texture));

        int argb = color == -1 ? 0xFFFFFFFF : color;
        QuickImports.drawEngine.quad(
                matrixStack.peek().getPositionMatrix(),
                x,
                y,
                width,
                height,
                argb
        );
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1, 1, 1, 1);
    }

    private net.minecraft.entity.Entity getEntity(PlayerInteractEntityC2SPacket packet) {
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
