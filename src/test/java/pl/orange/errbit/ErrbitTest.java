package pl.orange.errbit;

import org.junit.Ignore;
import pl.orange.util.ErrbitUtils;

public class ErrbitTest {

    @Ignore
    public void notifyError() throws Exception {
        Exception e = new NullPointerException("test");
        ErrbitUtils.notifyError(e);
    }
}
