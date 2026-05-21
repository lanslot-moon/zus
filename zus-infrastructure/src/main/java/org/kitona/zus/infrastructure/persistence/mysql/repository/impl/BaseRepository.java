package org.kitona.zus.infrastructure.persistence.mysql.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.kitona.zus.infrastructure.persistence.mysql.entity.BaseIdPO;

/*
 * Title: BaseRepository
 * Email: wangli.liu@kitona.org
 * Author  Kitona
 * Date  2025/10/22 17:44
 * Description: xxx
 */
public class BaseRepository<T extends BaseIdPO> extends ServiceImpl<BaseMapper<T>, T> {

     protected LambdaQueryWrapper<T> getLambdaQueryWrapper() {
         return new LambdaQueryWrapper<>(getEntityClass());
     }

     protected LambdaUpdateWrapper<T> getLambdaUpdateWrapper() {
         return new LambdaUpdateWrapper<>(getEntityClass());
     }
}
