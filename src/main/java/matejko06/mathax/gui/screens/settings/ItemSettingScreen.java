package matejko06.mathax.gui.screens.settings;

import matejko06.mathax.gui.GuiTheme;
import matejko06.mathax.gui.WindowScreen;
import matejko06.mathax.gui.widgets.WItemWithLabel;
import matejko06.mathax.gui.widgets.containers.WTable;
import matejko06.mathax.gui.widgets.input.WTextBox;
import matejko06.mathax.gui.widgets.pressable.WButton;
import matejko06.mathax.settings.ItemSetting;
import matejko06.mathax.utils.misc.Names;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.StringUtils;

public class ItemSettingScreen extends WindowScreen {
    private final ItemSetting setting;

    private final WTextBox filter;
    private WTable table;

    private String filterText = "";

    public ItemSettingScreen(GuiTheme theme, ItemSetting setting) {
        super(theme, "Select item");

        this.setting = setting;

        filter = add(theme.textBox("")).minWidth(400).expandX().widget();
        filter.setFocused(true);
        filter.action = () -> {
            filterText = filter.get().trim();

            table.clear();
            initWidgets();
        };

        table = add(theme.table()).expandX().widget();

        initWidgets();
    }

    private void initWidgets() {
        for (Item item : Registry.ITEM) {
            if (setting.filter != null) {
                if (!setting.filter.test(item)) continue;
            }
            else {
                if (item == Items.AIR) continue;
            }

            WItemWithLabel itemLabel = theme.itemWithLabel(item.getDefaultStack(), Names.get(item));
            if (!filterText.isEmpty()) {
                if (!StringUtils.containsIgnoreCase(itemLabel.getLabelText(), filterText)) continue;
            }
            table.add(itemLabel);

            WButton select = table.add(theme.button("Select")).expandCellX().right().widget();
            select.action = () -> {
                setting.set(item);
                onClose();
            };

            table.row();
        }
    }
}
