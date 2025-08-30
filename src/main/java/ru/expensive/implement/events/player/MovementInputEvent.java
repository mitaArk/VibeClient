package ru.expensive.implement.events.player;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.expensive.api.event.events.Event;
import ru.expensive.common.util.player.MovingUtil;
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@EqualsAndHashCode(callSuper = false)
public class MovementInputEvent implements Event {
    private MovingUtil.DirectionalInput directionalInput;
    private boolean jumping;
    private boolean sneaking;
}
