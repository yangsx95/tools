package io.github.yangsx95.tools.apidoc.core;

/**
 * @author yangshunxiang
 * @since 2024/1/10
 */
public class ApiBaseInfo {

    private final String apiName;

    private final String baseUrl;

    public ApiBaseInfo(String apiName, String baseUrl) {
        this.apiName = apiName;
        this.baseUrl = baseUrl;
    }

    public String apiName() {
        return apiName;
    }

    public String baseUrl() {
        return baseUrl;
    }
}