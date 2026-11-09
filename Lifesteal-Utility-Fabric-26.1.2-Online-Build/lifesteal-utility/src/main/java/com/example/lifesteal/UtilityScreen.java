package com.example.lifesteal;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class UtilityScreen extends Screen {
    private final Screen parent;

    public UtilityScreen(Screen parent) {
        super(Component.literal("Lifesteal Utility"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int startY = this.height / 2 - 22;

        Button espButton = Button.builder(espLabel(), button -> {
            LifestealClient.visibleBlockEsp = !LifestealClient.visibleBlockEsp;
            button.setMessage(espLabel());
        }).bounds(centerX - 105, startY, 210, 20).build();
        this.addRenderableWidget(espButton);

        Button planterButton = Button.builder(planterLabel(), button -> {
            LifestealClient.autoPlanter = !LifestealClient.autoPlanter;
            button.setMessage(planterLabel());
        }).bounds(centerX - 105, startY + 27, 210, 20).build();
        this.addRenderableWidget(planterButton);

        Button spectatorButton = Button.builder(spectatorLabel(), button -> {
            LifestealClient.spectatorListEnabled = !LifestealClient.spectatorListEnabled;
            button.setMessage(spectatorLabel());
        }).bounds(centerX - 105, startY + 54, 210, 20).build();
        this.addRenderableWidget(spectatorButton);

        this.addRenderableWidget(Button.builder(Component.literal("Done"), button -> this.onClose())
                .bounds(centerX - 105, startY + 81, 210, 20)
                .build());
    }

    private static Component espLabel() {
        return Component.literal("Visible Block ESP: " + (LifestealClient.visibleBlockEsp ? "ON" : "OFF"));
    }

    private static Component planterLabel() {
        return Component.literal("Auto Planter (singleplayer): " + (LifestealClient.autoPlanter ? "ON" : "OFF"));
    }

    private static Component spectatorLabel() {
        return Component.literal("Public Spectator List: " + (LifestealClient.spectatorListEnabled ? "ON" : "OFF"));
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);
        int centerX = this.width / 2;
        graphics.fill(centerX - 116, this.height / 2 - 69, centerX + 116, this.height / 2 - 40, 0xE5102B4A);
        graphics.fill(centerX - 116, this.height / 2 - 69, centerX - 112, this.height / 2 - 40, 0xFF279DFF);
        graphics.centeredText(this.font, "LIFESTEAL UTILITY", centerX, this.height / 2 - 59, 0xFFB8DEFF);
        graphics.centeredText(this.font, "Dark Blue  |  Fabric 26.1.2", centerX, this.height / 2 + 111, 0xFF8DA9CC);
    }

    @Override
    public void onClose() {
        if (this.minecraft != null) {
            this.minecraft.setScreen(this.parent);
        }
    }
}
