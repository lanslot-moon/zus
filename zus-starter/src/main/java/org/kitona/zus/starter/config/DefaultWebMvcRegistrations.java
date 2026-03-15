package org.kitona.zus.starter.config;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * Desc: 防止FeignClient接口和RestController路径冲突配置,设置如果方法或者类上存在FeignClient则不扫描为RestFul接口
 * <a href="https://blog.csdn.net/forezp/article/details/80069961"/a>
 */
@Configurable
public class DefaultWebMvcRegistrations implements WebMvcRegistrations {

    @Override
    public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
        return new FeignRequestMappingHandlerMapping();
    }

    private static class FeignRequestMappingHandlerMapping extends RequestMappingHandlerMapping {
        @Override
        protected boolean isHandler(Class<?> beanType) {
//            return super.isHandler(beanType) && !AnnotatedElementUtils.hasAnnotation(beanType, FeignClient.class);
            return super.isHandler(beanType);
        }
    }
}