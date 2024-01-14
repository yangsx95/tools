package io.github.yangsx95.tools.apidoc.core.exportor;

import io.github.yangsx95.tools.apidoc.core.ApiInfo;
import io.github.yangsx95.tools.apidoc.core.ApiOperation;

import java.io.OutputStream;
import java.util.List;

/**
 * 文档输出
 *
 * @author yangshunxiang
 * @since 2024/1/12
 */
public interface ApiDocOutput {

    String output(List<ApiInfo> apiInfoList);

}
