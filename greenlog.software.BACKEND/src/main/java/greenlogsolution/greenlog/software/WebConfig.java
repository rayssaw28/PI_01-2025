package greenlogsolution.greenlog.software;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // <-- ALTERE AQUI para aplicar a todos os endpoints
            .allowedOrigins("http://localhost:4200") 
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") 
            .allowedHeaders("*") 
            .allowCredentials(true);
    }
}