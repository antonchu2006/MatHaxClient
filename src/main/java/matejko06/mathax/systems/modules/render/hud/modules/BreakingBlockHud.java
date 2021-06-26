package matejko06.mathax.systems.modules.render.hud.modules;

import matejko06.mathax.mixin.ClientPlayerInteractionManagerAccessor;
import matejko06.mathax.systems.modules.render.hud.HUD;

public class BreakingBlockHud extends TripleTextHudElement {
    public BreakingBlockHud(HUD hud) {
        super(hud, "breaking-block", "Displays percentage of the block you are breaking.", "Breaking Block: ");
    }

    @Override
    protected String getRight() {
        if (isInEditor()) return "0%";

        return String.format("%.0f%%", ((ClientPlayerInteractionManagerAccessor) mc.interactionManager).getBreakingProgress() * 100);
    }

    public String getEnd() {
        return "";
    }
}
