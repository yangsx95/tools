package io.github.yangsx95.tools.apidoc.ss;

import io.github.yangsx95.tools.apidoc.core.mapper.ApiModelInfoSupply;
import io.github.yangsx95.tools.apidoc.core.util.BeanProperty;
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
    public ApiModelPropertyBasicInfo getModelPropertyInfo(BeanProperty beanProperty) {
        // 先从getter中取
        ApiModelProperty[] annos = new ApiModelProperty[0];
        if (Objects.nonNull(beanProperty.getter())) {
            annos = beanProperty.getter().getAnnotationsByType(ApiModelProperty.class);
        }

        if (Objects.nonNull(beanProperty.field()) &&  annos.length == 0) {
            annos = beanProperty.field().getAnnotationsByType(ApiModelProperty.class);
        }

        String propertyName = beanProperty.propertyName();
        String chineseName = propertyName;
        if (annos.length > 0) {
            chineseName = annos[0].value();
        }

        return new ApiModelPropertyBasicInfo(propertyName, chineseName);
    }
}
