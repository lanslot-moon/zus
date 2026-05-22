package org.kitona.zus.infrastructure.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DefaultMetaObjectHandler implements MetaObjectHandler {

    /**
     * 填充新增记录的审计字段。
     *
     * @param metaObject MyBatis 元对象
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        long now = System.currentTimeMillis();
        fillIfPresent(metaObject, "createTime", now);
        fillIfPresent(metaObject, "updateTime", now);
        fillIfPresent(metaObject, "isDeleted", Boolean.FALSE);
    }

    /**
     * 更新update fill。
     *
     * @param metaObject MyBatis 元对象
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        fillIfPresent(metaObject, "updateTime", System.currentTimeMillis());
    }

    /**
     * 填充fill if present。
     *
     * @param metaObject MyBatis 元对象
     * @param fieldName 字段名
     * @param fieldValue 字段值
     */
    private void fillIfPresent(MetaObject metaObject, String fieldName, Object fieldValue) {
        if (!metaObject.hasSetter(fieldName)) {
            return;
        }
        if (metaObject.getValue(fieldName) != null) {
            return;
        }
        this.setFieldValByName(fieldName, fieldValue, metaObject);
    }
}
