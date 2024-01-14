package io.github.yangsx95.tools.apidoc.core.model;

/**
 * 数据模型抽象
 *
 * @author yangshunxiang
 * @since 2024/1/10
 */
public abstract class AbstractApiModel {

    /**
     * 模型名称
     *
     * @return 模型的简略名称
     */
    public abstract String name();

    /**
     * 模型描述
     *
     * @return 针对模型的简介
     */
    public abstract String chineseName();

    /**
     * 是否是简单类型模型
     *
     * @return 是/否
     */
    public abstract boolean simpleTypeModel();

}
