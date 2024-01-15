package io.github.yangsx95.tools.apidoc.core.model;

public class AnyApiModel extends AbstractApiModel{
    @Override
    public String name() {
        return "any";
    }

    @Override
    public String chineseName() {
        return "任意值";
    }

    @Override
    public boolean simpleTypeModel() {
        return true;
    }
}
