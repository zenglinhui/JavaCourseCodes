package io.kimmking.rpcfx.demo.provider;

import io.kimmking.rpcfx.api.RpcfxReflectionResolver;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.InvocationTargetException;

public class ReflectionResolver<T> implements RpcfxReflectionResolver<T>, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public T resolve(String serviceClass) {
        T t = null;
        try {
            Class<?> klass = Class.forName(serviceClass);
            t = (T) applicationContext.getBean(klass);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return t;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
