package matejko06.mathax.systems.commands.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import matejko06.mathax.systems.commands.Command;
import matejko06.mathax.systems.modules.Module;
import matejko06.mathax.systems.modules.Modules;
import net.minecraft.command.CommandSource;

import java.util.ArrayList;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class PanicCommand extends Command {
    public PanicCommand() {
        super("panic", "Disables all modules.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            new ArrayList<>(Modules.get().getActive()).forEach(Module::toggle);

            return SINGLE_SUCCESS;
        });
    }
}
