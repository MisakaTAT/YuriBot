package com.mikuac.bot.entity;

import lombok.Data;
import javax.persistence.*;

/**插件开关
 * @author Zero
 * @date 2020/11/20 10:52
 */
@Data
@Entity
@Table(name = "plugin_switch")
public class PluginSwitchEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "plugin_name", nullable = false)
    private String pluginName;

    @Column(name = "private_disable", nullable = false)
    private Boolean privateDisable;

    @Column(name = "group_disable", nullable = false)
    private Boolean groupDisable;

    @Column(name = "global_disable", nullable = false)
    private Boolean globalDisable;

}