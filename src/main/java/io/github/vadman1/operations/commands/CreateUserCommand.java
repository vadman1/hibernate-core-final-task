package io.github.vadman1.operations.commands;

import org.springframework.stereotype.Component;
import io.github.vadman1.console.ConsoleInput;
import io.github.vadman1.operations.ConsoleOperationType;
import io.github.vadman1.operations.OperationCommand;
import io.github.vadman1.user.UserService;

@Component
public class CreateUserCommand implements OperationCommand {

    private final UserService userService;
    private final ConsoleInput consoleInput;

    public CreateUserCommand(UserService userService, ConsoleInput consoleInput) {
        this.userService = userService;
        this.consoleInput = consoleInput;
    }

    @Override
    public void execute() {
        String login = consoleInput.readRequiredString("Enter login:", "login");
        var user = userService.createUser(login);
        System.out.println("User created: " + user);
    }

    @Override
    public ConsoleOperationType getOperationType() {
        return ConsoleOperationType.USER_CREATE;
    }
}
