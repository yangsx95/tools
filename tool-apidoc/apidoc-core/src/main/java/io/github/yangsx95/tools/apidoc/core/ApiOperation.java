package io.github.yangsx95.tools.apidoc.core;

import java.util.Set;

/**
 * @author yangshunxiang
 * @since 2024/1/10
 */
public class ApiOperation {


    private final String operationName;
    private final String desc;
    private final Set<String> httpMethods;
    private final Set<String> paths;
    private final Set<String> consumes;
    private final Set<String> produces;
    private final ApiRequestBody apiRequestBody;
    private final ApiResponseBody apiResponseBody;

    public ApiOperation(
            String operationName,
            String desc,
            Set<String> httpMethods,
            Set<String> paths,
            Set<String> consumes,
            Set<String> produces,
            ApiRequestBody requestBody,
            ApiResponseBody apiResponseBody
    ) {
        this.operationName = operationName;
        this.desc = desc;
        this.httpMethods = httpMethods;
        this.paths = paths;
        this.consumes = consumes;
        this.produces = produces;
        this.apiRequestBody = requestBody;
        this.apiResponseBody = apiResponseBody;
    }

    public String operationName() {
        return operationName;
    }

    public String desc() {
        return desc;
    }

    public Set<String> httpMethods() {
        return httpMethods;
    }

    public Set<String> paths() {
        return paths;
    }

    public Set<String> consumes() {
        return consumes;
    }

    public Set<String> produces() {
        return produces;
    }

    public ApiRequestBody apiRequestBody() {
        return apiRequestBody;
    }

    public ApiResponseBody apiResponseBody() {
        return apiResponseBody;
    }
}