package com.example.html2pdf.controller;

import com.example.html2pdf.utlis.Html2Pdf;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * @author zhangxin
 * @since 2022/7/5 18:03
 */
@RestController
public class ToPdfController {

    @PostMapping("/htmlToPdf")
    public void htmlToPdf(@RequestParam("htmlFile") MultipartFile htmlFile, HttpServletResponse response) {
        Html2Pdf.html2pdf(htmlFile, response);
    }

    @PostMapping("/urlToPdf")
    public void urlToPdf(@RequestParam String url, HttpServletResponse response) {
        Html2Pdf.html2pdf(url, response);
    }

}
