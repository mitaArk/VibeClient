package ru.expensive.implement.features.modules.render;

import ru.expensive.api.event.EventHandler;
import ru.expensive.implement.events.render.DrawEvent;
import ru.expensive.api.feature.module.Module;
import ru.expensive.api.feature.module.ModuleCategory;
import ru.expensive.api.feature.module.setting.implement.MultiSelectSetting;
import ru.expensive.api.feature.module.setting.implement.ValueSetting;

public class InterfaceModule extends Module {

    private final MultiSelectSetting interfaceSettings = new MultiSelectSetting("Elements", "Customize the interface elements")
            .value("Watermark", "HotKeys", "Potions", "Target Hud", "Armor Hud", "Coords", "Speed", "TotemCount", "Nametags");

    private final ValueSetting sizeSetting = new ValueSetting("Size", "Размер интерфейса").setValue(1.0f).range(0.5f, 2.0f);

    public InterfaceModule() {
        super("Interface", ModuleCategory.RENDER);
        setup(interfaceSettings, sizeSetting);
    }

    @EventHandler
    public void onDraw(DrawEvent drawEvent) {
        if (isState()) {
            if (interfaceSettings.isSelected("Watermark")) {
            }
            if (interfaceSettings.isSelected("HotKeys")) {
            }
            if (interfaceSettings.isSelected("Potions")) {
            }
            if (interfaceSettings.isSelected("TargetHud")) {
            }
            if (interfaceSettings.isSelected("Armor")) {
            }
            if (interfaceSettings.isSelected("Coords")) {
            }
            if (interfaceSettings.isSelected("Speed")) {
            }
            if (interfaceSettings.isSelected("Totem")) {
            }
            if (interfaceSettings.isSelected("Nametags")) {
            }
        }
    }

    public MultiSelectSetting getInterfaceSettings() {
        return interfaceSettings;
    }

    public ValueSetting getSizeSetting() {
        return sizeSetting;
    }
}
