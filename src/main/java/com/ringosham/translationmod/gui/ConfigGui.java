package com.ringosham.translationmod.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.ringosham.translationmod.TranslationMod;
import com.ringosham.translationmod.client.LangManager;
import com.ringosham.translationmod.client.types.Language;
import com.ringosham.translationmod.common.ChatUtil;
import com.ringosham.translationmod.common.ConfigManager;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.ModList;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ConfigGui extends CommonGui {
    private static final int guiWidth = 250;
    private static final int guiHeight = 206;
    private static final StringTextComponent targetTooltip = new StringTextComponent("The language your chat will be translated to");
    private static final List<ITextComponent> selfTooltip = new ArrayList<>();
    private static final List<ITextComponent> speakAsTooltip = new ArrayList<>();
    private static final List<ITextComponent> regexTooltip = new ArrayList<>();
    private static final List<ITextComponent> apiKeyTooltip = new ArrayList<>();
    private static final List<ITextComponent> colorTooltip = new ArrayList<>();
    private static final List<ITextComponent> boldTooltip = new ArrayList<>();
    private static final List<ITextComponent> underlineTooltip = new ArrayList<>();
    private static final List<ITextComponent> italicTooltip = new ArrayList<>();
    private static final List<ITextComponent> signTooltip = new ArrayList<>();
    private static final String title;

    static {
        selfTooltip.add(new StringTextComponent("The language you speak in game"));
        selfTooltip.add(new StringTextComponent("This will be utilised when you want to translate what you speak"));
        speakAsTooltip.add(new StringTextComponent("The language your messages will be translated to."));
        speakAsTooltip.add(new StringTextComponent("After you typed your messages through this mod,"));
        speakAsTooltip.add(new StringTextComponent("it will be translated to the language you specified"));
        regexTooltip.add(new StringTextComponent("Regex are patterns for the mod to detect chat messages."));
        regexTooltip.add(new StringTextComponent("If you notice the mod doesn't do anything on a server,"));
        regexTooltip.add(new StringTextComponent("chances are you need to add one here."));
        signTooltip.add(new StringTextComponent("Translates signs when you look at them"));
        apiKeyTooltip.add(new StringTextComponent("Use your own API key for translation"));
        colorTooltip.add(new StringTextComponent("Changes the color of the translated message"));
        boldTooltip.add(new StringTextComponent("Bolds the translated message"));
        italicTooltip.add(new StringTextComponent("Italics the translated message"));
        underlineTooltip.add(new StringTextComponent("Underlines the translated message"));
        @SuppressWarnings("OptionalGetWithoutIsPresent")
        String modName = ModList.get().getModContainerById(TranslationMod.MODID).get().getModInfo().getDisplayName();
        title = modName + " - Settings";
    }

    //If this instance is between transition between other GUIs
    private boolean isTransition = false;
    private Language targetLang;
    private Language speakAsLang;
    private Language selfLang;
    private String color;
    private boolean bold;
    private boolean italic;
    private boolean underline;
    private boolean translateSign;

    ConfigGui() {
        super(title, guiHeight, guiWidth);
    }

    //Use for passing unsaved configurations between GUIs
    ConfigGui(ConfigGui instance, int langSelect, Language lang) {
        super(title, guiHeight, guiWidth);
        this.targetLang = instance.targetLang;
        this.speakAsLang = instance.speakAsLang;
        this.selfLang = instance.selfLang;
        this.color = instance.color;
        this.bold = instance.bold;
        this.italic = instance.italic;
        this.underline = instance.underline;
        this.translateSign = instance.translateSign;
        this.isTransition = true;
        if (lang != null) {
            switch (langSelect) {
                case 0:
                    this.targetLang = lang;
                    break;
                case 1:
                    this.selfLang = lang;
                    break;
                case 2:
                    this.speakAsLang = lang;
                    break;
            }
        }
    }

    @Override
    public void render(MatrixStack stack, int x, int y, float tick) {
        super.render(stack, x, y, tick);
        drawStringLine(stack, title, null, 0);
        font.drawString(stack, "Regex list:", getLeftMargin(), getYOrigin() + 25, 0x555555);
        font.drawString(stack, "Target language:", getLeftMargin(), getYOrigin() + 55, 0x555555);
        font.drawString(stack, "Self language:", getLeftMargin(), getYOrigin() + 75, 0x555555);
        font.drawString(stack, "Speak as language:", getLeftMargin(), getYOrigin() + 95, 0x555555);
        //func_243308_b(MatrixStack, List<ITextComponent>, int, int) -> renderTooltip(...)
        //Target language
        if (this.buttons.get(2).isHovered())
            renderTooltip(stack, targetTooltip, x, y);
        //Self language
        if (this.buttons.get(3).isHovered())
            func_243308_b(stack, selfTooltip, x, y);
        //Speak as language
        if (this.buttons.get(4).isHovered())
            func_243308_b(stack, speakAsTooltip, x, y);
        //Regex list
        if (this.buttons.get(11).isHovered())
            func_243308_b(stack, regexTooltip, x, y);
        //API key
        if (this.buttons.get(6).isHovered())
            func_243308_b(stack, apiKeyTooltip, x, y);
        //Translate sign
        if (this.buttons.get(5).isHovered())
            func_243308_b(stack, signTooltip, x, y);
        //Color message
        if (this.buttons.get(7).isHovered())
            func_243308_b(stack, colorTooltip, x, y);
        //Bold
        if (this.buttons.get(8).isHovered())
            func_243308_b(stack, boldTooltip, x, y);
        //Italic
        if (this.buttons.get(9).isHovered())
            func_243308_b(stack, italicTooltip, x, y);
        //Underline
        if (this.buttons.get(10).isHovered())
            func_243308_b(stack, underlineTooltip, x, y);
    }

    @Override
    public void init() {
        if (!isTransition) {
            color = ConfigManager.config.color.get();
            bold = ConfigManager.config.bold.get();
            italic = ConfigManager.config.italic.get();
            underline = ConfigManager.config.underline.get();
            translateSign = ConfigManager.config.translateSign.get();
            targetLang = LangManager.getInstance().findLanguageFromName(ConfigManager.config.targetLanguage.get());
            selfLang = LangManager.getInstance().findLanguageFromName(ConfigManager.config.selfLanguage.get());
            speakAsLang = LangManager.getInstance().findLanguageFromName(ConfigManager.config.speakAsLanguage.get());
        }
        getMinecraft().keyboardListener.enableRepeatEvents(true);
        addButton(new Button(getLeftMargin(), getYOrigin() + guiHeight - 5 - regularButtonHeight, regularButtonWidth, regularButtonHeight, new StringTextComponent("Save and close"),
                (button) -> this.applySettings()));
        addButton(new Button(getRightMargin(regularButtonWidth), getYOrigin() + guiHeight - 5 - regularButtonHeight, regularButtonWidth, regularButtonHeight, new StringTextComponent("Reset to default"),
                (button) -> this.resetDefault()));
        addButton(new Button(getRightMargin(regularButtonWidth), getYOrigin() + 50, regularButtonWidth, regularButtonHeight, new StringTextComponent(targetLang.getName()),
                (button) -> this.langSelect(0)));
        addButton(new Button(getRightMargin(regularButtonWidth), getYOrigin() + 70, regularButtonWidth, regularButtonHeight, new StringTextComponent(selfLang.getName()),
                (button) -> this.langSelect(1)));
        addButton(new Button(getRightMargin(regularButtonWidth), getYOrigin() + 90, regularButtonWidth, regularButtonHeight, new StringTextComponent(speakAsLang.getName()),
                (button) -> this.langSelect(2)));
        addButton(new Button(getLeftMargin(), getYOrigin() + guiHeight - 15 - regularButtonHeight * 3, regularButtonWidth, regularButtonHeight, new StringTextComponent(translateSign ? TextFormatting.GREEN + "Translate signs" : TextFormatting.RED + "Translate signs"),
                (button) -> {
                    translateSign = !translateSign;
                    this.toggleButtonBool(translateSign, button);
                }));
        addButton(new Button(getLeftMargin(), getYOrigin() + guiHeight - 10 - regularButtonHeight * 2, regularButtonWidth, regularButtonHeight, new StringTextComponent("API key"),
                (button) -> this.addKeyGui()));
        addButton(new Button(getRightMargin(regularButtonWidth), getYOrigin() + guiHeight - 10 - regularButtonHeight * 2, regularButtonWidth, regularButtonHeight, new StringTextComponent(TextFormatting.getValueByName(color) + "Message color"),
                this::rotateColor));
        addButton(new Button(getLeftMargin() + regularButtonWidth + 10, getYOrigin() + guiHeight - 15 - regularButtonHeight * 3, smallButtonLength, smallButtonLength, new StringTextComponent(bold ? "\u00a7a" + TextFormatting.BOLD + "B" : "\u00a7c" + TextFormatting.BOLD + "B"),
                (button) -> {
                    bold = !bold;
                    this.toggleButtonBool(bold, button);
                }));
        addButton(new Button(getLeftMargin() + regularButtonWidth + 10, getYOrigin() + guiHeight - 10 - regularButtonHeight * 2, smallButtonLength, smallButtonLength, new StringTextComponent(italic ? "\u00a7a" + TextFormatting.ITALIC + "I" : "\u00a7c" + TextFormatting.ITALIC + "I"),
                (button) -> {
                    italic = !italic;
                    this.toggleButtonBool(italic, button);
                }));
        addButton(new Button(getLeftMargin() + regularButtonWidth + 10, getYOrigin() + guiHeight - 5 - regularButtonHeight, smallButtonLength, smallButtonLength, new StringTextComponent(underline ? "\u00a7a" + TextFormatting.UNDERLINE + "U" : "\u00a7c" + TextFormatting.UNDERLINE + "U"),
                (button) -> {
                    underline = !underline;
                    this.toggleButtonBool(underline, button);
                }));
        addButton(new Button(getRightMargin(regularButtonWidth), getYOrigin() + 20, regularButtonWidth, regularButtonHeight, new StringTextComponent("View / Add"),
                (button) -> this.regexGui()));
    }

    private void applySettings() {
        getMinecraft().keyboardListener.enableRepeatEvents(false);
        ConfigManager.config.targetLanguage.set(targetLang.getName());
        ConfigManager.config.selfLanguage.set(selfLang.getName());
        ConfigManager.config.speakAsLanguage.set(speakAsLang.getName());
        ConfigManager.config.color.set(color);
        ConfigManager.config.bold.set(bold);
        ConfigManager.config.italic.set(italic);
        ConfigManager.config.underline.set(underline);
        ConfigManager.config.translateSign.set(translateSign);
        ConfigManager.saveConfig();
        ChatUtil.printChatMessage(true, "Settings applied.", TextFormatting.WHITE);
        exitGui();
    }

    private void exitGui() {
        getMinecraft().keyboardListener.enableRepeatEvents(false);
        getMinecraft().displayGuiScreen(null);
    }

    private void langSelect(int id) {
        getMinecraft().keyboardListener.enableRepeatEvents(false);
        getMinecraft().displayGuiScreen(new LanguageSelectGui(this, id));
    }

    private void toggleButtonBool(boolean state, Button button) {
        getMinecraft().keyboardListener.enableRepeatEvents(false);
        StringTextComponent buttonText = new StringTextComponent(button.getMessage().getUnformattedComponentText().replaceAll("§(.)", ""));
        buttonText.setStyle(Style.EMPTY.setFormatting((state ? TextFormatting.GREEN : TextFormatting.RED)));
        button.setMessage(buttonText);
    }

    private void rotateColor(Button button) {
        getMinecraft().keyboardListener.enableRepeatEvents(false);
        TextFormatting formatColor = TextFormatting.getValueByName(color);
        int colorCode = Objects.requireNonNull(formatColor).getColorIndex();
        colorCode++;
        if (colorCode == 16)
            colorCode = 0;
        TextFormatting newColor = TextFormatting.fromColorIndex(colorCode);
        assert newColor != null;
        color = newColor.getFriendlyName();
        StringTextComponent buttonText = new StringTextComponent(button.getMessage().getUnformattedComponentText().replaceAll("§(.)", ""));
        buttonText.setStyle(Style.EMPTY.setFormatting(newColor));
        button.setMessage(buttonText);
    }

    private void regexGui() {
        getMinecraft().keyboardListener.enableRepeatEvents(true);
        getMinecraft().displayGuiScreen(new RegexGui());
    }

    private void addKeyGui() {
        getMinecraft().keyboardListener.enableRepeatEvents(true);
        getMinecraft().displayGuiScreen(new AddKeyGui());
    }

    private void resetDefault() {
        color = "gray";
        bold = false;
        italic = false;
        underline = false;
        translateSign = true;
        targetLang = LangManager.getInstance().findLanguageFromName("English");
        selfLang = targetLang;
        speakAsLang = LangManager.getInstance().findLanguageFromName("Japanese");
        this.buttons.get(2).setMessage(new StringTextComponent("English"));
        this.buttons.get(3).setMessage(new StringTextComponent("English"));
        this.buttons.get(4).setMessage(new StringTextComponent("Japanese"));
        this.buttons.get(5).setMessage(new StringTextComponent(TextFormatting.GREEN + "Translate signs"));
        this.buttons.get(7).setMessage(new StringTextComponent(TextFormatting.getValueByName(color) + "Message color"));
        this.buttons.get(8).setMessage(new StringTextComponent(bold ? "\u00a7a" : "\u00a7c" + TextFormatting.BOLD + "B"));
        this.buttons.get(9).setMessage(new StringTextComponent(italic ? "\u00a7a" : "\u00a7c" + TextFormatting.ITALIC + "I"));
        this.buttons.get(10).setMessage(new StringTextComponent(underline ? "\u00a7a" : "\u00a7c" + TextFormatting.UNDERLINE + "U"));
    }
}
