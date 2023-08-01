package com.datn.application.utils;

import javax.servlet.http.HttpServletRequest;

public class PayPalUtil {

    //xây dựng URL cho việc tích hợp thanh toán PayPal
    public static String getBaseURL(HttpServletRequest request) {
        String scheme = request.getScheme(); //lấy giao thức http
        String serverName = request.getServerName(); //lấy tên máy chủ
        int serverPort = request.getServerPort(); //lấy cổng máy chủ
        String contextPath = request.getContextPath(); //lấy đường dẫn context

        StringBuffer url =  new StringBuffer();
        url.append(scheme).append("://").append(serverName);
        // Kiểm tra nếu cổng máy chủ không phải là cổng mặc định (80 cho HTTP, 443 cho HTTPS)
        // Thêm cổng vào URL nếu nó khác cổng mặc định để tạo URL hoàn chỉnh
        if ((serverPort != 80) && (serverPort != 443)) {
            url.append(":").append(serverPort);
        }
        url.append(contextPath);
        if(url.toString().endsWith("/")){
            url.append("/");
        }
        return url.toString();
    }
}
