package mathax.client.systems.modules.misc;

import io.netty.buffer.Unpooled;
import mathax.client.MatHaxClient;
import mathax.client.events.packets.PacketEvent;
import mathax.client.mixin.CustomPayloadC2SPacketAccessor;
import mathax.client.systems.modules.Categories;
import mathax.client.systems.modules.Module;
import mathax.client.bus.EventHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;

public class VanillaSpoof extends Module {
    public VanillaSpoof() {
        super(Categories.Misc, "vanilla-spoof", "When connecting to a server it spoofs the client name to be 'vanilla'.");

        MatHaxClient.EVENT_BUS.subscribe(new Listener());
    }

    private class Listener {
        @EventHandler
        private void onPacketSend(PacketEvent.Send event) {
            if (!isActive() || !(event.packet instanceof CustomPayloadC2SPacket)) return;
            CustomPayloadC2SPacketAccessor packet = (CustomPayloadC2SPacketAccessor) event.packet;
            Identifier id = packet.getChannel();

            if (id.equals(CustomPayloadC2SPacket.BRAND)) {
                packet.setData(new PacketByteBuf(Unpooled.buffer()).writeString("vanilla"));
            }
            else if (StringUtils.containsIgnoreCase(packet.getData().toString(StandardCharsets.UTF_8), "fabric")) {
                event.cancel();
            }
        }
    }
}
