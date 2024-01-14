package io.github.yangsx95.tools.apidoc.core.model;

/**
 * @author yangshunxiang
 * @since 2024/1/10
 */

public class BooleanApiModel extends AbstractApiModel {

    @Override
    public String name() {
        return "boolean";
    }

    @Override
    public String chineseName() {
        return "布尔";
    }

    @Override
    public boolean simpleTypeModel() {
        return true;
    }

}
