package com.mikuac.bot.bean.saucenao;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Zero
 * @date 2020/12/3 13:32
 */
@Data
@Component
public class SauceNaoBean {

    private Header header;

    private List<Results> results;

}