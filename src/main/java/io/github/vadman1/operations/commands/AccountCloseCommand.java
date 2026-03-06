package io.github.vadman1.operations.commands;

import org.springframework.stereotype.Component;
import io.github.vadman1.account.AccountService;
import io.github.vadman1.console.ConsoleInput;
import io.github.vadman1.operations.ConsoleOperationType;
import io.github.vadman1.operations.OperationCommand;
import io.github.vadman1.user.UserService;

@Component
public class AccountCloseCommand implements OperationCommand {

    private final AccountService accountService;
    private final UserService userService;
    private final ConsoleInput consoleInput;

    public AccountCloseCommand(AccountService accountService, UserService userService, ConsoleInput consoleInput) {
        this.accountService = accountService;
        this.userService = userService;
        this.consoleInput = consoleInput;
    }

    @Override
    public void execute() {
        int accountId = consoleInput.readPositiveInt("Enter account id to close:", "account id");
        accountService.closeAccount(accountId);

        System.out.println("Account " + accountId + " closed.");
    }

    @Override
    public ConsoleOperationType getOperationType() {
        return ConsoleOperationType.ACCOUNT_CLOSE;
    }
}
