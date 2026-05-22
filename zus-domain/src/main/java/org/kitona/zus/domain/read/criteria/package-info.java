/**
 * 读侧查询条件对象。
 *
 * <p>本包只保留仍被 read port 使用的查询条件。面向写侧聚合装载的标识对象
 * 应放回对应聚合子域，避免 read criteria 被滥用成通用参数包。
 */
package org.kitona.zus.domain.read.criteria;
