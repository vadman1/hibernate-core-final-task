package io.github.vadman1;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import io.github.vadman1.console.OperationsConsoleListener;

public class Main {
    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext("io.github.vadman1")) {
            OperationsConsoleListener consoleListener = context.getBean(OperationsConsoleListener.class);
            consoleListener.runBank();
        }
    }
}