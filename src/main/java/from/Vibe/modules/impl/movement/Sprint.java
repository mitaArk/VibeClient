package from.Vibe.modules.impl.movement;

import from.Vibe.Vibe;
import from.Vibe.api.events.impl.EventTick;
import from.Vibe.modules.api.Category;
import from.Vibe.modules.api.Module;
import from.Vibe.modules.impl.combat.Aura;
import meteordevelopment.orbit.EventHandler;

public class Sprint extends Module {
	
    public Sprint() {
        super("Sprint", Category.Movement);
    }

    @EventHandler
    public void onTick(EventTick e) {
        if (fullNullCheck()) return;

        if (Vibe.getInstance().getModuleManager().getModule(Aura.class).isToggled()
        		&& Vibe.getInstance().getModuleManager().getModule(Aura.class).getTarget() != null
                && Vibe.getInstance().getModuleManager().getModule(Aura.class).sprint.getValue() == Aura.Sprint.Legit) {
        	if (mc.player.getAbilities().flying
        			|| mc.player.isRiding()
        			|| Vibe.getInstance().getServerManager().getFallDistance() <= 0f
        			&& mc.player.isOnGround()) {
        		mc.options.sprintKey.setPressed(true);
        	} else {
                mc.options.sprintKey.setPressed(false);
                mc.player.setSprinting(false);
        	}
        } else mc.options.sprintKey.setPressed(true);
    }
}