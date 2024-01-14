package io.github.yangsx95.tools.apidoc.core.model;

/**
 * @author yangshunxiang
 * @since 2024/1/10
 */
public class NumberApiModel extends AbstractApiModel {

    @Override
    public String name() {
        return "number";
    }

    @Override
    public String chineseName() {
        return "数字";
    }

    @Override
    public boolean simpleTypeModel() {
        return true;
    }
}
