package ps.abgabe;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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
        Assert.assertEquals(value, result);
    }
}
