package io.github.yangsx95.tools.apidoc.core.util;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ArrayUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

public class ReflectUtil {

    public static boolean hasGetter(Class<?> clazz) {
        if (clazz == null) {
            return false;
        }

        if (clazz.isEnum()) {
            return false;
        }

        if (clazz.isArray()) {
            return false;
        }

        if (clazz.isAnnotation()) {
            return false;
        }

        if (clazz.isPrimitive()) {
            return false;
        }

        if (clazz.isSynthetic()) {
            return false;
        }

        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if (isGetterMethod(method)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isGetterMethod(Method method) {
        return method.getName().startsWith("get")
                && method.getName().length() > 3
                && !Modifier.isStatic(method.getModifiers())
                && method.getParameterCount() == 0
                && method.getReturnType() != void.class
                && method.getReturnType() != Void.class
                && !Objects.equals(method.getName(), "getClass");
    }

    public static List<BeanProperty> getBeanProperties(Class<?> clazz) {
        if (Objects.isNull(clazz)) {
            throw new NullPointerException("class can not null!");
        }

        // 至少拥有一个getter 才算是一个bean
        if (!hasGetter(clazz)) {
            return Collections.emptyList();
        }

        // 获取所有的字段
        Field[] fields = getFieldsDirectly(clazz, true);
        if (fields.length == 0) {
            return Collections.emptyList();
        }

        Map<String, Field> fieldMap = new HashMap<>();
        for (Field field : fields) {
            fieldMap.put(field.getName(), field);
        }

        return Arrays.stream(clazz.getMethods())
                .filter(ReflectUtil::isGetterMethod)
                .map(m -> new BeanProperty(fieldMap.get( getFieldNameFromGetterMethod(m)), m, null))
                .collect(Collectors.toList());
    }

    public static Field[] getFieldsDirectly(Class<?> beanClass, boolean withSuperClassFields) throws SecurityException {
        Assert.notNull(beanClass);
        Field[] allFields = null;

        for (Class<?> searchType = beanClass; searchType != null; searchType = withSuperClassFields ? searchType.getSuperclass() : null) {
            Field[] declaredFields = searchType.getDeclaredFields();
            if (null == allFields) {
                allFields = declaredFields;
            } else {
                allFields = ArrayUtil.append(allFields, declaredFields);
            }
        }

        return allFields;
    }


    public static String getFieldNameFromGetterMethod(Method method) {
        if (!isGetterMethod(method)) {
            throw new IllegalArgumentException("this method is not a getter");
        }

        String methodName = method.getName();
        String fieldName = methodName.substring(3); // 去除 "get"，获取剩余部分
        fieldName = fieldName.substring(0, 1).toLowerCase() + fieldName.substring(1); // 将首字母转换为小写
        return fieldName;
    }

}
