package mathax.client.gui.tabs.builtin;

import mathax.client.events.mathax.KeyEvent;
import mathax.client.events.mathax.MouseButtonEvent;
import mathax.client.gui.GuiTheme;
import mathax.client.gui.WindowScreen;
import mathax.client.gui.renderer.GuiRenderer;
import mathax.client.gui.tabs.Tab;
import mathax.client.gui.tabs.TabScreen;
import mathax.client.gui.tabs.WindowTabScreen;
import mathax.client.gui.widgets.WKeybind;
import mathax.client.gui.widgets.containers.WTable;
import mathax.client.gui.widgets.input.WTextBox;
import mathax.client.gui.widgets.pressable.WButton;
import mathax.client.gui.widgets.pressable.WMinus;
import mathax.client.gui.widgets.pressable.WPlus;
import mathax.client.systems.macros.Macro;
import mathax.client.systems.macros.Macros;
import mathax.client.bus.EventHandler;
import mathax.client.bus.EventPriority;
import net.minecraft.client.gui.screen.Screen;

import static mathax.client.utils.Utils.mc;

public class MacrosTab extends Tab {
    public MacrosTab() {
        super("Macros");
    }

    @Override
    public TabScreen createScreen(GuiTheme theme) {
        return new MacrosScreen(theme, this);
    }

    @Override
    public boolean isScreen(Screen screen) {
        return screen instanceof MacrosScreen;
    }

    private static class MacrosScreen extends WindowTabScreen {
        public MacrosScreen(GuiTheme theme, Tab tab) {
            super(theme, tab);
        }

        @Override
        protected void init() {
            super.init();

            clear();
            initWidgets();
        }

        private void initWidgets() {
            // Macros
            if (Macros.get().getAll().size() > 0) {
                WTable table = add(theme.table()).expandX().widget();

                for (Macro macro : Macros.get()) {
                    table.add(theme.label(macro.name + " (" + macro.keybind + ")"));

                    WButton edit = table.add(theme.button(GuiRenderer.EDIT)).expandCellX().right().widget();
                    edit.action = () -> mc.openScreen(new MacroEditorScreen(theme, macro));

                    WMinus remove = table.add(theme.minus()).widget();
                    remove.action = () -> {
                        Macros.get().remove(macro);

                        clear();
                        initWidgets();
                    };

                    table.row();
                }
            }

            // New
            WButton create = add(theme.button("Create")).expandX().widget();
            create.action = () -> mc.openScreen(new MacroEditorScreen(theme, null));
        }
    }

    private static class MacroEditorScreen extends WindowScreen {
        private final Macro macro;
        private final boolean isNewMacro;

        private WKeybind keybind;
        private boolean binding;

        public MacroEditorScreen(GuiTheme theme, Macro m) {
            super(theme, m == null ? "Create Macro" : "Edit Macro");
            isNewMacro = m == null;
            this.macro = isNewMacro ? new Macro() : m;

            initWidgets(m);
        }

        private void initWidgets(Macro m) {
            // Name
            WTable t = add(theme.table()).widget();

            t.add(theme.label("Name:"));
            WTextBox name = t.add(theme.textBox(m == null ? "" : macro.name)).minWidth(400).expandX().widget();
            name.setFocused(true);
            name.action = () -> macro.name = name.get().trim();
            t.row();

            // Messages
            t.add(theme.label("Messages:")).padTop(4).top();
            WTable lines = t.add(theme.table()).widget();
            fillMessagesTable(lines);

            // Bind
            keybind = add(theme.keybind(macro.keybind)).expandX().widget();
            keybind.actionOnSet = () -> binding = true;

            // Apply
            WButton apply = add(theme.button(isNewMacro ? "Add" : "Apply")).expandX().widget();
            apply.action = () -> {
                if (isNewMacro) {
                    if (macro.name != null && !macro.name.isEmpty() && macro.messages.size() > 0 && macro.keybind.isSet()) {
                        Macros.get().add(macro);
                        onClose();
                    }
                } else {
                    Macros.get().save();
                    onClose();
                }
            };

            enterAction = apply.action;
        }

        private void fillMessagesTable(WTable lines) {
            if (macro.messages.isEmpty()) macro.addMessage("");

            for (int i = 0; i < macro.messages.size(); i++) {
                int ii = i;

                WTextBox line = lines.add(theme.textBox(macro.messages.get(i))).minWidth(400).expandX().widget();
                line.action = () -> macro.messages.set(ii, line.get().trim());

                if (i != macro.messages.size() - 1) {
                    WMinus remove = lines.add(theme.minus()).widget();
                    remove.action = () -> {
                        macro.removeMessage(ii);

                        clear();
                        initWidgets(macro);
                    };
                } else {
                    WPlus add = lines.add(theme.plus()).widget();
                    add.action = () -> {
                        macro.addMessage("");

                        clear();
                        initWidgets(macro);
                    };
                }

                lines.row();
            }
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        private void onKey(KeyEvent event) {
            if (onAction(true, event.key)) event.cancel();
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        private void onButton(MouseButtonEvent event) {
            if (onAction(false, event.button)) event.cancel();
        }

        private boolean onAction(boolean isKey, int value) {
            if (binding) {
                keybind.onAction(isKey, value);

                binding = false;
                return true;
            }

            return false;
        }
    }
}
