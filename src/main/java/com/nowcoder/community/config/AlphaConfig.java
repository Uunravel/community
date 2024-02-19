package com.nowcoder.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;

@Configuration
public class AlphaConfig {//配置类，装配第三方的bean
    @Bean
    public SimpleDateFormat simpleDateFormate(){
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }


}
