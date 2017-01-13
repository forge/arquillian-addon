package ${package};

import org.testng.annotations.Test;
import org.testng.Assert;
import org.jboss.arquillian.testng.Arquillian;


public class ${ClassToTest}Test extends Arquillian {

    @Test
    public void should_be_deployed() {
        Assert.assertNotNull(url);
    }
}