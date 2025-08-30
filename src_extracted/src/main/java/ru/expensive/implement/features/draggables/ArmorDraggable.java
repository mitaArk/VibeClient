package ru.expensive.implement.features.draggables;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import org.joml.Matrix4f;
import ru.expensive.api.feature.draggable.AbstractDraggable;
import ru.expensive.api.system.shape.ShapeProperties;
import ru.expensive.api.system.shape.implement.Image;
import ru.expensive.common.QuickImports;
import ru.expensive.core.Expensive;
import ru.expensive.implement.features.modules.render.InterfaceModule;

public class ArmorDraggable extends AbstractDraggable {
    private DefaultedList<ItemStack> armor;

    public ArmorDraggable() {
        super("Armor", 220, 10, 15, 16);
    }

    @Override
    public boolean visible() {
        InterfaceModule interfaceModule = (InterfaceModule) Expensive.getInstance().getModuleProvider().module("Interface");
        return interfaceModule != null && interfaceModule.isState() && interfaceModule.getInterfaceSettings().isSelected("Armor Hud") && (!armor.isEmpty() || mc.currentScreen instanceof ChatScreen);
    }

    @Override
    public void tick(float delta) {
        armor = mc.player.getInventory().armor;
        super.tick(delta);
    }

    @Override
    public void drawDraggable(DrawContext context) {
        MatrixStack stack = context.getMatrices();
        Matrix4f positionMatrix = stack.peek().getPositionMatrix();

        rectangle.render(ShapeProperties.create(positionMatrix, getX(), getY(), getWidth(), getHeight())
                .round(12)
                .softness(1)
                .thickness(2)
                .outlineColor(0xFF2D2E41)
                .color(0xF2141724)
                .build()
        );

        rectangle.render(ShapeProperties.create(positionMatrix, getX() + 10.5, getY() + getHeight() / 2 - 2, 0.8, 4)
                .color(0xFF2D2E41)
                .build()
        );

        Image image = QuickImports.image.setMatrixStack(stack);
        image.setTexture("textures/shield.png").render(ShapeProperties.create(positionMatrix, getX() + 3, getY() + getHeight() / 2 - 3, 6, 6)
                .build()
        );

        int offset = 14;

        RenderSystem.disableDepthTest();

        for (int i = armor.size() - 1; i >= 0; i--) {
            ItemStack itemStack = armor.get(i);
            if (itemStack.isEmpty()) continue;

            stack.push();
            stack.translate(getX() + offset, getY() + 2.5f, 0);
            stack.scale(0.8F, 0.8F, 0);
            RenderSystem.setShaderGlintAlpha(0);
            context.drawItem(itemStack, 0, 0);
            context.drawItemInSlot(mc.textRenderer, itemStack, 0, 0);
            RenderSystem.setShaderGlintAlpha(1);
            stack.pop();

            offset += 13;
        }

        RenderSystem.enableDepthTest();

        offset += 3;
        setWidth(offset);
        setHeight(18);
    }
}
