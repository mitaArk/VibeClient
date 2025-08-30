package ru.expensive.implement.features.draggables;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.client.gui.hud.ClientBossBar;
import org.joml.Matrix4f;
import ru.expensive.api.feature.draggable.AbstractDraggable;
import ru.expensive.api.system.font.FontRenderer;
import ru.expensive.api.system.font.Fonts;
import ru.expensive.api.system.shape.ShapeProperties;
import ru.expensive.common.QuickImports;
import ru.expensive.common.util.other.PingUtil;
import ru.expensive.core.Expensive;
import ru.expensive.implement.features.modules.render.InterfaceModule;
import ru.expensive.asm.mixins.accessors.BossBarHudAccessor;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

import static ru.expensive.api.system.font.Fonts.Type.BOLD;

public class DynamicIslandDraggable extends AbstractDraggable {
    private static final int ISLAND_HEIGHT = 11; // Reduced by 2x
    private static final int ISLAND_WIDTH = 90;  // Reduced by 2x
    private static final int BOSSBAR_HEIGHT = 12; // Reduced by 1.7x (20/1.7 ≈ 12)
    private static final int BOSSBAR_WIDTH = 94;  // Reduced by 1.7x (160/ at 1.7 ≈ 94)
    private static final int ISLAND_Y = 8;
    private static final int ISLAND_RADIUS = 8;   // Increased rounding
    private static final int SCROLL_SPEED = 1;    // px per tick

    private int scrollOffset = 0;
    private int scrollDirection = 1;
    private long lastScrollTime = 0;

    public DynamicIslandDraggable() {
        super("DynamicIsland", 0, ISLAND_Y, ISLAND_WIDTH, ISLAND_HEIGHT);
    }

    @Override
    public boolean visible() {
        InterfaceModule interfaceModule = (InterfaceModule) Expensive.getInstance().getModuleProvider().module("Interface");
        return interfaceModule != null && interfaceModule.isState();
    }

    @Override
    public void drawDraggable(DrawContext context) {
        MinecraftClient mc = MinecraftClient.getInstance();
        Matrix4f positionMatrix = context.getMatrices().peek().getPositionMatrix();
        int windowWidth = mc.getWindow().getScaledWidth();
        int x = (windowWidth - ISLAND_WIDTH) / 2;
        int y = ISLAND_Y;
        setX(x);
        setY(y);
        setWidth(ISLAND_WIDTH);
        setHeight(ISLAND_HEIGHT);

        // Получаем boss bars
        BossBarHud bossBarHud = mc.inGameHud.getBossBarHud();
        Map<Integer, ClientBossBar> bossBars = ((BossBarHudAccessor) bossBarHud).expensive$getBossBars();
        List<ClientBossBar> bars = new ArrayList<>(bossBars.values());

        // Определяем PVP-режим
        ClientBossBar pvpBar = null;
        for (ClientBossBar bar : bars) {
            String txt = bar.getName().getString();
            if (txt.contains("PVP-режим активен, до конца")) {
                pvpBar = bar;
                bars.remove(bar); // Удаляем PVP бар из списка
                break;
            }
        }

        // Рисуем время и пинг вне островка
        FontRenderer font = Fonts.getSize(18, BOLD); // Increased from 15 to 18
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        float timeWidth = font.getStringWidth(time);
        int ping = PingUtil.getPing();
        String pingStr = ping >= 0 ? String.valueOf(ping) : "-";
        float pingWidth = font.getStringWidth(pingStr);
        font.drawString(context.getMatrices(), time, x - timeWidth - 10, y + ISLAND_HEIGHT / 2 + 1, 0xFFD4D6E1);
        font.drawString(context.getMatrices(), pingStr, x + ISLAND_WIDTH + 10, y + ISLAND_HEIGHT / 2 + 1, 0xFFD4D6E1);

        // Основной островок
        drawMainIsland(context, positionMatrix, x, y, pvpBar);

        // Рисуем остальные boss bars
        int bossBarY = y + ISLAND_HEIGHT + 6;
        for (ClientBossBar bar : bars) {
            drawBossBarIsland(context, positionMatrix, x + (ISLAND_WIDTH - BOSSBAR_WIDTH) / 2, bossBarY, bar);
            bossBarY += BOSSBAR_HEIGHT + 6;
        }
    }

    private void drawMainIsland(DrawContext context, Matrix4f positionMatrix, int x, int y, ClientBossBar pvpBar) {
        // Фон островка
        rectangle.render(ShapeProperties.create(positionMatrix, x, y, ISLAND_WIDTH, ISLAND_HEIGHT)
                .round(ISLAND_RADIUS)
                .softness(1)
                .thickness(2)
                .outlineColor(0xFF2D2E41)
                .color(0xF2141724)
                .build()
        );

        FontRenderer font = Fonts.getSize(12, BOLD); // Increased from 10 to 12
        int centerY = y + ISLAND_HEIGHT / 2 + 1;

        // По центру — статус или VibeClient
        if (pvpBar != null) {
            // Красный круг слева от текста
            int circleRadius = 4;
            int circleX = x + ISLAND_WIDTH / 2 - 20;
            int circleY = centerY - 4;
            context.fill(circleX, circleY, circleX + circleRadius * 2, circleY + circleRadius * 2, 0xFFFF3B3B);

            // Текст "PVP Nс"
            String txt = pvpBar.getName().getString();
            int sec = extractSeconds(txt);
            String pvpText = "PVP " + sec + "с";
            font.drawString(context.getMatrices(), pvpText, x + ISLAND_WIDTH / 2 - font.getStringWidth(pvpText) / 2 + 8, centerY, 0xFFFF3B3B);
        } else {
            // Отображаем "VibeClient" если нет PVP
            String vibeText = "VibeClient";
            font.drawString(context.getMatrices(), vibeText, x + ISLAND_WIDTH / 2 - font.getStringWidth(vibeText) / 2, centerY, 0xFFD4D6E1);
        }
    }

    private void drawBossBarIsland(DrawContext context, Matrix4f positionMatrix, int x, int y, ClientBossBar bar) {
        // Фон островка
        rectangle.render(ShapeProperties.create(positionMatrix, x, y, BOSSBAR_WIDTH, BOSSBAR_HEIGHT)
                .round(ISLAND_RADIUS)
                .softness(1)
                .thickness(2)
                .outlineColor(0xFF2D2E41)
                .color(0xF2141724)
                .build()
        );

        FontRenderer font = Fonts.getSize(12, BOLD); // Increased from 10 to 12
        int padding = 10;
        int centerY = y + BOSSBAR_HEIGHT / 2 + 1;

        // Слева — круг (вместо квадрата)
        int circleRadius = 4;
        int circleX = x + padding - 6;
        int circleY = centerY - 4;
        int color = getBossBarColor(bar);
        context.fill(circleX, circleY, circleX + circleRadius * 2, circleY + circleRadius * 2, color);

        // По центру — текст boss bar (автоскроллинг)
        String text = bar.getName().getString();
        float textWidth = font.getStringWidth(text);
        int availableWidth = BOSSBAR_WIDTH - padding * 2 - 24;
        int drawX = (int) (x + (BOSSBAR_WIDTH - Math.min(textWidth, availableWidth)) / 2);
        int offset = 0;
        if (textWidth > availableWidth) {
            long now = System.currentTimeMillis();
            if (now - lastScrollTime > 30) {
                scrollOffset += scrollDirection * SCROLL_SPEED;
                if (scrollOffset > textWidth - availableWidth || scrollOffset < 0) {
                    scrollDirection *= -1;
                }
                lastScrollTime = now;
            }
            offset = scrollOffset;
        } else {
            scrollOffset = 0;
        }
        font.drawString(context.getMatrices(), text, drawX - offset, centerY, 0xFFD4D6E1);

        // Справа — проценты
        int percent = (int) (bar.getPercent() * 100);
        String percentStr = percent + "%";
        float percentWidth = font.getStringWidth(percentStr);
        font.drawString(context.getMatrices(), percentStr, x + BOSSBAR_WIDTH - padding - percentWidth, centerY, 0xFFD4D6E1);
    }

    private int extractSeconds(String txt) {
        try {
            int idx1 = txt.indexOf("до конца ");
            int idx2 = txt.indexOf(" сек");
            if (idx1 != -1 && idx2 != -1 && idx2 > idx1) {
                String num = txt.substring(idx1 + 9, idx2).replaceAll("[^0-9]", "");
                return Integer.parseInt(num);
            }
        } catch (Exception ignored) {}
        return 0;
    }

    private int getBossBarColor(ClientBossBar bar) {
        String colorName = bar.getColor().name();
        return switch (colorName) {
            case "PINK" -> 0xFFFF55FF;
            case "BLUE" -> 0xFF5555FF;
            case "RED" -> 0xFFFF5555;
            case "GREEN" -> 0xFF55FF55;
            case "YELLOW" -> 0xFFFFFF55;
            case "PURPLE" -> 0xFFAA00AA;
            case "WHITE" -> 0xFFFFFFFF;
            default -> 0xFFD4D6E1;
        };
    }
}