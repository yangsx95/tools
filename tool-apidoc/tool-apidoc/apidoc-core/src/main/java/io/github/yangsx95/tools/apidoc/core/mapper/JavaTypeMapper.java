package io.github.yangsx95.tools.apidoc.core.mapper;

import cn.hutool.core.bean.BeanUtil;
import io.github.yangsx95.tools.apidoc.core.model.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 根据java类型映射数据Model
 *
 * @author yangshunxiang
 * @since 2024/1/11
 */
public class JavaTypeMapper {

    public AbstractApiModel map(Type type, ApiModelInfoSupply supply, Type ownerObjectType) {
        if (type == null) {
            throw new NullPointerException("type cannot null");
        }

        // 如果type没有泛型
        if (type instanceof Class<?> clazz) {
            if (CharSequence.class.isAssignableFrom(clazz)) {
                return new StringApiModel();
            } else if (Number.class.isAssignableFrom(clazz)
                    || int.class == clazz
                    || long.class == clazz
                    || short.class == clazz
                    || float.class == clazz
                    || double.class == clazz) {
                return new NumberApiModel();
            } else if (clazz.isArray()) {
                return new ArrayApiModel(map(clazz.getComponentType(), supply, type));
            }
            // 如果是Bean就对该对象进行解析
            else if (BeanUtil.isBean(clazz)) {
                List<ObjectApiModel.Property> properties = getObjectProperties(clazz.getDeclaredFields(), supply, type);
                ApiModelInfoSupply.ApiModelBasicInfo info = supply.getModelInfo(clazz);
                return new ObjectApiModel(info.name(), info.chineseName(), properties);
            }
        }
        // 如果type有泛型
        else if (type instanceof ParameterizedType) {
            Class<?> clazz = (Class<?>) ((ParameterizedType) type).getRawType();
            // 如果是集合类型
            if (Collection.class.isAssignableFrom(clazz)) {
                Type typeArg = ((ParameterizedType) type).getActualTypeArguments()[0];
                return new ArrayApiModel(map(typeArg, supply, ownerObjectType));
            }
            // 不是集合类型，就是带有泛型的对象类型
            else {
                List<ObjectApiModel.Property> properties = getObjectProperties(clazz.getDeclaredFields(), supply, type);
                ApiModelInfoSupply.ApiModelBasicInfo info = supply.getModelInfo(clazz);
                return new ObjectApiModel(info.name(), info.chineseName(), properties);
            }
        }
        // 如果type本身就是个泛型
        else if (type instanceof TypeVariable<?>) {
            // 找到泛型所在的下标
            int idx = 0;
            for (int i = 0; i < ((TypeVariable<GenericDeclaration>) type).getGenericDeclaration().getTypeParameters().length; i++) {
                if (Objects.equals(((TypeVariable<GenericDeclaration>) type).getGenericDeclaration().getTypeParameters()[i].getName(), ((TypeVariable<?>) type).getName())) {
                    idx = i;
                    break;
                }
            }
            // 实际处理的是真实的类型
            return map(((ParameterizedType) ownerObjectType).getActualTypeArguments()[idx], supply, null);
        }
        return null;
    }

    private List<ObjectApiModel.Property> getObjectProperties(Field[] clazz, ApiModelInfoSupply supply, Type type) {
        return Arrays.stream(clazz).map(f ->
                new ObjectApiModel.Property(
                        supply.getModelPropertyInfo(f).name(),
                        map(f.getGenericType(), supply, type),
                        supply.getModelPropertyInfo(f).chineseName())
        ).collect(Collectors.toList());
    }
}
