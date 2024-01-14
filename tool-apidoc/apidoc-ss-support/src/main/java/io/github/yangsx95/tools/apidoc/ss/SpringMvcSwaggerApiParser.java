package io.github.yangsx95.tools.apidoc.ss;

import io.github.yangsx95.tools.apidoc.core.ApiBaseInfo;
import io.github.yangsx95.tools.apidoc.core.ApiOperation;
import io.github.yangsx95.tools.apidoc.core.ApiRequestBody;
import io.github.yangsx95.tools.apidoc.core.ApiResponseBody;
import io.github.yangsx95.tools.apidoc.core.mapper.JavaTypeMapper;
import io.github.yangsx95.tools.apidoc.core.model.AbstractApiModel;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

/**
 * @author yangshunxiang
 * @since 2024/1/11
 */
public class SpringMvcSwaggerApiParser {

    /**
     * 生成方法的API信息
     *
     * @param m       方法
     * @param context 上下文
     * @return api 操作信息
     */
    public static ApiBaseInfo parseApiBaseInfoFromMethod(Method m, SpringMvcSwaggerCollectContext context) {
        Class<?> controllerClass = m.getDeclaringClass();
        ApiBaseInfo apiBaseInfo = context.getApiInfo(controllerClass);
        if (Objects.isNull(apiBaseInfo)) {
            String baseUrl = Optional.ofNullable(SpringMvcSwaggerUtil.getRequestMappingAnnoOnClass(controllerClass))
                    .map(SpringMvcSwaggerUtil::getRequestMappingAnnoInfo)
                    .map(SpringMvcSwaggerUtil.SpringMvcRequestMappingInfo::getPaths)
                    .orElse(Collections.emptySet())
                    .stream()
                    .findFirst()
                    .orElse("");
            String apiName = SpringMvcSwaggerUtil.getApiNameAnnoOnClass(controllerClass);
            apiBaseInfo = new ApiBaseInfo(apiName, baseUrl);
            context.putApiInfo(controllerClass, apiBaseInfo);
        }
        return apiBaseInfo;
    }

    public static ApiOperation parseApiOperationFromMethod(Method m, SpringMvcSwaggerCollectContext context) {
        io.swagger.annotations.ApiOperation swaggerApiOperation = SpringMvcSwaggerUtil.getSwaggerApiOperationOnMethod(m);
        Assert.notNull(swaggerApiOperation, "不可能出现的情况，swaggerApiOperation为空");
        SpringMvcSwaggerUtil.SpringMvcRequestMappingInfo info = SpringMvcSwaggerUtil.getRequestMappingAnnoInfo(SpringMvcSwaggerUtil.getRequestMappingAnnoOnMethod(m));
        ApiRequestBody requestBody = parseApiRequestBodyFromMethod(m, context);
        ApiResponseBody responseBody = parseApiResponseBodyFromMethod(m, context);
        return new ApiOperation(swaggerApiOperation.value(), swaggerApiOperation.notes(), info.getMethods(), info.getPaths(), info.getConsumes(), info.getProduces(), requestBody, responseBody);
    }

    public static ApiRequestBody parseApiRequestBodyFromMethod(Method m, SpringMvcSwaggerCollectContext context) {
        Parameter parameter = SpringMvcSwaggerUtil.getRequestBodyParameterOnMethod(m);
        if (Objects.isNull(parameter)) {
            return null;
        }
        JavaTypeMapper javaTypeMapper = new JavaTypeMapper();
        AbstractApiModel model = javaTypeMapper.map(
                parameter.getParameterizedType(),
                ApiModelInfoFromClassNameAndSwaggerApiModelSupply.getInstance(),
                null
        );
        return new ApiRequestBody("application/json", model);
    }


    public static ApiResponseBody parseApiResponseBodyFromMethod(Method m, SpringMvcSwaggerCollectContext context) {
        Class<?> returnType = m.getReturnType();
        if (returnType == void.class || returnType == Void.class) {
            return null;
        }
        JavaTypeMapper javaTypeMapper = new JavaTypeMapper();

        AbstractApiModel model = javaTypeMapper.map(m.getGenericReturnType(), ApiModelInfoFromClassNameAndSwaggerApiModelSupply.getInstance(), null);
        return new ApiResponseBody("application/json", model);
    }
}
