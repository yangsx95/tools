package io.github.yangsx95.tools.apidoc.core.model;

public class DateApiModel  extends AbstractApiModel{

    @Override
    public String name() {
        return "date";
    }

    @Override
    public String chineseName() {
        return "日期";
    }

    @Override
    public boolean simpleTypeModel() {
        return true;
    }

}
