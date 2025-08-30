package ru.expensive.implement.features.draggables;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.joml.Matrix4f;
import ru.expensive.api.feature.draggable.AbstractDraggable;
import ru.expensive.api.system.font.Fonts;
import ru.expensive.core.Expensive;
import ru.expensive.implement.features.modules.render.InterfaceModule;

import static ru.expensive.api.system.font.Fonts.Type.BOLD;

public class TotemCountDraggable extends AbstractDraggable {

    public TotemCountDraggable() {
        super("TotemCount", 400, 100, 23, 18);
    }

    private int getTotemCount() {
        return (int) mc.player.getInventory().main.stream()
                .filter(stack -> stack.getItem() == Items.TOTEM_OF_UNDYING)
                .count();
    }

    @Override
    public boolean visible() {
        InterfaceModule interfaceModule = (InterfaceModule) Expensive.getInstance().getModuleProvider().module("Interface");
        return interfaceModule != null && interfaceModule.isState() && interfaceModule.getInterfaceSettings().isSelected("TotemCount") && getTotemCount() > 0 || mc.currentScreen instanceof ChatScreen;
    }

    @Override
    public void drawDraggable(DrawContext context) {
        Matrix4f positionMatrix = context.getMatrices().peek().getPositionMatrix();

        ItemStack totemStack = new ItemStack(Items.TOTEM_OF_UNDYING);

        context.drawItem(totemStack, getX() + 4, getY() + 4);

        int totemCount = getTotemCount();
        Fonts.getSize(13, BOLD).drawString(context.getMatrices(), String.valueOf(totemCount), getX() + 16, getY() + 17, 0xFFD4D6E1);

        setHeight(24);
    }
}
