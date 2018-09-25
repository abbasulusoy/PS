package ps.abgabe;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.naming.spi.DirStateFactory.Result;

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
		String[] exprArray = null;
		DataStack<String> inputStream = new DataStack<>();
		DataStack<String> outputs = new DataStack<>();

		inputStream.push(expression.substring(expression.length() - 1));
		expression = expression.substring(0, expression.length() - 1);
		outputs.push("3");

		exprArray = expression.split("");
		String resul="";
		for (int i = 0; i < expression.length(); i++) {
			if (expression.charAt(i) == '(') {
				String list = generateList(expression);
				if(!list.isEmpty()) {
					outputs.push(list);
					expression = expression.replace(list, "");
					i=0;
				} 
				
			} else {
				outputs.push(expression.charAt(i)+"");
			}
		}
		
		
		for (String s : outputs.getList()) {
			System.out.println("Item ::" + s);
		}

		Iterator<String> iter = inputStream.iterator();

		while (iter.hasNext()) {
			String token = inputStream.pop();
			if (calculator.isUnaryOperator(token)) {
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

	public List<String> getStringFromListToExecute(String strFromList) {
		List<String> separteList = Arrays.asList(strFromList.split(""));
		return separteList;
	}

	public static String generateList(String expression) {

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

	public static void main(String[] args) {
		System.out.println(generateList("(2c1 3c-1c1=3c()(3c4d1+da)a2d*)2c3d2ca2d"));
	}
}
