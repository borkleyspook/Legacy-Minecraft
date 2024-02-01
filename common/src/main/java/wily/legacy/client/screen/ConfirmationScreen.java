package wily.legacy.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class ConfirmationScreen extends PanelBackgroundScreen{
    protected final MultiLineLabel messageLabel;
    protected Predicate<Button> okAction;
    public Button okButton;

    public ConfirmationScreen(Screen parent, int imageWidth, int imageHeight, Component title, MultiLineLabel messageLabel, Predicate<Button> okAction) {
        super(imageWidth, imageHeight, title);
        this.messageLabel = messageLabel;
        this.okAction = okAction;
        this.parent = parent;
    }
    public ConfirmationScreen(Screen parent, int imageWidth, int imageHeight, Component title, Component message, Consumer<Button> okAction) {
        this(parent, imageWidth, imageHeight, title, MultiLineLabel.create(Minecraft.getInstance().font,message,imageWidth - 30), b-> {okAction.accept(b);return false;});
    }
    public ConfirmationScreen(Screen parent, Component title, Component message, Consumer<Button> okAction) {
        this(parent,230, 133,title,message,okAction);
    }
    public static ConfirmationScreen createInfoScreen(Screen parent, Component title,Component message) {
        return createInfoScreen(parent,title,230,MultiLineLabel.create(Minecraft.getInstance().font,message,200));
    }
    public static ConfirmationScreen createInfoScreen(Screen parent, Component title, int imageWidth, MultiLineLabel messageLines) {
        return new ConfirmationScreen(parent, imageWidth, 97 + messageLines.getLineCount() * 12,title,messageLines, b-> true){
            protected void initButtons() {
                okButton = addRenderableWidget(Button.builder(Component.translatable("gui.ok"),b-> {if (okAction.test(b)) onClose();}).bounds(panel.x + 15, panel.y + panel.height - 30,200,20).build());
            }
        };
    }

    @Override
    protected void init() {
        super.init();
        initButtons();
        parent.resize(minecraft,width,height);
    }
    protected void initButtons(){
        addRenderableWidget(Button.builder(Component.translatable("gui.cancel"), b-> this.onClose()).bounds(panel.x + 15, panel.y + panel.height - 52,200,20).build());
        okButton = addRenderableWidget(Button.builder(Component.translatable("gui.ok"),b-> {if (okAction.test(b)) onClose();}).bounds(panel.x + 15, panel.y + panel.height - 30,200,20).build());
    }

    @Override
    public void renderDefaultBackground(GuiGraphics guiGraphics, int i, int j, float f) {
        guiGraphics.pose().translate(0,0,-800);
        parent.render(guiGraphics,0,0,f);
        guiGraphics.pose().translate(0,0,800);
        renderTransparentBackground(guiGraphics);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        super.render(guiGraphics, i, j, f);
        guiGraphics.drawString(font,title,panel.x+ 15,panel.y+ 15, 4210752,false);
        messageLabel.renderLeftAlignedNoShadow(guiGraphics,panel.x + 15, panel.y + 35, 12, 4210752);
    }
}
