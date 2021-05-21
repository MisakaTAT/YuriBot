package com.mikuac.bot.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * 敏感词
 *
 * @author Zero
 */
@Data
@Entity
@Table(name = "sensitive_words")
public class SensitiveWordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "word", nullable = false, unique = true)
    private String word;

}
