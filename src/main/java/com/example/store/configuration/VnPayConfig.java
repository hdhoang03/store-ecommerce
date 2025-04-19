package com.example.store.configuration;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;
import java.util.*;
@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VnPayConfig {
    static final String TIME_ZONE = "Etc/GMT+7";
    static final String DATE_PATTERN = "yyyyMMddHHmmss";
    static final String CURRENCY_CODE = "VND";
    static final String LOCALE = "vn";

    @Getter
    @Value("${payment.vnPay.url}")
    String vnp_PayUrl;

    @Value("${payment.vnPay.returnUrl}")
    String vnp_ReturnUrl;

    @Value("${payment.vnPay.tmnCode}")
    String vnp_TmnCode;

    @Getter
    @Value("${payment.vnPay.secretKey}")
    String secretKey;

    @Value("${payment.vnPay.version}")
    String vnp_Version;

    @Value("${payment.vnPay.command}")
    String vnp_Command;

    @Value("${payment.vnPay.orderType}")
    String orderType;

    public Map<String, String> getVNPayConfig(String orderCode){//String txnRef, String orderInfo
        Map<String, String> vnParamsMap = new HashMap<>();

        vnParamsMap.put("vnp_Version", vnp_Version);
        vnParamsMap.put("vnp_Command", vnp_Command);
        vnParamsMap.put("vnp_TmnCode", vnp_TmnCode);
        vnParamsMap.put("vnp_CurrCode", CURRENCY_CODE);

        vnParamsMap.put("vnp_TxnRef", orderCode);//
        vnParamsMap.put("vnp_OrderInfo", "Thanh toan don hang" + orderCode);//

        vnParamsMap.put("vnp_OrderType", orderType);
        vnParamsMap.put("vnp_Locale", LOCALE);
        vnParamsMap.put("vnp_ReturnUrl", vnp_ReturnUrl);

        vnParamsMap.put("vnp_CreateDate", getCurrentTime());
        vnParamsMap.put("vnp_ExpireDate", getExpireTime(15));

        //Tạo mã đơn hàng ngẫu nhiên
//        vnParamsMap.put("vnp_TxnRef", VnPayUtil.getRandomNumber(8));
//        vnParamsMap.put("vnp_OrderInfo", "Thanh toan don hang: " + VnPayUtil.getRandomNumber(8));

//        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
//        String vnpCreateDate = formatter.format(calendar.getTime());
//        vnParamsMap.put("vnp_CreateDate", vnpCreateDate);
//        calendar.add(Calendar.MINUTE, 15);
//        String vnp_ExpireDate = formatter.format(calendar.getTime());
//        vnParamsMap.put("vnp_ExpireDate", vnp_ExpireDate);
//        return vnParamsMap;
        return vnParamsMap;
    }

    private String getCurrentTime(){
        return formatDate(Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE)));
    }

    private String getExpireTime(int minutes){
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.add(Calendar.MINUTE, minutes);
        return formatDate(calendar);
    }

    private String formatDate(Calendar calendar){
        return new SimpleDateFormat(DATE_PATTERN).format(calendar.getTime());
    }
}
