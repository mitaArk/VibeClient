package ru.expensive.implement.features.commands.defaults;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Formatting;
import ru.expensive.core.Expensive;
import ru.expensive.api.feature.command.Command;
import ru.expensive.api.feature.command.argument.IArgConsumer;
import ru.expensive.api.feature.command.exception.CommandException;
import ru.expensive.api.feature.command.helpers.TabCompleteHelper;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReconnectCommand extends Command {
    MinecraftClient mc = MinecraftClient.getInstance();
    String serverIP = "funtime", lastGameType, lastGameNumber;

    protected ReconnectCommand(Expensive expensive) {
        super("reconnect", "rct");
    }

    @Override
    public void execute(String label, IArgConsumer args) throws CommandException {
        if (!isOnFunTime()) {
            logDirect("Команда может использоваться только на сервере FunTime", Formatting.RED);
            return;
        }

        String[] mode = parseMode(parseScoreboardTitle());
        if (mode == null) {
            if (lastGameType == null || lastGameNumber == null) {
                logDirect("Не удалось распознать режим на сервере FunTime.", Formatting.RED);
                return;
            }
            logDirect("Заход на последний известный режим: " + lastGameType + "-" + lastGameNumber, Formatting.YELLOW);
            mode = new String[]{lastGameType, lastGameNumber};
        } else {
            lastGameType = mode[0];
            lastGameNumber = mode[1];
        }

        sendCommand("/hub");
        pause();
        sendCommand("/" + (mode[0].equals("Гриферский") ? "grief" : "an") + mode[1]);
    }

    private boolean isOnFunTime() {
        return mc.getCurrentServerEntry() != null && mc.getCurrentServerEntry().address.contains(serverIP);
    }

    private String parseScoreboardTitle() {
        return mc.world != null && mc.player != null && mc.world.getScoreboard().getObjectiveForSlot(1) != null
                ? mc.world.getScoreboard().getObjectiveForSlot(1).getDisplayName().getString()
                : "";
    }

    private String[] parseMode(String scoreboardTitle) {
        Matcher matcher = Pattern.compile("(Гриферский|Анархия)-(\\d+)").matcher(scoreboardTitle);
        return matcher.find() ? new String[]{matcher.group(1), matcher.group(2)} : null;
    }

    private void sendCommand(String command) {
        mc.player.networkHandler.sendChatMessage(command);
    }

    private void pause() {
        try { Thread.sleep(860); } catch (InterruptedException ignored) {}
    }

    @Override
    public Stream<String> tabComplete(String label, IArgConsumer args) throws CommandException {
        return new TabCompleteHelper().sortAlphabetically().prepend("reconnect", "rct").filterPrefix(args.getString()).stream();
    }

    @Override
    public String getShortDesc() {
        return "Команда для переподключения и выполнения действий в зависимости от титула Scoreboard.";
    }

    @Override
    public List<String> getLongDesc() {
        return List.of("Позволяет быстро переподключиться к режиму на сервере FunTime.", "", "Использование:", "> reconnect (или rct) - Быстрое переподключение.");
    }
}
