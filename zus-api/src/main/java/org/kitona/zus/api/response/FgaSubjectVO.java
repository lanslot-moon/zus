package org.kitona.zus.api.response;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.kitona.zus.service.dto.response.ListSubjectsResultDTO;

/**
 * FGA 主体 VO。
 */
@Data
@AutoMapper(target = ListSubjectsResultDTO.SubjectDTO.class)
public class FgaSubjectVO {

    /**
     * 主体类型，如 user、group、service-account。
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
