package indi.kurok1.rpcfx.http;

import indi.kurok1.rpcfx.api.RpcfxRequest;
import indi.kurok1.rpcfx.api.RpcfxResponse;
import okhttp3.MediaType;

/**
 * TODO
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.07.04
 */
public abstract class HttpClient {

    public static final MediaType JSONTYPE = MediaType.get("application/json; charset=utf-8");

    public abstract RpcfxResponse post(RpcfxRequest req, String url);

}
