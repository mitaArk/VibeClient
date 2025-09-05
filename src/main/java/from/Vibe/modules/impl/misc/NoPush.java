package from.Vibe.modules.impl.misc;

import from.Vibe.modules.api.Category;
import from.Vibe.modules.api.Module;
import from.Vibe.modules.settings.impl.BooleanSetting;

public class NoPush extends Module {

    public BooleanSetting players = new BooleanSetting("settings.nopush.players", true);
    public BooleanSetting blocks = new BooleanSetting("settings.nopush.blocks", true);
    public BooleanSetting water = new BooleanSetting("settings.nopush.water", true);

    public NoPush() {
        super("NoPush", Category.Misc);
    }
}