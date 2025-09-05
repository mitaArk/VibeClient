package from.Vibe.api.mixins;

import from.Vibe.Vibe;
import from.Vibe.modules.impl.render.NoRender;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.BossBarHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BossBarHud.class)
public abstract class BossBarHudMixin {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(DrawContext context, CallbackInfo ci) {
        if (Vibe.getInstance().getModuleManager().getModule(NoRender.class).isToggled() && Vibe.getInstance().getModuleManager().getModule(NoRender.class).bossBar.getValue()) ci.cancel();
    }
}