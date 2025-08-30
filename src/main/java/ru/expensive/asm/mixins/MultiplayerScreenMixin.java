package ru.expensive.asm.mixins;

import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.expensive.common.SelectedProtocol;
import ru.expensive.asm.mixins.accessors.ScreenAccessor;

@Mixin(MultiplayerScreen.class)
public class MultiplayerScreenMixin {
    @Inject(method = "init", at = @At("TAIL"))
    private void addVersionSelector(CallbackInfo ci) {
        MultiplayerScreen screen = (MultiplayerScreen) (Object) this;
        ScreenAccessor accessor = (ScreenAccessor) screen;
        accessor.callAddSelectableChild(ButtonWidget.builder(Text.of("1.20.1"), btn -> {
            SelectedProtocol.version = "1.20.1";
        }).position(10, 10).size(80, 20).build());
        accessor.callAddSelectableChild(ButtonWidget.builder(Text.of("1.16.5"), btn -> {
            SelectedProtocol.version = "1.16.5";
        }).position(10, 35).size(80, 20).build());
    }
}
