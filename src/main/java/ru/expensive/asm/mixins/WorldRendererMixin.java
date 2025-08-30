package ru.expensive.asm.mixins;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.BoatEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.expensive.implement.features.modules.render.ClearRenderModule;
import ru.expensive.core.Expensive;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

    @Inject(method = "renderEntity", at = @At("HEAD"), cancellable = true)
    public void renderEntity(Entity entity, double cameraX, double cameraY, double cameraZ, float tickDelta,
                             MatrixStack matrices, VertexConsumerProvider vertexConsumers, CallbackInfo callbackInfo) {
        ClearRenderModule clearRenderModule = (ClearRenderModule) Expensive.getInstance().getModuleProvider().module("ClearRender");

        if (clearRenderModule.isState() && clearRenderModule.getClearRenderSettings().isSelected("Boat")
                && entity instanceof BoatEntity) {
            callbackInfo.cancel();
        }
    }
}
