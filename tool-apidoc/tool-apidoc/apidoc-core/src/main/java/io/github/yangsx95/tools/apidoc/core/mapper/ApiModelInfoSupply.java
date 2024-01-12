package io.github.yangsx95.tools.apidoc.core.mapper;

import java.lang.reflect.Field;

/**
 * @author yangshunxiang
 * @since 2024/1/11
 */
public interface ApiModelInfoSupply {

    record ApiModelBasicInfo(String name, String chineseName) {

    }

    record ApiModelPropertyBasicInfo(String name, String chineseName) {

    }

    ApiModelBasicInfo getModelInfo(Class<?> clazz);

    ApiModelPropertyBasicInfo getModelPropertyInfo(Field field);
}
