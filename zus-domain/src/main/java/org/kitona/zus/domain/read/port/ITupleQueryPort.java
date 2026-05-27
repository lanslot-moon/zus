package org.kitona.zus.domain.read.port;

import org.kitona.zus.domain.read.criteria.TupleQueryCriteria;
import org.kitona.zus.domain.read.view.TupleView;
import org.kitona.zus.domain.valueobject.ObjectRef;

import java.util.List;

/**
 * 关系元组读侧查询端口。
 *
 * <p>该接口面向元组列表、过滤和读侧展示，不承担关系元组聚合根保存职责。
 */
public interface ITupleQueryPort {

    /**
     * 分页列出关系元组。
     *
     * @param criteria 查询条件
     * @return 关系元组列表
     */
    List<TupleView> list(TupleQueryCriteria criteria);

    /**
     * 根据主体查询元组。
     *
     * @param criteria 查询条件
     * @return 关系元组列表
     */
    List<TupleView> findBySubject(TupleQueryCriteria criteria);

    /**
     * 根据资源对象查询元组。
     *
     * @param criteria 查询条件
     * @return 关系元组列表
     */
    List<TupleView> findByObject(TupleQueryCriteria criteria);

    /**
     * 判断对象在当前一致性版本下是否存在任意有效授权事实。
     *
     * @param storeId   Store 标识
     * @param object    授权对象引用
     * @param maxZookie 最大可见 zookie 版本，为空表示不限制版本
     * @return 存在有效授权事实返回 {@code true}
     */
    boolean existsObjectFact(String storeId, ObjectRef object, Long maxZookie);
}
