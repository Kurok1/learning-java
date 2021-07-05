package indi.kurok1.rpcfx.http;

import indi.kurok1.rpcfx.api.RpcfxRequest;
import indi.kurok1.rpcfx.api.RpcfxResponse;

/**
 * TODO
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.07.04
 */
public abstract class HttpClient {


    public abstract RpcfxResponse post(RpcfxRequest req, String url);

}
