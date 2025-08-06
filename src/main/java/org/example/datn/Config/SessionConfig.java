package org.example.datn.Config;

import org.springframework.boot.web.servlet.server.CookieSameSiteSupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SessionConfig {

    // Cấu hình cookie cho phép frontend khác domain nhận được JSESSIONID
    @Bean
    public CookieSameSiteSupplier applicationCookieSameSiteSupplier() {
        return CookieSameSiteSupplier.ofLax().whenHasName("JSESSIONID");
        // Nếu cần cookie gửi đi với mọi request, dùng:
        // return CookieSameSiteSupplier.ofNone().whenHasName("JSESSIONID");
    }
}
