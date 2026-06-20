package com.titlo10.automarker.client;

import com.titlo10.automarker.AutoMarkerConfig;
import com.titlo10.automarker.AutoMarkerMod;

//#if MC>=260100
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.GuiGraphicsExtractor;
//#else
//$$ import net.minecraft.client.gui.screen.Screen;
//$$ import net.minecraft.text.Text;
//$$ import net.minecraft.client.gui.widget.ButtonWidget;
//$$ import net.minecraft.client.gui.widget.CyclingButtonWidget;
//$$ import net.minecraft.client.gui.widget.TextFieldWidget;
//#endif

public class AutoMarkerConfigScreen extends Screen {
    //#if MC>=260100
    private final Screen parent;
    private EditBox chatKeywordsEdit;
    private final AutoMarkerConfig config;

    public AutoMarkerConfigScreen(Screen parent) {
        super(Component.translatable("gui.automarker.title"));
        this.parent = parent;
        this.config = AutoMarkerMod.config;
    }
    //#else
    //$$ private final Screen parent;
    //$$ private TextFieldWidget chatKeywordsEdit;
    //$$ private final AutoMarkerConfig config;
    //$$
    //$$ public AutoMarkerConfigScreen(Screen parent) {
    //$$     super(Text.translatable("gui.automarker.title"));
    //$$     this.parent = parent;
    //$$     this.config = AutoMarkerMod.config;
    //$$ }
    //#endif

    private static final int COL_W = 150;
    private static final int GAP = 10;
    private static final int ROW_H = 24;

    @Override
    protected void init() {
        int x = this.width / 2;
        int y = this.height / 4;
        int leftX = x - COL_W - GAP / 2;
        int rightX = x + GAP / 2;
        int fullW = COL_W * 2 + GAP;
        int keywordsY = y + ROW_H * 3 + 18;
        int doneY = keywordsY + 28;

        //#if MC>=260100
        this.addRenderableWidget(CycleButton.onOffBuilder(config.enableDeaths)
            .create(leftX, y, COL_W, 20, Component.translatable("gui.automarker.track_deaths"), (button, value) -> {
                config.enableDeaths = value;
            })
        );

        this.addRenderableWidget(CycleButton.onOffBuilder(config.enablePvpKills)
            .create(rightX, y, COL_W, 20, Component.translatable("gui.automarker.track_pvp_kills"), (button, value) -> {
                config.enablePvpKills = value;
            })
        );

        this.addRenderableWidget(CycleButton.onOffBuilder(config.enableTotemPops)
            .create(leftX, y + ROW_H, COL_W, 20, Component.translatable("gui.automarker.track_totem_pops"), (button, value) -> {
                config.enableTotemPops = value;
            })
        );

        this.addRenderableWidget(CycleButton.onOffBuilder(config.enableAchievements)
            .create(rightX, y + ROW_H, COL_W, 20, Component.translatable("gui.automarker.track_achievements"), (button, value) -> {
                config.enableAchievements = value;
            })
        );

        this.addRenderableWidget(CycleButton.onOffBuilder(config.enableDimensionChanges)
            .create(leftX, y + ROW_H * 2, COL_W, 20, Component.translatable("gui.automarker.track_dimension_changes"), (button, value) -> {
                config.enableDimensionChanges = value;
            })
        );

        this.chatKeywordsEdit = new EditBox(this.font, leftX, keywordsY, fullW, 20, Component.translatable("gui.automarker.keywords"));
        this.chatKeywordsEdit.setValue(config.chatKeywords);
        this.addRenderableWidget(this.chatKeywordsEdit);

        this.addRenderableWidget(Button.builder(Component.translatable("gui.automarker.done"), button -> {
            onClose();
        }).bounds(leftX, doneY, fullW, 20).build());
        //#else
        //$$ this.addDrawableChild(CyclingButtonWidget.onOffBuilder(config.enableDeaths)
        //$$     .build(leftX, y, COL_W, 20, Text.translatable("gui.automarker.track_deaths"), (button, value) -> {
        //$$         config.enableDeaths = value;
        //$$     })
        //$$ );
        //$$
        //$$ this.addDrawableChild(CyclingButtonWidget.onOffBuilder(config.enablePvpKills)
        //$$     .build(rightX, y, COL_W, 20, Text.translatable("gui.automarker.track_pvp_kills"), (button, value) -> {
        //$$         config.enablePvpKills = value;
        //$$     })
        //$$ );
        //$$
        //$$ this.addDrawableChild(CyclingButtonWidget.onOffBuilder(config.enableTotemPops)
        //$$     .build(leftX, y + ROW_H, COL_W, 20, Text.translatable("gui.automarker.track_totem_pops"), (button, value) -> {
        //$$         config.enableTotemPops = value;
        //$$     })
        //$$ );
        //$$
        //$$ this.addDrawableChild(CyclingButtonWidget.onOffBuilder(config.enableAchievements)
        //$$     .build(rightX, y + ROW_H, COL_W, 20, Text.translatable("gui.automarker.track_achievements"), (button, value) -> {
        //$$         config.enableAchievements = value;
        //$$     })
        //$$ );
        //$$
        //$$ this.addDrawableChild(CyclingButtonWidget.onOffBuilder(config.enableDimensionChanges)
        //$$     .build(leftX, y + ROW_H * 2, COL_W, 20, Text.translatable("gui.automarker.track_dimension_changes"), (button, value) -> {
        //$$         config.enableDimensionChanges = value;
        //$$     })
        //$$ );
        //$$
        //$$ this.chatKeywordsEdit = new TextFieldWidget(this.textRenderer, leftX, keywordsY, fullW, 20, Text.translatable("gui.automarker.keywords"));
        //$$ this.chatKeywordsEdit.setText(config.chatKeywords);
        //$$ this.addDrawableChild(this.chatKeywordsEdit);
        //$$
        //$$ this.addDrawableChild(ButtonWidget.builder(Text.translatable("gui.automarker.done"), button -> {
        //$$     close();
        //$$ }).dimensions(leftX, doneY, fullW, 20).build());
        //#endif
    }

    //#if MC>=260100
    @Override
    public void extractRenderState(net.minecraft.client.gui.GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTicks);
        int x = this.width / 2;
        int y = this.height / 4;
        int leftX = x - COL_W - GAP / 2;
        graphics.text(this.font, this.title, x - this.font.width(this.title) / 2, y - 20, 0xFFFFFFFF);
        Component label = Component.translatable("gui.automarker.chat_keywords_label");
        graphics.text(this.font, label, leftX, y + ROW_H * 3 + 6, 0xFFA0A0A0);
    }
    //#else
    //$$ @Override
    //$$ public void render(net.minecraft.client.gui.DrawContext context, int mouseX, int mouseY, float delta) {
    //#if MC<12002
    //$$     this.renderBackground(context);
    //#endif
    //$$     super.render(context, mouseX, mouseY, delta);
    //$$     int x = this.width / 2;
    //$$     int y = this.height / 4;
    //$$     int leftX = x - COL_W - GAP / 2;
    //$$     context.drawTextWithShadow(this.textRenderer, this.title, x - this.textRenderer.getWidth(this.title) / 2, y - 20, 0xFFFFFFFF);
    //$$     Text label = Text.translatable("gui.automarker.chat_keywords_label");
    //$$     context.drawTextWithShadow(this.textRenderer, label, leftX, y + ROW_H * 3 + 6, 0xFFA0A0A0);
    //$$ }
    //#endif

    //#if MC>=260100
    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean isInGameUi() {
        return true;
    }
    //#else
    //$$ @Override
    //$$ public boolean shouldPause() {
    //$$     return false;
    //$$ }
    //#if MC>=12002
    //$$ @Override
    //$$ public void renderBackground(net.minecraft.client.gui.DrawContext context, int mouseX, int mouseY, float delta) {
    //$$     context.fillGradient(0, 0, this.width, this.height, -1072689136, -804253680);
    //$$ }
    //#endif
    //#endif

    //#if MC>=260100
    @Override
    public void onClose() {
        if (this.chatKeywordsEdit != null) {
            config.chatKeywords = this.chatKeywordsEdit.getValue();
        }
        config.save();
        this.minecraft.setScreen(this.parent);
    }
    //#else
    //$$ @Override
    //$$ public void close() {
    //$$     if (this.chatKeywordsEdit != null) {
    //$$         config.chatKeywords = this.chatKeywordsEdit.getText();
    //$$     }
    //$$     config.save();
    //$$     this.client.setScreen(this.parent);
    //$$ }
    //#endif
}
