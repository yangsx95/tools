package io.github.yangsx95.tools.apidoc.core.model;

import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

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

    public record Property(String propertyName, AbstractApiModel model, String propertyChineseName) {
    }
}
