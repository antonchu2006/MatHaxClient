package matejko06.mathax.systems.modules.render;

import com.google.common.reflect.TypeToken;
import matejko06.mathax.events.render.Render2DEvent;
import matejko06.mathax.mixin.ProjectileEntityAccessor;
import matejko06.mathax.renderer.Renderer2D;
import matejko06.mathax.renderer.text.TextRenderer;
import matejko06.mathax.settings.BoolSetting;
import matejko06.mathax.settings.DoubleSetting;
import matejko06.mathax.settings.Setting;
import matejko06.mathax.settings.SettingGroup;
import matejko06.mathax.systems.modules.Categories;
import matejko06.mathax.systems.modules.Module;
import matejko06.mathax.utils.misc.Vec3;
import matejko06.mathax.utils.network.HttpUtils;
import matejko06.mathax.utils.network.MatHaxExecutor;
import matejko06.mathax.utils.render.NametagUtils;
import matejko06.mathax.utils.render.color.Color;
import matejko06.mathax.bus.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EntityOwner extends Module {
    private static final Color BACKGROUND = new Color(0, 0, 0, 75);
    private static final Color TEXT = new Color(255, 255, 255);
    private static final Type RESPONSE_TYPE = new TypeToken<List<UuidNameHistoryResponseItem>>() {}.getType();

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Double> scale = sgGeneral.add(new DoubleSetting.Builder()
            .name("scale")
            .description("The scale of the text.")
            .defaultValue(1)
            .min(0)
            .build()
    );

    private final Setting<Boolean> projectiles = sgGeneral.add(new BoolSetting.Builder()
            .name("projectiles")
            .description("Display owner names of projectiles.")
            .defaultValue(false)
            .build()
    );

    private final Vec3 pos = new Vec3();
    private final Map<UUID, String> uuidToName = new HashMap<>();

    public EntityOwner() {
        super(Categories.Render, "entity-owner", "Displays the name of the player who owns the entity you're looking at.");
    }

    @Override
    public void onDeactivate() {
        uuidToName.clear();
    }

    @EventHandler
    private void onRender2D(Render2DEvent event) {
        for (Entity entity : mc.world.getEntities()) {
            UUID ownerUuid;

            if (entity instanceof TameableEntity) ownerUuid = ((TameableEntity) entity).getOwnerUuid();
            else if (entity instanceof HorseBaseEntity) ownerUuid = ((HorseBaseEntity) entity).getOwnerUuid();
            else if (entity instanceof ProjectileEntity && projectiles.get()) ownerUuid = ((ProjectileEntityAccessor) entity).getOwnerUuid();
            else continue;

            if (ownerUuid != null) {
                pos.set(entity, event.tickDelta);
                pos.add(0, entity.getEyeHeight(entity.getPose()) + 0.75, 0);

                if (NametagUtils.to2D(pos, scale.get())) {
                    renderNametag(getOwnerName(ownerUuid));
                }
            }
        }
    }

    private void renderNametag(String name) {
        TextRenderer text = TextRenderer.get();

        NametagUtils.begin(pos);
        text.beginBig();

        double w = text.getWidth(name);

        double x = -w / 2;
        double y = -text.getHeight();

        Renderer2D.COLOR.begin();
        Renderer2D.COLOR.quad(x - 1, y - 1, w + 2, text.getHeight() + 2, BACKGROUND);
        Renderer2D.COLOR.render(null);

        text.render(name, x, y, TEXT);

        text.end();
        NametagUtils.end();
    }

    private String getOwnerName(UUID uuid) {
        // Check if the player is online
        PlayerEntity player = mc.world.getPlayerByUuid(uuid);
        if (player != null) return player.getEntityName();

        // Check cache
        String name = uuidToName.get(uuid);
        if (name != null) return name;

        // Makes a HTTP request to Mojang API
        MatHaxExecutor.execute(() -> {
            if (isActive()) {
                List<UuidNameHistoryResponseItem> response = HttpUtils.get("https://api.mojang.com/user/profiles/" + uuid.toString().replace("-", "") + "/names", RESPONSE_TYPE);

                if (isActive()) {
                    if (response == null || response.size() <= 0) uuidToName.put(uuid, "Failed to get name");
                    else uuidToName.put(uuid, response.get(response.size() - 1).name);
                }
            }
        });

        name = "Retrieving";
        uuidToName.put(uuid, name);
        return name;
    }

    public static class UuidNameHistoryResponseItem {
        public String name;
    }
}
