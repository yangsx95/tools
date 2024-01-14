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
        System.out.println(MdKiller.of().table().data(new Object[]{"1", "2"}, new Object[][]{{"1", "2"}}).endTable().build().toString());
    }

}