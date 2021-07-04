package indi.kurok1.rpcfx.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.util.*;

/**
 * 远程服务代理注册到Spring Context
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.07.04
 */
public class RemoteServiceRegistrar implements ImportBeanDefinitionRegistrar
        , BeanFactoryAware, ResourceLoaderAware, EnvironmentAware, BeanClassLoaderAware {
    private static final Logger logger = LoggerFactory.getLogger(RemoteServiceRegistrar.class);

    private DefaultListableBeanFactory beanFactory;

    private Environment environment;

    private ResourceLoader resourceLoader;

    static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";

    private ResourcePatternResolver resourcePatternResolver;

    private MetadataReaderFactory metadataReaderFactory;

    private ClassLoader classLoader;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (DefaultListableBeanFactory) beanFactory;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        final Map<String, Object> annotationAttributes = importingClassMetadata.getAnnotationAttributes(getAnnotation().getName());
        if (annotationAttributes == null) {
            return;
        }
        String[] basePackage = (String[]) annotationAttributes.getOrDefault("baskPackages", new String[0]);
        Set<RemoteServiceWrapper> wrappers = new LinkedHashSet<>();
        for (String packageName : basePackage) {
            wrappers.addAll(scanPackageAndProcess(packageName));
        }
        logger.info("found [{}] remote repository", wrappers.size());
        for (RemoteServiceWrapper wrapper : wrappers) {
            this.beanFactory.registerSingleton(wrapper.beanName, wrapper.getProxyInstance());
        }
    }

    private Class<? extends Annotation> getAnnotation() {
        return EnableRemoteService.class;
    }

    protected Set<RemoteServiceWrapper> scanPackageAndProcess(String basePackage) {
        try {
            String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                    resolveBasePackage(basePackage) + '/' + DEFAULT_RESOURCE_PATTERN;
            Resource[] resources = getResourcePatternResolver().getResources(packageSearchPath);
            Set<RemoteServiceWrapper> wrappers = new LinkedHashSet<>();
            for (Resource resource : resources) {
                if (resource.isReadable()) {
                    try {
                        MetadataReader metadataReader = getMetadataReaderFactory().getMetadataReader(resource);
                        ClassMetadata classMetadata = metadataReader.getClassMetadata();
                        AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();
                        //检查是否为接口，不为接口之间跳过
                        if (!classMetadata.isInterface())
                            continue;
                        //检查是否使用RemoteRepository标记
                        if (!annotationMetadata.hasAnnotation(RemoteService.class.getName()))
                            continue;

                        Map<String, Object> attributes = annotationMetadata.getAnnotationAttributes(RemoteService.class.getName());
                        if (attributes == null)
                            continue;
                        String serviceName = (String) attributes.get("serviceName");
                        if (!StringUtils.hasLength(serviceName))
                            continue;//过滤
                        // Create proxy
                        ProxyFactory result = new ProxyFactory();
                        result.setTarget(new Object());
                        Class clazz = Class.forName(classMetadata.getClassName());
                        result.setInterfaces(clazz);
                        result.addAdvice(new RemoteServiceInterceptor());

                        Object repository = result.getProxy(this.classLoader);
                        //生成 BeanDefinition
                        BeanDefinition definition = new ScannedGenericBeanDefinition(metadataReader);
                        definition.setBeanClassName(result.getClass().getName());
                        String beanName = (String) attributes.get("id");
                        if (StringUtils.isEmpty(beanName))
                            beanName = clazz.getSimpleName();
                        RemoteServiceWrapper wrapper = new RemoteServiceWrapper(repository, definition, beanName);
                        wrappers.add(wrapper);
                    }
                    catch (Throwable ex) {
                        throw new BeanDefinitionStoreException(
                                "Failed to read class: " + resource, ex);
                    }
                }
            }
            return wrappers;
        } catch (Exception e) {
            //扫描失败
            return Collections.emptySet();
        }
    }

    private ResourcePatternResolver getResourcePatternResolver() {
        if (this.resourcePatternResolver == null) {
            this.resourcePatternResolver = new PathMatchingResourcePatternResolver(this.resourceLoader);
        }
        return this.resourcePatternResolver;
    }

    private MetadataReaderFactory getMetadataReaderFactory() {
        if (this.metadataReaderFactory == null) {
            this.metadataReaderFactory = new CachingMetadataReaderFactory();
        }
        return this.metadataReaderFactory;
    }

    protected String resolveBasePackage(String basePackage) {
        return ClassUtils.convertClassNameToResourcePath(this.environment.resolveRequiredPlaceholders(basePackage));
    }

    public static class RemoteServiceWrapper {
        private final Object proxyInstance;
        private final BeanDefinition beanDefinition;
        private final String beanName;

        public RemoteServiceWrapper(Object proxyInstance, BeanDefinition beanDefinition, String beanName) {
            this.proxyInstance = proxyInstance;
            this.beanDefinition = beanDefinition;
            this.beanName = beanName;
        }

        public Object getProxyInstance() {
            return proxyInstance;
        }

        public BeanDefinition getBeanDefinition() {
            return beanDefinition;
        }

        public String getBeanName() {
            return beanName;
        }
    }
}
