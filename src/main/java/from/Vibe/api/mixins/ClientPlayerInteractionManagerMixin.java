package from.Vibe.api.mixins;

import from.Vibe.Vibe;
import from.Vibe.api.events.impl.EventAttackEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class ClientPlayerInteractionManagerMixin {

    @Inject(method = "attackEntity", at = @At("HEAD"))
    public void attackEntity(PlayerEntity player, Entity target, CallbackInfo ci) {
        EventAttackEntity event = new EventAttackEntity(player, target);
        Vibe.getInstance().getEventHandler().post(event);
    }
}