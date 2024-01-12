package io.github.yangsx95.tools.apidoc.core.model;

/**
 * @author yangshunxiang
 * @since 2024/1/10
 */
public class StringApiModel extends AbstractApiModel {

    @Override
    public String name() {
        return "string";
    }

    @Override
    public String chineseName() {
        return "字符串";
    }

    @Override
    public boolean simpleTypeModel() {
        return true;
    }

}
