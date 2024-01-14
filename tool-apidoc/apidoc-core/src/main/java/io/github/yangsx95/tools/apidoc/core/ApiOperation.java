package io.github.yangsx95.tools.apidoc.core;

import java.util.Set;

/**
 * @author yangshunxiang
 * @since 2024/1/10
 */
public record ApiOperation(String operationName,
                           String desc,
                           Set<String> httpMethods,
                           Set<String> paths,
                           Set<String> consumes,
                           Set<String> produces,
                           ApiRequestBody requestBody,
                           ApiResponseBody apiResponseBody
) {
}
