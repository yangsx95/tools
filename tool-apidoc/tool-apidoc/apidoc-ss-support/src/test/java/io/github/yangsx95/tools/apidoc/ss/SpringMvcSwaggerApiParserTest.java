package io.github.yangsx95.tools.apidoc.ss;

import io.github.yangsx95.tools.apidoc.core.ApiBaseInfo;
import io.github.yangsx95.tools.apidoc.core.ApiRequestBody;
import io.github.yangsx95.tools.apidoc.core.ApiResponseBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author yangshunxiang
 * @since 2024/1/11
 */
class SpringMvcSwaggerApiParserTest {

    public static class AClass {
        public void aMethod() {
        }
    }

    @RequestMapping("/bClass")
    @Api(tags = "B类API")
    public static class BClass {
        @RequestMapping("/bMethod")
        public void bMethod() {
        }

        @RequestMapping("b2Method")
        public Void b2Method(@RequestBody String rb) {
            return null;
        }

        @RequestMapping("b3Method")
        public void b3Method(@RequestBody List<String> list) {
        }

        @RequestMapping("b4Method")
        public void b4Method(@RequestBody String[] arrays) {
        }

        @RequestMapping("b5Method")
        public void b5Method(@RequestBody Person person) {
        }

        @RequestMapping("b6Method")
        public R<String> b6Method() {
            return null;
        }

        @RequestMapping("b7Method")
        public R<List<String>> b7Method() {
            return null;
        }

        @RequestMapping("b8Method")
        public R<Page<Person>> b8Method() {
            return null;
        }

        @PostMapping("b9Method")
        @ApiOperation("B类第9个方法")
        public R<Page<Person>> b9Method(@RequestBody Person person) {
            return null;
        }

        @ApiModel("人")
        public static class Person {
            @ApiModelProperty("姓名")
            private String name;
            @ApiModelProperty("年龄")
            private Integer age;
            @ApiModelProperty("爱好")
            private List<String> hobbies;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public Integer getAge() {
                return age;
            }

            public void setAge(Integer age) {
                this.age = age;
            }

            public List<String> getHobbies() {
                return hobbies;
            }

            public void setHobbies(List<String> hobbies) {
                this.hobbies = hobbies;
            }
        }

        public static class R<S> {
            private Integer code;

            private String msg;

            private S data;

            public Integer getCode() {
                return code;
            }

            public void setCode(Integer code) {
                this.code = code;
            }

            public String getMsg() {
                return msg;
            }

            public void setMsg(String msg) {
                this.msg = msg;
            }

            public S getData() {
                return data;
            }

            public void setData(S data) {
                this.data = data;
            }
        }

        public static class Page<P>  {

            @ApiModelProperty("分页条数")
            private int pageSize;

            @ApiModelProperty("分页数据")
            private List<P> records;

            public int getPageSize() {
                return pageSize;
            }

            public void setPageSize(int pageSize) {
                this.pageSize = pageSize;
            }

            public List<P> getRecords() {
                return records;
            }

            public void setRecords(List<P> records) {
                this.records = records;
            }
        }
    }

    @Test
    void parseApiBaseInfoFromMethod() throws NoSuchMethodException {
        ApiBaseInfo aApiBaseInfo = SpringMvcSwaggerApiParser.parseApiBaseInfoFromMethod(AClass.class.getMethod("aMethod"), new SpringMvcSwaggerCollectContext());
        Assertions.assertNotNull(aApiBaseInfo);
        Assertions.assertEquals(aApiBaseInfo.baseUrl(), "");
        Assertions.assertEquals(aApiBaseInfo.apiName(), AClass.class.getSimpleName());

        ApiBaseInfo bApiBaseInfo = SpringMvcSwaggerApiParser.parseApiBaseInfoFromMethod(BClass.class.getMethod("bMethod"), new SpringMvcSwaggerCollectContext());
        Assertions.assertNotNull(bApiBaseInfo);
        Assertions.assertEquals(bApiBaseInfo.baseUrl(), "/bClass");
        Assertions.assertEquals(bApiBaseInfo.apiName(), "B类API");
    }

    @Test
    void parseApiBaseInfoFromMethod2() throws NoSuchMethodException {
//        ApiRequestBody requestBody = SpringMvcSwaggerApiParser.parseApiRequestBodyFromMethod(BClass.class.getMethod("b2Method", String.class), new SpringMvcSwaggerCollectContext());
//        ApiRequestBody requestBody = SpringMvcSwaggerApiParser.parseApiRequestBodyFromMethod(BClass.class.getMethod("b4Method", String[].class), new SpringMvcSwaggerCollectContext());
        ApiRequestBody requestBody = SpringMvcSwaggerApiParser.parseApiRequestBodyFromMethod(BClass.class.getMethod("b5Method", BClass.Person.class), new SpringMvcSwaggerCollectContext());
        System.out.println(requestBody);
    }

    @Test
    void parseApiRequestBodyFromMethodWhenReturnVoid() throws NoSuchMethodException{
        ApiResponseBody responseBody = SpringMvcSwaggerApiParser.parseApiResponseBodyFromMethod(BClass.class.getMethod("bMethod"), new SpringMvcSwaggerCollectContext());
        Assertions.assertNull(responseBody);
        responseBody = SpringMvcSwaggerApiParser.parseApiResponseBodyFromMethod(BClass.class.getMethod("b2Method", String.class), new SpringMvcSwaggerCollectContext());
        Assertions.assertNull(responseBody);
    }

    @Test
    void parseApiRequestBodyFromMethodWhenReturnGeneric() throws NoSuchMethodException{
        ApiResponseBody responseBody = SpringMvcSwaggerApiParser.parseApiResponseBodyFromMethod(BClass.class.getMethod("b6Method"), new SpringMvcSwaggerCollectContext());
        System.out.println(responseBody);
    }

    @Test
    void parseApiRequestBodyFromMethodWhenReturnGeneric2() throws NoSuchMethodException{
        ApiResponseBody responseBody = SpringMvcSwaggerApiParser.parseApiResponseBodyFromMethod(BClass.class.getMethod("b7Method"), new SpringMvcSwaggerCollectContext());
        System.out.println(responseBody);
    }

    @Test
    void parseApiRequestBodyFromMethodWhenReturnGeneric3() throws NoSuchMethodException {
        ApiResponseBody responseBody = SpringMvcSwaggerApiParser.parseApiResponseBodyFromMethod(BClass.class.getMethod("b8Method"), new SpringMvcSwaggerCollectContext());
        System.out.println(responseBody);
    }

    @Test
    void parseApiOperationFromMethod() throws NoSuchMethodException {
        io.github.yangsx95.tools.apidoc.core.ApiOperation apiOperation = SpringMvcSwaggerApiParser.parseApiOperationFromMethod(BClass.class.getMethod("b9Method", BClass.Person.class), new SpringMvcSwaggerCollectContext());
        System.out.println(apiOperation);

    }

}