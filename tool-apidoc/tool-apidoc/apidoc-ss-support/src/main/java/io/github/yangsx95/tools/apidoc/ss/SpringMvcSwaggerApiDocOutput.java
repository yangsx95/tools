package io.github.yangsx95.tools.apidoc.ss;

import cn.hutool.core.collection.CollectionUtil;
import io.github.yangsx95.tools.apidoc.core.ApiBaseInfo;
import io.github.yangsx95.tools.apidoc.core.ApiInfo;
import io.github.yangsx95.tools.apidoc.core.ApiOperation;
import io.github.yangsx95.tools.apidoc.core.exportor.ApiDocOutput;

import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author yangshunxiang
 * @since 2024/1/12
 */
public class SpringMvcSwaggerApiDocOutput implements ApiDocOutput {

    @Override
    public String output(List<ApiInfo> apiInfoList) {

        MdKiller.SectionBuilder builder = MdKiller.of();

        Map<ApiBaseInfo, List<ApiInfo>> apiGroup = apiInfoList.stream().collect(Collectors.groupingBy(ApiInfo::apiBaseInfo));
        apiGroup.forEach((apiBaseInfo, apiInfos) -> {
            apiInfos.forEach(apiInfo -> {
                ApiOperation operation = apiInfo.apiOperation();
                builder.bigTitle("接口名称:" + operation.operationName())
                        .br()
                        .text("接口URL：" + apiBaseInfo.baseUrl() + CollectionUtil.join(operation.paths(), ", "));
            });
        });

        return builder.build();
    }

}
