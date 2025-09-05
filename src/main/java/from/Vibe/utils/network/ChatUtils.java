package from.Vibe.utils.network;

import java.awt.Color;

import from.Vibe.utils.Wrapper;
import from.Vibe.utils.render.ColorUtils;
import lombok.experimental.UtilityClass;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;

@UtilityClass
public class ChatUtils implements Wrapper {

    public void sendMessage(String message) {
        MutableText text = Text.literal("");
        for (int i = 0; i < "Vibe".length(); i++) {
            text.append(Text.literal("Vibe".charAt(i) + "")
                    .setStyle(Style.EMPTY
                            .withBold(true)
                            .withColor(TextColor.fromRgb(ColorUtils.gradient(ColorUtils.getGlobalColor(), Color.WHITE, (float) i / "Vibe".length()).getRGB()))
                    )
            );
        }

        text.append(Text.literal(" ⇨ ")
                .setStyle(Style.EMPTY
                        .withBold(false)
                        .withColor(TextColor.fromRgb(new Color(200, 200, 200).getRGB()))
                )
        );

        text.append(Text.literal(message)
                .setStyle(Style.EMPTY
                        .withBold(false)
                        .withColor(TextColor.fromRgb(new Color(200, 200, 200).getRGB()))
                )
        );

        mc.player.sendMessage(text, false);
    }
}