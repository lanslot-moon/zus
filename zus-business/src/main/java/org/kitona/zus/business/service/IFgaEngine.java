package org.kitona.zus.business.service;

/**
 * Author: 登林
 * Email: wangli.liu@kitona.org
 * Date: 2025/10/29 23:49
 * Version: V1.0
 */
public interface IFgaEngine {

    boolean checkPermission(Long userId, String resourceType, Long resourceId, String action);
}
