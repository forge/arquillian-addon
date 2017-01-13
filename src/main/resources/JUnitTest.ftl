package ${package};

import ${packageImport}.${ClassToTest};

<#if asClient>
import java.net.URL;
import org.jboss.arquillian.test.api.ArquillianResource;
<#else>
import javax.inject.Inject;
</#if>

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import ${archiveType.className};
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


import static org.junit.Assert.*;
import static org.hamcrest.core.Is.*;

@RunWith(Arquillian.class)
public class ${ClassToTest}Test {

    <#if asClient>
    @ArquillianResource
    URL applicationUrl;
    <#else>
    @Inject
    private ${ClassToTest} ${classToTest};
    </#if>

    <#if asClient>
    @Deployment(testable = false)
    <#else>
    @Deployment
    </#if>
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
