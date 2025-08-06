package org.example.datn.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // ✅ Cấu hình cho ảnh tĩnh
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String uploadPath = System.getProperty("user.dir") + "/uploads/images/";
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:///" + uploadPath.replace("\\", "/"));
    }

}
