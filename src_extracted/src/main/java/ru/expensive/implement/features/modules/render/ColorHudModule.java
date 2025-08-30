package ru.expensive.implement.features.modules.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;
import ru.expensive.api.event.EventHandler;
import ru.expensive.api.feature.module.Module;
import ru.expensive.api.feature.module.ModuleCategory;
import ru.expensive.api.feature.module.setting.implement.ColorSetting;
import ru.expensive.implement.events.render.WorldRenderEvent;

public class ColorHudModule extends Module {

    private final ColorSetting gameColorSetting = new ColorSetting("Game Color", "Color of game hud")
            .presets(0x80FFFFFF, 0x80FF0000, 0x8000FF00, 0x800000FF);

    public ColorHudModule() {
        super("ColorHud", "Color Hud", ModuleCategory.RENDER);
        setup(gameColorSetting);
    }

    @EventHandler
    public void onWorldRender(WorldRenderEvent event) {
        MatrixStack stack = event.getStack();

        RenderSystem.enableBlend();
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.disableCull();

        for (int i = 0; i < mc.world.getPlayers().size(); i++) {
            if (mc.world.getPlayers().get(i) == mc.player) {
                continue;
            }

            double x1 = mc.player.prevX + (mc.player.getX() - mc.player.prevX) * mc.getTickDelta();
            double y1 = mc.player.getEyeHeight(mc.player.getPose()) + mc.player.prevY + (mc.player.getY() - mc.player.prevY) * mc.getTickDelta();
            double z1 = mc.player.prevZ + (mc.player.getZ() - mc.player.prevZ) * mc.getTickDelta();

            renderLine(stack, new Vec3d(x1, y1, z1), new Vec3d(x1, y1 + 0.5, z1));
        }

        applyGameColor();

        RenderSystem.enableCull();
        RenderSystem.enableDepthTest();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        RenderSystem.disableBlend();
    }

    private void renderLine(MatrixStack stack, Vec3d start, Vec3d end) {
        stack.push();

        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        buffer.begin(VertexFormat.DrawMode.LINES, VertexFormats.POSITION_COLOR);

        buffer.vertex(start.x, start.y, start.z).color(1.0f, 1.0f, 1.0f, 1.0f).next();
        buffer.vertex(end.x, end.y, end.z).color(1.0f, 1.0f, 1.0f, 1.0f).next();

        tessellator.draw();

        stack.pop();
    }

    private void applyGameColor() {
        int gameColor = gameColorSetting.getColor();
        float gameRed = (gameColor >> 16 & 0xFF) / 255f;
        float gameGreen = (gameColor >> 8 & 0xFF) / 255f;
        float gameBlue = (gameColor & 0xFF) / 255f;
        float gameAlpha = (gameColor >> 24 & 0xFF) / 255f;

        RenderSystem.setShaderColor(gameRed, gameGreen, gameBlue, gameAlpha);
    }
}