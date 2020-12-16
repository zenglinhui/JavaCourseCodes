package io.kimmking.rpcfx.exception;

public class RpcfxException extends Exception {

    public RpcfxException() {
        super();
    }

    public RpcfxException(Throwable throwable) {
        super(throwable);
    }

    public RpcfxException(String message) {
        super(message);
    }

    public RpcfxException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
