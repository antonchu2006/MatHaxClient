package matejko06.mathax.mixin;

import matejko06.mathax.mixininterface.IEntityRenderer;
import matejko06.mathax.systems.modules.Modules;
import matejko06.mathax.systems.modules.render.Nametags;
import matejko06.mathax.systems.modules.render.NoRender;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity> implements IEntityRenderer {
    @Shadow public abstract Identifier getTexture(Entity entity);

    @Inject(method = "renderLabelIfPresent", at = @At("HEAD"), cancellable = true)
    private void onRenderLabel(T entity, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo info) {
        if (!(entity instanceof PlayerEntity)) return;
        if (Modules.get().isActive(Nametags.class)) info.cancel();
    }

    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    private void shouldRender(T entity, Frustum frustum, double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        if (Modules.get().get(NoRender.class).noEntity(entity)) cir.cancel();
        if (Modules.get().get(NoRender.class).noFallingBlocks() && entity instanceof FallingBlockEntity) cir.cancel();
    }

    @Override
    public Identifier getTextureInterface(Entity entity) {
        return getTexture(entity);
    }
}
