package indi.kurok1.rpcfx.demo.consumer;

import indi.kurok1.rpcfx.api.Filter;
import indi.kurok1.rpcfx.api.LoadBalancer;
import indi.kurok1.rpcfx.api.Router;
import indi.kurok1.rpcfx.api.RpcfxRequest;
import indi.kurok1.rpcfx.client.EnableRemoteService;
import indi.kurok1.rpcfx.client.Rpcfx;
import indi.kurok1.rpcfx.demo.api.User;
import indi.kurok1.rpcfx.demo.api.UserService;
import indi.kurok1.rpcfx.http.HttpClient;
import indi.kurok1.rpcfx.http.NettyHttpClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication(scanBasePackages = "indi.kurok1")
@EnableRemoteService(baskPackages = {"indi.kurok1.rpcfx.demo.api"})
public class RpcfxClientApplication {

	// 二方库
	// 三方库 lib
	// nexus, userserivce -> userdao -> user
	//

	public static void main(String[] args) {

		// UserService service = new xxx();
		// service.findById
		ConfigurableApplicationContext run = SpringApplication.run(RpcfxClientApplication.class, args);
		UserService userService = run.getBean(UserService.class);
		User user = userService.findById(1);
		System.out.println("find user id=1 from server: " + user.getName());

//		OrderService orderService = Rpcfx.create(OrderService.class, "http://localhost:8080/");
//		Order order = orderService.findOrderById(1992129);
//		System.out.println(String.format("find order name=%s, amount=%f",order.getName(),order.getAmount()));
//
//		//
//		UserService userService2 = Rpcfx.createFromRegistry(UserService.class, "localhost:2181", new TagRouter(), new RandomLoadBalancer(), new CuicuiFilter());

//		SpringApplication.run(RpcfxClientApplication.class, args);
	}

	private static class TagRouter implements Router {
		@Override
		public List<String> route(List<String> urls) {
			return urls;
		}
	}

	private static class RandomLoadBalancer implements LoadBalancer {
		@Override
		public String select(List<String> urls) {
			return urls.get(0);
		}
	}

	@Slf4j
	private static class CuicuiFilter implements Filter {
		@Override
		public boolean filter(RpcfxRequest request) {
			log.info("filter {} -> {}", this.getClass().getName(), request.toString());
			return true;
		}
	}

	@Bean
	public HttpClient httpClient() {
		return new NettyHttpClient();
	}
}



