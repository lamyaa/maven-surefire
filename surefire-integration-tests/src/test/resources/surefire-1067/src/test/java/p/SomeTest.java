package p;

import java.io.IOException;
import org.junit.Test;

public class SomeTest {
    @Test 
    public void t() throws Exception {
        try {
            m();
        } catch (RuntimeException x) {
            throw new IOException(x);
        }
    }
    private void m() {
        throw new UnsupportedOperationException();
    }
}
