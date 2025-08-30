package ru.expensive.implement.features.modules.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;
import ru.expensive.api.event.EventHandler;
import ru.expensive.api.feature.module.Module;
import ru.expensive.api.feature.module.ModuleCategory;
import ru.expensive.api.feature.module.setting.implement.ColorSetting;
import ru.expensive.api.feature.module.setting.implement.ValueSetting;
import ru.expensive.api.repository.friend.FriendRepository;
import ru.expensive.common.util.render.VertexUtil;
import ru.expensive.implement.events.render.WorldRenderEvent;

import java.awt.*;

public class TracersModule extends Module {

    private final ValueSetting lineWidthSetting = new ValueSetting("Line Width", "Thickness of tracers lines")
            .setValue(2.0f)
            .range(1.0f, 5.0f);

    private final ColorSetting lineColorSetting = new ColorSetting("Line Color", "Color of tracers lines")
            .presets(0x80FFFFFF, 0x80FF0000);

    private final ColorSetting friendLineColorSetting = new ColorSetting("Friend Line Color", "Color of tracers lines for friends")
            .presets(0x80FFFFFF, 0x8000FF00);

    public TracersModule() {
        super("Tracers", "Tracers", ModuleCategory.RENDER);
        setup(lineColorSetting, friendLineColorSetting, lineWidthSetting);
    }

    @EventHandler
    public void onWorld(WorldRenderEvent worldRenderEvent) {
        MatrixStack stack = worldRenderEvent.getStack();
        Vec3d cameraPos = mc.getEntityRenderDispatcher().camera.getPos();
        stack.push();
        stack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        RenderSystem.enableBlend();
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getRenderTypeLinesProgram);
        RenderSystem.lineWidth(lineWidthSetting.getValue());

        buffer.begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);

        Vec3d crosshairPos = new Vec3d(
                mc.player.prevX + (mc.player.getX() - mc.player.prevX) * mc.getTickDelta(),
                mc.player.prevY + (mc.player.getY() - mc.player.prevY) * mc.getTickDelta() + mc.player.getEyeHeight(mc.player.getPose()),
                mc.player.prevZ + (mc.player.getZ() - mc.player.prevZ) * mc.getTickDelta()
        );

        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == mc.player) continue;

            boolean isFriend = FriendRepository.isFriend(player.getName().getString());

            Vec3d interpolatedPlayerPos = new Vec3d(
                    player.prevX + (player.getX() - player.prevX) * mc.getTickDelta(),
                    player.prevY + (player.getY() - player.prevY) * mc.getTickDelta() + player.getEyeHeight(player.getPose()),
                    player.prevZ + (player.getZ() - player.prevZ) * mc.getTickDelta()
            );

            Color lineColor = isFriend ? new Color(friendLineColorSetting.getColor()) : new Color(lineColorSetting.getColor());

            VertexUtil.vertexLine(stack, buffer,
                    (float) crosshairPos.x, (float) crosshairPos.y, (float) crosshairPos.z,
                    (float) interpolatedPlayerPos.x, (float) interpolatedPlayerPos.y, (float) interpolatedPlayerPos.z,
                    lineColor);
        }

        tessellator.draw();
        RenderSystem.enableCull();
        RenderSystem.enableDepthTest();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        RenderSystem.disableBlend();
        stack.pop();
    }
}
