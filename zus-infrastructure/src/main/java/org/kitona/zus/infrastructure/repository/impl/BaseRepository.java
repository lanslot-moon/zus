package org.kitona.zus.infrastructure.repository.impl;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

/*
 * Title: BaseRepository
 * Email: wangli.liu@kitona.org
 * Author  Kitona
 * Date  2025/10/22 17:44
 * Description: xxx
 */
@Repository
public class BaseRepository<T> extends ServiceImpl<BaseMapper<T>, T> {



}
