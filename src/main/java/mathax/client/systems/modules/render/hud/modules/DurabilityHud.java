package mathax.client.systems.modules.render.hud.modules;

import mathax.client.systems.modules.render.hud.HUD;

public class DurabilityHud extends TripleTextHudElement {
    public DurabilityHud(HUD hud) {
        super(hud, "durability", "Displays durability of the item you are holding.", "Durability: ");
    }

    @Override
    protected String getRight() {
        if (isInEditor()) return "69";

        Integer amount = null;
        if (!mc.player.getMainHandStack().isEmpty() && mc.player.getMainHandStack().isDamageable()) amount = mc.player.getMainHandStack().getMaxDamage() - mc.player.getMainHandStack().getDamage();

        return amount == null ? "Infinite" : amount.toString();
    }

    public String getEnd() {
        return "";
    }
}
