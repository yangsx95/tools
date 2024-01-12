package io.github.yangsx95.tools.apidoc.ss;

import io.github.yangsx95.tools.apidoc.core.mapper.ApiModelInfoSupply;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Optional;

/**
 * @author yangshunxiang
 * @since 2024/1/12
 */
public class ApiModelInfoFromClassNameAndSwaggerApiModelSupply implements ApiModelInfoSupply {

    private static ApiModelInfoFromClassNameAndSwaggerApiModelSupply instance = null;

    public static ApiModelInfoFromClassNameAndSwaggerApiModelSupply getInstance() {
        if (Objects.isNull(instance)) {
            instance = new ApiModelInfoFromClassNameAndSwaggerApiModelSupply();
        }
        return instance;
    }

    @Override
    public ApiModelBasicInfo getModelInfo(Class<?> clazz) {
        return new ApiModelInfoSupply.ApiModelBasicInfo(clazz.getSimpleName(), Optional.ofNullable(clazz.getAnnotation(ApiModel.class)).map(ApiModel::value).orElse(clazz.getSimpleName()));
    }

    @Override
    public ApiModelPropertyBasicInfo getModelPropertyInfo(Field field) {
        ApiModelProperty[] properties = field.getAnnotationsByType(ApiModelProperty.class);
        String chineseName;
        if (properties.length == 0) {
            chineseName = field.getName();
        } else {
            chineseName = properties[0].value();
        }

        return new ApiModelPropertyBasicInfo(field.getName(), chineseName);
    }
}
