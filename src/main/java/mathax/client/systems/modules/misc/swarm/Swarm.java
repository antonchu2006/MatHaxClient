package mathax.client.systems.modules.misc.swarm;

import mathax.client.events.game.GameJoinedEvent;
import mathax.client.events.game.GameLeftEvent;
import mathax.client.events.world.TickEvent;
import mathax.client.gui.GuiTheme;
import mathax.client.gui.widgets.WWidget;
import mathax.client.gui.widgets.containers.WHorizontalList;
import mathax.client.gui.widgets.containers.WVerticalList;
import mathax.client.gui.widgets.pressable.WButton;
import mathax.client.settings.*;
import mathax.client.systems.modules.Categories;
import mathax.client.systems.modules.Module;
import mathax.client.bus.EventHandler;
import mathax.client.settings.*;
import net.minecraft.util.Util;

public class Swarm extends Module {
    public enum Mode {
        Host,
        Worker
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
            .name("mode")
            .description("What type of client to run.")
            .defaultValue(Mode.Host)
            .build()
    );

    private final Setting<String> ipAddress = sgGeneral.add(new StringSetting.Builder()
            .name("ip")
            .description("The IP address of the host server.")
            .defaultValue("localhost")
            .visible(() -> mode.get() == Mode.Worker)
            .build()
    );

    private final Setting<Integer> serverPort = sgGeneral.add(new IntSetting.Builder()
            .name("port")
            .description("The port used for connections.")
            .defaultValue(7777)
            .sliderMin(1).sliderMax(65535)
            .build()
    );

    public SwarmHost host;
    public SwarmWorker worker;

    public Swarm() {
        super(Categories.Misc, "Swarm", "Allows you to control multiple instances of MatHax from one central host.");
    }

    @Override
    public WWidget getWidget(GuiTheme theme) {
        WVerticalList list = theme.verticalList();

        WHorizontalList b = list.add(theme.horizontalList()).expandX().widget();

        WButton start = b.add(theme.button("Start")).expandX().widget();
        start.action = () -> {
            if (!isActive()) return;

            close();
            if (mode.get() == Mode.Host) host = new SwarmHost(serverPort.get());
            else worker = new SwarmWorker(ipAddress.get(), serverPort.get());
        };

        WButton stop = b.add(theme.button("Stop")).expandX().widget();
        stop.action = this::close;

        WButton guide = list.add(theme.button("Guide")).expandX().widget();
        guide.action = () -> Util.getOperatingSystem().open("https://mathaxclient.xyz/Swarm-Guide");

        return list;
    }

    @Override
    public String getInfoString() {
        return mode.get().name();
    }

    @Override
    public void onActivate() {
        close();
    }

    @Override
    public void onDeactivate() {
        close();
    }

    public void close() {
        try {
            if (host != null) {
                host.disconnect();
                host = null;
            }
            if (worker != null) {
                worker.disconnect();
                worker = null;
            }
        } catch (Exception ignored) {}
    }

    @EventHandler
    private void onGameLeft(GameLeftEvent event) {
        toggle();
    }

    @EventHandler
    private void onGameJoin(GameJoinedEvent event) {
        toggle();
    }

    @Override
    public void toggle() {
        close();
        super.toggle();
    }

    public boolean isHost() {
        return mode.get() == Mode.Host && host != null && !host.isInterrupted();
    }

    public boolean isWorker() {
        return mode.get() == Mode.Worker && worker != null && !worker.isInterrupted();
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (isWorker()) worker.tick();
    }
}
