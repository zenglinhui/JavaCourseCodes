package io.kimmking.rpcfx.exception;

public class RpcfxException extends Exception {

    public RpcfxException() {
        super();
    }

    public RpcfxException(Throwable throwable) {
        super(throwable);
    }

    public RpcfxException(String exception) {
        super(exception);
    }

}
