package ru.expensive.implement.screens.menu.components.implement.settings;

import lombok.RequiredArgsConstructor;
import net.minecraft.client.gui.DrawContext;
import ru.expensive.api.feature.module.setting.Setting;
import ru.expensive.api.feature.module.setting.implement.GroupSetting;
import ru.expensive.api.system.font.Fonts;
import ru.expensive.common.util.other.StringUtil;
import ru.expensive.implement.screens.menu.components.AbstractComponent;
import ru.expensive.implement.screens.menu.components.implement.other.CheckComponent;
import ru.expensive.implement.screens.menu.components.implement.other.SettingComponent;
import ru.expensive.implement.screens.menu.components.implement.window.AbstractWindow;
import ru.expensive.implement.screens.menu.components.implement.window.implement.settings.group.GroupWindow;

import static ru.expensive.api.system.font.Fonts.Type.BOLD;

public class GroupComponent extends AbstractSettingComponent {
    private final CheckComponent checkComponent = new CheckComponent();
    private final SettingComponent settingComponent = new SettingComponent();

    private final GroupSetting setting;

    public GroupComponent(GroupSetting setting) {
        super(setting);
        this.setting = setting;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        String wrapped = StringUtil.wrap(setting.getDescription(), 100, 12);
        height = (int) (18 + Fonts.getSize(12).getStringHeight(wrapped) / 3);

        Fonts.getSize(14, BOLD).drawString(context.getMatrices(), setting.getName(), x + 9, y + 6, 0xFFD4D6E1);
        Fonts.getSize(12).drawString(context.getMatrices(), wrapped, x + 9, y + 15, 0xFF878894);

        ((CheckComponent) checkComponent.position(x + width - 16, y + 7.5F))
                .setRunnable(() -> setting.setValue(!setting.isValue()))
                .setState(setting.isValue())
                .render(context, mouseX, mouseY, delta);

        ((SettingComponent) settingComponent.position(x + width - 28, y + 8))
                .setRunnable(() -> spawnWindow(mouseX, mouseY))
                .render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        checkComponent.mouseClicked(mouseX, mouseY, button);
        settingComponent.mouseClicked(mouseX, mouseY, button);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void spawnWindow(int mouseX, int mouseY) {
        AbstractWindow existingWindow = null;

        for (AbstractWindow window : windowManager.getWindows()) {
            if (window instanceof GroupWindow && ((GroupWindow) window).getSetting() == setting) {
                existingWindow = window;
                break;
            }
        }

        if (existingWindow != null) {
            windowManager.delete(existingWindow);
        } else {
            AbstractWindow groupWindow = new GroupWindow(setting)
                    .position(mouseX + 5, mouseY + 5)
                    .size(137, 23)
                    .draggable(false);

            windowManager.add(groupWindow);
        }
    }
}
