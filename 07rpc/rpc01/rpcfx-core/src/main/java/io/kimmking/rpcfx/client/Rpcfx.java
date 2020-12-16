package io.kimmking.rpcfx.client;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.thoughtworks.xstream.XStream;
import io.kimmking.rpcfx.exception.RpcfxException;
import io.kimmking.rpcfx.param.RpcfxRequest;
import io.kimmking.rpcfx.param.RpcfxResponse;
import io.kimmking.rpcfx.utils.XStreamUtils;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public final class Rpcfx {

    static {
        ParserConfig parserConfig = ParserConfig.getGlobalInstance();
        parserConfig.addAccept("io.kimmking");
        parserConfig.setAutoTypeSupport(true);
    }

    public static <T> T create(final Class<T> serviceClass, final String url) {
        //Gglib方式
        Enhancer enhancer = new Enhancer();
        enhancer.setCallback(new RpcfxInvocationHandler(serviceClass, url));
        enhancer.setSuperclass(serviceClass);
        return (T) enhancer.create();
        // 0. 替换动态代理 -> AOP
        //return (T) Proxy.newProxyInstance(Rpcfx.class.getClassLoader(), new Class[]{serviceClass}, new RpcfxInvocationHandler(serviceClass, url));

    }

    public static class RpcfxInvocationHandler implements InvocationHandler, MethodInterceptor {

        public static final MediaType JSONTYPE = MediaType.get("application/json; charset=utf-8");

        private final Class<?> serviceClass;
        private final String url;
        private final XStream stream = XStreamUtils.createToJson();

        public <T> RpcfxInvocationHandler(Class<T> serviceClass, String url) {
            this.serviceClass = serviceClass;
            this.url = url;
        }

        // 可以尝试，自己去写对象序列化，二进制还是文本的，，，rpcfx是xml自定义序列化、反序列化，json: code.google.com/p/rpcfx
        // int byte char float double long bool
        // [], data class

        @Override
        public Object invoke(Object proxy, Method method, Object[] params) throws Throwable {
            return post(method, params, url);
        }

        private Object post(Method method, Object[] params, String url) throws IOException, RpcfxException {
            RpcfxRequest rpcfxRequest = new RpcfxRequest();
            rpcfxRequest.setServiceClass(this.serviceClass.getName());
            rpcfxRequest.setMethod(method.getName());
            rpcfxRequest.setParams(params);
            String reqJson = JSON.toJSONString(rpcfxRequest);
            System.out.println("req json: " + reqJson);

            // 1.可以复用client
            // 2.尝试使用httpclient或者netty client
            OkHttpClient client = new OkHttpClient();
            final Request request = new Request.Builder()
                    .url(url)
                    .post(RequestBody.create(JSONTYPE, reqJson))
                    .build();
            String respJson = client.newCall(request).execute().body().string();
            System.out.println("resp json: " + respJson);
            RpcfxResponse response = JSON.parseObject(respJson, RpcfxResponse.class);
            // 这里判断response.status，处理异常
            // 考虑封装一个全局的RpcfxException
            if (!response.isStatus()) {
                throw new RpcfxException("invoke error", response.getException());
            }
            return XStreamUtils.fromBean(stream, response.getResult().toString());
        }

        @Override
        public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
            return post(method, objects, url);
        }
    }
}
