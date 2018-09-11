package ps.abgabe;

/**
 * @author Abbas ULUSOY 
 * fill stacks with operators and values 
 *
 */
public class StackCalculator {
    DataStack<String> stack = new DataStack<String>();
    DataStack<String> outputs = new DataStack<String>();
    DataStack<String> inputs = new DataStack<String>();

	/**
	 * @return result
	 */
	public int calculate() {
		return 0;
	}

	/**
	 * Evaluates the expression
	 *
	 * @param expr
	 *            - input linked list, each operator/number is stored in the
	 *            list as an element
	 * @return string - result as a string
	 */
	public String evaluate(String expr) {
        String[] exprArray = expr.split("");
        DataStack<Integer> inputs = new DataStack<Integer>();
        DataStack<String> operators = new DataStack<String>();
        DataStack<String> inputsForOutputsToParse = new DataStack<>();

		for (String item : exprArray) {
            inputsForOutputsToParse.push(item);

			if (!Controller.isNumeric(item)) {
				for (String nextItem : item.split("")) {
					fillStacks(inputs, operators, nextItem);
				}
			} else {
				fillStacks(inputs, operators, item);
			}
		}
        outputs = new Controller().parse(inputsForOutputsToParse);

		Controller c = new Controller();
		Integer result = null;
		Integer index = operators.size();
        DataStack<String> reverseOperators = new DataStack<String>();
		for (Integer i = 0; i < index; i++) {
			reverseOperators.push(operators.pop());
		}

		index = reverseOperators.size();
		for (Integer i = 0; i < index; i++) {
			String token = reverseOperators.pop();
			if(token.contains("x")) {
                System.out.println("X is selected Calculation has been closed");
                System.exit(0);
                break;
			}
			result = c.evaluateOperator(token, inputs);
			fillResultSecondPlace(inputs, result);

		}
		if (result == null) {
			return "ERROR";
		}

		return result.toString();
	}

	private void fillResultSecondPlace(DataStack<Integer> inputs, Integer result) {
		if (!inputs.isEmpty()) {
			Integer next = inputs.pop();
			inputs.push(result);
			inputs.push(next);
		}
	}

	private void fillStacks(DataStack<Integer> inputs, DataStack<String> operators, String item) {
		if (Controller.isNumeric(item)) {
			inputs.push(Controller.converToNumber(item));
		} else {
			if (Controller.isBinaryOperator(item)) {
				operators.push(item);
			}
		}
	}

}
