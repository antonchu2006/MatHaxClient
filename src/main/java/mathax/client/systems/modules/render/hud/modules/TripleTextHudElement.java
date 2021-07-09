package mathax.client.systems.modules.render.hud.modules;

import mathax.client.systems.modules.render.hud.HUD;
import mathax.client.systems.modules.render.hud.HudRenderer;
import mathax.client.utils.render.color.Color;

public abstract class TripleTextHudElement extends HudElement {
    protected Color rightColor;
    protected boolean visible = true;

    private String left;
    private String right;
    private String end;

    private double leftWidth;
    private double rightWidth;

    public TripleTextHudElement(HUD hud, String name, String description, String left) {
        super(hud, name, description);
        this.rightColor = hud.secondaryColor.get();
        this.left = left;
    }

    @Override
    public void update(HudRenderer renderer) {
        right = getRight();
        end = getEnd();
        leftWidth = renderer.textWidth(left);
        rightWidth = renderer.textWidth(right);

        double textWidth = leftWidth + renderer.textWidth(right);

        box.setSize(textWidth + renderer.textWidth(end), renderer.textHeight());
    }

    @Override
    public void render(HudRenderer renderer) {
        if (!visible) return;

        double x = box.getX();
        double y = box.getY();

        renderer.text(left, x, y, hud.primaryColor.get());
        renderer.text(right, x + leftWidth, y, rightColor);
        renderer.text(end, x + leftWidth + rightWidth, y, hud.primaryColor.get());
    }

    protected void setLeft(String left) {
        this.left = left;
        this.leftWidth = 0;
    }

    protected abstract String getRight();
    protected abstract String getEnd();
}
