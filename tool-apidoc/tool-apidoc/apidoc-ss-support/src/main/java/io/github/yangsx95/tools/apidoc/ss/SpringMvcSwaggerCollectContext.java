package io.github.yangsx95.tools.apidoc.ss;

import io.github.yangsx95.tools.apidoc.core.ApiBaseInfo;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yangshunxiang
 * @since 2024/1/10
 */
public class SpringMvcSwaggerCollectContext {

    private final Map<Class<?>, ApiBaseInfo> apiInfoCache ;

    public SpringMvcSwaggerCollectContext() {
        apiInfoCache = new ConcurrentHashMap<>();
    }

    public ApiBaseInfo getApiInfo(Class<?> controllerClass) {
        return apiInfoCache.get(controllerClass);
    }

    public void putApiInfo(Class<?> controllerClass, ApiBaseInfo apiBaseInfo) {
        Assert.notNull(apiBaseInfo, "apiInfo can not null");
        apiInfoCache.put(controllerClass, apiBaseInfo);
    }

}
