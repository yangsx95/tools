package io.github.yangsx95.tools.apidoc.core.mapper;

import java.lang.reflect.Field;

/**
 * @author yangshunxiang
 * @since 2024/1/11
 */
public interface ApiModelInfoSupply {

    class ApiModelBasicInfo {
        private final String name;
        private final String chineseName;

        public ApiModelBasicInfo(String name, String chineseName) {
            this.name = name;
            this.chineseName = chineseName;
        }

        public String name() {
            return name;
        }

        public String chineseName() {
            return chineseName;
        }
    }

    class ApiModelPropertyBasicInfo {
        private final String name;
        private final String chineseName;

        public ApiModelPropertyBasicInfo(String name, String chineseName) {
            this.name = name;
            this.chineseName = chineseName;
        }

        public String name() {
            return name;
        }

        public String chineseName() {
            return chineseName;
        }
    }

    ApiModelBasicInfo getModelInfo(Class<?> clazz);

    ApiModelPropertyBasicInfo getModelPropertyInfo(Field field);
}
