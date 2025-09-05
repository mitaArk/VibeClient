package fun.drughack.client.gui;

import fun.drughack.client.gui.AltManagerScreen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class MainMenuScreen extends Screen {

    private static final Identifier BG = new Identifier("drughack", "textures/mainmenu/background.png");

    public MainMenuScreen() {
        super(Text.literal("Main Menu"));
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int startY = this.height / 2 - 80;

        // Singleplayer
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Singleplayer"), b -> {
            this.client.setScreen(new SelectWorldScreen(this));
        }).dimensions(centerX - 100, startY, 200, 20).build());

        // Multiplayer
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Multiplayer"), b -> {
            this.client.setScreen(new MultiplayerScreen(this));
        }).dimensions(centerX - 100, startY + 24, 200, 20).build());

        // AltManager
        this.addDrawableChild(ButtonWidget.builder(Text.literal("AltManager"), b -> {
            this.client.setScreen(new AltManagerScreen(this));
        }).dimensions(centerX - 100, startY + 48, 200, 20).build());

        // Options
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Options"), b -> {
            this.client.setScreen(new OptionsScreen(this, this.client.options));
        }).dimensions(centerX - 100, startY + 72, 200, 20).build());

        // Quit
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Quit"), b -> {
            this.client.scheduleStop();
        }).dimensions(centerX - 100, startY + 96, 200, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.drawTexture(BG, 0, 0, this.width, this.height,
                0, 0,
                1920, 1080,
                1920, 1080);

        context.drawCenteredTextWithShadow(this.textRenderer, "DRUGHACK", this.width / 2, 40, 0xFFFFFF);
        super.render(context, mouseX, mouseY, delta);
    }
}