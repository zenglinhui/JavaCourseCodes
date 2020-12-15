package io.kimmking.rpcfx.api;

public interface RpcfxReflectionResolver<T> {

    <T> T resolve(String serviceClass);

}
