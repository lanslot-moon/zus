package org.kitona.zus.api.response;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.kitona.zus.service.dto.response.ListUsersResultDTO;

/**
 * FGA 用户 VO
 */
@Data
@AutoMapper(target = ListUsersResultDTO.UserDTO.class)
public class FgaUserVO {

    /**
     * 主体类型，如 user
     */
    private String type;

    /**
     * 主体ID
     */
    private String id;

    /**
     * 主体关系（用户集时使用）
     */
    private String relation;
}
