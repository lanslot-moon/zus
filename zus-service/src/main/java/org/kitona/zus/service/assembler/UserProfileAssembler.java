package org.kitona.zus.service.assembler;

import org.kitona.zus.domain.authorization.user.UserProfile;
import org.kitona.zus.service.dto.UserInfoDTO;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户信息 Assembler
 *
 * <p>负责 {@link UserProfile} 与 {@link UserInfoDTO} 之间的转换。
 * <p>DDD 规范：Assembler 位于应用服务层，用于领域对象与 DTO 之间的双向转换。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-02-26
 */
public final class UserProfileAssembler {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private UserProfileAssembler() {
    }

    /**
     * 将领域实体转换为应用层 DTO
     *
     * @param entity 用户信息实体
     * @return 用户信息 DTO，entity 为 null 时返回 null
     */
    public static UserInfoDTO toDTO(UserProfile entity) {
        return toDTO(entity, null);
    }

    /**
     * 将领域实体转换为应用层 DTO，并填充外部数据
     *
     * @param entity      用户信息实体
     * @param userAddress 用户地址（来自外部适配器）
     * @return 用户信息 DTO，entity 为 null 时返回 null
     */
    public static UserInfoDTO toDTO(UserProfile entity, String userAddress) {
        if (entity == null) {
            return null;
        }
        UserInfoDTO dto = new UserInfoDTO();
        dto.setUserId(entity.getUserId());
        dto.setUserName(entity.getUserName());
        dto.setUserRole(entity.getUserRole());
        dto.setUserStatus(entity.getUserStatus());
        dto.setUserPhone(entity.getUserPhone());
        dto.setUserEmail(userAddress != null ? userAddress : entity.getUserEmail());
        dto.setUserAddress(entity.getUserAddress());
        dto.setUserAvatar(entity.getUserAvatar());
        dto.setUserCreateTime(entity.getCreateTime() != null ? entity.getCreateTime().format(FORMATTER) : null);
        dto.setUserUpdateTime(entity.getUpdateTime() != null ? entity.getUpdateTime().format(FORMATTER) : null);
        return dto;
    }

    /**
     * 批量将领域实体转换为应用层 DTO
     *
     * @param entities 用户信息实体列表
     * @return DTO 列表，entities 为 null 或空时返回空列表
     */
    public static List<UserInfoDTO> toDTOList(List<UserProfile> entities) {
        if (entities == null || entities.isEmpty()) {
            return Collections.emptyList();
        }
        return entities.stream()
                .map(UserProfileAssembler::toDTO)
                .collect(Collectors.toList());
    }
}
