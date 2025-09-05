package from.Vibe.modules.impl.misc;

import from.Vibe.api.events.impl.EventTick;
import from.Vibe.api.mixins.accessors.ILivingEntity;
import from.Vibe.modules.api.Category;
import from.Vibe.modules.api.Module;
import meteordevelopment.orbit.EventHandler;

public class NoJumpDelay extends Module {

    public NoJumpDelay() {
        super("NoJumpDelay", Category.Misc);
    }

    @EventHandler
    public void onTick(EventTick e) {
        if (fullNullCheck()) return;

        ((ILivingEntity) mc.player).setJumpingCooldown(0);
    }
}