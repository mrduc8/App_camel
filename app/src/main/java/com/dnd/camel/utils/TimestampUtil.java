package com.dnd.camel.utils;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimestampUtil {
    @SuppressLint("DefaultLocale")
    public static String convertTimestampToDateString(long timestamp) {
        long now = System.currentTimeMillis();
        long elapsedTime = now - timestamp;

        long seconds = elapsedTime / 1000;

        // Xác định đơn vị thời gian phù hợp để hiển thị
        if (seconds < 60) {
            return "vừa đăng";
        } else if (seconds < 60*60) {
            long minutes = seconds / 60;
            return String.format("%d phút trước", minutes);
        } else if (seconds < 60*60*24) {
            // Hiển thị số giờ trước
            long hours = seconds / (60*60);
            return String.format("%d giờ trước", hours);
        } else {
            // Hiển thị ngày tháng
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd 'thg' MM", Locale.getDefault());
            Date date = new Date(timestamp);
            return dateFormat.format(date);
        }
    }
}
