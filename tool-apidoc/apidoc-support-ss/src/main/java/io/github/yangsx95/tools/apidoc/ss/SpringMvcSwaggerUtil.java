package io.github.yangsx95.tools.apidoc.ss;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author yangshunxiang
 * @since 2024/1/10
 */
public class SpringMvcSwaggerUtil {

    /**
     * 判断注解是否是 RequestMapping注解
     *
     * @param annotationType 注解类型
     * @return true 是 false 不是
     */
    public static boolean isRequestMapping(Class<? extends Annotation> annotationType) {
        Assert.notNull(annotationType, "annotationType can not null");
        return annotationType == RequestMapping.class
                || AnnotationUtils.findAnnotation(annotationType, RequestMapping.class) != null;
    }

    public static Annotation getRequestMappingAnnoOnMethod(Method method) {
        Assert.notNull(method, "method can not null");
        return Arrays.stream(method.getAnnotations())
                .filter(anno -> isRequestMapping(anno.annotationType()))
                .findFirst()
                .orElse(null);
    }

    public static boolean methodHasRequestMappingAnno(Method method) {
        return Objects.nonNull(getRequestMappingAnnoOnMethod(method));
    }

    public static Annotation getRequestMappingAnnoOnClass(Class<?> clazz) {
        Assert.notNull(clazz, "class can not null");
        return Arrays.stream(clazz.getAnnotations())
                .filter(anno -> isRequestMapping(anno.annotationType()))
                .findFirst()
                .orElse(null);
    }

    public static SpringMvcRequestMappingInfo getRequestMappingAnnoInfo(Annotation anno) {
        try {
            String[] paths = (String[]) anno.annotationType().getDeclaredMethod("value").invoke(anno);
            String[] params = (String[]) anno.annotationType().getDeclaredMethod("params").invoke(anno);
            String[] headers = (String[]) anno.annotationType().getDeclaredMethod("headers").invoke(anno);
            String[] consumes = (String[]) anno.annotationType().getDeclaredMethod("consumes").invoke(anno);
            String[] produces = (String[]) anno.annotationType().getDeclaredMethod("produces").invoke(anno);
            Set<String> methods;
            if (anno instanceof RequestMapping) {
                 methods = Arrays.stream((RequestMethod[]) anno.annotationType().getDeclaredMethod("method").invoke(anno)).map(Enum::toString).collect(Collectors.toSet());
            } else if (anno instanceof PostMapping) {
                methods = Collections.singleton("POST");
            } else if (anno instanceof GetMapping) {
                methods = Collections.singleton("GET");
            } else if (anno instanceof DeleteMapping) {
                methods = Collections.singleton("DELETE");
            } else if (anno instanceof PutMapping) {
                methods = Collections.singleton("PUT");
            } else {
                methods = Collections.emptySet();
            }
            return new SpringMvcRequestMappingInfo(methods, paths, params, headers, consumes, produces);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static ApiOperation getSwaggerApiOperationOnMethod(Method m) {
        return m.getAnnotation(ApiOperation.class);
    }

    public static class SpringMvcRequestMappingInfo {
        private final Set<String> methods;
        private final Set<String> paths;
        private final Set<String> params;
        private final Set<String> headers;
        private final Set<String> consumes;
        private final Set<String> produces;

        public SpringMvcRequestMappingInfo(Set<String> methods, String[] paths, String[] params, String[] headers, String[] consumes, String[] produces) {
            this.methods = methods;
            this.paths = new HashSet<>(Arrays.asList(paths));
            this.params = new HashSet<>(Arrays.asList(params));
            this.headers = new HashSet<>(Arrays.asList(headers));
            this.consumes = new HashSet<>(Arrays.asList(consumes));
            this.produces = new HashSet<>(Arrays.asList(produces));
        }

        public Set<String> getPaths() {
            return paths;
        }

        public Set<String> getParams() {
            return params;
        }

        public Set<String> getHeaders() {
            return headers;
        }

        public Set<String> getConsumes() {
            return consumes;
        }

        public Set<String> getProduces() {
            return produces;
        }

        public Set<String> getMethods() {
            return methods;
        }
    }

    public static String getApiNameAnnoOnClass(Class<?> clazz) {
        Api api = clazz.getAnnotation(Api.class);
        if (Objects.isNull(api)) {
            return clazz.getSimpleName();
        }
        String apiName = api.value();
        if (!StringUtils.hasText(apiName)) {
            apiName = Arrays.stream(api.tags()).findFirst().orElse(clazz.getSimpleName());
        }
        return apiName;
    }

    public static Parameter getRequestBodyParameterOnMethod(Method method) {
        Assert.notNull(method, "method cannot null");
        return Arrays.stream(method.getParameters())
                .filter(p -> Objects.nonNull(p.getAnnotation(RequestBody.class)))
                .findFirst()
                .orElse(null);
    }
}
