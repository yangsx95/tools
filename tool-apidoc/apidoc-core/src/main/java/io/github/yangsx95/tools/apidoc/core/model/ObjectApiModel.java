package io.github.yangsx95.tools.apidoc.core.model;

import java.util.List;

/**
 * @author yangshunxiang
 * @since 2024/1/10
 */
public class ObjectApiModel extends AbstractApiModel {

    private final List<Property> properties;

    private final String name;

    private final String chineseName;

    public ObjectApiModel(String name, String chineseName, List<Property> propertiesApiModels) {
        this.name = name;
        this.chineseName = chineseName;
        this.properties = propertiesApiModels;
    }

    public ObjectApiModel(List<Property> properties) {
        this("object", "对象", properties);
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String chineseName() {
        return chineseName;
    }

    @Override
    public boolean simpleTypeModel() {
        return false;
    }

    public List<Property> properties() {
        return properties;
    }

    public static class Property {
        private final String propertyName;
        private final AbstractApiModel model;
        private final String propertyChineseName;

        public Property(String propertyName, AbstractApiModel model, String propertyChineseName) {

            this.propertyName = propertyName;
            this.model = model;
            this.propertyChineseName = propertyChineseName;
        }

        public String propertyName() {
            return propertyName;
        }

        public AbstractApiModel model() {
            return model;
        }

        public String propertyChineseName() {
            return propertyChineseName;
        }
    }
}
