package p;

import org.junit.Test;

public class ATest {

    static {
        System.out.println("ATest: Fork number is " + System.getProperty("surefire.forkNumber"));
    }

    @Test

    public void t() throws Exception {
        Thread.sleep(5000);
    }

}
