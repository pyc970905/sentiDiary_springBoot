package io.bit.busnaeryeo.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class CorsConfig implements WebMvcConfigurer {

    @Value("${jwt.response.header.acc}")
    private String jwtHeaderAcc;

    @Value("${jwt.response.header.ref}")
    private String jwtHeaderRef;
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedHeaders("*")
                .allowedOriginPatterns("http://localhost:3000","http://43.201.23.126:6379")
                .exposedHeaders(jwtHeaderAcc)
                .exposedHeaders(jwtHeaderRef)
                .allowedMethods("*")
                .allowCredentials(true);
    }
}