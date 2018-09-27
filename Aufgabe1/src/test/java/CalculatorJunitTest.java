import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ps.abgabe.StackCalculator;

/**
 * Test Class
 * Test all possible functions of calculator
 * @author Abbas ULUSOY
 *
 */
public class CalculatorJunitTest {

    StackCalculator stackCalculator = null;
    String value = "";

    @Before
    public void setup() {
        stackCalculator = new StackCalculator();
    }

    @Test
    public void testNegation() {
      value = "9~";
        stackCalculator = new StackCalculator();
        stackCalculator.getOutputs().push("1");
      stackCalculator.evaluate(value);
        String result = stackCalculator.getOutputs().get(0);
        Assert.assertEquals("-9", result);
    }

    @Test
    public void test_5_8_Plus() {
        value = "5 8+";
        stackCalculator = new StackCalculator();
        stackCalculator.getOutputs().push("2");
        stackCalculator.evaluate(value);
        String result = stackCalculator.getOutputs().get(0);
        Assert.assertEquals("13", result);
    }

    @Test
    public void test_5_12_Plus() {
        value = "5 12+";
        stackCalculator = new StackCalculator();
        stackCalculator.getOutputs().push("5");
        stackCalculator.evaluate(value);
        String result = stackCalculator.getOutputs().get(0);
        Assert.assertEquals("17", result);
    }

    @Test
    public void test_45_12_Plus() {
        value = "45 2 12+-";
        stackCalculator = new StackCalculator();
        stackCalculator.getOutputs().push("5");
        stackCalculator.evaluate(value);
        String result = stackCalculator.getOutputs().get(0);
        Assert.assertEquals("-31", result);
    }

    @Test
    public void test_15_2_3_4_12_Plus() {
        value = "15 2 3 4+*-";
        stackCalculator = new StackCalculator();
        stackCalculator.getOutputs().push("5");
        stackCalculator.evaluate(value);
        String result = stackCalculator.getOutputs().get(0);
        Assert.assertEquals("-1", result);
    }

    @Test
    public void test_list_operation() {
        value = "4 3(2*)a+";
        stackCalculator = new StackCalculator();
        stackCalculator.getOutputs().push("1");
        stackCalculator.evaluate(value);
        String result = stackCalculator.getOutputs().get(0);
        Assert.assertEquals("10", result);
    }

    @Test
    public void test_firstTest() {
        value = "(9~)(8)(3c4d1+da)a";
        stackCalculator = new StackCalculator();
        stackCalculator.getOutputs().push("0");
        stackCalculator.evaluate(value);
        String result = stackCalculator.getOutputs().get(0);
        Assert.assertEquals("-9", result);
    }

    @Test
    public void test_secondTest() {
        value = "(2c1 3c-1c1=3c()(3c4d1+da)a2d*)2c3d2ca2d";
        stackCalculator.getOutputs().push("3");
        stackCalculator.evaluate(value);
        String result = stackCalculator.getOutputs().get(0);
        Assert.assertEquals("6", result);
    }

    @Test
    public void test_primeTest() {
        value = "63 p";
        stackCalculator.getOutputs().push("5");
        stackCalculator.evaluate(value);
        String result = stackCalculator.getOutputs().get(0);
        Assert.assertEquals("3 3 7", result);
    }
}
