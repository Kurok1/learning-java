package indi.kurok1.gateway.autoconfigure;

import indi.kurok1.gateway.common.DefaultDeserializer;
import indi.kurok1.gateway.common.DefaultSerializer;
import indi.kurok1.gateway.common.Deserializer;
import indi.kurok1.gateway.common.Serializer;
import indi.kurok1.gateway.filter.HttpRequestFilter;
import indi.kurok1.gateway.filter.HttpResponseFilter;
import indi.kurok1.gateway.inbound.HttpInboundHandler;
import indi.kurok1.gateway.inbound.HttpInboundInitializer;
import indi.kurok1.gateway.inbound.HttpInboundServer;
import indi.kurok1.gateway.outbound.HttpClient;
import indi.kurok1.gateway.outbound.netty.NettyHttpClient;
import indi.kurok1.gateway.route.Dispatcher;
import indi.kurok1.gateway.route.RandomServiceChooser;
import indi.kurok1.gateway.route.ServiceChooser;
import io.netty.bootstrap.ServerBootstrap;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.List;

/**
 * 网关自动装配,主要提供一些线程池,还有一些组件,替换原来的SPI注入方式
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.06.06
 * @see indi.kurok1.gateway.common.ServiceUtils
 */
@Configuration
@EnableConfigurationProperties(GatewayProperties.class)
@ConditionalOnClass(ServerBootstrap.class)
public class GatewayAutoConfiguration {

    private final GatewayProperties gatewayProperties;

    public GatewayAutoConfiguration(GatewayProperties gatewayProperties) {
        this.gatewayProperties = gatewayProperties;
    }

    @Bean
    @ConditionalOnMissingBean
    public Serializer serializer() {
        return new DefaultSerializer();
    }

    @Bean
    @ConditionalOnMissingBean
    public Deserializer deserializer() {
        return new DefaultDeserializer();
    }

    @Bean
    @ConditionalOnMissingBean
    public ServiceChooser serviceChooser() {
        return new RandomServiceChooser();
    }

    @Bean
    @ConditionalOnMissingBean
    public HttpClient httpClient(
            Serializer serializer,
            Deserializer deserializer,
            List<HttpResponseFilter> filterList
    ) {
        return new NettyHttpClient(serializer, deserializer, filterList);
    }

    @Bean
    @Primary
    public HttpInboundHandler httpInboundHandler(Dispatcher dispatcher, List<HttpRequestFilter> filterList) {
        return new HttpInboundHandler(dispatcher, filterList);
    }

    @Bean
    @Primary
    public HttpInboundServer httpInboundServer(HttpInboundInitializer httpInboundInitializer) {
        return new HttpInboundServer(gatewayProperties, httpInboundInitializer);
    }

}
