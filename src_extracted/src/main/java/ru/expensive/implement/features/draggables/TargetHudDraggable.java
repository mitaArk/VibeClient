package ru.expensive.implement.features.draggables;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;
import ru.expensive.api.feature.draggable.AbstractDraggable;
import ru.expensive.api.feature.module.Module;
import ru.expensive.api.feature.module.ModuleProvider;
import ru.expensive.api.system.font.Fonts;
import ru.expensive.api.system.shape.ShapeProperties;
import ru.expensive.api.system.shape.implement.Image;
import ru.expensive.common.QuickImports;
import ru.expensive.common.util.render.ScissorManager;
import ru.expensive.core.Expensive;
import ru.expensive.implement.features.modules.render.InterfaceModule;
import ru.expensive.implement.features.modules.combat.AuraModule;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

import static ru.expensive.api.system.font.Fonts.Type.BOLD;

public class TargetHudDraggable extends AbstractDraggable {
    private LivingEntity currentTarget;
    private float health;
    private float absorptionAmount;
    private float goldenHealth;

    public TargetHudDraggable() {
        super("TargetHud", 10, 10, 100, 40);
    }

    @Override
    public boolean visible() {
        InterfaceModule interfaceModule = (InterfaceModule) Expensive.getInstance().getModuleProvider().module("Interface");
        ModuleProvider moduleProvider = Expensive.getInstance().getModuleProvider();
        Module aura = moduleProvider.module("Aura");
        AuraModule auraModule = (AuraModule) aura;

        return interfaceModule != null
                && interfaceModule.isState()
                && interfaceModule.getInterfaceSettings().isSelected("Target Hud")
                && currentTarget != null
                && !(auraModule.getMaxDistanceSetting().getValue() <= mc.player.distanceTo(currentTarget))
                && aura.isState()
                || mc.currentScreen instanceof ChatScreen;
    }

    @Override
    public void tick(float delta) {
        Module aura = Expensive.getInstance().getModuleProvider().module("Aura");
        AuraModule auraModule = (AuraModule) aura;

        if (auraModule.getTarget() != null) {
            currentTarget = auraModule.getTarget();
        } else {
            currentTarget = mc.player;
        }

        if (!aura.isState() || currentTarget == null) {
            startCloseAnimation();
        } else {
            startAnimation();
        }

        absorptionAmount = currentTarget.getAbsorptionAmount();
        goldenHealth = MathHelper.lerp(0.1F, goldenHealth, absorptionAmount);

        super.tick(delta);
    }

    @Override
    public void drawDraggable(DrawContext context) {
        Matrix4f positionMatrix = context
                .getMatrices()
                .peek()
                .getPositionMatrix();

        if (currentTarget != null) {
            health = MathHelper.clamp(MathHelper.lerp(0.05F, health, currentTarget.getHealth()), 0, currentTarget.getMaxHealth());

            rectangle.render(ShapeProperties.create(positionMatrix, getX(), getY(), getWidth(), getHeight())
                    .round(12)
                    .softness(1)
                    .thickness(2)
                    .outlineColor(0xFF2D2E41)
                    .color(0xF2141724)
                    .build()
            );

            ScissorManager scissorManager = Expensive.getInstance().getScissorManager();

            scissorManager.push(getX(), getY(), getWidth() - 5, getHeight());
            Fonts.getSize(18, BOLD).drawString(context.getMatrices(), currentTarget.getName().getString(), getX() + 34, getY() + 10, -1);
            scissorManager.pop();

            // Отображение брони над ником
            Iterable<ItemStack> armor = currentTarget.getArmorItems();
            int armorOffset = 34;
            for (ItemStack itemStack : armor) {
                if (!itemStack.isEmpty()) {
                    context.drawItem(itemStack, getX() + armorOffset, getY() - 10);
                    armorOffset += 16;
                }
            }

            Fonts.getSize(14, BOLD).drawString(context.getMatrices(),
                    String.format("%.1f", currentTarget.getHealth()),
                    getX() + 34, getY() + 21, 0xFF8187FF);

            rectangle.render(ShapeProperties.create(positionMatrix, getX() + 34, getY() + 28.2F, 61, 2F)
                    .round(2)
                    .color(0xFF060712)
                    .build()
            );

            float barWidth = (health / currentTarget.getMaxHealth()) * 61;

            rectangle.render(ShapeProperties.create(positionMatrix, getX() + 34, getY() + 28.2F, barWidth, 2F)
                    .softness(20)
                    .round(6)
                    .color(0x188187FF)
                    .build()
            );
            rectangle.render(ShapeProperties.create(positionMatrix, getX() + 34, getY() + 28.2F, barWidth, 2F)
                    .round(2)
                    .color(0xFF8187FF)
                    .build()
            );

            if (goldenHealth > 0) {
                float goldenHearts = goldenHealth / 2;
                float maxGoldenHearts = currentTarget.getMaxHealth() / 2;

                float goldenBarWidth = Math.min((goldenHearts / maxGoldenHearts) * 61, 61);

                rectangle.render(ShapeProperties.create(positionMatrix, getX() + 34, getY() + 28.2F, goldenBarWidth, 2F)
                        .round(2)
                        .color(0xFFFFD700)
                        .build()
                );
            }

            Image image = QuickImports.image.setMatrixStack(context.getMatrices());
            image.setTexture("textures/steve.png").render(ShapeProperties.create(positionMatrix, getX() + 5, getY() + 7.5F, 25, 25)
                    .build()
            );

            image.setTexture("textures/health.png").render(ShapeProperties.create(positionMatrix, getX() + 88, getY() + 19, 7, 7)
                    .build()
            );

            // Отображение предметов в левой и правой руке под ником
            ItemStack mainHand = currentTarget.getMainHandStack();
            ItemStack offHand = currentTarget.getOffHandStack();
            int itemY = getY() + getHeight() + 2;
            if (!mainHand.isEmpty()) {
                context.drawItem(mainHand, getX() + 34, itemY);
            }
            if (!offHand.isEmpty()) {
                context.drawItem(offHand, getX() + 34 + 18, itemY);
            }
        }
    }
}
