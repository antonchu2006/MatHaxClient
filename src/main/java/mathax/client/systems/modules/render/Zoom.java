package mathax.client.systems.modules.render;

import mathax.client.events.mathax.MouseScrollEvent;
import mathax.client.events.render.GetFovEvent;
import mathax.client.events.world.TickEvent;
import mathax.client.settings.BoolSetting;
import mathax.client.settings.DoubleSetting;
import mathax.client.settings.Setting;
import mathax.client.settings.SettingGroup;
import mathax.client.systems.modules.Categories;
import mathax.client.systems.modules.Module;
import mathax.client.bus.EventHandler;

public class Zoom extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Double> zoom = sgGeneral.add(new DoubleSetting.Builder()
            .name("zoom")
            .description("How much to zoom.")
            .defaultValue(5)
            .min(1)
            .build()
    );

    private final Setting<Double> scrollSensitivity = sgGeneral.add(new DoubleSetting.Builder()
            .name("scroll-sensitivity")
            .description("Allows you to change zoom value using scroll wheel. 0 to disable.")
            .defaultValue(0)
            .min(0)
            .build()
    );

    private final Setting<Boolean> cinematic = sgGeneral.add(new BoolSetting.Builder()
            .name("cinematic")
            .description("Enables cinematic camera.")
            .defaultValue(true)
            .build()
    );

    private boolean preCinematic;
    private double preMouseSensitivity;
    private double value;
    private double lastFov;

    public Zoom() {
        super(Categories.Render, "zoom", "Zooms your view.");
    }

    @Override
    public void onActivate() {
        preCinematic = mc.options.smoothCameraEnabled;
        preMouseSensitivity = mc.options.mouseSensitivity;
        value = zoom.get();
        lastFov = mc.options.fov;
    }

    @Override
    public void onDeactivate() {
        mc.options.smoothCameraEnabled = preCinematic;
        mc.options.mouseSensitivity = preMouseSensitivity;

        mc.worldRenderer.scheduleTerrainUpdate();
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        mc.options.smoothCameraEnabled = cinematic.get();

        if (!cinematic.get()) {
            mc.options.mouseSensitivity = preMouseSensitivity / Math.max(value * 0.5, 1);
        }
    }

    @EventHandler
    private void onMouseScroll(MouseScrollEvent event) {
        if (scrollSensitivity.get() > 0) {
            value += event.value * 0.25 * (scrollSensitivity.get() * value);
            if (value < 1) value = 1;

            event.cancel();
        }
    }

    @EventHandler
    private void onGetFov(GetFovEvent event) {
        event.fov /= value;

        if (lastFov != event.fov) mc.worldRenderer.scheduleTerrainUpdate();
        lastFov = event.fov;
    }
}
