package ps.abgabe;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

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
     * @param expr input linked list, each operator/number is stored in the
     *            list as an element
     * @return string - result as a string
     */
    public void evaluate(String expr) {
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
                if (calculator.isApplyImmediately(token)) {
                    List<String> result = getStringFromListToExecute(inputs.pop());
                    for (String s : result) {
                        if (calculator.isNumeric(s)) {
                            inputs.push(s);
                        } else {
                            calculator.evaluateUnaryOperator(s, inputs);
                        }
                    }
                }
            } else {
                calculator.evaluateOperator(token, inputs);
            }
        }

        for (String s : inputs.getList()) {
            System.out.println("Item ::" + s);
        }
    }

    public List<String> getStringFromListToExecute(String strFromList) {
        List<String> separteList = Arrays.asList(strFromList.split(""));
        return separteList;
    }

}
