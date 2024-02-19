package io.github.yangsx95.tools.apidoc.core.model;

/**
 * @author yangshunxiang
 * @since 2024/1/10
 */
public class NumberApiModel extends AbstractApiModel {

    private final String name;

    private final String chineseName;

    public NumberApiModel() {
        this.name = "number";
        this.chineseName = "数字";
    }

    public NumberApiModel(String name, String chineseName) {
        this.name = name;
        this.chineseName = chineseName;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public String chineseName() {
        return this.chineseName;
    }

    @Override
    public boolean simpleTypeModel() {
        return true;
    }
}
