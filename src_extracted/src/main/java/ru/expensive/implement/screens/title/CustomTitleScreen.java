package ru.expensive.implement.screens.title;

import com.mojang.authlib.exceptions.AuthenticationException;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.util.Session;
import net.minecraft.text.Text;
import org.joml.Matrix4f;
import ru.expensive.api.system.font.Fonts;
import ru.expensive.common.QuickImports;
import ru.expensive.api.system.shape.ShapeProperties;
import ru.expensive.api.system.shape.implement.Image;
import ru.expensive.implement.screens.title.account.AccountManagerConfig;
import ru.expensive.implement.screens.title.account.AccountManagerScreen;
import ru.expensive.common.util.other.StringUtil;
import ru.expensive.implement.screens.title.button.AbstractButton;
import ru.expensive.implement.screens.title.button.implement.CustomTextTitleButton;
import ru.expensive.implement.screens.title.button.implement.CustomTitleButton;

import java.util.*;

public class CustomTitleScreen extends Screen implements QuickImports {
    private final List<AbstractButton> buttons = new ArrayList<>();

    private final AbstractButton singleplayer = new CustomTitleButton("Singleplayer", () -> mc.setScreen(new SelectWorldScreen(this)));
    private final AbstractButton multiplayer = new CustomTitleButton("Multiplayer", () -> mc.setScreen(new MultiplayerScreen(this)));
    private final AbstractButton accounts = new CustomTitleButton("Accounts", () -> mc.setScreen(new AccountManagerScreen()));
    private final AbstractButton options = new CustomTextTitleButton("Options", () -> mc.setScreen(new OptionsScreen(this, mc.options)));
    private final AbstractButton exit = new CustomTextTitleButton("Exit", MinecraftClient.getInstance()::scheduleStop);

    public CustomTitleScreen() {
        super(Text.of("betelgeuse custom title screen"));
        buttons.addAll(Arrays.asList(
                singleplayer,
                multiplayer,
                accounts,
                options,
                exit)
        );

        AccountManagerConfig.loadCurrentUsername();
        String currentUsername = AccountManagerConfig.loadCurrentUsername();

        if (currentUsername == null || currentUsername.isEmpty()) {
            currentUsername = "betelgeuse";
        }

        try {
            String sessionID = UUID.randomUUID().toString();
            StringUtil.setSession(new Session(currentUsername, sessionID, "", Optional.empty(), Optional.empty(), Session.AccountType.MOJANG));
        } catch (AuthenticationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        Matrix4f positionMatrix = context.getMatrices().peek().getPositionMatrix();

        int wHeight = window.getHeight() / 4;
        int wWidth = window.getScaledWidth() / 2;

        singleplayer.position(wWidth - 80, wHeight - 28)
                .size(160, 27);

        multiplayer.position(wWidth - 80, wHeight + 4)
                .size(160, 27);

        accounts.position(wWidth - 80, wHeight + 36)
                .size(160, 27);

        options.position(wWidth - 40, wHeight + 82);
        exit.position(wWidth + 25, wHeight + 82);

        Image image = QuickImports.image.setMatrixStack(context.getMatrices());

        image.setTexture("textures/mainmenu.png").render(ShapeProperties.create(positionMatrix,0, 0, width, height)
                .build()
        );

        Fonts.getSize(20, Fonts.Type.DEFAULT).drawCenteredString(context.getMatrices(), "Добро пожаловать, " + mc.getSession().getUsername(), wWidth, wHeight - 54, -1);

        buttons.forEach(buttons -> buttons.render(context, mouseX, mouseY, delta));
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        buttons.forEach(buttons -> buttons.mouseClicked(mouseX, mouseY, button));
        return super.mouseClicked(mouseX, mouseY, button);
    }
}