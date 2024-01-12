package io.github.yangsx95.tools.apidoc.ss;

import io.github.yangsx95.tools.apidoc.core.ApiInfo;
import io.github.yangsx95.tools.apidoc.core.ApiResponseBody;
import io.github.yangsx95.tools.apidoc.core.collector.JavaReflectMethodsCollectorSource;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author yangshunxiang
 * @since 2024/1/12
 */
class SpringMvcSwaggerApiDocOutputTest {

    @Test
    void output() {
        SpringMvcSwaggerApiCollector collector = new SpringMvcSwaggerApiCollector();
        List<ApiInfo> collect = collector.collect(new JavaReflectMethodsCollectorSource(Arrays.stream(SpringMvcSwaggerApiParserTest.BClass.class.getMethods()).collect(Collectors.toSet())));

        SpringMvcSwaggerApiDocOutput output = new SpringMvcSwaggerApiDocOutput();
        System.out.println(output.output(collect));
    }

}