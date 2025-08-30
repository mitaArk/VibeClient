package ru.expensive.implement.features.modules.render;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import ru.expensive.api.feature.module.Module;
import ru.expensive.api.feature.module.ModuleCategory;
import ru.expensive.api.feature.module.setting.implement.MultiSelectSetting;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ClearRenderModule extends Module {

    MultiSelectSetting clearRenderSettings = new MultiSelectSetting("NoRender", "Customize rendering settings for clarity")
            .value("HurtCam", "Fire", "Water", "Boat");

    public ClearRenderModule() {
        super("ClearRender", "NoRender", ModuleCategory.RENDER);
        setup(clearRenderSettings);
    }

    public void updateClearRenderSettings() {
        if (isState()) {
            if (clearRenderSettings.isSelected("HurtCam")) {
            }
            if (clearRenderSettings.isSelected("Fire")) {
            }
            if (clearRenderSettings.isSelected("Water")) {
            }
            if (clearRenderSettings.isSelected("Boat")) {
            }
        }
    }
}
