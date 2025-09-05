package from.Vibe.api.events.impl.rotations;

import from.Vibe.api.events.Event;
import lombok.*;

@AllArgsConstructor @Getter @Setter
public class EventJump extends Event {
    private float yaw;
}