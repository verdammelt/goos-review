import org.junit.Test;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class TestTest {
    @Test
    public void thingie() {
        assertThat(1, equalTo(2));
    }
}
