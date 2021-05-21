package com.mikuac.bot.repository;

import com.mikuac.bot.entity.BanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author Zero
 * @date 2020/12/4 11:14
 */
@Repository
public interface BanRepository extends JpaRepository<BanEntity, Integer> {

    /**
     * 根据用户ID查询
     *
     * @param userId
     * @return
     */
    @Query(value = "SELECT * FROM ban WHERE user_id = :userId", nativeQuery = true)
    Optional<BanEntity> findByUserId(long userId);

}
