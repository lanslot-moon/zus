package org.kitona.zus.service.listener.index;


/*
 * Title: IIndexListenerRegister
 * Email: wangli.liu@kitona.org
 * Author  Kitona
 * Date  2025/11/18 15:53
 * Description: xxx
 */
public interface IIndexListenerRegister {

    /**
     * 注册索引变更监听器
     */
    void registerChangeListener(IndexChangeListener listener);

    /**
     * 取消注册索引变更监听器
     */
    void unregisterChangeListener(IndexChangeListener listener);

    /**
     * 开始监听变更日志
     */
    void startWatchingChangeLog();

    /**
     * 停止监听变更日志
     */
    void stopWatchingChangeLog();
}
