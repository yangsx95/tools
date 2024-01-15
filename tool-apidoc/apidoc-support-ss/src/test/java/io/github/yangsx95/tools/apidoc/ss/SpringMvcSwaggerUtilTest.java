package io.github.yangsx95.tools.apidoc.ss;

import io.swagger.annotations.Api;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.Annotation;

/**
 * @author yangshunxiang
 * @since 2024/1/10
 */
class SpringMvcSwaggerUtilTest {

    @Test
    void isAliasForRequestMapping() {
        Assertions.assertTrue(SpringMvcSwaggerUtil.isRequestMapping(RequestMapping.class));
        Assertions.assertTrue(SpringMvcSwaggerUtil.isRequestMapping(PostMapping.class));
        Assertions.assertTrue(SpringMvcSwaggerUtil.isRequestMapping(GetMapping.class));
        Assertions.assertTrue(SpringMvcSwaggerUtil.isRequestMapping(DeleteMapping.class));
        Assertions.assertTrue(SpringMvcSwaggerUtil.isRequestMapping(PutMapping.class));
        Assertions.assertFalse(SpringMvcSwaggerUtil.isRequestMapping(Deprecated.class));
    }

    @Test
    void getRequestMappingAnnoOnMethod() throws Exception {
        Assertions.assertNotNull(SpringMvcSwaggerUtil.getRequestMappingAnnoOnMethod(A.class.getMethod("method1")));
        Assertions.assertNotNull(SpringMvcSwaggerUtil.getRequestMappingAnnoOnMethod(A.class.getMethod("method2")));
        Assertions.assertNull(SpringMvcSwaggerUtil.getRequestMappingAnnoOnMethod(A.class.getMethod("method3")));
    }

    @Test
    void methodHasRequestMappingAnno() throws Exception {
        Assertions.assertTrue(SpringMvcSwaggerUtil.methodHasRequestMappingAnno(A.class.getMethod("method1")));
        Assertions.assertTrue(SpringMvcSwaggerUtil.methodHasRequestMappingAnno(A.class.getMethod("method2")));
        Assertions.assertFalse(SpringMvcSwaggerUtil.methodHasRequestMappingAnno(A.class.getMethod("method3")));
    }

    @Api(value = "类A", tags = "类A的tag")
    @RequestMapping("/classA")
    public static class A {
        @RequestMapping("/method1")
        public void method1() {
        }

        @PostMapping("/method2")
        public void method2() {
        }

        public void method3() {
        }
    }

    @Api(tags = "类B的tag")
    public static class B {
    }

    public static class C {
    }

    @Test
    void getRequestMappingAnnoOnClass() {
        Assertions.assertNotNull(SpringMvcSwaggerUtil.getRequestMappingAnnoOnClass(A.class));
        Assertions.assertNull(SpringMvcSwaggerUtil.getRequestMappingAnnoOnClass(B.class));
    }

    @Test
    void getRequestMappingAnnoInfo() throws Exception {
        Annotation anno = SpringMvcSwaggerUtil.getRequestMappingAnnoOnMethod(A.class.getMethod("method1"));
        SpringMvcSwaggerUtil.SpringMvcRequestMappingInfo info = SpringMvcSwaggerUtil.getRequestMappingAnnoInfo(anno);
        Assertions.assertEquals(1, info.getPaths().size());
        Assertions.assertEquals(info.getPaths().iterator().next(), "/method1");
    }

    @Test
    void getApiNameAnnoOnClass() {
        Assertions.assertEquals(SpringMvcSwaggerUtil.getApiNameAnnoOnClass(A.class), "类A");
        Assertions.assertEquals(SpringMvcSwaggerUtil.getApiNameAnnoOnClass(B.class), "类B的tag");
        Assertions.assertEquals(SpringMvcSwaggerUtil.getApiNameAnnoOnClass(C.class), C.class.getSimpleName());
    }
}