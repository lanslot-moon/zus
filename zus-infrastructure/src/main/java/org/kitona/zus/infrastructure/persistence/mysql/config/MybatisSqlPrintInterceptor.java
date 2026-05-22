package org.kitona.zus.infrastructure.persistence.mysql.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.springframework.core.Ordered;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;

/**
 * 这里没有使用拦截 {@link org.apache.ibatis.executor.Executor
 * 主要是因为PageHelp处理的时候，直接调用Executor的方法进行处理，没有调用invocation.proceed()
 * 下一个拦截器处理，直接处理SQL
 * 的修改，因此，将这个拦截设置到最后的查询阶段去处理}
 * <p>
 * 因此拦截StatementHandler
 * 肯定不会错误【StatementHandler，语句处理器负责和JDBC层具体交互，包括prepare语句，执行语句，以及调用ParameterHandler.parameterize()设置参数】
 */
@Intercepts({
        @Signature(type = StatementHandler.class, method = "query", args = { Statement.class, ResultHandler.class }),
        @Signature(type = StatementHandler.class, method = "update", args = { Statement.class }),
        @Signature(type = StatementHandler.class, method = "batch", args = { Statement.class }) })
@Slf4j
public class MybatisSqlPrintInterceptor implements Interceptor, Ordered {

    private Configuration configuration = null;
    private static final ThreadLocal<SimpleDateFormat> dateFormatThreadLocal = ThreadLocal
            .withInitial(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"));

    /**
     * 拦截intercept。
     *
     * @param invocation MyBatis 调用上下文
     * @return 返回结果
     */
    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        Object target = invocation.getTarget();
        long nowTime = System.currentTimeMillis();
        try {
            return invocation.proceed();
        } finally {
            String sql = this.getSql(target);
            log.info("sql拦截打印:{}, 执行耗时:{}ms", sql, System.currentTimeMillis() - nowTime);
            // 释放 ThreadLocal 资源
            dateFormatThreadLocal.remove();
        }
    }

    /**
     * 获取sql
     */
    private String getSql(Object target) {
        try {
            StatementHandler statementHandler = (StatementHandler) target;
            BoundSql boundSql = statementHandler.getBoundSql();
            if (configuration == null) {
                final ParameterHandler parameterHandler = statementHandler.getParameterHandler();
                Field configurationField = ReflectionUtils.findField(parameterHandler.getClass(), "configuration");
                if (configurationField != null) {
                    ReflectionUtils.makeAccessible(configurationField);
                    this.configuration = (Configuration) configurationField.get(parameterHandler);
                }
            }
            // 替换参数格式化Sql语句，去除换行符
            return formatSql(boundSql, configuration);
        } catch (Exception e) {
            log.warn("get sql error {}", target, e);
        }
        return "";
    }

    /**
     * 创建plugin。
     *
     * @param target 代理目标对象
     * @return 返回结果
     */
    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    /**
     * 设置set properties。
     *
     * @param properties 插件配置
     */
    @Override
    public void setProperties(Properties properties) {
        throw new UnsupportedOperationException("setProperties is not supported.");
    }

    /**
     * 获取完整的sql实体的信息
     */
    private String formatSql(BoundSql boundSql, Configuration configuration) {
        String sql = boundSql.getSql();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        Object parameterObject = boundSql.getParameterObject();

        if (isInvalidSql(sql, configuration)) {
            return "";
        }

        TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
        sql = beautifySql(sql);

        if (parameterMappings != null) {
            for (ParameterMapping parameterMapping : parameterMappings) {
                if (parameterMapping.getMode() != ParameterMode.OUT) {
                    Object value = getParameterValue(boundSql, parameterMapping, parameterObject, typeHandlerRegistry,
                            configuration);
                    String paramValueStr = formatParameterValue(value, parameterMapping.getProperty());
                    sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(paramValueStr));
                }
            }
        }
        return sql;
    }

    /**
     * 判断is invalid sql。
     *
     * @param sql SQL 文本
     * @param configuration MyBatis 配置
     * @return 满足条件返回 true，否则返回 false
     */
    private boolean isInvalidSql(String sql, Configuration configuration) {
        return sql == null || sql.isEmpty() || configuration == null;
    }

    /**
     * 从 BoundSql 或参数对象中提取 SQL 参数值。
     *
     * @param boundSql            MyBatis BoundSql
     * @param parameterMapping    参数映射
     * @param parameterObject     原始参数对象
     * @param typeHandlerRegistry 类型处理器注册表
     * @param configuration       MyBatis 配置
     * @return 参数值
     */
    private Object getParameterValue(BoundSql boundSql, ParameterMapping parameterMapping, Object parameterObject,
            TypeHandlerRegistry typeHandlerRegistry, Configuration configuration) {
        String propertyName = parameterMapping.getProperty();
        if (boundSql.hasAdditionalParameter(propertyName)) {
            return boundSql.getAdditionalParameter(propertyName);
        } else if (parameterObject == null) {
            return null;
        } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
            return parameterObject;
        } else {
            MetaObject metaObject = configuration.newMetaObject(parameterObject);
            return metaObject.getValue(propertyName);
        }
    }

    /**
     * 格式化 SQL 日志参数值。
     *
     * @param value 参数值
     * @param propertyName propertyName 参数
     * @return 返回结果
     */
    private String formatParameterValue(Object value, String propertyName) {
        String paramValueStr;
        if (value instanceof String) {
            paramValueStr = "'" + value + "'";
        } else if (value instanceof Date) {
            paramValueStr = "'" + dateFormatThreadLocal.get().format(value) + "'";
        } else {
            paramValueStr = value + "";
        }
        if (!propertyName.contains("frch_criterion") && !propertyName.contains("paramNameValuePairs")) {
            paramValueStr = "/*" + propertyName + "*/" + paramValueStr;
        }
        return paramValueStr;
    }

    /**
     * 美化Sql
     */
    private String beautifySql(String sql) {
        sql = sql.replaceAll("\\s+", " ");
        return sql;
    }

    /**
     * 返回拦截器执行顺序。
     * @return 查询结果
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
