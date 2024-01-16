package io.github.yangsx95.tools.apidoc.core.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class BeanProperty {

    private final Method getter;

    private final Method setter;

    private final Field field;

    public BeanProperty(Field field, Method getter, Method setter) {
        this.getter = getter;
        this.setter = setter;
        this.field = field;
    }

    public Method getter() {
        return getter;
    }

    public Method setter() {
        return setter;
    }

    public Field field() {
        return field;
    }


    public Type getGenericType() {
        return this.getter.getGenericReturnType();
    }

    public String propertyName() {
        return ReflectUtil.getFieldNameFromGetterMethod(getter);
    }
}