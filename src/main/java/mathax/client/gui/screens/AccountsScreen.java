package mathax.client.gui.screens;

import mathax.client.gui.GuiTheme;
import mathax.client.gui.WidgetScreen;
import mathax.client.gui.WindowScreen;
import mathax.client.gui.widgets.WAccount;
import mathax.client.gui.widgets.containers.WContainer;
import mathax.client.gui.widgets.containers.WHorizontalList;
import mathax.client.gui.widgets.pressable.WButton;
import mathax.client.systems.accounts.Account;
import mathax.client.systems.accounts.Accounts;
import mathax.client.utils.network.MatHaxExecutor;

import static mathax.client.utils.Utils.mc;

public class AccountsScreen extends WindowScreen {
    public AccountsScreen(GuiTheme theme) {
        super(theme, "Accounts");
    }

    @Override
    protected void init() {
        super.init();

        clear();
        initWidgets();
    }

    private void initWidgets() {
        // Accounts
        for (Account<?> account : Accounts.get()) {
            WAccount wAccount = add(theme.account(this, account)).expandX().widget();
            wAccount.refreshScreenAction = () -> {
                clear();
                initWidgets();
            };
        }

        // Add account
        WHorizontalList l = add(theme.horizontalList()).expandX().widget();

        addButton(l, "Cracked", () -> mc.openScreen(new AddCrackedAccountScreen(theme)));
        addButton(l, "Premium", () -> mc.openScreen(new AddPremiumAccountScreen(theme)));
        addButton(l, "The Altening", () -> mc.openScreen(new AddAlteningAccountScreen(theme)));
    }

    private void addButton(WContainer c, String text, Runnable action) {
        WButton button = c.add(theme.button(text)).expandX().widget();
        button.action = action;
    }

    public static void addAccount(WButton add, WidgetScreen screen, Account<?> account) {
        add.set("...");
        screen.locked = true;

        MatHaxExecutor.execute(() -> {
            if (account.fetchInfo() && account.fetchHead()) {
                Accounts.get().add(account);
                screen.locked = false;
                screen.onClose();
            }

            add.set("Add");
            screen.locked = false;
        });
    }
}
