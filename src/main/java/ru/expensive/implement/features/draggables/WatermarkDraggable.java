package ru.expensive.implement.features.draggables;

import net.minecraft.client.gui.DrawContext;
import org.joml.Matrix4f;
import ru.expensive.api.feature.draggable.AbstractDraggable;
import ru.expensive.api.system.font.FontRenderer;
import ru.expensive.api.system.font.Fonts;
import ru.expensive.api.system.shape.ShapeProperties;
import ru.expensive.api.system.shape.implement.Image;
import ru.expensive.common.QuickImports;
import ru.expensive.common.util.other.PingUtil;
import ru.expensive.core.Expensive;
import ru.expensive.implement.features.modules.render.InterfaceModule;

import static ru.expensive.api.system.font.Fonts.Type.BOLD;

public class WatermarkDraggable extends AbstractDraggable {

    public WatermarkDraggable() {
        super("Watermark", 4, 4, 92, 16);
    }

    @Override
    public boolean visible() {
        InterfaceModule interfaceModule = (InterfaceModule) Expensive.getInstance().getModuleProvider().module("Interface");
        if (interfaceModule != null && interfaceModule.isState()) {
            return interfaceModule.getInterfaceSettings().isSelected("Watermark");
        }
        return false;
    }

    @Override
    public void drawDraggable(DrawContext context) {
        Matrix4f positionMatrix = context
                .getMatrices()
                .peek()
                .getPositionMatrix();

        // Чуть выше для лучшего вертикального центрирования контента
        setHeight(18);

        // Рисуем прямоугольник для водяного знака (чуть менее тёмное затемнение)
        rectangle.render(ShapeProperties.create(positionMatrix, getX(), getY(), getWidth(), getHeight())
                .round(6)
                .softness(1)
                .thickness(2)
                .outlineColor(0xFF2D2E41)
                .color(0xE0141724)
                .build()
        );

        // Рисуем логотип клиента
        Image image = QuickImports.image.setMatrixStack(context.getMatrices());
        // Левый ромб-логотип
        image.setTexture("textures/ico.png").render(ShapeProperties.create(positionMatrix, getX() + 6, getY() + (getHeight() - 12) / 2f, 12, 12)
                .build()
        );

        FontRenderer font = Fonts.getSize(22, BOLD);
        String name = Expensive.getInstance().getClientInfoProvider().clientName();

        // Получаем пинг с помощью PingUtil и добавляем "ms"
        String ms = PingUtil.getPing() + " ms";

        String fps = mc.getCurrentFps() + " fps";

        // Иконка пинга (вертикально по центру)
        image.setTexture("textures/ping.png").render(ShapeProperties.create(positionMatrix, getX() + font.getStringWidth(name) + 30, getY() + (getHeight() - 12) / 2f, 12, 12)
                .build()
        );

        // Иконка FPS (вертикально по центру)
        image.setTexture("textures/frame.png").render(ShapeProperties.create(positionMatrix, getX() + font.getStringWidth(name) + 50 + font.getStringWidth(ms), getY() + (getHeight() - 12) / 2f, 12, 12)
                .build()
        );

        // Отображаем информацию: имя клиента, пинг и FPS
        float baseline = getY() + getHeight() / 2f + 1;
        font.drawGradientString(context.getMatrices(), name, getX() + 24, baseline, 0xFF8187FF, 0xFF4D5199);
        font.drawString(context.getMatrices(), ms, getX() + font.getStringWidth(name) + 44, baseline, -1);
        font.drawString(context.getMatrices(), fps, getX() + font.getStringWidth(name) + 54 + font.getStringWidth(ms), baseline, -1);

        // Устанавливаем ширину элемента, чтобы учитывать длину текста
        setWidth((int) (font.getStringWidth(name + ms + fps) + 86));
    }
}
