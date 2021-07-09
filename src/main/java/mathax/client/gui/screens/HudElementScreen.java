package mathax.client.gui.screens;

import mathax.client.events.render.Render2DEvent;
import mathax.client.gui.GuiTheme;
import mathax.client.gui.WindowScreen;
import mathax.client.gui.renderer.GuiRenderer;
import mathax.client.gui.widgets.containers.WContainer;
import mathax.client.gui.widgets.containers.WHorizontalList;
import mathax.client.gui.widgets.pressable.WButton;
import mathax.client.gui.widgets.pressable.WCheckbox;
import mathax.client.systems.modules.Modules;
import mathax.client.systems.modules.render.hud.HUD;
import mathax.client.systems.modules.render.hud.modules.HudElement;
import mathax.client.utils.Utils;

import static mathax.client.utils.Utils.getWindowWidth;

public class HudElementScreen extends WindowScreen {
    private final HudElement element;
    private WContainer settings;

    public HudElementScreen(GuiTheme theme, HudElement element) {
        super(theme, element.title);
        this.element = element;

        // Description
        add(theme.label(element.description, getWindowWidth() / 2.0));

        // Settings
        if (element.settings.sizeGroups() > 0) {
            settings = add(theme.verticalList()).expandX().widget();
            settings.add(theme.settings(element.settings)).expandX();

            add(theme.horizontalSeparator()).expandX();
        }

        // Bottom
        WHorizontalList bottomList = add(theme.horizontalList()).expandX().widget();

        //   Active
        bottomList.add(theme.label("Active:"));
        WCheckbox active = bottomList.add(theme.checkbox(element.active)).widget();
        active.action = () -> {
            if (element.active != active.checked) element.toggle();
        };

        WButton reset = bottomList.add(theme.button(GuiRenderer.RESET)).expandCellX().right().widget();
        reset.action = () -> {
            if (element.active != element.defaultActive) element.active = active.checked = element.defaultActive;
        };
    }

    @Override
    public void tick() {
        super.tick();

        if (settings == null) return;

        element.settings.tick(settings, theme);
    }

    @Override
    protected void onRenderBefore(float delta) {
        if (!Utils.canUpdate()) Modules.get().get(HUD.class).onRender(Render2DEvent.get(0, 0, delta));
    }
}
