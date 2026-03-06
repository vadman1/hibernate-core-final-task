package io.github.vadman1.operations.commands;

import org.springframework.stereotype.Component;
import io.github.vadman1.account.Account;
import io.github.vadman1.account.AccountService;
import io.github.vadman1.console.ConsoleInput;
import io.github.vadman1.operations.ConsoleOperationType;
import io.github.vadman1.operations.OperationCommand;
import io.github.vadman1.user.UserService;

@Component
public class AccountCreateCommand implements OperationCommand {

    private final AccountService accountService;
    private final UserService userService;
    private final ConsoleInput consoleInput;

    public AccountCreateCommand(AccountService accountService, UserService userService, ConsoleInput consoleInput) {
        this.accountService = accountService;
        this.userService = userService;
        this.consoleInput = consoleInput;
    }

    @Override
    public void execute() {
        int userId = consoleInput.readPositiveInt("Enter user id:", "user id");
        var user = userService.findUserById(userId);
        Account account = accountService.createAccount(user);
        user.getAccountList().add(account);
        System.out.println("Account created: " + account);
    }

    @Override
    public ConsoleOperationType getOperationType() {
        return ConsoleOperationType.ACCOUNT_CREATE;
    }
}
