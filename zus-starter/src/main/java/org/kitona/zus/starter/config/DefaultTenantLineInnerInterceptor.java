//package org.kitona.zus.starter.config;
//
//
//import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
//import net.sf.jsqlparser.expression.Expression;
//import net.sf.jsqlparser.expression.LongValue;
//import org.apache.commons.lang3.StringUtils;
//import org.kitona.zus.business.utils.BeanFieldUtil;
//import org.kitona.zus.infrastructure.entity.po.BaseDo;
//
//
///**
// * 多租户分页插件
// */
//public class DefaultTenantLineInnerInterceptor implements TenantLineHandler {
//
//    @Override
//    public Expression getTenantId() {
//        // 假设有一个租户上下文，能够从中获取当前用户的租户
//         Long tenantId = TenantContextHolder.getCurrentTenantId();
//        // 返回租户ID的表达式，LongValue 是 JSQLParser 中表示 bigint 类型的 class
//        return new LongValue(tenantId);
//    }
//
//    @Override
//    public String getTenantIdColumn() {
//        return BeanFieldUtil.getFieldName(BaseDo::getTenantId);
//    }
//
//    @Override
//    public boolean ignoreTable(String tableName) {
//        // 根据需要返回是否忽略该表
//        return StringUtils.isBlank(getTenantIdColumn());
//    }
//}