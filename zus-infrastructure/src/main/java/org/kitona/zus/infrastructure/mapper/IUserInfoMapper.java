package org.kitona.zus.infrastructure.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.kitona.zus.infrastructure.entity.po.UserInfoDo;

/*
 * Title: IUserInfoMapperService
 * Email: wangli.liu@kitona.org
 * Author  Kitona
 * Date  2025/10/22 17:21
 * Description: xxx
 */
@Mapper
public interface IUserInfoMapper extends BaseMapper<UserInfoDo> {


//    UserInfoDo findByNameAndAge(@Param("name") String name,@Param("age") Integer age);
}
