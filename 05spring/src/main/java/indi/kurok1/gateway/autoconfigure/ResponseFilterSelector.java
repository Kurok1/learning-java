package indi.kurok1.gateway.autoconfigure;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.core.type.AnnotationMetadata;

import java.util.List;
import java.util.function.Predicate;

/**
 * TODO
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.06.06
 */
public class ResponseFilterSelector implements ImportSelector, BeanClassLoaderAware {

    private ClassLoader classLoader;

    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
        List<String> factoryNames = SpringFactoriesLoader.loadFactoryNames(EnableResponseFilter.class, classLoader);
        if (factoryNames.isEmpty()) {
            throw new IllegalStateException("Annotation @EnableResponseFilter"
                    + " found, but there are no implementations. Did you forget to include a starter?");
        }

        return factoryNames.toArray(new String[factoryNames.size()]);
    }

    @Override
    public Predicate<String> getExclusionFilter() {
        return null;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
}
