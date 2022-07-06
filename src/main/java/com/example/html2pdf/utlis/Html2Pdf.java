package com.example.html2pdf.utlis;

import cn.hutool.core.util.StrUtil;
import com.example.html2pdf.config.PdfProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * 需要配置wkhtmltopdf环境变量
 *
 * @author zhangxin
 * @since 2022/7/5 17:55
 */
@Slf4j
public class Html2Pdf {

    /**
     * url转pdf
     *
     * @param url      url
     * @param response response
     */
    public static void html2pdf(String url, HttpServletResponse response) {
        //生成pdf文件名 时间戳
        String pdfName = String.valueOf(System.currentTimeMillis());
        //pdf生成路径
        String pdfFile = osPath(String.format("%s/%s.pdf", PdfProperties.getPdf(), pdfName));
        try {
            //html转pdf
            convert(url, pdfFile);
            // 以流的形式下载文件。
            InputStream fis = new BufferedInputStream(new FileInputStream(pdfFile));
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            // 清空response
            response.reset();
            // 设置response的Header
            response.addHeader("Content-Disposition", "attachment;filename=" + pdfName + ".pdf");
            OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/octet-stream");
            toClient.write(buffer);
            toClient.flush();
            toClient.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            File pdfTempFile = new File(pdfFile);
            if (pdfTempFile.exists()) {
                pdfTempFile.delete();
            }
        }
    }

    /**
     * html文件转pdf
     *
     * @param htmlFile html文件
     * @param response response
     */
    public static void html2pdf(MultipartFile htmlFile, HttpServletResponse response) {
        //获取文件名
        String filename = htmlFile.getOriginalFilename();
        if (null != filename && !"".equals(filename)) {
            //获得前缀名称
            String prefixName = filename.substring(0, filename.indexOf("."));
            String ext = filename.substring(filename.lastIndexOf(".") + 1);
            if (!"html".equalsIgnoreCase(ext)) {
                throw new RuntimeException("unsupported file format");
            }
            //pdf生成路径
            String pdfFile = osPath(String.format("%s/%s.pdf", PdfProperties.getPdf(), prefixName));
            //html临时保存路径
            String temp = osPath(String.format("%s/%s", PdfProperties.getHtml(), filename));
            File tempHtmlFile = new File(temp);
            //如果临时文件保存路径不存在 则创建路径
            if (!tempHtmlFile.exists()) {
                tempHtmlFile.mkdirs();
            }
            try {
                //创建html临时文件
                htmlFile.transferTo(tempHtmlFile);
                String srcTempFilePath = tempHtmlFile.getAbsolutePath();
                log.info("srcTempFilePath：{}", srcTempFilePath);
                //html转pdf
                convert(srcTempFilePath, pdfFile);
                // 以流的形式下载文件。
                InputStream fis = new BufferedInputStream(new FileInputStream(pdfFile));
                byte[] buffer = new byte[fis.available()];
                fis.read(buffer);
                fis.close();
                // 清空response
                response.reset();
                // 设置response的Header
                response.addHeader("Content-Disposition", "attachment;filename=" + prefixName + ".pdf");
                OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
                response.setContentType("application/octet-stream");
                toClient.write(buffer);
                toClient.flush();
                toClient.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                //删除生成的临时文件
                if (tempHtmlFile.exists()) {
                    tempHtmlFile.delete();
                }
                File pdfTempFile = new File(pdfFile);
                if (pdfTempFile.exists()) {
                    pdfTempFile.delete();
                }
            }
        } else {
            log.info("filename can not be blank");
        }
    }

    /**
     * html转pdf
     *
     * @param srcPath  html路径，可以是硬盘上的路径，也可以是网络路径
     * @param destPath pdf保存路径
     */
    public static void convert(String srcPath, String destPath) {
        File file = new File(destPath);
        File parent = file.getParentFile();
        //如果pdf保存路径不存在，则创建路径
        if (!parent.exists()) {
            parent.mkdirs();
        }
        StringBuilder cmd = new StringBuilder();
        cmd.append("wkhtmltopdf");
        cmd.append(" ");
        //页眉下面的线
        cmd.append("  --header-line");
        //页眉中间内容
        cmd.append("  --header-center 这里是页眉这里是页眉这里是页眉这里是页眉 ");
        //设置页面上边距 (default 10mm)
        //cmd.append("  --margin-top 30mm ");
        //设置页眉和内容的距离 默认0
        cmd.append(" --header-spacing 10 ");
        cmd.append(srcPath);
        cmd.append(" ");
        cmd.append(destPath);
        try {
            //执行拼接后的命令
            Process proc = Runtime.getRuntime().exec(cmd.toString());
            HtmlToPdfInterceptor error = new HtmlToPdfInterceptor(proc.getErrorStream());
            HtmlToPdfInterceptor output = new HtmlToPdfInterceptor(proc.getInputStream());
            error.start();
            output.start();
            proc.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String osPath(String path) {
        if (StrUtil.isBlank(path)) {
            return path;
        }
        String os = System.getProperty("os.name").toLowerCase();
        if ("linux".equals(os)) {
            return path.replace("\\", "/");
        } else if ("windows".equals(os)) {
            return path.replace("/", "\\");
        } else {
            return path;
        }
    }

    public static void main(String[] args) {
        convert("C:\\Users\\XIN\\Desktop\\landmark.html", "D:\\htmltopdf\\demo.pdf");
    }

}
