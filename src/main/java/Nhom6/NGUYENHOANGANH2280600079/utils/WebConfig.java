package Nhom6.NGUYENHOANGANH2280600079.utils;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Cấu hình đường dẫn: "/images/**" sẽ trỏ tới thư mục "uploads/"
        // "file:uploads/" nghĩa là tìm thư mục uploads ngay tại vị trí file pom.xml
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:uploads/");
    }
}