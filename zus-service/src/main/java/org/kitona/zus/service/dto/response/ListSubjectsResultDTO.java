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

    private List<SubjectDTO> subjects;

    public static ListSubjectsResultDTO empty() {
        return ListSubjectsResultDTO.builder().subjects(List.of()).build();
    }

    /**
     * 主体结果项。
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubjectDTO implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * 主体类型。
         */
        private String type;

        /**
         * 主体标识。
         */
        private String id;

        /**
         * 主体关系。
         */
        private String relation;
    }
}
