package org.kitona.zus.api.response;

import lombok.Data;

import java.util.List;

/**
 * FGA ListSubjects API 响应
 */
@Data
public class FgaListSubjectsResponseVO {

    /**
     * 对 object relation 具备权限的主体列表。
     */
    private List<FgaSubjectVO> subjects;
}
