package org.kitona.zus.service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * ListObjects API 结果 DTO
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListObjectsResultDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 对象标识列表，格式 type:id */
    private List<String> objects;

    public static ListObjectsResultDTO empty() {
        return ListObjectsResultDTO.builder().objects(List.of()).build();
    }
}
