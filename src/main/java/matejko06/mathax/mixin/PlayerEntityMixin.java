package matejko06.mathax.mixin;

import matejko06.mathax.MatHaxClient;
import matejko06.mathax.events.entity.DropItemsEvent;
import matejko06.mathax.events.entity.player.ClipAtLedgeEvent;
import matejko06.mathax.systems.modules.Modules;
import matejko06.mathax.systems.modules.movement.Anchor;
import matejko06.mathax.systems.modules.player.SpeedMine;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static matejko06.mathax.utils.Utils.mc;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    @Inject(method = "clipAtLedge", at = @At("HEAD"), cancellable = true)
    protected void clipAtLedge(CallbackInfoReturnable<Boolean> info) {
        ClipAtLedgeEvent event = MatHaxClient.EVENT_BUS.post(ClipAtLedgeEvent.get());

        if (event.isSet()) info.setReturnValue(event.isClip());
    }

    @Inject(method = "dropItem(Lnet/minecraft/item/ItemStack;ZZ)Lnet/minecraft/entity/ItemEntity;", at = @At("HEAD"), cancellable = true)
    private void onDropItem(ItemStack stack, boolean bl, boolean bl2, CallbackInfoReturnable<ItemEntity> info) {
        if (mc.world.isClient) {
            if (MatHaxClient.EVENT_BUS.post(DropItemsEvent.get(stack)).isCancelled()) info.cancel();
        }
    }

    @Inject(method = "getBlockBreakingSpeed", at = @At(value = "RETURN"), cancellable = true)
    public void onGetBlockBreakingSpeed(BlockState block, CallbackInfoReturnable<Float> cir) {
        SpeedMine module = Modules.get().get(SpeedMine.class);
        if (!module.isActive() || module.mode.get() != SpeedMine.Mode.Normal) return;

        cir.setReturnValue((float) (cir.getReturnValue() * module.modifier.get()));
    }

    @Inject(method = "jump", at = @At("HEAD"), cancellable = true)
    public void dontJump(CallbackInfo info) {
        Anchor module = Modules.get().get(Anchor.class);
        if (module.isActive() && module.cancelJump) info.cancel();
    }
}
