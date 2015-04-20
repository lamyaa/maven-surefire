package test;

import org.junit.Test;

public class BTest {

    static {
        System.out.println("BTest: Fork number is " + System.getProperty("surefire.forkNumber"));
    }

    @Test
    public void t() throws Exception {
        Thread.sleep(5000);
    }

}
