package io.kimmking.rpcfx.demo.provider;

import io.kimmking.rpcfx.api.RpcfxReflectionResolver;

import java.lang.reflect.InvocationTargetException;

public class ReflectionResolver<T> implements RpcfxReflectionResolver<T> {
    @Override
    public T resolve(String serviceClass) {
        T t = null;
        try {
            Class<?> klass = Class.forName(serviceClass);
            t = (T) klass.getConstructor().newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return t;
    }
}
