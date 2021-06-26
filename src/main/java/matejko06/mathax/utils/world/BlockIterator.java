package matejko06.mathax.utils.world;

import matejko06.mathax.MatHaxClient;
import matejko06.mathax.events.world.TickEvent;
import matejko06.mathax.utils.Utils;
import matejko06.mathax.utils.misc.Pool;
import matejko06.mathax.bus.EventHandler;
import matejko06.mathax.bus.EventPriority;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;

import static matejko06.mathax.utils.Utils.mc;

public class BlockIterator {
    private static final Pool<Callback> callbackPool = new Pool<>(Callback::new);
    private static final List<Callback> callbacks = new ArrayList<>();

    private static final List<Runnable> afterCallbacks = new ArrayList<>();

    private static final BlockPos.Mutable blockPos = new BlockPos.Mutable();
    private static int hRadius, vRadius;

    private static boolean disableCurrent;

    public static void init() {
        MatHaxClient.EVENT_BUS.subscribe(BlockIterator.class);
    }

    @EventHandler(priority = EventPriority.LOWEST - 1)
    private static void onTick(TickEvent.Pre event) {
        if (!Utils.canUpdate()) return;

        int px = (int) mc.player.getX();
        int py = (int) mc.player.getY();
        int pz = (int) mc.player.getZ();

        for (int x = px - hRadius; x <= px + hRadius; x++) {
            for (int z = pz - hRadius; z <= pz + hRadius; z++) {
                for (int y = Math.max(0, py - vRadius); y <= py + vRadius; y++) {
                    if (y > 255) break;

                    blockPos.set(x, y, z);
                    BlockState blockState = mc.world.getBlockState(blockPos);

                    int dx = Math.abs(x - px);
                    int dy = Math.abs(y - py);
                    int dz = Math.abs(z - pz);

                    for (Iterator<Callback> it = callbacks.iterator(); it.hasNext(); ) {
                        Callback callback = it.next();

                        if (dx <= callback.hRadius && dy <= callback.vRadius && dz <= callback.hRadius) {
                            disableCurrent = false;
                            callback.function.accept(blockPos, blockState);
                            if (disableCurrent) it.remove();
                        }
                    }
                }
            }
        }

        hRadius = 0;
        vRadius = 0;

        for (Callback callback : callbacks) callbackPool.free(callback);
        callbacks.clear();

        for (Runnable callback : afterCallbacks) callback.run();
        afterCallbacks.clear();
    }

    public static void register(int horizontalRadius, int verticalRadius, BiConsumer<BlockPos, BlockState> function) {
        hRadius = Math.max(hRadius, horizontalRadius);
        vRadius = Math.max(vRadius, verticalRadius);

        Callback callback = callbackPool.get();

        callback.function = function;
        callback.hRadius = horizontalRadius;
        callback.vRadius = verticalRadius;

        callbacks.add(callback);
    }

    public static void disableCurrent() {
        disableCurrent = true;
    }

    public static void after(Runnable callback) {
        afterCallbacks.add(callback);
    }

    private static class Callback {
        public BiConsumer<BlockPos, BlockState> function;
        public int hRadius, vRadius;
    }
}
