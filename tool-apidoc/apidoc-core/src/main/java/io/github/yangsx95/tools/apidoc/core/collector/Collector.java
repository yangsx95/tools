package io.github.yangsx95.tools.apidoc.core.collector;

import io.github.yangsx95.tools.apidoc.core.ApiInfo;
import io.github.yangsx95.tools.apidoc.core.ApiOperation;

import java.util.List;
import java.util.Set;

/**
 * @author yangshunxiang
 * @since 2024/1/10
 */
public interface Collector<S extends CollectorSource> {

    List<ApiInfo> collect(S collectorSource);

}
