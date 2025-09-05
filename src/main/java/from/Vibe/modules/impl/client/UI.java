package from.Vibe.modules.impl.client;

import from.Vibe.Vibe;
import from.Vibe.api.events.impl.EventTick;
import from.Vibe.screen.clickgui.ClickGui;
import from.Vibe.modules.api.Category;
import from.Vibe.modules.api.Module;
import from.Vibe.modules.settings.api.Bind;
import meteordevelopment.orbit.EventHandler;
import org.lwjgl.glfw.GLFW;

public class UI extends Module {

    public UI() {
        super("UI", Category.Client);
        setBind(new Bind(GLFW.GLFW_KEY_RIGHT_SHIFT, false));
    }

    @EventHandler
    public void onTick(EventTick e) {
        if (!(mc.currentScreen instanceof ClickGui)) setToggled(false);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        mc.setScreen(Vibe.getInstance().getClickGui());
    }
}