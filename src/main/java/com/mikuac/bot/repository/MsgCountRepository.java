package com.mikuac.bot.repository;

import com.mikuac.bot.entity.MsgCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * @author Zero
 * @date 2020/11/18 9:50
 */
@Repository
public interface MsgCountRepository extends JpaRepository<MsgCount,Long> {

    /**
     * 查询群组当日发言次数最大值
     * @param groupId
     * @return
     */
    @Query(value = "SELECT MAX(today_msg_count) FROM msg_count WHERE group_id = :groupId", nativeQuery = true)
    Optional<MsgCount> findTodayMaxCount(long groupId);

    /**
     * 重置每日统计次数为0
     */
    @Query(value = "UPDATE msg_count SET today_msg_count = 0", nativeQuery = true)
    void setDefaultTodayMsgCount ();

}
