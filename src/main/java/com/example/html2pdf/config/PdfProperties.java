package com.example.html2pdf.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * xxx
 *
 * @author zhangxin
 * @since 2022/7/6 8:12
 */
@Configuration
@ConfigurationProperties(prefix = "temp")
public class PdfProperties {

    private static String html;

    private static String pdf;


    public static String getPdf() {
        return pdf;
    }

    public void setPdf(String pdf) {
        PdfProperties.pdf = pdf;
    }

    public static String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        PdfProperties.html = html;
    }

}
