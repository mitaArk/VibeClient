package ru.expensive.implement.screens.title.account;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import ru.expensive.api.system.font.Fonts;
import ru.expensive.api.system.shape.ShapeProperties;
import ru.expensive.api.system.shape.implement.Image;
import ru.expensive.api.system.shape.implement.Rectangle;
import ru.expensive.common.QuickImports;

import ru.expensive.common.util.math.MathUtil;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.Session;
import com.mojang.authlib.exceptions.AuthenticationException;
import ru.expensive.common.util.other.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class AccountManagerScreen extends Screen implements QuickImports {
    public static List<Account> ACCOUNTS = new ArrayList<>();
    private boolean typing;
    String currentUsername = !ACCOUNTS.isEmpty() ? ACCOUNTS.get(0).getUsername() : "";
    private int selectedIndex = -1;
    private int scrollOffset = 0;

    public AccountManagerScreen() {
        super(Text.of("Account Manager"));
        AccountManagerConfig.loadCurrentUsername();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        Matrix4f positionMatrix = context.getMatrices().peek().getPositionMatrix();

        Image image = QuickImports.image.setMatrixStack(context.getMatrices());
        image.setTexture("textures/mainmenu.png").render(ShapeProperties.create(positionMatrix, 0, 0, width, height).build());

        // Текущий ник сверху
        String current = "Текущий: " + (mc.getSession() != null ? mc.getSession().getUsername() : "-");
        Fonts.getSize(18, Fonts.Type.DEFAULT).drawCenteredString(
                context.getMatrices(),
                current,
                this.width / 2,
                this.height / 2 - 60,
                -1
        );

        rectangle.render(ShapeProperties.create(positionMatrix, this.width / 2 - 100, this.height / 2 - 20, 200, 20)
                .round(4)
                .softness(1)
                .thickness(2)
                .outlineColor(0xFF2D2E41)
                .color(0xF2141724)
                .build()
        );

        Fonts.getSize(18, Fonts.Type.DEFAULT).drawCenteredString(
                context.getMatrices(),
                typing ? (currentUsername + (typing ? System.currentTimeMillis() % 1000 > 500 ? "_" : "" : "")) : "Введите сюда свой никнейм",
                this.width / 2, this.height / 2 - 13,
                -1
        );

        // Кнопка плюс справа в поле ввода
        float plusX = this.width / 2f + 100 - 18;
        float plusY = this.height / 2f - 20 + 2;
        rectangle.render(ShapeProperties.create(positionMatrix, plusX, plusY, 16, 16)
                .round(4)
                .color(MathUtil.isHovered(mouseX, mouseY, plusX, plusY, 16, 16) ? 0xFF232431 : 0xFF191A28)
                .build());
        Fonts.getSize(16, Fonts.Type.BOLD).drawCenteredString(context.getMatrices(), "+", plusX + 8, plusY + 8, -1);

        if (currentUsername.length() == 16) {
            Fonts.getSize(18, Fonts.Type.DEFAULT).drawCenteredString(
                    context.getMatrices(),
                    "Вы превысили допустимый лимит по кол-ву символов (16). Пожайлуста, сделайте ник короче.",
                    this.width / 2, this.height / 2 - 40,
                    0xFFFF0000
            );
        }

        int color2 = MathUtil.isHovered(mouseX, mouseY, this.width / 2 - 100, this.height / 2 + 70, width, height)
                ? 0xFF232431
                : 0xFF191a28;

        // random
        new Rectangle().render(ShapeProperties.create(positionMatrix, this.width / 2 - 100, this.height / 2 + 70, 200, 20)
                .round(5)
                .thickness(2)
                .outlineColor(0xFF2d2e41)
                .color(color2)
                .build()
        );
        Fonts.getSize(18, Fonts.Type.DEFAULT).drawCenteredString(
                context.getMatrices(),
                "Случайный аккаунт",
                this.width / 2, this.height / 2 + 76.5f,
                -1
        );

        // Рендер списка аккаунтов под кнопками (с прокруткой)
        float listY = this.height / 2f + 100;
        float listX = this.width / 2f - 100;
        float itemH = 22;
        int maxVisible = 6;
        int startIndex = Math.max(0, Math.min(scrollOffset, Math.max(0, ACCOUNTS.size() - maxVisible)));
        for (int i = startIndex; i < Math.min(ACCOUNTS.size(), startIndex + maxVisible); i++) {
            Account acc = ACCOUNTS.get(i);
            int rowY = (int) (listY + (i - startIndex) * (itemH + 6));
            int bg = (i == selectedIndex) ? 0x8028A745 : (MathUtil.isHovered(mouseX, mouseY, listX, rowY, 200, itemH) ? 0x30232431 : 0x20191A28);
            rectangle.render(ShapeProperties.create(positionMatrix, listX, rowY, 200, itemH)
                    .round(5)
                    .color(bg)
                    .build());

            String uname = acc.getUsername();
            String date = new java.text.SimpleDateFormat("dd.MM.yyyy HH:mm").format(new java.util.Date(acc.getCreatedAt()));
            Fonts.getSize(16, Fonts.Type.DEFAULT).drawString(context.getMatrices(), uname, listX + 8, rowY + 7, -1);
            Fonts.getSize(12, Fonts.Type.DEFAULT).drawString(context.getMatrices(), date, listX + 8 + Fonts.getSize(16, Fonts.Type.DEFAULT).getStringWidth(uname) + 6, rowY + 8, 0xFFAAAAAA);

            // copy button
            float copyX = listX + 200 - 80;
            rectangle.render(ShapeProperties.create(positionMatrix, copyX, rowY + 4, 24, 14)
                    .round(3)
                    .color(0xFF232431)
                    .build());
            Fonts.getSize(12, Fonts.Type.BOLD).drawCenteredString(context.getMatrices(), "copy", copyX + 12, rowY + 11, -1);

            // edit button
            float editX = listX + 200 - 52;
            rectangle.render(ShapeProperties.create(positionMatrix, editX, rowY + 4, 24, 14)
                    .round(3)
                    .color(0xFF232431)
                    .build());
            Fonts.getSize(12, Fonts.Type.BOLD).drawCenteredString(context.getMatrices(), "edit", editX + 12, rowY + 11, -1);

            // delete X button
            float delX = listX + 200 - 24;
            rectangle.render(ShapeProperties.create(positionMatrix, delX, rowY + 4, 20, 14)
                    .round(3)
                    .color(0xFF3A1C1C)
                    .build());
            Fonts.getSize(12, Fonts.Type.BOLD).drawCenteredString(context.getMatrices(), "X", delX + 10, rowY + 11, 0xFFFF5555);
        }

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (MathUtil.isHovered(mouseX, mouseY, this.width / 2 - 100, this.height / 2 - 20, 200, 20)) {
            typing = !typing;
        }

        // плюсик добавления аккаунта
        float plusX = this.width / 2f + 100 - 18;
        float plusY = this.height / 2f - 20 + 2;
        if (MathUtil.isHovered(mouseX, mouseY, plusX, plusY, 16, 16)) {
            if (!currentUsername.isEmpty()) {
                ACCOUNTS.add(new Account(currentUsername));
                currentUsername = "";
                typing = false;
            }
        }

        // random
        if (MathUtil.isHovered(mouseX, mouseY, this.width / 2 - 100, this.height / 2 + 70, 200, 20)) {
            String randomNick = NicknameGenerator.generateGameNickname();
            this.currentUsername = randomNick;
        }

        // click on list: select/copy/edit/delete and right-click remove
        float listY = this.height / 2f + 100;
        float listX = this.width / 2f - 100;
        float itemH = 22;
        int maxVisible = 6;
        int startIndex = Math.max(0, Math.min(scrollOffset, Math.max(0, ACCOUNTS.size() - maxVisible)));
        for (int i = startIndex; i < Math.min(ACCOUNTS.size(), startIndex + maxVisible); i++) {
            int rowY = (int) (listY + (i - startIndex) * (itemH + 6));
            if (MathUtil.isHovered(mouseX, mouseY, listX, rowY, 200, itemH)) {
                float copyX = listX + 200 - 80;
                float editX = listX + 200 - 52;
                float delX = listX + 200 - 24;
                if (MathUtil.isHovered(mouseX, mouseY, copyX, rowY + 4, 24, 14)) {
                    mc.keyboard.setClipboard(ACCOUNTS.get(i).getUsername());
                    return true;
                }
                if (MathUtil.isHovered(mouseX, mouseY, editX, rowY + 4, 24, 14)) {
                    currentUsername = ACCOUNTS.get(i).getUsername();
                    typing = true;
                    return true;
                }
                if (MathUtil.isHovered(mouseX, mouseY, delX, rowY + 4, 20, 14) || button == 1) {
                    ACCOUNTS.remove(i);
                    return true;
                }
                // выбор аккаунта: подсветить и применить ник + сохранить
                selectedIndex = i;
                String name = ACCOUNTS.get(i).getUsername();
                try {
                    String sessionID = java.util.UUID.randomUUID().toString();
                    StringUtil.setSession(new Session(name, sessionID, "", java.util.Optional.empty(), java.util.Optional.empty(), Session.AccountType.MOJANG));
                } catch (AuthenticationException e) {
                    e.printStackTrace();
                }
                AccountManagerConfig.saveAccounts(name);
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.client.setScreen(null);
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_BACKSPACE && currentUsername.length() > 0) {
            currentUsername = currentUsername.substring(0, currentUsername.length() - 1);
        }

        if (keyCode == GLFW.GLFW_KEY_ENTER) {
            if (!currentUsername.isEmpty()) {
                ACCOUNTS.add(new Account(currentUsername));
            }

            typing = false;
            currentUsername = "";
        }
        return true;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (currentUsername.length() <= 15) currentUsername += Character.toString(codePoint);
        return super.charTyped(codePoint, modifiers);
    }
}