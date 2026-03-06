package io.github.vadman1.operations.commands;

import org.springframework.stereotype.Component;
import io.github.vadman1.operations.ConsoleOperationType;
import io.github.vadman1.operations.OperationCommand;

@Component
public class ExitCommand implements OperationCommand {

    @Override
    public void execute() {
        System.out.println("MiniBank stopped.");
    }

    @Override
    public ConsoleOperationType getOperationType() {
        return ConsoleOperationType.EXIT;
    }
}
