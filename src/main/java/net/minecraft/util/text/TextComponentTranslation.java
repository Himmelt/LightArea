package net.minecraft.util.text;

public class TextComponentTranslation implements ITextComponent {
    public TextComponentTranslation(String key, Object... args) {
    }

    public ITextComponent setStyle(Style style) {
        return new TextComponentTranslation("");
    }

}
