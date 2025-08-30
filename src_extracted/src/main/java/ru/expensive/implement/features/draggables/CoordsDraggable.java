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

public class CoordsDraggable extends AbstractDraggable {

    public CoordsDraggable() {
        super("Coords", 3, 492, 72, 10);
    }

    @Override
    public boolean visible() {
        InterfaceModule interfaceModule = (InterfaceModule) Expensive.getInstance().getModuleProvider().module("Interface");
        return interfaceModule != null && interfaceModule.isState() && interfaceModule.getInterfaceSettings().isSelected("Coords");
    }

    @Override
    public void drawDraggable(DrawContext context) {
        Matrix4f positionMatrix = context
                .getMatrices()
                .peek()
                .getPositionMatrix();

        float scale = 1.2f;

        rectangle.render(ShapeProperties.create(positionMatrix, getX(), getY(), getWidth() * scale, getHeight() * scale)
                .round(6)
                .softness(1)
                .thickness(2)
                .outlineColor(0xFF2D2E41)
                .color(0xF2141724)
                .build()
        );

        rectangle.render(ShapeProperties.create(positionMatrix, getX() + 10.5 * scale, getY() + 3 * scale, 0.8 * scale, 4 * scale)
                .color(0xFF2D2E41)
                .build()
        );

        String coordinate = "x:" + (int) mc.player.getX() + "  " +
                "y:" + (int) mc.player.getY() + "  " +
                "z:" + (int) mc.player.getZ();

        Image image = QuickImports.image.setMatrixStack(context.getMatrices());
        image.setTexture("textures/world.png").render(ShapeProperties.create(positionMatrix, getX() + 3.5 * scale, getY() + 2.5 * scale, 5 * scale, 5 * scale)
                .build()
        );

        FontRenderer fontRenderer = Fonts.getSize((int) (10 * scale), BOLD);
        fontRenderer.drawString(context.getMatrices(), coordinate, getX() + 13 * scale, getY() + 4.5 * scale, -1);

        setWidth((int) ((fontRenderer.getStringWidth(coordinate) + 17) * scale));
        setHeight((int) (10 * scale));
    }
}
