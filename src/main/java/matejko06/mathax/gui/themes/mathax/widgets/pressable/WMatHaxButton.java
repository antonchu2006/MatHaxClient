package matejko06.mathax.gui.themes.mathax.widgets.pressable;

import matejko06.mathax.gui.renderer.GuiRenderer;
import matejko06.mathax.gui.renderer.packer.GuiTexture;
import matejko06.mathax.gui.themes.mathax.MatHaxGuiTheme;
import matejko06.mathax.gui.themes.mathax.MatHaxWidget;
import matejko06.mathax.gui.widgets.pressable.WButton;

public class WMatHaxButton extends WButton implements MatHaxWidget {
    public WMatHaxButton(String text, GuiTexture texture) {
        super(text, texture);
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        MatHaxGuiTheme theme = theme();
        double pad = pad();

        renderBackground(renderer, this, pressed, mouseOver);

        if (text != null) {
            renderer.text(text, x + width / 2 - textWidth / 2, y + pad, theme.textColor.get(), false);
        }
        else {
            double ts = theme.textHeight();
            renderer.quad(x + width / 2 - ts / 2, y + pad, ts, ts, texture, theme.textColor.get());
        }
    }
}
