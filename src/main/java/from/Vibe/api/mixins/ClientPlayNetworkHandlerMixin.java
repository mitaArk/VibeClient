package from.Vibe.api.mixins;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import from.Vibe.Vibe;
import from.Vibe.utils.network.ChatUtils;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {

    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    public void sendChatMessage(@NotNull String message, CallbackInfo ci) {
        if (message.startsWith(Vibe.getInstance().getCommandManager().getPrefix()) && !Vibe.getInstance().isPanic()) {
            try {
                Vibe.getInstance().getCommandManager().getDispatcher().execute(message.substring(Vibe.getInstance().getCommandManager().getPrefix().length()), Vibe.getInstance().getCommandManager().getSource());
            } catch (CommandSyntaxException e) {
                ChatUtils.sendMessage(Formatting.RED + e.getMessage());
            }

            ci.cancel();
        }
    }
}