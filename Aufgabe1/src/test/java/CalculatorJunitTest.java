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
      stackCalculator.evaluate(value);
        String result = stackCalculator.getOutputs().get(0);
        Assert.assertEquals("-9", result);
    }

    @Test
    public void test_5_8_Plus() {
        value = "5 8+";
        stackCalculator.evaluate(value);
        String result = stackCalculator.getOutputs().get(0);
        Assert.assertEquals("13", result);
    }

    @Test
    public void test_5_12_Plus() {
        value = "5 12+";
        stackCalculator.evaluate(value);
        String result = stackCalculator.getOutputs().get(0);
        Assert.assertEquals("17", result);
    }

    @Test
    public void test_list_operation() {
        value = "4 3(2*)a+";
        stackCalculator.evaluate(value);
        String result = stackCalculator.getOutputs().get(0);
        Assert.assertEquals("17", result);
    }

    @Test
    public void test_firstTest() {
        value = "(9~)(8)(3c4d1+da)a";
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
}
