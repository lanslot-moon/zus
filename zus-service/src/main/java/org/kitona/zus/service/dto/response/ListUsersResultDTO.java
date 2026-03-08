package org.kitona.zus.service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * ListUsers API 结果 DTO
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListUsersResultDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<UserDTO> users;

    public static ListUsersResultDTO empty() {
        return ListUsersResultDTO.builder().users(List.of()).build();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserDTO implements Serializable {
        private static final long serialVersionUID = 1L;
        private String type;
        private String id;
        private String relation;
    }
}
