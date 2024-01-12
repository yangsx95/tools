package io.github.yangsx95.tools.apidoc.core.collector;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * @author yangshunxiang
 * @since 2024/1/10
 */
public class JavaReflectClassCollectorSource implements CollectorSource {

    private final Set<Class<?>> classes;

    public JavaReflectClassCollectorSource(Set<Class<?>> classes) {
        this.classes = classes;
    }

    public Set<Class<?>> classes() {
        return classes;
    }
}
