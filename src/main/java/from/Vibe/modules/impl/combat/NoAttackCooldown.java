package from.Vibe.modules.impl.combat;

import from.Vibe.api.events.impl.EventTick;
import from.Vibe.api.mixins.accessors.IMinecraftClient;
import from.Vibe.modules.api.Category;
import from.Vibe.modules.api.Module;
import meteordevelopment.orbit.EventHandler;

public class NoAttackCooldown extends Module {

    public NoAttackCooldown() {
        super("NoAttackCooldown", Category.Combat);
    }

    @EventHandler
    public void onTick(EventTick e) {
        if (fullNullCheck()) return;

        ((IMinecraftClient) mc).setAttackCooldown(0);
    }
}