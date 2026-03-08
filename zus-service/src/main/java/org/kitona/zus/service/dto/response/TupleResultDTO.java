package org.kitona.zus.service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 关系元组结果 DTO
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TupleResultDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String objectType;
    private String objectId;
    private String relation;
    private String subjectType;
    private String subjectId;
    private String subjectRelation;
}
