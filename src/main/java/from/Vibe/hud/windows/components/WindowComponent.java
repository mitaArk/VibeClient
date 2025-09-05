package from.Vibe.hud.windows.components;

import from.Vibe.screen.clickgui.components.Component;
import from.Vibe.utils.animations.Animation;
import lombok.*;

@Getter @Setter
public abstract class WindowComponent extends Component {
	protected Animation animation;

	public WindowComponent(String name) {
		super(name);
	}
}