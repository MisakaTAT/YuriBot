package com.mikuac.bot.repository;

import com.mikuac.bot.entity.PluginSwitchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

/**
 * @author Zero
 * @date 2020/11/20 11:07
 */
public interface PluginSwitchRepository extends JpaRepository<PluginSwitchEntity,Integer> {

    /**
     * 通过插件名查询
     * @param pluginName
     * @return
     */
    @Query(value = "SELECT * FROM plugin_switch WHERE plugin_name = :pluginName", nativeQuery = true)
    Optional<PluginSwitchEntity> findByPluginName(String pluginName);

    /**
     * 查询插件是否群组禁用
     * @param pluginName
     * @return
     */
    @Query(value = "SELECT group_disable FROM plugin_switch WHERE plugin_name = :pluginName", nativeQuery = true)
    Boolean isGroupDisable (String pluginName);

    /**
     * 查询插件是否私聊禁用
     * @param pluginName
     * @return
     */
    @Query(value = "SELECT private_disable FROM plugin_switch WHERE plugin_name = :pluginName", nativeQuery = true)
    Boolean isPrivateDisable (String pluginName);

    /**
     * 查询插件是否全局禁用
     * @param pluginName
     * @return
     */
    @Query(value = "SELECT global_disable FROM plugin_switch WHERE plugin_name = :pluginName", nativeQuery = true)
    Boolean isGlobalDisable (String pluginName);

    /**
     * 群组禁用
     * @param pluginName
     * @param disable
     */
    @Modifying
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "UPDATE plugin_switch SET group_disable = :disable WHERE plugin_name = :pluginName", nativeQuery = true)
    void groupDisable (String pluginName, Boolean disable);

    /**
     * 私聊禁用
     * @param pluginName
     * @param disable
     */
    @Modifying
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "UPDATE plugin_switch SET private_disable = :disable WHERE plugin_name = :pluginName", nativeQuery = true)
    void privateDisable (String pluginName, Boolean disable);

    /**
     * 全局禁用
     * @param pluginName
     * @param disable
     */
    @Modifying
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "UPDATE plugin_switch SET global_disable = :disable WHERE plugin_name = :pluginName", nativeQuery = true)
    void globalDisable (String pluginName, Boolean disable);

}