package indi.kurok1.rpcfx.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import indi.kurok1.rpcfx.api.RpcfxRequest;
import indi.kurok1.rpcfx.api.RpcfxResponse;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * TODO
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.07.04
 */
public class RemoteServiceInterceptor implements MethodInterceptor {

    static {
        ParserConfig.getGlobalInstance().addAccept("indi.kurok1");
    }

    public static final okhttp3.MediaType JSONTYPE = okhttp3.MediaType.get("application/json; charset=utf-8");


    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        //Object executor = invocation.getThis();
        Method method = invocation.getMethod();
        //具体拦截逻辑
        //获取serviceName
        RemoteService remoteService = AnnotationUtils.findAnnotation(method.getDeclaringClass(), RemoteService.class);
        if (remoteService == null)
            return null;

        String serviceName = remoteService.serviceName();
        if (StringUtils.isEmpty(serviceName))
            return null;

        RpcfxRequest request = new RpcfxRequest();
        request.setServiceClass(serviceName);
        request.setMethod(method.getName());
        request.setParams(invocation.getArguments());

        RpcfxResponse response = post(request, remoteService.url());

        // 加filter地方之三
        // Student.setTeacher("cuijing");

        // 这里判断response.status，处理异常
        // 考虑封装一个全局的RpcfxException
        try {
            return JSON.parse(response.getResult().toString());
        } catch (Throwable t) {
            throw new RpcfxException(t);
        }
    }

    private RpcfxResponse post(RpcfxRequest req, String url) throws IOException {
        String reqJson = JSON.toJSONString(req);
        System.out.println("req json: "+reqJson);

        // 1.可以复用client
        // 2.尝试使用httpclient或者netty client
        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(JSONTYPE, reqJson))
                .build();
        String respJson = client.newCall(request).execute().body().string();
        System.out.println("resp json: "+respJson);
        return JSON.parseObject(respJson, RpcfxResponse.class);
    }
}
