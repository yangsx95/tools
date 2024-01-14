package io.github.yangsx95.tools.apidoc.core;

/**
 * @author yangshunxiang
 * @since 2024/1/10
 */
public class ApiInfo {
    private final ApiBaseInfo apiBaseInfo;
    private final ApiOperation apiOperation;

    public ApiInfo(
            ApiBaseInfo apiBaseInfo,
            ApiOperation apiOperation
    ) {
        this.apiBaseInfo = apiBaseInfo;
        this.apiOperation = apiOperation;
    }

    public ApiBaseInfo apiBaseInfo() {
        return apiBaseInfo;
    }

    public ApiOperation apiOperation() {
        return apiOperation;
    }
}
