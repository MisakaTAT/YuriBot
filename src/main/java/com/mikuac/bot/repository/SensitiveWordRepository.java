package com.mikuac.bot.repository;

import com.mikuac.bot.entity.SensitiveWordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.Optional;

/**
 * @author Zero
 */
public interface SensitiveWordRepository extends JpaRepository<SensitiveWordEntity, Integer> {


    /**
     * 查找敏感词是否已存在
     *
     * @param word 敏感词
     * @return
     */
    @Query(value = "SELECT * FROM sensitive_words WHERE word = :word", nativeQuery = true)
    Optional<SensitiveWordEntity> findWord(String word);

    /**
     * 删除敏感词
     *
     * @param word 敏感词
     */
    @Modifying
    @Transactional(rollbackOn = {})
    @Query(value = "delete from sensitive_words where word = :word", nativeQuery = true)
    void deleteByWord(String word);

}
