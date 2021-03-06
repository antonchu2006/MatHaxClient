package mathax.client.mixin;

import mathax.client.systems.modules.Modules;
import mathax.client.systems.modules.render.Fullbright;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LightmapTextureManager.class)
public class LightmapTextureManagerMixin {
    @Redirect(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;hasStatusEffect(Lnet/minecraft/entity/effect/StatusEffect;)Z", ordinal = 0))
    private boolean updateHasStatusEffectProxy(ClientPlayerEntity player, StatusEffect effect) {
        Fullbright fullbright = Modules.get().get(Fullbright.class);

        return (Fullbright.isEnabled() && (fullbright.mode.get() == Fullbright.Mode.Gamma)) || player.hasStatusEffect(effect);
    }

    @Redirect(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;getNightVisionStrength(Lnet/minecraft/entity/LivingEntity;F)F"))
    private float updateGetNightVisionStrengthProxy(LivingEntity entity, float delta) {
        Fullbright fullbright = Modules.get().get(Fullbright.class);

        return (Fullbright.isEnabled() && (fullbright.mode.get() == Fullbright.Mode.Gamma)) ? 1 : GameRenderer.getNightVisionStrength(entity, delta);
    }
}
