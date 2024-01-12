package io.github.yangsx95.tools.apidoc.core;

import io.github.yangsx95.tools.apidoc.core.model.AbstractApiModel;

/**
 * @author yangshunxiang
 * @since 2024/1/10
 */
public record ApiResponseBody(String contentType, AbstractApiModel modelType) {
}
