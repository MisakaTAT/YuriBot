package com.mikuac.bot.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * 封禁
 *
 * @author Zero
 * @date 2020/12/4 11:09
 */
@Data
@Entity
@Table(name = "ban")
public class BanEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    /**
     * QQ账号，如果封禁为全局封禁，包括群组
     */
    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    /**
     * 是否被封禁
     */
    @Column(name = "is_banned", nullable = false)
    private Boolean isBanned;

}
