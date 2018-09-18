package ps.abgabe;

import java.util.Scanner;

/**
 * Tester class for Calculator class
 * @author Abbas ULUSOY
 *
 */
public class CalculatorTest{
	public static void main(String[] args) {
		StackCalculator calculator = new StackCalculator();
		Scanner sc = new Scanner(System.in);
		System.out.println("Welcome the to stack based calculator");
		for(;;) {
		System.out.println("Enter an expression:");

		String expr=sc.nextLine();
		String val=calculator.evaluate(expr);
            System.out.println("result ::: " + val);
            System.out.println("expression::  " + expr);
		}
	}
}
