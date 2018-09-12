package ps.abgabe;

public class Controller {

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
            Integer value = Integer.parseInt(str);
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

    public static boolean isOperator(String str) {
        if (str.equals("+") || str.equals("-") || str.equals("*") || str.equals("/") || str.equals("%")
                || str.equals("!") || str.equals("&") || str.equals("|") || str.equals("=") || str.equals("<")
                || str.equals(">")) {
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
     * @param arguments
     *            - list of arguments for the operator
     * @return a result for a operator token
     */
    public Integer evaluateOperator(String token, DataStack<Integer> arguments) {
        Integer result = null;

        switch (token) {
            case "+":
                result = add(arguments.pop(), arguments.pop());
                break;
            case "-":
                result = subtract(arguments.pop(), arguments.pop());
                break;
            case "*":
                result = multiply(arguments.pop(), arguments.pop());
                break;
            case "/":
                result = divide(arguments.pop(), arguments.pop());
                break;
            case "^":
                result = pow(arguments.pop(), arguments.pop());
                break;
            case "!":
                Integer temp = factorial(arguments.pop());
                if (temp != null) {
                    result = temp.intValue();
                } else {
                    return null;
                }
                break;
            case "<":
                result = smaller(arguments.pop(), arguments.pop());
                break;
            case ">":
                result = greater(arguments.pop(), arguments.pop());
                break;
            case "%":
                result = restOfDivision(arguments.pop(), arguments.pop());
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
     * @param arguments
     *            - list of arguments for the operator
     */
    public void evaluateUnaryOperator(String token, DataStack<String> arguments) {
        switch (token) {
            case "~":
                negation(arguments.pop(), arguments);
                break;
            case "c":
                copy(arguments.pop(), arguments);
                break;
            case "d":
                delete(arguments.pop(), arguments);
                break;
            case "a":
                applimmediately(arguments.pop(), arguments);
                break;
            default:
                break;
        }

    }

    private void applimmediately(String item, DataStack<String> arguments) {
        if (isItemList(item)) {
            throw new IllegalArgumentException("this Item :: " + item + " is not a list");
        }
        String result = item.replace("(", "").replace(")", "");
        arguments.push(result);
    }

    private void delete(String find, DataStack<String> arguments) {
        if (isIntegerPositiv(find)) {
            arguments.removeWithIndex(converToNumber(find));
        }
    }

    private String copy(String find, DataStack<String> arguments) {
        if (isIntegerPositiv(find)) {
            return arguments.get(converToNumber(find));
        }
        return null;
    }

    private boolean isItemList(String item) {
        if (item.startsWith("(") && item.endsWith(")")) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isIntegerPositiv(String find) {
        if (!isInteger(find)) {
            String message = "error ::" + find + " a not number.";
            throw new IllegalArgumentException(message);
        } else {
            int number = converToNumber(find);
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
     * @param s
     * @return true or false
     */
    private boolean isApplyImmediately(String s) {
        if (s.equals("a")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param s
     * @return true or false
     */
    private boolean isApplyLater(String s) {
        if (s.equals("z")) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isReadRegister(String s) {
        if (s.equals("r")) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isWriteRegister(String s) {
        if (s.equals("w")) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isInteger(String s) {
        if (s.equals("i")) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isNoEmptyListCheck(String s) {
        if (s.equals("l")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param s if s than get the stack key
     * @return true or false
     */
    public boolean isStackSize(String s) {
        if (s.equals("s")) {
            return true;
        } else {
            return false;
        }
    }

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

    public static int getPrecedence(String token) {
        switch (token) {
            case "+":
                return 2;
            case "-":
                return 2;
            case "*":
                return 3;
            case "/":
                return 3;
            case "%":
                return 3;
            case "^":
                return 4;
            case "!":
                return 5;
            default:
                return 1;
        }
    }

    /**
     * Parses the expression
     * 
     * @param input2
     *            - input expression as a list
     * @return output list
     */
    public DataStack<String> parse(DataStack<String> input2) {
        DataStack<String> output = new DataStack();
        DataStack<String> functionsstack = new DataStack();
        DataStack<String> input = input2;

        while (!input.isEmpty()) {
            String token = input.remove();

            if (isNumeric(token)) {
                output.push(token);
            } else if (isFunction(token)) {
                functionsstack.push(token);
            } else if (isFunctionSeparator(token)) {
                while (!functionsstack.isEmpty() && !functionsstack.peek().equals("(")) {
                    output.push(functionsstack.pop());
                }
                if (functionsstack.peek().equals("(")) {

                } else {
                    System.out.println("The separator or parentheses were misplaced.");
                    return null;
                }
            } else if (isOperator(token)) {
                while (!functionsstack.isEmpty() && isOperator(functionsstack.peek())
                        && ((isBinaryOperator(token) && (getPrecedence(token) <= getPrecedence(functionsstack.peek()))
                                || (isBinaryOperator(token)
                                        && (getPrecedence(token) < getPrecedence(functionsstack.peek())))))) {
                    output.push(functionsstack.pop());
                }
                functionsstack.push(token);
            } else if (token.equals("(")) {
                functionsstack.push(token);
            } else if (token.equals(")")) {
                while (!functionsstack.isEmpty() && !functionsstack.peek().equals("(")) {
                    output.push(functionsstack.pop());
                }
                if (functionsstack.peek().equals("(")) {
                    functionsstack.pop();
                    if (!functionsstack.isEmpty() && isFunction(functionsstack.peek())) {
                        output.push(functionsstack.pop());
                    }
                } else {
                    System.err.println("There are mismatched parentheses.");
                    return null;
                }

            }
        }

        if (input.isEmpty()) {
            while (!functionsstack.isEmpty()) {
                if (functionsstack.peek().equals("(") || functionsstack.peek().equals(")")) {
                    System.err.println("There are mismatched parentheses.");
                    return null;
                }
                output.push(functionsstack.pop());
            }
        }
        return output;
    }

}