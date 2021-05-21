package com.mikuac.bot.repository;

import com.mikuac.bot.entity.SensitiveWordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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

}
