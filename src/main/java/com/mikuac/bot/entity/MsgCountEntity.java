package com.mikuac.bot.entity;

import lombok.Data;
import javax.persistence.*;

/**
 * @author Zero
 * @date 2020/11/18 9:35
 */
@Data
@Entity
@Table(name = "msg_count")
public class MsgCountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "group_id", nullable = false)
    private Long groupId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "today_msg_count", nullable = false)
    private Integer todayMsgCount;

    @Column(name = "all_msg_count", nullable = false)
    private Integer allMsgCount;

}
