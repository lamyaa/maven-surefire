package p;

import junit.framework.TestCase;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test
public class JUnitTNGTest extends TestCase {

    @BeforeClass
    protected void setUp() {
        System.out.println("In setUp");
    }

    @Test
    public void testMethod() {
        System.out.println("In testMethod");
    }

    @AfterClass
    public void cleanUp() {
        System.out.println("In cleanUp");
    }
}
