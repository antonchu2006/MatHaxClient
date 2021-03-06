package mathax.client.systems.modules.render.hud.modules;

import mathax.client.settings.EnumSetting;
import mathax.client.settings.Setting;
import mathax.client.settings.SettingGroup;
import mathax.client.systems.modules.render.hud.HUD;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class TimeHud extends TripleTextHudElement {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<TimeType> timeType = sgGeneral.add(new EnumSetting.Builder<TimeType>()
            .name("use-time")
            .description("Which time to use.")
            .defaultValue(TimeType.World)
            .build()
    );

    public TimeHud(HUD hud) {
        super(hud, "time", "Displays the world time.", "Time: ");
    }

    @Override
    protected String getRight() {
        return switch (timeType.get()) {
            case World -> {
                if (isInEditor()) yield "00:00";

                int ticks = (int) (mc.world.getTimeOfDay() % 24000);
                ticks += 6000;
                if (ticks > 24000) ticks -= 24000;

                yield String.format("%02d:%02d", ticks / 1000, (int) (ticks % 1000 / 1000.0 * 60));
            }
            case Local ->  LocalTime.now().format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT));
        };
    }

    public enum TimeType {
        World,
        Local
    }

    public String getEnd() {
        return "";
    }
}
