package ru.expensive.implement.features.modules.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import ru.expensive.api.event.EventHandler;
import ru.expensive.api.feature.module.Module;
import ru.expensive.api.feature.module.ModuleCategory;
import ru.expensive.api.feature.module.setting.implement.BooleanSetting;
import ru.expensive.api.feature.module.setting.implement.ColorSetting;
import ru.expensive.api.feature.module.setting.implement.ValueSetting;
import ru.expensive.api.repository.friend.FriendRepository;
import ru.expensive.api.system.font.FontRenderer;
import ru.expensive.api.system.font.Fonts;
import ru.expensive.api.system.shape.ShapeProperties;
import ru.expensive.common.QuickImports;
import ru.expensive.common.util.math.ProjectionUtil;
import ru.expensive.implement.events.render.DrawEvent;

public class ESPModule extends Module implements QuickImports {

    private final BooleanSetting showNamesSetting = new BooleanSetting("Show Names", "Отображать никнеймы игроков сверху их")
            .setValue(true);

    private final BooleanSetting showArmorSetting = new BooleanSetting("Show Armor", "Отображать броню сверху никнейма")
            .setValue(true);

    private final BooleanSetting showHeldItemsSetting = new BooleanSetting("Show Held Items", "Отображать предметы в руках сверху никнейма")
            .setValue(true);

    private final ValueSetting nameSizeSetting = new ValueSetting("Name Size", "Размер шрифта никнейма (в условных единицах Fonts)")
            .setValue(10.0f)
            .range(6.0f, 20.0f);

    private final ColorSetting nameColorSetting = new ColorSetting("Name Color", "Цвет никнейма (по умолчанию белый)")
            .presets(0xFFFFFFFF, 0xFFFFCCCC, 0xFFCCCCFF);

    private final ColorSetting friendNameColorSetting = new ColorSetting("Friend Name Color", "Цвет никнейма друга")
            .presets(0xFF80FF80, 0xFF00FF00);

    private final ColorSetting nameBackgroundColorSetting = new ColorSetting("Name Background", "Цвет фона никнейма")
            .value(0xB2060712)
            .presets(0xB2060712, 0x80000000, 0xA0000000);

    public ESPModule() {
        super("ESP", "ESP", ModuleCategory.RENDER);
        setup(showNamesSetting, showArmorSetting, showHeldItemsSetting, nameSizeSetting, nameColorSetting, friendNameColorSetting, nameBackgroundColorSetting);
    }

    @EventHandler
    public void onDraw(DrawEvent event) {
        if (mc.world == null || mc.player == null) return;

        DrawContext drawContext = event.getDrawContext();
        MatrixStack stack = drawContext.getMatrices();
        int screenW = window.getScaledWidth();
        int screenH = window.getScaledHeight();

        RenderSystem.disableDepthTest();

        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == mc.player) continue;

            // Skip entities that are behind the camera (angle culling)
            Vec3d cameraPos = mc.getEntityRenderDispatcher().camera.getPos();
            Vec3d toTarget = player.getPos().add(0, player.getEyeHeight(player.getPose()), 0).subtract(cameraPos);
            Vec3d forward = mc.getCameraEntity() != null ? mc.getCameraEntity().getRotationVec(mc.getTickDelta()) : new Vec3d(0, 0, 1);
            if (forward.dotProduct(toTarget.normalize()) <= 0.0) {
                continue;
            }

            Vec3d headPos = new Vec3d(
                    player.getX(),
                    player.getY() + player.getHeight() + 0.3,
                    player.getZ()
            );

            Vector2f screen = ProjectionUtil.project(headPos.x, headPos.y, headPos.z);
            if (screen.equals(new Vector2f(Float.MAX_VALUE, Float.MAX_VALUE))) continue;

            // Screen bounds culling (avoid drawing far offscreen projections)
            if (screen.getX() < -32 || screen.getX() > screenW + 32 || screen.getY() < -32 || screen.getY() > screenH + 32) {
                continue;
            }

            float screenX = screen.getX();
            float screenY = screen.getY();

            float textYOffset = 0;

            if (showNamesSetting.isValue()) {
                drawName(stack, player, screenX, screenY, (int) nameSizeSetting.getValue());
                FontRenderer renderer = Fonts.getSize((int) nameSizeSetting.getValue());
                textYOffset += renderer.getStringHeight(player.getName().getString()) + 2;
            }

            if (showArmorSetting.isValue()) {
                textYOffset += drawArmorRow(drawContext, stack, player, screenX, screenY + textYOffset);
            }

            if (showHeldItemsSetting.isValue()) {
                drawHeldItems(drawContext, stack, player, screenX, screenY + textYOffset);
            }
        }

        RenderSystem.enableDepthTest();
    }

    private void drawName(@NotNull MatrixStack stack, @NotNull PlayerEntity player, float centerX, float y, int size) {
        String name = player.getName().getString();
        boolean isFriend = FriendRepository.isFriend(name);
        int color = isFriend ? friendNameColorSetting.getColor() : nameColorSetting.getColor();

        FontRenderer renderer = Fonts.getSize(size);
        float textWidth = renderer.getStringWidth(name);

        float paddingX = 3.0f;
        float paddingY = 1.0f;
        float bgX = centerX - textWidth / 2.0f - paddingX;
        float bgY = y - 1.5f;
        float bgW = textWidth + paddingX * 2.0f;
        float bgH = 10.0f;

        rectangle.render(ShapeProperties.create(stack.peek().getPositionMatrix(), bgX, bgY, bgW, bgH)
                .round(3)
                .thickness(2)
                .softness(1)
                .outlineColor(0xFF060712)
                .color(nameBackgroundColorSetting.getColor())
                .build()
        );

        renderer.drawString(stack, name, centerX - textWidth / 2.0f + 0.8f, y + 1.2f, color);
    }

    private float drawArmorRow(DrawContext ctx, MatrixStack stack, PlayerEntity player, float centerX, float y) {
        DefaultedList<ItemStack> armor = player.getInventory().armor;

        int count = 0;
        for (ItemStack s : armor) if (!s.isEmpty()) count++;
        if (count == 0) return 0;

        float gap = 12f;
        float totalWidth = count * 16f + (count - 1) * gap;
        float startX = centerX - totalWidth / 2f;

        RenderSystem.setShaderGlintAlpha(0);
        int drawn = 0;
        for (int i = armor.size() - 1; i >= 0; i--) {
            ItemStack item = armor.get(i);
            if (item.isEmpty()) continue;
            float x = startX + drawn * (16f + gap);
            drawItemAt(ctx, stack, item, x, y);
            drawn++;
        }
        RenderSystem.setShaderGlintAlpha(1);
        return 16f + 2f;
    }

    private void drawHeldItems(DrawContext ctx, MatrixStack stack, PlayerEntity player, float centerX, float y) {
        ItemStack main = player.getEquippedStack(EquipmentSlot.MAINHAND);
        ItemStack off = player.getEquippedStack(EquipmentSlot.OFFHAND);

        int count = (main.isEmpty() ? 0 : 1) + (off.isEmpty() ? 0 : 1);
        if (count == 0) return;

        float gap = 12f;
        float totalWidth = count * 16f + (count - 1) * gap;
        float startX = centerX - totalWidth / 2f;

        int drawn = 0;
        if (!main.isEmpty()) {
            drawItemAt(ctx, stack, main, startX + drawn * (16f + gap), y);
            drawn++;
        }
        if (!off.isEmpty()) {
            drawItemAt(ctx, stack, off, startX + drawn * (16f + gap), y);
        }
    }

    private void drawItemAt(DrawContext ctx, MatrixStack stack, ItemStack item, float x, float y) {
        stack.push();
        stack.translate(x, y, 0);
        ctx.drawItem(item, 0, 0);
        ctx.drawItemInSlot(mc.textRenderer, item, 0, 0);
        stack.pop();
    }
}

