package org.kitona.zus.infrastructure.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.kitona.zus.common.utils.BeanFieldUtil;
import org.kitona.zus.infrastructure.entity.BasePo;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DefaultMetaObjectHandler implements MetaObjectHandler {

    private final String createTimeFieldName = BeanFieldUtil.getFieldName(BasePo::getCreateTime);

    private final String updateTimeFieldName = BeanFieldUtil.getFieldName(BasePo::getUpdateTime);

    private final String createdByFieldName = BeanFieldUtil.getFieldName(BasePo::getCreatedBy);

    private final String updatedByFieldName = BeanFieldUtil.getFieldName(BasePo::getUpdatedBy);

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