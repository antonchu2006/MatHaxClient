package matejko06.mathax.gui.widgets;

import matejko06.mathax.gui.renderer.GuiRenderer;
import matejko06.mathax.gui.widgets.containers.WHorizontalList;
import matejko06.mathax.gui.widgets.pressable.WButton;
import matejko06.mathax.utils.misc.Keybind;

public class WKeybind extends WHorizontalList {
    public Runnable action;
    public Runnable actionOnSet;

    private WLabel label;

    private final Keybind keybind;
    private final Keybind defaultValue;
    private boolean listening;

    public WKeybind(Keybind keybind, Keybind defaultValue) {
        this.keybind = keybind;
        this.defaultValue = defaultValue;
    }

    @Override
    public void init() {
        label = add(theme.label("")).widget();

        WButton set = add(theme.button("Set")).widget();
        set.action = () -> {
            listening = true;
            label.set(appendBindText("..."));

            if (actionOnSet != null) actionOnSet.run();
        };

        WButton reset = add(theme.button(GuiRenderer.RESET)).expandCellX().right().widget();
        reset.action = this::resetBind;

        refreshLabel();
    }

    public boolean onAction(boolean isKey, int value) {
        if (listening && keybind.canBindTo(isKey, value)) {
            keybind.set(isKey, value);
            reset();

            if (action != null) action.run();
            return true;
        }

        return false;
    }

    public void resetBind() {
        keybind.set(defaultValue);
        reset();
    }

    public void reset() {
        listening = false;
        refreshLabel();
    }

    private void refreshLabel() {
        label.set(appendBindText(keybind.toString()));
    }

    private String appendBindText(String text) {
        return "Bind: " + text;
    }
}
