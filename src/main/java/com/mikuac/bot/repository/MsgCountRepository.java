package com.mikuac.bot.repository;

import com.mikuac.bot.entity.MsgCountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

/**
 * @author Zero
 * @date 2020/11/18 9:50
 */
@Repository
public interface MsgCountRepository extends JpaRepository<MsgCountEntity,Integer> {

    /**
     * 更新字段
     * @param groupId
     * @param userId
     * @param todayMsgCount
     * @param allMsgCount
     */
    @Modifying
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "UPDATE msg_count SET today_msg_count = :todayMsgCount, all_msg_count = :allMsgCount WHERE group_id = :groupId AND user_id = :userId ", nativeQuery = true)
    void update(long groupId, long userId, int todayMsgCount, int allMsgCount);

    /**
     * 查询群组当日发言次数最大值
     * @param groupId
     * @return
     */
    @Query(value = "SELECT * FROM msg_count WHERE group_id = :groupId order by today_msg_count desc limit 1;", nativeQuery = true)
    Optional<MsgCountEntity> findTodayMaxCount(long groupId);

    /**
     * 根据群组与用户ID查询
     * @param groupId
     * @param userId
     * @return
     */
    @Query(value = "SELECT * FROM msg_count WHERE group_id = :groupId AND user_id = :userId", nativeQuery = true)
    Optional<MsgCountEntity> findByGroupAndUserId(long groupId, long userId);

    /**
     * 重置每日统计次数为0
     */
    @Modifying
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "UPDATE msg_count SET today_msg_count = 0", nativeQuery = true)
    void setDefaultTodayMsgCount ();

}
