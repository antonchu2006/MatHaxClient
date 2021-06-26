package matejko06.mathax.gui.widgets.pressable;

import matejko06.mathax.gui.renderer.packer.GuiTexture;

public abstract class WButton extends WPressable {
    protected String text;
    protected double textWidth;

    protected GuiTexture texture;

    public WButton(String text, GuiTexture texture) {
        this.text = text;
        this.texture = texture;
    }

    @Override
    protected void onCalculateSize() {
        double pad = pad();

        if (text != null) {
            textWidth = theme.textWidth(text);

            width = pad + textWidth + pad;
            height = pad + theme.textHeight() + pad;
        }
        else {
            double s = theme.textHeight();

            width = pad + s + pad;
            height = pad + s + pad;
        }
    }

    public void set(String text) {
        if (this.text == null || Math.round(theme.textWidth(text)) != textWidth) invalidate();

        this.text = text;
    }
}
