package ps.abgabe;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Abbas ULUSOY
 *
 */
public class Calculator {

    List<String> register = null;

    /**
     *  Constructor
     */
    public Calculator() {
        register = new ArrayList<>(32);
        register.add("0");
    }
    /**
     * @param value
     * @return get int value parse string to int
     */
    private Integer getIntValue(String value) {
        return getIntValue(value);
    }

    /**
     * @param str
     * @return is number than true
     */
    public static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    /**
     * @param str
     * @return integer
     */
    public static Integer converToNumber(String str) {
        Integer value = null;
        try {
            value = Integer.parseInt(str);
        } catch (NumberFormatException nfe) {
            return null;
        }
        return value;
    }

    /**
     * @param token
     * @return true or false 
     */
    public static boolean isFunctionSeparator(String token) {
        if (token.equals(" ")) {
            return true;
        }
        return false;
    }

    public static boolean isOperator(String token) {
        if (token.equals("+") || token.equals("-") || token.equals("*") || token.equals("/") || token.equals("%")
                || token.equals("!") || token.equals("&") || token.equals("|") || token.equals("=") || token.equals("<")
                || token.equals(">")) {
            return true;
        }
        return false;
    }

    /**
     * Helper function Checks the operator and applies the corresponding
     * operation
     *
     * @param token
     *            - the operator
     * @param dataStack
     *            - list of arguments for the operator
     * @return a result for a operator token
     */
    public Integer evaluateOperator(String token, DataStack<String> dataStack) {
        Integer result = null;

        switch (token) {
            case "+":
                result = add(getIntValue(dataStack.pop()), getIntValue(dataStack.pop()));
                break;
            case "-":
                result = subtract(getIntValue(dataStack.pop()), getIntValue(dataStack.pop()));
                break;
            case "*":
                result = multiply(getIntValue(dataStack.pop()), getIntValue(dataStack.pop()));
                break;
            case "/":
                result = divide(getIntValue(dataStack.pop()), getIntValue(dataStack.pop()));
                break;
            case "^":
                result = pow(getIntValue(dataStack.pop()), getIntValue(dataStack.pop()));
                break;
            case "!":
                Integer temp = factorial(getIntValue(dataStack.pop()));
                if (temp != null) {
                    result = temp.intValue();
                } else {
                    return null;
                }
                break;
            case "<":
                result = smaller(getIntValue(dataStack.pop()), getIntValue(dataStack.pop()));
                break;
            case ">":
                result = greater(getIntValue(dataStack.pop()), getIntValue(dataStack.pop()));
                break;
            case "%":
                result = restOfDivision(getIntValue(dataStack.pop()), getIntValue(dataStack.pop()));
                break;
            default:
                result = 0;
        }

        return result;
    }

    /**
     * Helper function Checks the operator and applies the corresponding
     * operation
     *
     * @param token
     *            - the operator
     * @param datastack
     *            - list of arguments for the operator
     */
    public void evaluateUnaryOperator(String token, DataStack<String> datastack) {
        switch (token) {
            case "~":
                negation(datastack.pop(), datastack);
                break;
            case "c":
                copy(datastack.pop(), datastack);
                break;
            case "d":
                delete(datastack.pop(), datastack);
                break;
            case "a":
                applimmediately(datastack.pop(), datastack);
                break;
            case "i":
                isInteger(datastack.pop(), datastack);
                break;
            case "s":
                sizeOfDataStack(datastack);
                break;
            case "l":
                isNonEmptyListCheck(datastack.pop(), datastack);
                break;
            case "r":
                readRegister(datastack.pop(), datastack);
                break;
            case "w":
                writeRegister(datastack.pop(), datastack.getLast());
                break;
            case ":":
                combine(datastack.pop(), datastack.getLast(), datastack);
                break;
            case "!":
                divideList(datastack.pop(), datastack);
                break;
            case "z":
                applyLater(datastack.pop(), datastack);
                break;
            case "x":
                exit();
                break;
            default:
                break;
        }

    }

    /**
     * takes the top element n and the second element
     * x from the data stack and writes x to register n. An error is
     * reported if n is not an integer between 0 and 31
     * @param pop
     * @param arguments
     */
    private void writeRegister(String pop, String last) {
        if (getIntValue(pop) > 0 && getIntValue(pop) < 31) {
            register.add(getIntValue(pop), last);
        } else {

            throw new IllegalArgumentException("An error is reported if n is not an integer between 0 and 31.");
        }
    }

    /**
     * takes the top element n from the data stack and
     * pushes the contents of register n onto the data stack. An error
     * is reported if n is not an integer between 0 and 31.
     * @param pop
     * @param arguments
     */
    private void readRegister(String pop, DataStack<String> arguments) {
        if (getIntValue(pop) > 0 && getIntValue(pop) < 31) {
            arguments.push(register.get(getIntValue(pop)));
        } else {
            throw new IllegalArgumentException("An error is reported if n is not an integer between 0 and 31.");
        }
    }

    /**
     * Takes the top element h and the second element t
     * (which is a list) from the data stack, creates a new list by
     * adding h as new head element to t, and pushes the new list
     * onto the data stack. An error is reported if t is not a list
     * @param firstElement
     * @param lastElement
     * @param arguments
     */
    private void combine(String firstElement, String lastElement, DataStack<String> arguments) {
        if (isItemList(lastElement)) {
            throw new IllegalArgumentException("An error is reported that the next element is not a list");
        } else {
            arguments.push("(" + firstElement + lastElement + ")");
        }
    }

    /**
     * takes an argument (which is a nonempty list) from
     * the data stack and pushes first the tail of this list and then
     * the list head onto the data stack
     * @param pop
     * @param arguments
     */
    private void divideList(String pop, DataStack<String> arguments) {
        if (!isNonEmptyListCheck(pop, arguments)) {
            throw new IllegalArgumentException("An error is reported that the argument is not a nonempty list");
        }

        String nextElement = arguments.pop();
        if (isItemList(nextElement)) {
            throw new IllegalArgumentException("An error is reported that the next element is not a list");
        }
        nextElement.substring(0);
        nextElement = "(" + pop + nextElement;
        arguments.push(nextElement);
    }

    /**
     * exit calculator and programm
     */
    private void exit() {
        System.exit(0);
    }

    /**
     * s
     * Pushes the number of stack entries onto the stack.
     * @param arguments
     */
    public void sizeOfDataStack(DataStack<String> arguments) {
        String size = arguments.size() + "";
        arguments.push(size);

    }

    /** 
     * l
     * Checks if the top element on the data
     * stack is a nonempty list (without removing an element) and
     *  pushes a corresponding Boolean value (0 or 1) onto the stack.
     * @param item
     * @param arguments
     * @return if non empty list  
     */
    public boolean isNonEmptyListCheck(String item, DataStack<String> arguments) {
        applimmediately(item, arguments);
        String s = arguments.pop();
        if (s.isEmpty()) {
            arguments.push("1");
            return true;
        } else {
            arguments.push("0");
            return false;
        }
    }

    /**
     * i ::  Checks if the top element on the data stack is
     * an integer (without removing an element) and pushes a corresponding
     * Boolean value (0 or 1) onto the stack
     * @param item
     * @param arguments
     */
    public void isInteger(String item, DataStack<String> arguments) {
        try {
            int value = Integer.parseInt(item);
            if (Integer.valueOf(value) != null) {
                arguments.push("1");
            }
        } catch (Exception e) {
            arguments.push("0");
        }

    }

    /**
     * z
     * An argument (which is a list) is taken from the
     * data stack, and the list contents (without parentheses) are inserted
     * at the end of the command stream to be executed after
     * everything else currently being in the command stream. An
     * error is reported if the argument is no list 
     * @param item
     * @param arguments
     */
    private void applyLater(String item, DataStack<String> arguments) {
        if (!isItemList(item)) {
            throw new IllegalArgumentException("this Item :: " + item + " is not a list");
        }
        String result = item.replace("(", "").replace(")", "");
        arguments.addLast(result);
    }

    /**
     * An argument (which is a list) is taken
     * from the data stack, and the list contents (without parentheses)
     * are inserted at the begin of the command stream to be
     * executed next. An error is reported if the argument is no list
     *  a 
     * @param item
     * @param arguments
     */
    private void applimmediately(String item, DataStack<String> arguments) {
        if (!isItemList(item)) {
            throw new IllegalArgumentException("this Item :: " + item + " is not a list");
        }
        String result = item.replace("(", "").replace(")", "");
        arguments.push(result);
    }

    /**
     *  d
     * An argument (which is a list) is taken
     * from the data stack, and the list contents (without parentheses)
     * are inserted at the begin of the command stream to be
     * executed next. An error is reported if the argument is no list 
     * @param find
     * @param arguments
     */
    private void delete(String find, DataStack<String> arguments) {
        if (isIntegerPositiv(find)) {
            arguments.removeWithIndex(converToNumber(find));
        }
    }

    /**
     * c
     * replaces the top element n on the data stack with a copy
     * of the nth element on the data stack (counted from the top of
     * the stack). An error is reported if n is not a positive number.
     * @param find
     * @param arguments
     * @return copy function c please see assignment one 
     */
    private String copy(String find, DataStack<String> arguments) {
        if (isIntegerPositiv(find)) {
            return arguments.get(converToNumber(find));
        }
        return null;
    }

    /**
     * 
     * @param item
     * @return  check,  is item a list 
     */
    private boolean isItemList(String item) {
        if (item.startsWith("(") && item.endsWith(")")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param input
     * @return Checks if the top element on the data stack is an integer
     */
    private boolean isIntegerPositiv(String input) {
        if (!isInteger(input)) {
            String message = "error ::" + input + " a not number.";
            throw new IllegalArgumentException(message);
        } else {
            int number = converToNumber(input);
            if (number < 0) {
                String message = "error ::" + number + " a positive number.";
                throw new IllegalArgumentException(message);

            }
        }
        return true;
    }

    private void negation(String pop, DataStack<String> arguments) {
        if (isIntegerPositiv(pop)) {
            arguments.push(-converToNumber(pop) + "");
        }
    }

    /**
     * Adds two numbers together
     *
     * @param a
     *            first number
     * @param b
     *            second number
     * @return result
     */
    private Integer add(Integer a, Integer b) {
        return a + b;
    }

    /**
     * Subtracts two numbers
     *
     * @param a
     *            first number
     * @param b
     *            second number
     * @return result
     */
    private Integer subtract(Integer firstNumber, Integer secondNumber) {
        return firstNumber - secondNumber;
    }

    /**
     * Multiply two numbers together
     *
     * @param a
     *            first number
     * @param b
     *            second number
     * @return result
     */
    private Integer multiply(Integer firstNumber, Integer secondNumber) {
        return firstNumber * secondNumber;
    }

    /**
     * rest of Divides two numbers
     *
     * @param a
     *            first number
     * @param b
     *            second number
     * @return result
     */
    private Integer restOfDivision(Integer firstNumber, Integer secondNumber) {
        return firstNumber % secondNumber;
    }

    /**
     * Divides two numbers
     *
     * @param a
     *            first number
     * @param b
     *            second number
     * @return result
     */
    private Integer divide(Integer firstNumber, Integer secondNumber) {
        if (firstNumber == 0) {
            return 0;
        }

        return firstNumber / secondNumber;
    }

    /**
     * comparison two numbers for smaller
     *
     * @param a
     *            first number
     * @param b
     *            second number
     * @return result
     */
    private int smaller(Integer firstNumber, Integer secondNumber) {
        if (firstNumber < secondNumber) {
            return 1;
        }
        return 0;
    }

    /**
     * comparison two numbers for greater
     *
     * @param a
     *            first number
     * @param b
     *            second number
     * @return result
     */
    private int greater(Integer firstNumber, Integer secondNumber) {
        if (firstNumber > secondNumber) {
            return 1;
        }
        return 0;
    }

    /**
     * Raises the first number to the power of the second number
     *
     * @param a
     *            first number
     * @param b
     *            second number
     * @return result
     */
    private Integer pow(Integer firstNumber, Integer secondNumber) {
        return (int) Math.pow(firstNumber, secondNumber);
    }

    private Integer factorial(Integer firstNumber) {
        int p = 1;
        while (firstNumber != 0) {
            p *= firstNumber;
            firstNumber -= 1;
        }
        return p;
    }

    private boolean isExit(String s) {
        if (s.equals("x")) {
            System.exit(1);
            return true;
        } else {
            return false;
        }
    }

    private boolean isDivideList(String s) {
        if (s.equals("!")) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isCombine(String s) {
        if (s.equals(":")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param s
     * @return true or false
     */
    private boolean isCopy(String s) {
        if (s.equals("c")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param s
      * @return true or false
     */
    private boolean isNegation(String s) {
        if (s.equals("~")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param s
     * @return true or false
     */
    private boolean isDelete(String s) {
        if (s.equals("d")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param input
     * @return true or false
     */
    private boolean isApplyImmediately(String input) {
        if (input.equals("a")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param input
     * @return true or false
     */
    private boolean isApplyLater(String input) {
        if (input.equals("z")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param input
     * @return read register 
     */
    private boolean isReadRegister(String input) {
        if (input.equals("r")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param input
     * @return function for write register 
     */
    private boolean isWriteRegister(String input) {
        if (input.equals("w")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param input
     * @return is integer function
     */
    private boolean isInteger(String input) {
        if (input.equals("i")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param input
     * @return is non empty list check
     */
    private boolean isNoEmptyListCheck(String input) {
        if (input.equals("l")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param input if s than get the stack key
     * @return true or false
     */
    public boolean isStackSize(String input) {
        if (input.equals("s")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param token
     * @return ask if function sqrt and so on 
     */
    public static boolean isFunction(String token) {
        if (token.equals("sqrt")) {
            return true;
        }
        return false;
    }

    /**
     * @param token
     * @return left associative
     */
    public static boolean isBinaryOperator(String token) {
        if (token.equals("*") || token.equals("/") || token.equals("+") || token.equals("-") || token.equals("%")
                || token.equals("|") || token.equals("=") || token.equals("<") || token.equals(">")) {
            return true;
        }
        return false;
    }

    /**
     * @param token
     * @return left associative
     */
    public boolean isUnaryOperator(String token) {
        if (isNegation(token) || isExit(token) || isApplyImmediately(token) || isApplyLater(token) || isDelete(token)
                || isReadRegister(token) || isWriteRegister(token) || isCopy(token) || isInteger(token)
                || isNoEmptyListCheck(token) || isStackSize(token) || isDivideList(token) || isCombine(token)) {
            return true;
        }
        return false;
    }

    /**
     * A function to print all prime factors 
     * @param number
     */
    public static void primeFactors(int number) {
        // Print the number of 2s that divide n 
        while (number % 2 == 0) {
            number /= 2;
        }
        for (int i = 3; i <= Math.sqrt(number); i += 2) {
            // While i divides n, print i and divide n 
            while (number % i == 0) {
                System.out.print(i + " ");
                number /= i;
            }
        }

        if (number > 2)
            System.out.print(number);
    }
}