package io.github.yangsx95.tools.apidoc.ss;

import io.github.yangsx95.tools.apidoc.core.ApiInfo;
import io.github.yangsx95.tools.apidoc.core.ApiResponseBody;
import io.github.yangsx95.tools.apidoc.core.collector.JavaReflectMethodsCollectorSource;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yangshunxiang
 * @since 2024/1/14
 */
public class SpringMvcSwaggerApiCollectorTest {

    @Test
    void collect() {
        SpringMvcSwaggerApiCollector collector = new SpringMvcSwaggerApiCollector();
        List<ApiInfo> collect = collector.collect(new JavaReflectMethodsCollectorSource(Arrays.stream(SpringMvcSwaggerApiParserTest.BClass.class.getMethods()).collect(Collectors.toSet())));
        System.out.println(collect);
    }
}
