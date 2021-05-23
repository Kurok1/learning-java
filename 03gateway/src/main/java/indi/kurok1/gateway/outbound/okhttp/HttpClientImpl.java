package indi.kurok1.gateway.outbound.okhttp;

import indi.kurok1.gateway.common.Deserializer;
import indi.kurok1.gateway.common.Serializer;
import indi.kurok1.gateway.common.ServiceUtils;
import indi.kurok1.gateway.filter.HttpResponseFilter;
import indi.kurok1.gateway.outbound.DefaultErrorResponse;
import indi.kurok1.gateway.outbound.HttpClient;
import indi.kurok1.gateway.common.HttpMethod;
import indi.kurok1.gateway.route.ServiceInstance;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import kotlin.Pair;
import okhttp3.Protocol;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import sun.security.krb5.internal.crypto.Des;

import java.io.IOException;
import java.util.*;

/**
 * OkHttp的http client实现
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.05.20
 */
public class HttpClientImpl extends HttpClient {

    private Deserializer deserializer;
    private Serializer serializer;
    private Iterable<HttpResponseFilter> filters;

    public HttpClientImpl() {
        //加载默认deserializer
        deserializer = ServiceUtils.defaultImplement(Deserializer.class, null);
        //加载默认serializer
        serializer = ServiceUtils.defaultImplement(Serializer.class, null);
        filters = ServiceUtils.allImplement(HttpResponseFilter.class);
    }

    @Override
    protected void execute(ServiceInstance serviceInstance, String uri, HttpMethod method, Object requestBody, Map<String, String> httpHeaders) {
        String url = String.format("http://%s:%s/%s", serviceInstance.getIpAddress(), serviceInstance.getPort(), uri);

        if (httpHeaders == null)
            httpHeaders = new HashMap<>();

        //序列化请求体
        RequestBody body = null;
        if (requestBody != null) {
            if (requestBody instanceof String) {
                body = RequestBody.create((String) requestBody, null);
            } else {
                byte[] bytes = this.serializer.encode(requestBody);
                body = RequestBody.create(bytes, null);
            }
        }


        //构建httpTask
        HttpTask httpTask = new HttpTask.Builder()
                .url(url)
                .header(httpHeaders)
                .method(method)
                .requestBody(body)
                .build();
        try {
            Response response = httpTask.call();
            FullHttpResponse fullHttpResponse = handle(response);
            if (fullHttpResponse == null)
                fullHttpResponse = DefaultErrorResponse.INTERNAL_SERVER_ERROR_500;
            //处理响应
            //1.过滤
            for (HttpResponseFilter filter : filters) {
                boolean needFilter = filter.filter(fullHttpResponse, super.context);
                if (!needFilter)
                    break;
            }


            //如果指定了上位context，写入
            if (super.context != null)
                super.context.write(fullHttpResponse);


        } catch (Exception e) {
            e.printStackTrace();
            if (super.context != null)
                super.context.write(DefaultErrorResponse.INTERNAL_SERVER_ERROR_500);
        }
    }


    private static FullHttpResponse handle(Response response) {
        if (response == null)
            return null;
        if (!response.isSuccessful())
            return null;

        try {
            ResponseBody body = response.body();
            if (body == null)
                return null;
            Protocol protocol = response.protocol();
            HttpVersion version = null;
            switch (protocol) {
                case HTTP_1_0:version = HttpVersion.HTTP_1_0;break;
                case HTTP_1_1:version = HttpVersion.HTTP_1_1;break;
                default:version = null;
            }
            if (version == null)
                return null;

            HttpResponseStatus status = HttpResponseStatus.valueOf(response.code());
            ByteBuf byteBuf = Unpooled.wrappedBuffer(body.bytes());
            DefaultFullHttpResponse defaultFullHttpResponse = new DefaultFullHttpResponse(version, status, byteBuf);
            for (Pair<? extends String, ? extends String> pair : response.headers())
                defaultFullHttpResponse.headers().add(pair.component1(), pair.component2());
            return defaultFullHttpResponse;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
