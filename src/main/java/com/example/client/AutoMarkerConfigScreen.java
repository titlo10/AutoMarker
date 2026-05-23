package com.example.client;

import com.example.AutoMarkerConfig;
import com.example.AutoMarkerMod;

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

    @Override
    protected void init() {
        int x = this.width / 2;
        int y = this.height / 4;

        //#if MC>=260100
        // Toggle Deaths button
        this.addRenderableWidget(CycleButton.onOffBuilder(config.enableDeaths)
            .create(x - 100, y, 200, 20, Component.translatable("gui.automarker.track_deaths"), (button, value) -> {
                config.enableDeaths = value;
            })
        );

        // Toggle Achievements button
        this.addRenderableWidget(CycleButton.onOffBuilder(config.enableAchievements)
            .create(x - 100, y + 25, 200, 20, Component.translatable("gui.automarker.track_achievements"), (button, value) -> {
                config.enableAchievements = value;
            })
        );

        // Text field input for chat keywords
        this.chatKeywordsEdit = new EditBox(this.font, x - 100, y + 70, 200, 20, Component.translatable("gui.automarker.keywords"));
        this.chatKeywordsEdit.setValue(config.chatKeywords);
        this.addRenderableWidget(this.chatKeywordsEdit);

        // Done button
        this.addRenderableWidget(Button.builder(Component.translatable("gui.automarker.done"), button -> {
            onClose();
        }).bounds(x - 100, y + 110, 200, 20).build());
        //#else
        //$$ // Toggle Deaths button
        //$$ this.addDrawableChild(CyclingButtonWidget.onOffBuilder(config.enableDeaths)
        //$$     .build(x - 100, y, 200, 20, Text.translatable("gui.automarker.track_deaths"), (button, value) -> {
        //$$         config.enableDeaths = value;
        //$$     })
        //$$ );
        //$$
        //$$ // Toggle Achievements button
        //$$ this.addDrawableChild(CyclingButtonWidget.onOffBuilder(config.enableAchievements)
        //$$     .build(x - 100, y + 25, 200, 20, Text.translatable("gui.automarker.track_achievements"), (button, value) -> {
        //$$         config.enableAchievements = value;
        //$$     })
        //$$ );
        //$$
        //$$ // Text field input for chat keywords
        //$$ this.chatKeywordsEdit = new TextFieldWidget(this.textRenderer, x - 100, y + 70, 200, 20, Text.translatable("gui.automarker.keywords"));
        //$$ this.chatKeywordsEdit.setText(config.chatKeywords);
        //$$ this.addDrawableChild(this.chatKeywordsEdit);
        //$$
        //$$ // Done button
        //$$ this.addDrawableChild(ButtonWidget.builder(Text.translatable("gui.automarker.done"), button -> {
        //$$     close();
        //$$ }).dimensions(x - 100, y + 110, 200, 20).build());
        //#endif
    }

    //#if MC>=260100
    @Override
    public void extractRenderState(net.minecraft.client.gui.GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTicks);
        int x = this.width / 2;
        int y = this.height / 4;
        graphics.text(this.font, this.title, x - this.font.width(this.title) / 2, y - 20, 0xFFFFFFFF);
        Component label = Component.translatable("gui.automarker.chat_keywords_label");
        graphics.text(this.font, label, x - 100, y + 55, 0xFFA0A0A0);
    }
    //#else
    //$$ @Override
    //$$ public void render(net.minecraft.client.gui.DrawContext context, int mouseX, int mouseY, float delta) {
    //$$     super.render(context, mouseX, mouseY, delta);
    //$$     int x = this.width / 2;
    //$$     int y = this.height / 4;
    //$$     context.drawTextWithShadow(this.textRenderer, this.title, x - this.textRenderer.getWidth(this.title) / 2, y - 20, 0xFFFFFFFF);
    //$$     Text label = Text.translatable("gui.automarker.chat_keywords_label");
    //$$     context.drawTextWithShadow(this.textRenderer, label, x - 100, y + 55, 0xFFA0A0A0);
    //$$ }
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
