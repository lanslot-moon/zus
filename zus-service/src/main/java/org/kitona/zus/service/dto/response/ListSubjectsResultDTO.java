package org.kitona.zus.service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * ListSubjects API 结果 DTO
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListSubjectsResultDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private List<String> subjects;

    public static ListSubjectsResultDTO empty() {
        return ListSubjectsResultDTO.builder().subjects(List.of()).build();
    }
}
