package io.github.yangsx95.tools.apidoc.core.model;

public class EnumApiModel  extends AbstractApiModel{

    @Override
    public String name() {
        return "enum";
    }

    @Override
    public String chineseName() {
        return "枚举";
    }

    @Override
    public boolean simpleTypeModel() {
        return true;
    }

}
