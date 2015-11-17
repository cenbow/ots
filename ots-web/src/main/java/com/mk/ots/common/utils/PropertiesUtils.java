package com.mk.ots.common.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * Created by Thinkpad on 2015/11/6.
 */
@Configuration
@PropertySource({"classpath:ots_config.properties","classpath:pay_config.properties"})
public class PropertiesUtils {
    @Value("${transferCheckinUsernameTime}")
    private Long transferCheckinUsernameTime;
    @Value("${inform_sale_url}")
    private String informSaleUrl;

    public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    public Long getTransferCheckinUsernameTime() {
        return transferCheckinUsernameTime;
    }

    public String getInformSaleUrl() {
        return informSaleUrl;
    }
}