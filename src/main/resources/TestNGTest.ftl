package ${package};

import ${packageImport}.${ClassToTest};
import javax.inject.Inject;
import org.jboss.arquillian.container.test.api.Deployment;
import org.testng.annotations.Test;
import org.testng.Assert;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import ${archiveType.className};
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.arquillian.testng.Arquillian;


public class ${ClassToTest}Test extends Arquillian {

    @Inject
    private ${ClassToTest} ${classToTest};

    @Deployment(testable = ${testable?c})
    public static ${archiveType.simpleClassName} createDeployment() {
        return ShrinkWrap.create(${archiveType.simpleClassName}.class)
                .addClass(${ClassToTest}.class)
                <#if enableJPA>
                .addAsManifestResource("META-INF/persistence.xml", "persistence.xml")
                </#if>
                .${archiveType.beansXmlLocationAdder}(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Test
    public void should_be_deployed() {
        Assert.assertNotNull(${classToTest});
    }
}