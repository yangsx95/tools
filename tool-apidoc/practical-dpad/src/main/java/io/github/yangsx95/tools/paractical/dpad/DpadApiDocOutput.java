package io.github.yangsx95.tools.paractical.dpad;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import io.github.yangsx95.tools.apidoc.core.ApiBaseInfo;
import io.github.yangsx95.tools.apidoc.core.ApiInfo;
import io.github.yangsx95.tools.apidoc.core.ApiOperation;
import io.github.yangsx95.tools.apidoc.core.exportor.ApiDocOutput;
import io.github.yangsx95.tools.apidoc.core.model.AbstractApiModel;
import io.github.yangsx95.tools.apidoc.core.model.ArrayApiModel;
import io.github.yangsx95.tools.apidoc.core.model.ObjectApiModel;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author yangshunxiang
 * @since 2024/1/12
 */
public class DpadApiDocOutput implements ApiDocOutput {

    @Override
    public String output(List<ApiInfo> apiInfoList) {

        MdKiller.SectionBuilder builder = MdKiller.of();
        builder.text("[TOC]");
        builder.text("");
        Map<ApiBaseInfo, List<ApiInfo>> apiGroup = apiInfoList.stream().collect(Collectors.groupingBy(ApiInfo::apiBaseInfo));
        apiGroup.forEach((apiBaseInfo, apiInfos) -> {
            apiInfos.forEach(apiInfo -> {
                ApiOperation operation = apiInfo.apiOperation();
                builder.bigTitle("接口名称:" + operation.operationName())
                        .text("接口URL：" + apiBaseInfo.baseUrl() + CollectionUtil.join(operation.paths(), ",") + "  ")
                        .text("请求方式：" + CollectionUtil.join( apiInfo.apiOperation().httpMethods(), ",") + "  ")
                        .text("使用场景：  ")
                ;
                AbstractApiModel requestModel = operation.apiRequestBody().modelType();
                if (Objects.nonNull(requestModel)) {
                    Set<String> cache = new HashSet<>();
                    // 请求体是一个简单类型
                    if (requestModel.simpleTypeModel()) {
                        builder.table()
                                .data(new Object[]{"参数名", "参数类型", "必填", "描述", "是否在用"}, new Object[][]{{"请求体", requestModel.name(), "是", requestModel.chineseName(), "是"}})
                                .endTable();
                    }
                    // 请求体是一个数组，暂时不考虑
                    else if (requestModel instanceof ArrayApiModel) {
                    } else if (requestModel instanceof ObjectApiModel) {
                        writeTable(requestModel, builder, true, true, cache);
                    }
                }

                AbstractApiModel responseModel = operation.apiResponseBody().modelType();
                if (Objects.nonNull(responseModel)) {
                    Set<String> cache = new HashSet<>();
                    // 响应体是一个简单类型
                    if (responseModel.simpleTypeModel()) {
                        builder.table()
                                .data(new Object[]{"参数名", "参数类型", "必填", "描述", "是否在用"}, new Object[][]{{"请求体", requestModel.name(), "是", requestModel.chineseName(), "是"}})
                                .endTable();
                    }
                    // 请求体是一个数组，暂时不考虑
                    else if (responseModel instanceof ArrayApiModel) {
                    } else if (responseModel instanceof ObjectApiModel) {
                        writeTable(responseModel, builder, true, false, cache);
                    }
                }

            });
        });

        return builder.build();
    }

    private void writeTable(AbstractApiModel model, MdKiller.SectionBuilder builder, boolean isRoot, boolean isReq, Set<String> cache) {
        if (model.simpleTypeModel()) {
            return;
        }
        if (model instanceof ArrayApiModel) {
            writeTable(((ArrayApiModel) model).elementApiModel(), builder, false, isReq, cache);
            return;
        }

        if (cache.contains(model.name())) {
            return;
        }

        if (model instanceof ObjectApiModel) {
            List<ObjectApiModel.Property> ps = ((ObjectApiModel) model).properties();
            Object[][] data = new Object[ps.size()][5];
            for (int i = 0; i < ps.size(); i++) {
                ObjectApiModel.Property p = ps.get(i);
                data[i] = new Object[]{p.propertyName(), p.model().name(), "是", p.propertyChineseName(), "是"};
            }

            if (isRoot) {
                builder.title(isReq ? "请求体" : "响应体");
            } else {
                builder.subTitle(model.name());
            }

            builder.table()
                    .data(new Object[]{"参数名", "字段类型", "必填", "描述", "是否在用"}, data)
                    .endTable();

            ps.stream().filter(property -> !property.model().simpleTypeModel())
                    .forEach(property -> writeTable(property.model(), builder, false, isReq, cache));

            cache.add(model.name());
        }

    }
}
