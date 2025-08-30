package ru.expensive.implement.features.draggables;

import net.minecraft.client.gui.DrawContext;
import org.joml.Matrix4f;
import ru.expensive.api.system.font.FontRenderer;
import ru.expensive.api.system.shape.implement.Image;
import ru.expensive.common.QuickImports;
import ru.expensive.core.Expensive;
import ru.expensive.api.feature.draggable.AbstractDraggable;
import ru.expensive.api.system.font.Fonts;
import ru.expensive.api.system.shape.ShapeProperties;
import ru.expensive.implement.features.modules.render.InterfaceModule;

import static ru.expensive.api.system.font.Fonts.Type.*;

public class SpeedDraggable extends AbstractDraggable {

    public SpeedDraggable() {
        super("Speed", 3, 480, 72, 10);
    }

    @Override
    public boolean visible() {
        InterfaceModule interfaceModule = (InterfaceModule) Expensive.getInstance().getModuleProvider().module("Interface");
        return interfaceModule != null && interfaceModule.isState() && interfaceModule.getInterfaceSettings().isSelected("Speed");
    }

    @Override
    public void drawDraggable(DrawContext context) {
        Matrix4f positionMatrix = context
                .getMatrices()
                .peek()
                .getPositionMatrix();

        // Slightly larger than original and positioned under watermark by default
        float scale = 1.2f;
        int drawX = getX();
        int drawY = getY();

        rectangle.render(ShapeProperties.create(positionMatrix, drawX, drawY, getWidth() * scale, getHeight() * scale)
                .round(6)
                .softness(1)
                .thickness(2)
                .outlineColor(0xFF2D2E41)
                .color(0xF2141724)
                .build()
        );

        rectangle.render(ShapeProperties.create(positionMatrix, drawX + 10.5 * scale, drawY + 3 * scale, 0.8 * scale, 4 * scale)
                .color(0xFF2D2E41)
                .build()
        );

        double deltaX = mc.player.getX() - mc.player.prevX;
        double deltaY = mc.player.getY() - mc.player.prevY;
        double deltaZ = mc.player.getZ() - mc.player.prevZ;
        double speed = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) * 20;

        String speedText = "speed: " + String.format("%.2f", speed);

        Image image = QuickImports.image.setMatrixStack(context.getMatrices());
        image.setTexture("textures/running.png").render(ShapeProperties.create(positionMatrix, drawX + 3.0 * scale, drawY + 2.5 * scale, 6 * scale, 6 * scale)
                .build()
        );

        FontRenderer fontRenderer = Fonts.getSize((int) (10 * scale), BOLD);
        fontRenderer.drawString(context.getMatrices(), speedText, drawX + 13 * scale, drawY + 4.5 * scale, -1);

        setWidth((int) ((fontRenderer.getStringWidth(speedText) + 17) * scale));
        setHeight((int) (10 * scale));
    }
}
