package from.Vibe.utils.network;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import from.Vibe.Vibe;
import from.Vibe.api.mixins.accessors.IBossBarHud;
import from.Vibe.modules.impl.client.Targets;
import from.Vibe.modules.impl.combat.AntiBot;
import from.Vibe.modules.impl.misc.ScoreboardHealth;
import from.Vibe.utils.Wrapper;
import lombok.experimental.UtilityClass;
import net.minecraft.client.gui.hud.ClientBossBar;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.ReadableScoreboardScore;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.number.StyledNumberFormat;
import net.minecraft.text.MutableText;
import net.minecraft.world.GameMode;

@UtilityClass
public class Server implements Wrapper {

    public boolean is(String server) {
        if (mc.getNetworkHandler() == null || mc.getNetworkHandler().getServerInfo() == null) return false;
        return mc.getNetworkHandler().getServerInfo().address.toLowerCase().contains(server);
    }
    
    public int getPing(PlayerEntity entity) {
        PlayerListEntry list = mc.getNetworkHandler().getPlayerListEntry(entity.getUuid());
        return list != null ? list.getLatency() : 0;
    }

    public boolean isBot(PlayerEntity entity) {
        return Vibe.getInstance().getModuleManager().getModule(AntiBot.class).isToggled() 
        		&& Vibe.getInstance().getModuleManager().getModule(AntiBot.class).bots.contains(entity);
    }

    public boolean isValid(LivingEntity entity) {
        return Vibe.getInstance().getModuleManager().getModule(Targets.class).isValid(entity);
    }
    
    public boolean isPvp() {
    	return !getBossBarText().isEmpty() && (getBossBarText().toLowerCase().contains("pvp") || getBossBarText().toLowerCase().contains("пвп"));
    }
    
    public String getPvpTimer() {
    	return getBossBarText().isEmpty() ? "" : getBossBarText().replaceAll("\\D", "");
    }
    
    public String getBossBarText() {
    	for (ClientBossBar bossBar : ((IBossBarHud) mc.inGameHud.getBossBarHud()).getBossBars().values()) {
    		if (bossBar == null) continue;
    		return bossBar.getName().getString();
    	}

    	return "";
    }

    public List<String> getSuckers() {
    	List<String> suckers = new ArrayList<>();
        for (PlayerListEntry list : mc.getNetworkHandler().getPlayerList()) {
            if (list == null) return Collections.emptyList();
            if (list.getGameMode() == GameMode.SPECTATOR) suckers.add(list.getProfile().getName());
        }

        return suckers;
    }
    
    public float getHealth(LivingEntity entity, boolean gapple) {
    	if (Vibe.getInstance().getModuleManager().getModule(ScoreboardHealth.class).isToggled()) {
    		if (entity instanceof PlayerEntity player) {
        		if (player.getScoreboard().getObjectiveForSlot(ScoreboardDisplaySlot.BELOW_NAME) == null) return 0f;
                ScoreboardObjective objective = player.getScoreboard().getObjectiveForSlot(ScoreboardDisplaySlot.BELOW_NAME);
                if (objective == null) return 0f;
                ReadableScoreboardScore score = player.getScoreboard().getScore(player, objective);
                MutableText text = ReadableScoreboardScore.getFormattedScore(score, objective.getNumberFormatOr(StyledNumberFormat.EMPTY));

                return Float.parseFloat(text.getString().replaceAll("\\D", ""));
        	} else return entity.getHealth() + (gapple ? entity.getAbsorptionAmount() : 0f);
    	} else return entity.getHealth() + (gapple ? entity.getAbsorptionAmount() : 0f);
    }
}