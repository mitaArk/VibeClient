package ru.expensive.implement.features.modules.misc;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import ru.expensive.api.feature.module.Module;
import ru.expensive.api.feature.module.ModuleCategory;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemScrollerModule extends Module {

    public ItemScrollerModule() {
        super("ItemScroller", "Зажми ЛКМ+Shift и веди по предметам для быстрого переноса/одевания", ModuleCategory.MISC);
    }
}


