package io.github.yangsx95.tools.apidoc.core.collector;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * @author yangshunxiang
 * @since 2024/1/10
 */
public class JavaReflectMethodsCollectorSource implements CollectorSource {

    private final Set<Method> methods;

    public JavaReflectMethodsCollectorSource(Set<Method> methods) {
        this.methods = methods;
    }

    public Set<Method> methods() {
        return methods;
    }
}
