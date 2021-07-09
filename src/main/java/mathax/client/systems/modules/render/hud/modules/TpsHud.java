package mathax.client.systems.modules.render.hud.modules;

import mathax.client.systems.modules.render.hud.HUD;
import mathax.client.utils.world.TickRate;

public class TpsHud extends TripleTextHudElement {
    public TpsHud(HUD hud) {
        super(hud, "tps", "Displays the server's TPS.", "TPS: ");
    }

    @Override
    protected String getRight() {
        return String.format("%.1f", TickRate.INSTANCE.getTickRate());
    }

    public String getEnd() {
        return "";
    }
}
