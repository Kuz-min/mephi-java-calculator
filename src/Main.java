import java.util.Scanner;
import java.util.function.Consumer;

public final class Main {
    public static void main(String[] args) {
        new ConsoleCalculator().run();
    }
}

final class ConsoleCalculator {
    private enum OperationType {FIRST_OPERAND_INPUT, OPERATION_INPUT, SECOND_OPERAND_INPUT, COMMAND_EXECUTION}

    private enum CommandType {NONE, RESET, EXIT}

    private final Calculator calculator = new Calculator();
    private final Scanner scanner = new Scanner(System.in);
    private OperationType currentOperation = OperationType.FIRST_OPERAND_INPUT;
    private CommandType currentCommand = CommandType.NONE;

    public void run() {
        while (true) {
            switch (currentOperation) {
                case FIRST_OPERAND_INPUT: {
                    System.out.println("Значение: " + calculator.getValue());
                    readUntilSuccess("Введите число или команду (C — сброс, S — выход): ",
                            (line) -> {
                                var command = parseCommand(line);
                                if (command != CommandType.NONE) {
                                    currentCommand = command;
                                    currentOperation = OperationType.COMMAND_EXECUTION;
                                    return;
                                }
                                calculator.setValue(Double.parseDouble(line));
                                currentOperation = OperationType.OPERATION_INPUT;
                            });
                    break;
                }

                case OPERATION_INPUT: {
                    System.out.println("Значение: " + calculator.getValue());
                    readUntilSuccess("Введите операцию (+, -, *, /) или команду (C — сброс, S — выход): ",
                            (line) -> {
                                var command = parseCommand(line);
                                if (command != CommandType.NONE) {
                                    currentCommand = command;
                                    currentOperation = OperationType.COMMAND_EXECUTION;
                                    return;
                                }
                                calculator.setOperation(parseOperation(line));
                                currentOperation = OperationType.SECOND_OPERAND_INPUT;
                            });
                    break;
                }

                case SECOND_OPERAND_INPUT: {
                    System.out.println("Значение: " + calculator.getValue());
                    readUntilSuccess("Введите число или команду (C — сброс, S — выход): ",
                            (line) -> {
                                var command = parseCommand(line);
                                if (command != CommandType.NONE) {
                                    currentCommand = command;
                                    currentOperation = OperationType.COMMAND_EXECUTION;
                                    return;
                                }
                                calculator.apply(Double.parseDouble(line));
                                currentOperation = OperationType.OPERATION_INPUT;
                            });
                    break;
                }

                case COMMAND_EXECUTION: {
                    switch (currentCommand) {
                        case RESET: {
                            calculator.reset();
                            currentCommand = CommandType.NONE;
                            currentOperation = OperationType.FIRST_OPERAND_INPUT;
                            break;
                        }
                        case EXIT: {
                            return;
                        }
                        case NONE:
                            break;
                    }
                    break;
                }
            }
        }
    }

    private void readUntilSuccess(String title, Consumer<String> func) {
        while (true) {
            System.out.println(title);
            try {
                String line = scanner.nextLine().trim();
                func.accept(line);
                return;
            } catch (RuntimeException exception) {
                System.out.println("Ошибка: " + exception.getMessage());
            }
        }
    }

    private CommandType parseCommand(String line) {
        return switch (line) {
            case "c", "C" -> CommandType.RESET;
            case "s", "S" -> CommandType.EXIT;
            default -> CommandType.NONE;
        };
    }

    private Calculator.OperationType parseOperation(String line) {
        return switch (line) {
            case "+" -> Calculator.OperationType.ADD;
            case "-" -> Calculator.OperationType.SUBTRACT;
            case "*" -> Calculator.OperationType.MULTIPLY;
            case "/" -> Calculator.OperationType.DIVIDE;
            default -> throw new RuntimeException("неподдерживаемая операция: " + line);
        };
    }
}

final class Calculator {
    public enum OperationType {ADD, SUBTRACT, MULTIPLY, DIVIDE}

    private Double value = null;
    private OperationType operation = null;

    public Double getValue() {
        return value;
    }

    public void reset() {
        value = null;
        operation = null;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public void setOperation(OperationType operation) {
        this.operation = operation;
    }

    public void apply(double operand) {
        if (value == null) {
            throw new RuntimeException("отсутствует значение для выполнения операции");
        }

        if (operation == null) {
            throw new RuntimeException("отсутствует операция для выполнения");
        }

        value = switch (operation) {
            case ADD -> value + operand;
            case SUBTRACT -> value - operand;
            case MULTIPLY -> value * operand;
            case DIVIDE -> {
                if (operand == 0.0) {
                    throw new RuntimeException("деление на ноль");
                }
                yield value / operand;
            }
            default -> throw new RuntimeException("неподдерживаемая операция");
        };
    }
}