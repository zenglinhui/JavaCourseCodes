package io.kimmking.rpcfx.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;
import io.kimmking.rpcfx.api.RpcfxReflectionResolver;
import io.kimmking.rpcfx.exception.RpcfxException;
import io.kimmking.rpcfx.param.RpcfxRequest;
import io.kimmking.rpcfx.api.RpcfxResolver;
import io.kimmking.rpcfx.param.RpcfxResponse;
import io.kimmking.rpcfx.utils.XStreamUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class RpcfxInvoker {

//    private RpcfxResolver resolver;
//
//    public RpcfxInvoker(RpcfxResolver resolver){
//        this.resolver = resolver;
//    }
    private RpcfxReflectionResolver reflectionResolver;

    public RpcfxInvoker(RpcfxReflectionResolver reflectionResolver) {
        this.reflectionResolver = reflectionResolver;
    }

    public RpcfxResponse invoke(RpcfxRequest request) {
        RpcfxResponse response = new RpcfxResponse();
        String serviceClass = request.getServiceClass();

        // 作业1：改成泛型和反射
        //Object service = resolver.resolve(serviceClass);//this.applicationContext.getBean(serviceClass);
        Object service = reflectionResolver.resolve(serviceClass);

        try {
            Method method = resolveMethodFromClass(service.getClass(), request.getMethod());
            Object result = method.invoke(service, request.getParams()); // dubbo, fastjson,
            // 两次json序列化能否合并成一个
            //response.setResult(JSON.toJSONString(result, SerializerFeature.WriteClassName));
            XStream stream = XStreamUtils.createToJson();
            response.setResult(XStreamUtils.to(stream, result));
            response.setStatus(true);
            return response;
        } catch ( IllegalAccessException | InvocationTargetException e) {

            // 3.Xstream

            // 2.封装一个统一的RpcfxException
            // 客户端也需要判断异常
            e.printStackTrace();
            response.setException(new RpcfxException(e));
            response.setStatus(false);
            return response;
        }
    }

    private Method resolveMethodFromClass(Class<?> klass, String methodName) {
        return Arrays.stream(klass.getMethods()).filter(m -> methodName.equals(m.getName())).findFirst().get();
    }

}
