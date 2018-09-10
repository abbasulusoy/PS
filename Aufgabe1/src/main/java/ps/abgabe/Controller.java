package ps.abgabe;

import java.util.LinkedList;

public class Controller {

	/**
	 * @param value
	 * @return get int value parse string to int
	 */
	private Integer getIntValue(String value) {
		return getIntValue(value);
	}

	public static boolean isNumeric(String str) {
		try {
			Integer value = Integer.parseInt(str);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	public static Integer converToNumber(String str) {
		Integer value = null;
		try {
			value = Integer.parseInt(str);
		} catch (NumberFormatException nfe) {
			return null;
		}
		return value;
	}
	
	public static int numberOfParameters(String token) {
		switch (token) {
		case "+":
			return 2;
		case "-":
			return 2;
		case "*":
			return 2;
		case "/":
			return 2;
		case "^":
			return 2;
		case "%":
			return 2;
		case "!":
			return 1;
		case "|":
			return 2;
		case "~":
            return 1;
		case "<":
			return 2;
		case ">":
			return 2;
		default:
			return -1;
		}
	}

	

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
	 * @return
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
            case "~":
                result = negation(arguments.pop());
		}

		return result;
	}

    private Integer negation(Integer pop) {
        return -pop;
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
		return a+ b;
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

	public boolean isExit(String s) {
		if (s.equals("x")) {
			System.exit(1);
			return true;
		}
		return false;
	}

	public boolean isDivideList(String s) {
		if (s.equals(":")) {
			return true;
		}
		return false;
	}

	public boolean isCopy(String s) {
		if (s.equals("c")) {
			return true;
		}
		return false;
	}

	public boolean isNegation(String s) {
		if (s.equals("~")) {
			return true;
		}
		return false;
	}

	public boolean isDelete(String s) {
		if (s.equals("d")) {
			return true;
		}
		return false;
	}

	public boolean isApplyImmediately(String s) {
		if (s.equals("a")) {
			return true;
		}
		return false;
	}

	public boolean isApplyLater(String s) {
		if (s.equals("z")) {
			return true;
		}
		return false;
	}

	public boolean isReadRegister(String s) {
		if (s.equals("r")) {
			return true;
		}
		return false;
	}

	public boolean isWriteRegister(String s) {
		if (s.equals("w")) {
			return true;
		}
		return false;
	}

	public boolean isInteger(String s) {
		if (s.equals("i")) {
			return true;
		}
		return false;
	}

	public boolean isNoEmptyListCheck(String s) {
		if (s.equals("l")) {
			return true;
		}
		return false;
	}

	public boolean isStackSize(String s) {
		if (s.equals("s")) {
			return true;
		}
		return false;
	}

	public LinkedList<String> stringToQueue(String inputString) {
		LinkedList<String> inputStack = new LinkedList<String>();

		int len = inputString.length();
		for (int i = 0; i < len; i++) {
			inputStack.add(String.valueOf(inputString.charAt(i)));
		}

		return inputStack;
	}

	/**
	 * Prints the output string
	 * 
	 * @param stuff
	 */
	public void printOutput(DataStack<String> stuff) {
		StringBuilder string = new StringBuilder();
		// stuff.forEach(c -> {
		// string.append(c);
		// });
		System.out.println(string.toString());
	}

	public static boolean isFunction(String token) {
		if (token.equals("sqrt") || token.equals("sin") || token.equals("asin") || token.equals("cos")
				|| token.equals("acos") || token.equals("tan") || token.equals("atan") || token.equals("log")
				|| token.equals("log2")) {
			return true;
		}
		return false;
	}

    /**
     * @param token
     * @return left associative
     */
	public static boolean isLeftAssociative(String token) {
		if (token.equals("*") || token.equals("/") 
				|| token.equals("+") || token.equals("-") || token.equals("%") 
				|| token.equals("|") || token.equals("=") 
                || token.equals("<") || token.equals(">") || token.equals("~") || token.equals("x")) {
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
					System.err.println("The separator or parentheses were misplaced.");
					return null;
				}
			} else if (isOperator(token)) {
				while (!functionsstack.isEmpty() && isOperator(functionsstack.peek()) && ((isLeftAssociative(token)
						&& (getPrecedence(token) <= getPrecedence(functionsstack.peek()))
						|| (isLeftAssociative(token) && (getPrecedence(token) < getPrecedence(functionsstack.peek())))))) {
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