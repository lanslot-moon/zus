package org.kitona.zus.infrastructure.persistence.mysql.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.kitona.zus.infrastructure.enums.DeletedStatusEnum;
import org.kitona.zus.infrastructure.persistence.mysql.entity.BasePoMinimal;

/*
 * Title: BaseRepository
 * Email: wangli.liu@kitona.org
 * Author  Kitona
 * Date  2025/10/22 17:44
 * Description: xxx
 */
public class BaseRepository<T extends BasePoMinimal> extends ServiceImpl<BaseMapper<T>, T> {

     LambdaQueryWrapper<T> getLambdaQueryWrapper() {
         // 关键点：构造函数传入 getEntityClass()
         // 这样 MP 就会去解析具体子类（如 UserPo）的缓存，而不是 BasePoMinimal 的缓存
         return new LambdaQueryWrapper<>(getEntityClass())
                 .eq(BasePoMinimal::getIsDeleted, DeletedStatusEnum.NOT_DELETED.getCode());
     }
}
