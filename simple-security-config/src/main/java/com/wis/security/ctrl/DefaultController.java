package com.wis.security.ctrl;

import com.google.code.kaptcha.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Created by liuBo
 * 2020/1/30.
 */
@Controller
public class DefaultController {
    @Autowired
    private Producer producer;

    @GetMapping("/captcha.jpg")
    public void captcha(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("image/jpeg");
        String capText = producer.createText();
        request.getSession().setAttribute(com.google.code.kaptcha.Constants.KAPTCHA_SESSION_KEY,capText);
        BufferedImage bi =  producer.createImage(capText);
        ServletOutputStream outputStream = response.getOutputStream();
        ImageIO.write(bi,"jpg",outputStream);
        try {
            outputStream.flush();
        } finally {
            outputStream.close();
        }
    }

    @RequestMapping("/login")
    public  Object toLoginPage(){
        return "login";
    }
}
