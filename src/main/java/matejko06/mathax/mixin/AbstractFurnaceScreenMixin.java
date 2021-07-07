package matejko06.mathax.mixin;

import matejko06.mathax.systems.modules.Modules;
import matejko06.mathax.systems.modules.world.AutoSmelter;
import net.minecraft.client.gui.screen.ingame.AbstractFurnaceScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.AbstractFurnaceScreenHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractFurnaceScreen.class)
public abstract class AbstractFurnaceScreenMixin<T extends AbstractFurnaceScreenHandler> extends HandledScreen<T> implements RecipeBookProvider {
    public AbstractFurnaceScreenMixin(T container, PlayerInventory playerInventory, Text name) {
        super(container, playerInventory, name);
    }

    @Inject(method = "handledScreenTick", at = @At("TAIL"))
    private void onTick(CallbackInfo info) {
        if (Modules.get().isActive(AutoSmelter.class)) Modules.get().get(AutoSmelter.class).tick(handler);
    }

    @Override
    public void onClose() {
        super.onClose();

        if (Modules.get().isActive(AutoSmelter.class)) Modules.get().get(AutoSmelter.class).onFurnaceClose();
    }
}
