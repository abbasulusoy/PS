package ps.abgabe;

import java.util.Iterator;

/**
 * @author Abbas ULUSOY 
 * fill stacks with operators and values 
 *
 */
public class StackCalculator {
    DataStack<String> outputs = new DataStack<String>();
    DataStack<String> inputs = new DataStack<String>();
    Calculator calculator = new Calculator();

	/**
	 * Evaluates the expression
	 *
	 * @param expr
	 *            - input linked list, each operator/number is stored in the
	 *            list as an element
	 * @return string - result as a string
	 */
	public String evaluate(String expr) {
        String[] exprArray = null;
        DataStack<String> inputs = new DataStack<>();
        DataStack<String> output = new DataStack<>();

        inputs.push("0");
        if (expr.contains("(") || expr.contains(")")) {
            exprArray = expr.split("\\)");
            for (String s : exprArray) {
                if (s.contains("(")) {
                    s = s + ")";
                }
                inputs.push(s);
            }
        }

        Iterator<String> iter = inputs.iterator();
        while (iter.hasNext()) {
            String token = inputs.pop();
            if (calculator.isUnaryOperator(token)) {
                calculator.evaluateUnaryOperator(token, inputs);
            } else {
                calculator.evaluateOperator(token, inputs);
            }
        }

        return exprArray[0] + " " + exprArray[1] + " " + exprArray[2];
	}


}
