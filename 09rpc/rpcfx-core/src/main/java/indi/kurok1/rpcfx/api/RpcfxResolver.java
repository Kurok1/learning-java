package indi.kurok1.rpcfx.api;

public interface RpcfxResolver {

    Object resolve(String serviceClass);

    Object resolve(Class<?> serviceClass);

}
