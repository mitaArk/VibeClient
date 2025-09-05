package from.Vibe.modules.impl.misc;

import from.Vibe.api.events.impl.EventTick;
import from.Vibe.api.mixins.accessors.IMinecraftClient;
import from.Vibe.modules.api.Category;
import from.Vibe.modules.api.Module;
import meteordevelopment.orbit.EventHandler;

public class FastUse extends Module {

    public FastUse() {
        super("FastUse", Category.Misc);
    }

    @EventHandler
    public void onTick(EventTick e) {
        if (fullNullCheck()) return;

        ((IMinecraftClient) mc).setItemUseCooldown(0);
    }
}