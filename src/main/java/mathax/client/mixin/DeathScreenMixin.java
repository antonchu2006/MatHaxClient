package mathax.client.mixin;

//Created by squidoodly 21/05/2020

import mathax.client.systems.modules.Modules;
import mathax.client.systems.modules.misc.BypassDeathScreen;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DeathScreen.class)
public class DeathScreenMixin extends Screen {
    protected DeathScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("HEAD"))
    protected void init(CallbackInfo ci) {
        if (Modules.get().isActive(BypassDeathScreen.class)) {
            this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 48, 200, 20, new LiteralText("Ghost Spectate"), (buttonWidgetx) -> client.openScreen(null)));
        }
    }
}
