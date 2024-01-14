package io.github.yangsx95.tools.apidoc.core.model;

/**
 * @author yangshunxiang
 * @since 2024/1/10
 */
public class ArrayApiModel extends AbstractApiModel{

    private final AbstractApiModel elementApiModel;

    public ArrayApiModel(AbstractApiModel elementApiModel) {
        this.elementApiModel = elementApiModel;
    }

    @Override
    public String name() {
        return "array<" + elementApiModel.name() + ">";
    }

    @Override
    public String chineseName() {
        return "数组";
    }

    @Override
    public boolean simpleTypeModel() {
        return false;
    }

    public AbstractApiModel elementApiModel() {
        return this.elementApiModel;
    }
}
