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
        this(parent, AutoMarkerMod.config.copy());
    }

    private AutoMarkerConfigScreen(Screen parent, AutoMarkerConfig workingConfig) {
        super(Component.translatable("gui.automarker.title"));
        this.parent = parent;
        this.config = workingConfig;
    }
    //#else
    //$$ private final Screen parent;
    //$$ private TextFieldWidget chatKeywordsEdit;
    //$$ private final AutoMarkerConfig config;
    //$$
    //$$ public AutoMarkerConfigScreen(Screen parent) {
    //$$     this(parent, AutoMarkerMod.config.copy());
    //$$ }
    //$$
    //$$ private AutoMarkerConfigScreen(Screen parent, AutoMarkerConfig workingConfig) {
    //$$     super(Text.translatable("gui.automarker.title"));
    //$$     this.parent = parent;
    //$$     this.config = workingConfig;
    //$$ }
    //#endif

    private static final int COL_W = 150;
    private static final int GAP = 10;
    private static final int ROW_H = 24;

    @Override
    protected void init() {
        int x = this.width / 2;
        int y = this.height / 4;
        int colW = Math.min(COL_W, Math.max(90, (this.width - 30 - GAP) / 2));
        int leftX = x - colW - GAP / 2;
        int rightX = x + GAP / 2;
        int fullW = colW * 2 + GAP;
        int keywordsY = y + ROW_H * 3 + 18;
        int doneY = keywordsY + 28;
        int buttonW = (fullW - GAP * 2) / 3;

        //#if MC>=260100
        this.addRenderableWidget(CycleButton.onOffBuilder(config.enableDeaths)
            .create(leftX, y, colW, 20, Component.translatable("gui.automarker.track_deaths"), (button, value) -> {
                config.enableDeaths = value;
            })
        );

        this.addRenderableWidget(CycleButton.onOffBuilder(config.enablePvpKills)
            .create(rightX, y, colW, 20, Component.translatable("gui.automarker.track_pvp_kills"), (button, value) -> {
                config.enablePvpKills = value;
            })
        );

        this.addRenderableWidget(CycleButton.onOffBuilder(config.enableTotemPops)
            .create(leftX, y + ROW_H, colW, 20, Component.translatable("gui.automarker.track_totem_pops"), (button, value) -> {
                config.enableTotemPops = value;
            })
        );

        this.addRenderableWidget(CycleButton.onOffBuilder(config.enableAchievements)
            .create(rightX, y + ROW_H, colW, 20, Component.translatable("gui.automarker.track_achievements"), (button, value) -> {
                config.enableAchievements = value;
            })
        );

        this.addRenderableWidget(CycleButton.onOffBuilder(config.enableDimensionChanges)
            .create(leftX, y + ROW_H * 2, colW, 20, Component.translatable("gui.automarker.track_dimension_changes"), (button, value) -> {
                config.enableDimensionChanges = value;
            })
        );

        this.chatKeywordsEdit = new EditBox(this.font, leftX, keywordsY, fullW, 20, Component.translatable("gui.automarker.keywords"));
        this.chatKeywordsEdit.setValue(config.chatKeywords);
        this.addRenderableWidget(this.chatKeywordsEdit);

        this.addRenderableWidget(Button.builder(Component.translatable("gui.automarker.reset"), button -> {
            this.minecraft.setScreen(new AutoMarkerConfigScreen(parent, new AutoMarkerConfig()));
        }).bounds(leftX, doneY, buttonW, 20).build());
        this.addRenderableWidget(Button.builder(Component.translatable("gui.automarker.cancel"), button -> onClose())
            .bounds(leftX + buttonW + GAP, doneY, buttonW, 20).build());
        this.addRenderableWidget(Button.builder(Component.translatable("gui.automarker.done"), button -> saveAndClose())
            .bounds(leftX + (buttonW + GAP) * 2, doneY, buttonW, 20).build());
        //#else
        //$$ this.addDrawableChild(CyclingButtonWidget.onOffBuilder(config.enableDeaths)
        //$$     .build(leftX, y, colW, 20, Text.translatable("gui.automarker.track_deaths"), (button, value) -> {
        //$$         config.enableDeaths = value;
        //$$     })
        //$$ );
        //$$
        //$$ this.addDrawableChild(CyclingButtonWidget.onOffBuilder(config.enablePvpKills)
        //$$     .build(rightX, y, colW, 20, Text.translatable("gui.automarker.track_pvp_kills"), (button, value) -> {
        //$$         config.enablePvpKills = value;
        //$$     })
        //$$ );
        //$$
        //$$ this.addDrawableChild(CyclingButtonWidget.onOffBuilder(config.enableTotemPops)
        //$$     .build(leftX, y + ROW_H, colW, 20, Text.translatable("gui.automarker.track_totem_pops"), (button, value) -> {
        //$$         config.enableTotemPops = value;
        //$$     })
        //$$ );
        //$$
        //$$ this.addDrawableChild(CyclingButtonWidget.onOffBuilder(config.enableAchievements)
        //$$     .build(rightX, y + ROW_H, colW, 20, Text.translatable("gui.automarker.track_achievements"), (button, value) -> {
        //$$         config.enableAchievements = value;
        //$$     })
        //$$ );
        //$$
        //$$ this.addDrawableChild(CyclingButtonWidget.onOffBuilder(config.enableDimensionChanges)
        //$$     .build(leftX, y + ROW_H * 2, colW, 20, Text.translatable("gui.automarker.track_dimension_changes"), (button, value) -> {
        //$$         config.enableDimensionChanges = value;
        //$$     })
        //$$ );
        //$$
        //$$ this.chatKeywordsEdit = new TextFieldWidget(this.textRenderer, leftX, keywordsY, fullW, 20, Text.translatable("gui.automarker.keywords"));
        //$$ this.chatKeywordsEdit.setText(config.chatKeywords);
        //$$ this.addDrawableChild(this.chatKeywordsEdit);
        //$$
        //$$ this.addDrawableChild(ButtonWidget.builder(Text.translatable("gui.automarker.reset"), button -> {
        //$$     this.client.setScreen(new AutoMarkerConfigScreen(parent, new AutoMarkerConfig()));
        //$$ }).dimensions(leftX, doneY, buttonW, 20).build());
        //$$ this.addDrawableChild(ButtonWidget.builder(Text.translatable("gui.automarker.cancel"), button -> close())
        //$$     .dimensions(leftX + buttonW + GAP, doneY, buttonW, 20).build());
        //$$ this.addDrawableChild(ButtonWidget.builder(Text.translatable("gui.automarker.done"), button -> saveAndClose())
        //$$     .dimensions(leftX + (buttonW + GAP) * 2, doneY, buttonW, 20).build());
        //#endif
    }

    //#if MC>=260100
    @Override
    public void extractRenderState(net.minecraft.client.gui.GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTicks);
        int x = this.width / 2;
        int y = this.height / 4;
        int colW = Math.min(COL_W, Math.max(90, (this.width - 30 - GAP) / 2));
        int leftX = x - colW - GAP / 2;
        graphics.text(this.font, this.title, x - this.font.width(this.title) / 2, y - 20, 0xFFFFFFFF);
        Component label = Component.translatable("gui.automarker.chat_keywords_label");
        graphics.text(this.font, label, leftX, y + ROW_H * 3 + 6, 0xFFA0A0A0);
        Component status = Component.translatable(AutoMarkerMod.isRecordingActive()
            ? "gui.automarker.recording_active" : "gui.automarker.recording_inactive");
        graphics.text(this.font, status, x - this.font.width(status) / 2, y + ROW_H * 3 + 82,
            AutoMarkerMod.isRecordingActive() ? 0xFF55FF55 : 0xFFFFAA00);
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
    //$$     int colW = Math.min(COL_W, Math.max(90, (this.width - 30 - GAP) / 2));
    //$$     int leftX = x - colW - GAP / 2;
    //$$     context.drawTextWithShadow(this.textRenderer, this.title, x - this.textRenderer.getWidth(this.title) / 2, y - 20, 0xFFFFFFFF);
    //$$     Text label = Text.translatable("gui.automarker.chat_keywords_label");
    //$$     context.drawTextWithShadow(this.textRenderer, label, leftX, y + ROW_H * 3 + 6, 0xFFA0A0A0);
    //$$     Text status = Text.translatable(AutoMarkerMod.isRecordingActive()
    //$$         ? "gui.automarker.recording_active" : "gui.automarker.recording_inactive");
    //$$     context.drawTextWithShadow(this.textRenderer, status, x - this.textRenderer.getWidth(status) / 2,
    //$$         y + ROW_H * 3 + 82, AutoMarkerMod.isRecordingActive() ? 0xFF55FF55 : 0xFFFFAA00);
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
        this.minecraft.setScreen(this.parent);
    }

    private void saveAndClose() {
        if (this.chatKeywordsEdit != null) {
            config.chatKeywords = this.chatKeywordsEdit.getValue();
        }
        AutoMarkerMod.config.copyFrom(config);
        AutoMarkerMod.config.save();
        this.minecraft.setScreen(this.parent);
    }
    //#else
    //$$ @Override
    //$$ public void close() {
    //$$     this.client.setScreen(this.parent);
    //$$ }
    //$$
    //$$ private void saveAndClose() {
    //$$     if (this.chatKeywordsEdit != null) {
    //$$         config.chatKeywords = this.chatKeywordsEdit.getText();
    //$$     }
    //$$     AutoMarkerMod.config.copyFrom(config);
    //$$     AutoMarkerMod.config.save();
    //$$     this.client.setScreen(this.parent);
    //$$ }
    //#endif
}
