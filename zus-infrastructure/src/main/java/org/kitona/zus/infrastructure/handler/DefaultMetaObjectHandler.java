package org.kitona.zus.infrastructure.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.kitona.zus.common.utils.BeanFieldUtil;
import org.kitona.zus.infrastructure.persistence.mysql.entity.BasePoMinimal;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DefaultMetaObjectHandler implements MetaObjectHandler {

    private final String createTimeFieldName = BeanFieldUtil.getFieldName(BasePoMinimal::getCreateTime);

    private final String updateTimeFieldName = BeanFieldUtil.getFieldName(BasePoMinimal::getUpdateTime);

    private final String createdByFieldName = BeanFieldUtil.getFieldName(BasePoMinimal::getCreatedBy);

    private final String updatedByFieldName = BeanFieldUtil.getFieldName(BasePoMinimal::getUpdatedBy);

    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("开始插入填充...");
        this.strictInsertFill(metaObject, createTimeFieldName, Long.class, System.currentTimeMillis());
        this.strictInsertFill(metaObject, createdByFieldName, String.class, null);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("开始更新填充...");
        this.strictUpdateFill(metaObject, updateTimeFieldName, Long.class, System.currentTimeMillis());
        this.strictUpdateFill(metaObject, updatedByFieldName, String.class, null);
    }
}