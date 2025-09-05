package from.Vibe.api.mixins;

import from.Vibe.utils.Wrapper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(TitleScreen.class)
public abstract class MixinTitleScreen extends Screen implements Wrapper {

    protected MixinTitleScreen(Text title) {
        super(title);
    }

//    @Inject(method = "init", at = @At("RETURN"))
//    public void init(CallbackInfo ci) {
//        mc.setScreen(Vibe.getInstance().getMainMenu());
//    }
}