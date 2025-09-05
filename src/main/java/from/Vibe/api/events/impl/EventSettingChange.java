package from.Vibe.api.events.impl;

import from.Vibe.api.events.Event;
import from.Vibe.modules.settings.Setting;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor @Getter
public class EventSettingChange extends Event {
    private final Setting<?> setting;
}