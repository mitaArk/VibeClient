package from.Vibe.api.events.impl.rotations;

import from.Vibe.api.events.Event;
import lombok.*;

@AllArgsConstructor @Getter @Setter
public class EventTrace extends Event {
    private float yaw, pitch;
}