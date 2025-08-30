package ru.expensive.api.feature.draggable;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import ru.expensive.implement.features.draggables.*;
import ru.expensive.implement.features.draggables.DynamicIslandDraggable;

import java.util.ArrayList;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DraggableRepository {
    List<AbstractDraggable> draggable = new ArrayList<>();

    public void setup() {
        register(
                new TargetHudDraggable(),
                new PotionsDraggable(),
                new HotKeysDraggable(),
                new ArmorDraggable(),
                new DynamicIslandDraggable(),
                new CoordsDraggable(),
                new SpeedDraggable(),
                new TotemCountDraggable()
        );
    }

    public void register(AbstractDraggable... module) {
        draggable.addAll(List.of(module));
    }

    public List<AbstractDraggable> draggable() {
        return draggable;
    }
}
