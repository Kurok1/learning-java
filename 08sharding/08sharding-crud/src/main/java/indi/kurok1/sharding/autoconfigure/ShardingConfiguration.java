package indi.kurok1.sharding.autoconfigure;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * TODO
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.06.27
 */
@Configuration
public class ShardingConfiguration implements InitializingBean, BeanFactoryAware {

    private BeanFactory beanFactory;

    public void afterPropertiesSet() throws Exception {
        //被迫这样注入，希望后续能改进，支持Spring Bean
        StringRedisTemplate redisTemplate = beanFactory.getBean(StringRedisTemplate.class);
        RedisIdGenerator.setRedisTemplate(redisTemplate);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
