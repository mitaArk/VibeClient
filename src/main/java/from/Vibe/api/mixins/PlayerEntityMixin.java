package from.Vibe.api.mixins;

import from.Vibe.Vibe;
import from.Vibe.api.events.impl.rotations.EventTravel;
import from.Vibe.utils.Wrapper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements Wrapper {

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Redirect(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getRotationVector()Lnet/minecraft/util/math/Vec3d;"))
    public Vec3d travel(PlayerEntity instance) {
        if (instance == mc.player) {
            EventTravel event = new EventTravel(getYaw(), getPitch());
            Vibe.getInstance().getEventHandler().post(event);
            return getRotationVector(event.getPitch(), event.getYaw());
        }

        return instance.getRotationVector();
    }
}