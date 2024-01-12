package io.github.yangsx95.tools.apidoc.ss;

import io.github.yangsx95.tools.apidoc.core.ApiBaseInfo;
import io.github.yangsx95.tools.apidoc.core.ApiInfo;
import io.github.yangsx95.tools.apidoc.core.ApiOperation;
import io.github.yangsx95.tools.apidoc.core.collector.Collector;
import io.github.yangsx95.tools.apidoc.core.collector.JavaReflectMethodsCollectorSource;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author yangshunxiang
 * @since 2024/1/14
 */
public class SpringMvcSwaggerApiCollector implements Collector<JavaReflectMethodsCollectorSource> {

    @Override
    public List<ApiInfo> collect(JavaReflectMethodsCollectorSource collectorSource) {
        Set<Method> methods = collectorSource.methods();
        if (methods.isEmpty()) {
            return Collections.emptyList();
        }
        methods = methods.stream()
                .filter(m -> m.getAnnotation(io.swagger.annotations.ApiOperation.class) != null)
                .collect(Collectors.toSet());

        SpringMvcSwaggerCollectContext context = new SpringMvcSwaggerCollectContext();
        return methods.stream().map(m -> {
            ApiOperation opr = SpringMvcSwaggerApiParser.parseApiOperationFromMethod(m, context);
            ApiBaseInfo apiBaseInfo = SpringMvcSwaggerApiParser.parseApiBaseInfoFromMethod(m, context);
            return new ApiInfo(apiBaseInfo, opr);
        }).toList();
    }
}
