package ru.expensive.implement.features.modules.combat;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import ru.expensive.api.event.EventHandler;
import ru.expensive.api.feature.module.Module;
import ru.expensive.api.feature.module.ModuleCategory;
import ru.expensive.api.feature.module.setting.implement.BooleanSetting;
import ru.expensive.core.listener.impl.PacketEventListener;
import ru.expensive.api.repository.friend.FriendRepository;
import ru.expensive.implement.events.player.TickEvent;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class TriggerBotModule extends Module {
    final BooleanSetting criticalsOnly = new BooleanSetting("Criticals Only", "Only attacks with critical hits");
    final BooleanSetting ignoreFriends = new BooleanSetting("Ignore Friends", "Ignores friends as targets");
    long lastAttackTime;

    public TriggerBotModule() {
        super("TriggerBot", "Trigger Bot", ModuleCategory.COMBAT);
        setup(criticalsOnly, ignoreFriends);
    }

    @EventHandler
    public void onTick(TickEvent event) {
        ClientPlayerEntity localPlayer = mc.player;
        if (localPlayer == null) {
            return;
        }

        HitResult crosshairTarget = mc.crosshairTarget;

        if (crosshairTarget != null && crosshairTarget.getType() == HitResult.Type.ENTITY) {
            Entity targetEntity = ((EntityHitResult) crosshairTarget).getEntity();

            boolean criticalsOnlyAsBoolean = criticalsOnly.isValue();
            boolean ignoreFriendsAsBoolean = ignoreFriends.isValue();

            if (canAttack(localPlayer) && canPerformCriticalHit(localPlayer, criticalsOnlyAsBoolean) && isValidTarget(targetEntity, ignoreFriendsAsBoolean)) {
                attackCrosshairTarget(localPlayer, criticalsOnlyAsBoolean);
                lastAttackTime = System.currentTimeMillis();
            }
        }
    }

    private void attackCrosshairTarget(ClientPlayerEntity localPlayer, boolean criticalsOnly) {
        boolean sprinting = localPlayer.isSprinting();

        if (criticalsOnly && sprinting) {
            if (PacketEventListener.serverSprint) {
                localPlayer.setSprinting(false);
                localPlayer.networkHandler.sendPacket(new ClientCommandC2SPacket(localPlayer,
                        ClientCommandC2SPacket.Mode.STOP_SPRINTING));
            }
        }

        MinecraftClient.getInstance().doAttack();

        if (criticalsOnly && sprinting) {
            localPlayer.networkHandler.sendPacket(new ClientCommandC2SPacket(localPlayer,
                    ClientCommandC2SPacket.Mode.START_SPRINTING));
            localPlayer.setSprinting(true);
        }
    }

    private boolean canAttack(ClientPlayerEntity localPlayer) {
        return localPlayer.getAttackCooldownProgress(0.5F) >= 0.9F
                && (System.currentTimeMillis() - lastAttackTime) >= 500;
    }

    private boolean canPerformCriticalHit(ClientPlayerEntity localPlayer, boolean criticalsOnly) {
        PlayerAbilities localPlayerAbilities = localPlayer.getAbilities();

        boolean hasMovementRestrictions = localPlayer.hasStatusEffect(StatusEffects.BLINDNESS)
                || localPlayer.hasStatusEffect(StatusEffects.LEVITATION)
                || localPlayer.isSubmergedInWater()
                || localPlayer.isInLava()
                || localPlayer.isClimbing()
                || localPlayerAbilities.flying;

        if (criticalsOnly && !hasMovementRestrictions) {
            return localPlayer.fallDistance > 0 && !localPlayer.isOnGround();
        }

        return true;
    }

    private boolean isValidTarget(Entity entity, boolean ignoreFriends) {
        return entity instanceof LivingEntity
                && !(ignoreFriends && FriendRepository.isFriend(entity.getName().getString()));
    }
}
