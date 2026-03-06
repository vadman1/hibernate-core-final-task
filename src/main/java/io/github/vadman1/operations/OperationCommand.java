package io.github.vadman1.operations;

public interface OperationCommand {
    void execute();
    ConsoleOperationType getOperationType();
}
