package mathax.client.systems.modules.player;


import mathax.client.events.entity.player.StartBreakingBlockEvent;
import mathax.client.events.world.TickEvent;
import mathax.client.settings.*;
import mathax.client.systems.modules.Categories;
import mathax.client.systems.modules.Module;
import mathax.client.utils.player.InvUtils;
import mathax.client.bus.EventHandler;
import mathax.client.bus.EventPriority;
import mathax.client.settings.*;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;

public class AutoTool extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<EnchantPreference> prefer = sgGeneral.add(new EnumSetting.Builder<EnchantPreference>()
        .name("prefer")
        .description("Either to prefer Silk Touch, Fortune, or none.")
        .defaultValue(EnchantPreference.Fortune)
        .build()
    );

    private final Setting<Boolean> silkTouchForEnderChest = sgGeneral.add(new BoolSetting.Builder()
        .name("silk-touch-for-ender-chest")
        .description("Mines Ender Chests only with the Silk Touch enchantment.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> antiBreak = sgGeneral.add(new BoolSetting.Builder()
        .name("anti-break")
        .description("Stops you from breaking your tool.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Integer> breakDurability = sgGeneral.add(new IntSetting.Builder()
        .name("anti-break-percentage")
        .description("The durability percentage to stop using a tool.")
        .defaultValue(10)
        .min(1).max(100)
        .sliderMax(50)
        .visible(antiBreak::get)
        .build()
    );

    private final Setting<Boolean> switchBack = sgGeneral.add(new BoolSetting.Builder()
        .name("switch-back")
        .description("Switches your hand to whatever was selected when releasing your attack key.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Integer> switchDelay = sgGeneral.add((new IntSetting.Builder()
        .name("switch-delay")
        .description("Delay in ticks before switching tools.")
        .defaultValue(0)
        .build()
    ));

    private int prevSlot;
    private boolean wasPressed;
    private boolean shouldSwitch;
    private int ticks;
    private int bestSlot;

    public AutoTool() {
        super(Categories.Player, "auto-tool", "Automatically switches to the most effective tool when performing an action.");
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (switchBack.get() && !mc.options.keyAttack.isPressed() && wasPressed && prevSlot != -1) {
            InvUtils.swap(prevSlot);
            prevSlot = -1;
            wasPressed = false;
            return;
        }

        if (ticks <= 0 && shouldSwitch && bestSlot != -1) {
            InvUtils.swap(bestSlot);
            shouldSwitch = false;
        } else {
            ticks--;
        }

        wasPressed = mc.options.keyAttack.isPressed();
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void onStartBreakingBlock(StartBreakingBlockEvent event) {
        // Get blockState
        BlockState blockState = mc.world.getBlockState(event.blockPos);
        if (blockState.getHardness(mc.world, event.blockPos) < 0 || blockState.isAir()) return;

        // Check if we should switch to a better tool
        ItemStack currentStack = mc.player.getMainHandStack();

        double bestScore = -1;
        bestSlot = -1;

        for (int i = 0; i < 9; i++) {
            double score = getScore(mc.player.getInventory().getStack(i), blockState);
            if (score < 0) continue;

            if (score > bestScore) {
                bestScore = score;
                bestSlot = i;
            }
        }

        if ((bestSlot != -1 && (bestScore > getScore(currentStack, blockState)) || shouldStopUsing(currentStack) || !(currentStack.getItem() instanceof ToolItem))) {
            ticks = switchDelay.get();

            prevSlot = mc.player.getInventory().selectedSlot;

            if (ticks == 0) InvUtils.swap(bestSlot);
            else shouldSwitch = true;
        }

        // Anti break
        currentStack = mc.player.getMainHandStack();

        if (shouldStopUsing(currentStack) && currentStack.getItem() instanceof ToolItem) {
            mc.options.keyAttack.setPressed(false);
            event.setCancelled(true);
        }
    }

    private double getScore(ItemStack itemStack, BlockState state) {
        if (shouldStopUsing(itemStack) || !(itemStack.getItem() instanceof ToolItem)) return -1;

        if (silkTouchForEnderChest.get()
            && state.getBlock() == Blocks.ENDER_CHEST
            && EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, itemStack) == 0) {
            return -1;
        }

        double score = 0;

        score += itemStack.getMiningSpeedMultiplier(state) * 1000;
        score += EnchantmentHelper.getLevel(Enchantments.UNBREAKING, itemStack);
        score += EnchantmentHelper.getLevel(Enchantments.EFFICIENCY, itemStack);
        score += EnchantmentHelper.getLevel(Enchantments.MENDING, itemStack);

        if (prefer.get() == EnchantPreference.Fortune) score += EnchantmentHelper.getLevel(Enchantments.FORTUNE, itemStack);
        if (prefer.get() == EnchantPreference.SilkTouch) score += EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, itemStack);

        return score;
    }

    private boolean shouldStopUsing(ItemStack itemStack) {
        return antiBreak.get() && itemStack.getMaxDamage() - itemStack.getDamage() < breakDurability.get();
    }

    public enum EnchantPreference {
        None,
        Fortune,
        SilkTouch
    }
}
