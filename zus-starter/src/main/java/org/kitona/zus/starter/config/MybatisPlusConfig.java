package org.kitona.zus.starter.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MybatisPlusConfig {


    /**
     * 如果配置多个插件, 切记分页最后添加
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        TenantLineInnerInterceptor tenantInterceptor = new TenantLineInnerInterceptor();
//        tenantInterceptor.setTenantLineHandler(new DefaultTenantLineInnerInterceptor());

        interceptor.addInnerInterceptor(tenantInterceptor);
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

    /**
     * SQL语句拦截器，用于打印SQL
     */
    @Bean
    public MybatisSqlPrintInterceptor mybatisSqlPrintInterceptor(){
        return new MybatisSqlPrintInterceptor();
    }
}