package from.Vibe.modules.api;

import from.Vibe.Vibe;
import from.Vibe.modules.settings.Setting;
import from.Vibe.modules.settings.api.Bind;
import from.Vibe.utils.Wrapper;
import from.Vibe.utils.notify.Notify;
import from.Vibe.utils.notify.NotifyIcons;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class Module implements Wrapper {
    private final String name, description;
    private final Category category;
    protected boolean toggled;
    @Setter private Bind bind = new Bind(-1, false);
    private final List<Setting<?>> settings = new ArrayList<>();

    public Module(String name, Category category) {
        this.name = name;
        this.category = category;
        this.description = "descriptions" + "." + category.name().toLowerCase() + "." + name.toLowerCase();
    }

    public void onEnable() {
        toggled = true;
        Vibe.getInstance().getEventHandler().subscribe(this);
        if (!fullNullCheck()) Vibe.getInstance().getNotifyManager().add(new Notify(NotifyIcons.successIcon, "Feature " + name + " was enable", 1000));
    }

    public void onDisable() {
        toggled = false;
        Vibe.getInstance().getEventHandler().unsubscribe(this);
        if (!fullNullCheck()) Vibe.getInstance().getNotifyManager().add(new Notify(NotifyIcons.failIcon, "Feature " + name + " was disable", 1000));
    }

    public void setToggled(boolean toggled) {
        if (toggled) onEnable();
        else onDisable();
    }

    public void toggle() {
        setToggled(!toggled);
    }

    public static boolean fullNullCheck() {
        return mc.player == null || mc.world == null;
    }
}