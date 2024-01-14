package io.github.yangsx95.tools.apidoc.core;

import io.github.yangsx95.tools.apidoc.core.model.AbstractApiModel;

/**
 * @author yangshunxiang
 * @since 2024/1/10
 */
public class ApiResponseBody {
    private final String contentType;
    private final AbstractApiModel modelType;

    public ApiResponseBody(String contentType, AbstractApiModel modelType) {
        this.contentType = contentType;
        this.modelType = modelType;
    }

    public String contentType() {
        return contentType;
    }

    public AbstractApiModel modelType() {
        return modelType;
    }
}
