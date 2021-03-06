package mathax.client.systems.modules.misc;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import mathax.client.MatHaxClient;
import mathax.client.events.world.TickEvent;
import mathax.client.settings.BoolSetting;
import mathax.client.settings.Setting;
import mathax.client.settings.SettingGroup;
import mathax.client.systems.modules.Categories;
import mathax.client.systems.modules.Module;
import mathax.client.utils.Utils;
import mathax.client.utils.misc.Placeholders;
import mathax.client.bus.EventHandler;

public class DiscordPresence extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> playericon = sgGeneral.add(new BoolSetting.Builder()
        .name("player-icon")
        .description("If your a special person for the MatHax Team, your head will be on the presence.")
        .defaultValue(true)
        .build()
    );

    String line1 = "%username% | %server%";
    String line2 = "https://mathaxclient.xyz";

    private static final DiscordRichPresence rpc = new DiscordRichPresence();
    private static final DiscordRPC instance = DiscordRPC.INSTANCE;
    private SmallImage currentSmallImage;
    private int ticks;

    public DiscordPresence() {
        super(Categories.Misc, "discord-presence", "Displays a RPC for you on Discord to show that you're playing MatHax Client!");
    }

    @Override
    public void onActivate() {
        DiscordEventHandlers handlers = new DiscordEventHandlers();
        instance.Discord_Initialize("822968662012067844", handlers, true, null);

        rpc.startTimestamp = System.currentTimeMillis() / 1000L;
        rpc.largeImageKey = "mathax_logo";
        String largeText = "MatHax Client" + MatHaxClient.discordversion;
        rpc.largeImageText = largeText;
        updateDetails();

        instance.Discord_UpdatePresence(rpc);
        instance.Discord_RunCallbacks();
    }

    @Override
    public void onDeactivate() {
        instance.Discord_ClearPresence();
        instance.Discord_Shutdown();
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (!Utils.canUpdate()) return;
        ticks++;

        if (ticks >= 200) {
            if (playericon.get()) {
                if (mc.getSession().getUsername().equals("Matejko06")) {
                    currentSmallImage = SmallImage.Matejko06;
                } else if (mc.getSession().getUsername().equals("GeekieCoder")) {
                    currentSmallImage = SmallImage.GeekieCoder;
                } else {
                    currentSmallImage = SmallImage.NotClientDeveloper;
                }
            } else {
                currentSmallImage = SmallImage.NotClientDeveloper;
            }
            currentSmallImage.apply();
            instance.Discord_UpdatePresence(rpc);

            ticks = 0;
        }

        updateDetails();
        instance.Discord_RunCallbacks();
    }

    private void updateDetails() {
        if (isActive() && Utils.canUpdate()) {
            rpc.details = Placeholders.apply(line1);
            rpc.state = Placeholders.apply(line2);

            instance.Discord_UpdatePresence(rpc);
        }
    }

    private enum SmallImage {
        Matejko06("matejko06", "Developer: Matejko06"),
        GeekieCoder("geekiecoder", "Developer: GeekieCoder"),
        NotClientDeveloper("", "");

        private final String key, text;

        SmallImage(String key, String text) {
            this.key = key;
            this.text = text;
        }

        void apply() {
            rpc.smallImageKey = key;
            rpc.smallImageText = text;
        }
    }
}
