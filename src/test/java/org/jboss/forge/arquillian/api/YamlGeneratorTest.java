package org.jboss.forge.arquillian.api;

import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class YamlGeneratorTest {

    @Test
    public void should_convert_to_yml() {
        //given
        final Map<String, Object> configParams = getConfigParams();

        //when
        final String yml = YamlGenerator.toYml(configParams);

        //then
        assertThat(yml).isEqualTo(
            "containerName:\n" +
            "  build:\n" +
            "    context: src/test/resources\n" +
            "    dockerfile: Dockerfile_alternative");
    }

    @Test
    public void should_convert_to_docker_compose_yml() {
        //given
        Map<String, Object> compose = new LinkedHashMap<>();

        final String yml = YamlGenerator.toYml(getConfigParams());
        compose.put("version", "2");
        compose.put("services", yml);

        //when
        final String toYml = YamlGenerator.toYml(compose);

        //then
        assertThat(toYml).isEqualTo(
            "version: '2'\n" +
                "services:\n" +
                "  containerName:\n" +
                "    build:\n" +
                "      context: src/test/resources\n" +
                "      dockerfile: Dockerfile_alternative"
        );
    }

    private Map<String, Object> getConfigParams(){
        Map<String, Object> params = new LinkedHashMap<>();
        Map<String, String> build = new LinkedHashMap<>();

        String imageParams = "context: src/test/resources"  + System.lineSeparator() +
            "dockerfile: Dockerfile_alternative";

        build.put("build", imageParams);
        params.put("containerName", build);

        return params;
    }
}
