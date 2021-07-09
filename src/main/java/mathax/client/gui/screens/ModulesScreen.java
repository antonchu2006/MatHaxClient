package mathax.client.gui.screens;

import mathax.client.gui.GuiTheme;
import mathax.client.gui.tabs.TabScreen;
import mathax.client.gui.tabs.Tabs;
import mathax.client.gui.utils.Cell;
import mathax.client.gui.widgets.containers.WContainer;
import mathax.client.gui.widgets.containers.WSection;
import mathax.client.gui.widgets.containers.WVerticalList;
import mathax.client.gui.widgets.containers.WWindow;
import mathax.client.gui.widgets.input.WTextBox;
import mathax.client.systems.modules.Category;
import mathax.client.systems.modules.Module;
import mathax.client.systems.modules.Modules;
import net.minecraft.item.Items;
import net.minecraft.util.Pair;

import java.util.List;

import static mathax.client.utils.Utils.getWindowHeight;
import static mathax.client.utils.Utils.getWindowWidth;

public class ModulesScreen extends TabScreen {
    public ModulesScreen(GuiTheme theme) {
        super(theme, Tabs.get().get(0));

        add(createCategoryContainer());

        // Help
        WVerticalList help = add(theme.verticalList()).pad(4).bottom().widget();
        help.add(theme.label("Left click - Toggle module"));
        help.add(theme.label("Right click - Open module settings"));
    }

    protected WCategoryController createCategoryContainer() {
        return new WCategoryController();
    }

    // Category

    protected void createCategory(WContainer c, Category category) {
        WWindow w = theme.window(category.name);
        w.id = category.name;
        w.padding = 0;
        w.spacing = 0;

        if (theme.categoryIcons()) {
            w.beforeHeaderInit = wContainer -> wContainer.add(theme.item(category.icon)).pad(2);
        }

        c.add(w);
        w.view.scrollOnlyWhenMouseOver = true;
        w.view.hasScrollBar = false;
        w.view.spacing = 0;

        for (Module module : Modules.get().getGroup(category)) {
            w.add(theme.module(module)).expandX().widget().tooltip = module.description;
        }
    }

    // Search

    protected void createSearchW(WContainer w, String text) {
        if (!text.isEmpty()) {
            // Titles
            List<Pair<Module, Integer>> modules = Modules.get().searchTitles(text);

            if (modules.size() > 0) {
                WSection section = w.add(theme.section("Modules")).expandX().widget();
                section.spacing = 0;

                for (Pair<Module, Integer> pair : modules) {
                    section.add(theme.module(pair.getLeft())).expandX();
                }
            }

            // Settings
            modules = Modules.get().searchSettingTitles(text);

            if (modules.size() > 0) {
                WSection section = w.add(theme.section("Settings")).expandX().widget();
                section.spacing = 0;

                for (Pair<Module, Integer> pair : modules) {
                    section.add(theme.module(pair.getLeft())).expandX();
                }
            }
        }
    }

    protected void createSearch(WContainer c) {
        WWindow w = theme.window("Search");
        w.id = "search";

        if (theme.categoryIcons()) {
            w.beforeHeaderInit = wContainer -> wContainer.add(theme.item(Items.COMPASS.getDefaultStack())).pad(2);
        }

        c.add(w);
        w.view.scrollOnlyWhenMouseOver = true;
        w.view.hasScrollBar = false;
        w.view.maxHeight -= 20;

        WVerticalList l = theme.verticalList();

        WTextBox text = w.add(theme.textBox("")).minWidth(140).expandX().widget();
        text.setFocused(true);
        text.action = () -> {
            l.clear();
            createSearchW(l, text.get());
        };

        w.add(l).expandX();
        createSearchW(l, text.get());
    }

    // Stuff

    protected class WCategoryController extends WContainer {
        @Override
        public void init() {
            for (Category category : Modules.loopCategories()) {
                createCategory(this, category);
            }

            createSearch(this);
        }

        @Override
        protected void onCalculateWidgetPositions() {
            double pad = theme.scale(4);
            double h = theme.scale(40);

            double x = this.x + pad;
            double y = this.y;

            for (Cell<?> cell : cells) {
                double windowWidth = getWindowWidth();
                double windowHeight = getWindowHeight();

                if (x + cell.width > windowWidth) {
                    x = x + pad;
                    y += h;
                }

                if (x > windowWidth) {
                    x = windowWidth / 2.0 - cell.width / 2.0;
                    if (x < 0) x = 0;
                }
                if (y > windowHeight) {
                    y = windowHeight / 2.0 - cell.height / 2.0;
                    if (y < 0) y = 0;
                }

                cell.x = x;
                cell.y = y;

                cell.width = cell.widget().width;
                cell.height = cell.widget().height;

                cell.alignWidget();

                x += cell.width + pad;
            }
        }
    }
}
