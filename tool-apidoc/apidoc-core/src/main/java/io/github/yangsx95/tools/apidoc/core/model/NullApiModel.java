package io.github.yangsx95.tools.apidoc.core.model;

public class NullApiModel extends AbstractApiModel{
    @Override
    public String name() {
        return "null";
    }

    @Override
    public String chineseName() {
        return "空";
    }

    @Override
    public boolean simpleTypeModel() {
        return true;
    }
}
