package from.Vibe.modules.impl.movement;

import from.Vibe.modules.api.Category;
import from.Vibe.modules.api.Module;
import from.Vibe.modules.settings.impl.NumberSetting;

public class ElytraForward extends Module {

    public final NumberSetting forward = new NumberSetting("settings.elytraforward.forward", 3f, 1f, 6f, 1f);

    public ElytraForward() {
        super("ElytraForward", Category.Movement);
    }
}