package mathax.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.passive.LlamaEntity;

import mathax.client.systems.modules.Modules;
import mathax.client.systems.modules.movement.EntityControl;


@Mixin(LlamaEntity.class)
public class LlamaEntityMixin {
    @Inject(method = "canBeControlledByRider", at = @At("HEAD"), cancellable = true)
    public void canBeControlledByRider(CallbackInfoReturnable<Boolean> ci) {
        if (Modules.get().get(EntityControl.class).isActive()) ci.setReturnValue(true);
    }
}
