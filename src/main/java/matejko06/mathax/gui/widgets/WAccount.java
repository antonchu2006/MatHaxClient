package matejko06.mathax.gui.widgets;

import matejko06.mathax.gui.WidgetScreen;
import matejko06.mathax.gui.widgets.containers.WHorizontalList;
import matejko06.mathax.gui.widgets.pressable.WButton;
import matejko06.mathax.gui.widgets.pressable.WMinus;
import matejko06.mathax.systems.accounts.Account;
import matejko06.mathax.systems.accounts.Accounts;
import matejko06.mathax.utils.Utils;
import matejko06.mathax.utils.network.MatHaxExecutor;
import matejko06.mathax.utils.render.color.Color;

public abstract class WAccount extends WHorizontalList {
    public Runnable refreshScreenAction;

    private final WidgetScreen screen;
    private final Account<?> account;

    public WAccount(WidgetScreen screen, Account<?> account) {
        this.screen = screen;
        this.account = account;
    }

    protected abstract Color loggedInColor();

    protected abstract Color accountTypeColor();

    @Override
    public void init() {
        // Head
        add(theme.texture(32, 32, 90, account.getCache().getHeadTexture()));

        // Name
        WLabel name = add(theme.label(account.getUsername())).widget();
        if (Utils.mc.getSession().getUsername().equalsIgnoreCase(account.getUsername())) name.color = loggedInColor();

        // Type
        WLabel label = add(theme.label("(" + account.getType() + ")")).expandCellX().right().widget();
        label.color = accountTypeColor();

        // Login
        WButton login = add(theme.button("Login")).widget();
        login.action = () -> {
            login.minWidth = login.width;
            login.set("...");
            screen.locked = true;

            MatHaxExecutor.execute(() -> {
                if (account.login()) {
                    name.set(account.getUsername());

                    Accounts.get().save();

                    screen.taskAfterRender = refreshScreenAction;
                }

                login.minWidth = 0;
                login.set("Login");
                screen.locked = false;
            });
        };

        // Remove
        WMinus remove = add(theme.minus()).widget();
        remove.action = () -> {
            Accounts.get().remove(account);
            if (refreshScreenAction != null) refreshScreenAction.run();
        };
    }
}
