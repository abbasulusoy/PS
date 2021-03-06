package ps.abgabe;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author Abbas ULUSOY fill stacks with operators and values
 *
 */
public class StackCalculator {

	DataStack<String> outputs = new DataStack<String>();
	DataStack<String> inputs = new DataStack<String>();
	Calculator calculator = new Calculator();

	/**
	 * Evaluates the expression
	 * 
	 * @param inputStream
	 *            input linked list, each operator/number is stored in the list
	 *            as an element
	 * @return string - result as a string
	 */
	public void evaluate(String expression) {
		DataStack<String> inputStream = extractInputStream(expression);
		Iterator<String> iter = inputStream.iterator();
		while (iter.hasNext()) {
			System.out.println();
			String token = inputStream.pop();
			System.out.println("token ::" + token);
			System.out.println("inputs ::");
			for (String s : inputStream.getList()) {
				System.out.print(s + " next ");
			}
			System.out.println();
			System.out.println("Outputs ::");
			for (String s : outputs.getList()) {
				System.out.print(s + " ##");
			}
			if (token.equals(" ")) {
				continue;
			}
			if (calculator.isItemList(token)) {
				if (token.length() == 3) {
					outputs.push("0");
				} else {
					outputs.push(token);
				}
			} else if (calculator.isUnaryOperator(token)) {
				calculator.evaluateUnaryOperator(token, inputStream, outputs);
			} else if (calculator.isBinaryOperator(token)) {
				calculator.evaluateOperator(token, outputs);
			} else if (calculator.isNumeric(token)) {
				outputs.push(token);
			}
		}

		for (String s : outputs.getList()) {
			System.out.println("Result ::" + s);
		}
	}

	private DataStack<String> extractInputStream(String expression) {
		DataStack<String> inputStream = new DataStack<>();
		String navigate = "";
		if (!outputs.isEmpty()) {
			navigate = outputs.pop();
		}
		if (navigate.equals("3") || navigate.equals("0") || navigate.isEmpty()) {
			if (navigate.isEmpty()) {
				outputs.push("0");
			} else {
				outputs.push(navigate);
			}
			while (expression.contains("(")) {
				String list = generateList(expression, outputs);
				if (!list.isEmpty()) {
					outputs.push(list);
					expression = expression.replace(list, "");

				}
			}

			if (!expression.contains("(")) {
				for (String s : expression.split("")) {
					if (!s.isEmpty()) {
						inputStream.push(s);
					}
				}
				Collections.reverse(inputStream.getList());
			}
			for (String s : inputStream.getList()) {
				System.out.println("Item ::" + s);
			}
		} else {
			if (navigate.equals("5")) {
				calculateWithOutList(expression, inputStream);
			} else {
				expression = "(" + expression + ")";
				calculator.applimmediately(expression, inputStream);
			}
		}
		return inputStream;
	}

	/**
	 * calculate for sample 5 12+
	 * 
	 * @param expression
	 * @param inputStream
	 */
	private void calculateWithOutList(String expression, DataStack<String> inputStream) {
		for (String s : expression.split(" ")) {
			if (calculator.isContainsBinaryOperator(s)) {
				String newValue = "";
				for (String newS : s.split("")) {
					if (calculator.isNumeric(newS)) {
						newValue += newS;
					} else {
						inputStream.push(newS);
					}
				}
				outputs.push(newValue);
			} else {
				if (calculator.isNumeric(s)) {
					outputs.push(s);
				} else {
					inputStream.push(s);
				}
			}
		}
		Collections.reverse(inputStream.getList());
	}

	public List<String> getStringFromListToExecute(String strFromList) {
		List<String> separteList = Arrays.asList(strFromList.split(""));
		return separteList;
	}

	public static String generateList(String expression, DataStack<String> outputs) {

		int open = 0;
		String list = "";

		for (int i = 0; i < expression.length(); i++) {
			if (expression.charAt(i) == '(') {
				open++;
			} else if (expression.charAt(i) == ')') {

				open--;
			}
			if (open > 0) {
				list += expression.charAt(i);
			} else {
				list += expression.charAt(i);
				break;
			}
		}
		return list;
	}

	public DataStack<String> getOutputs() {
		return outputs;
	}

	/**
	 * getter and setters
	 * 
	 * @param outputs
	 */
	public void setOutputs(DataStack<String> outputs) {
		this.outputs = outputs;
	}

	public DataStack<String> getInputs() {
		return inputs;
	}

	public void setInputs(DataStack<String> inputs) {
		this.inputs = inputs;
	}

	public Calculator getCalculator() {
		return calculator;
	}

	public void setCalculator(Calculator calculator) {
		this.calculator = calculator;
	}

	public static void main(String[] args) {
		System.out.println(generateList("(2c1 3c-1c1=3c()(3c4d1+da)a2d*)2c3d2ca2d", new DataStack<String>()));
	}
}
