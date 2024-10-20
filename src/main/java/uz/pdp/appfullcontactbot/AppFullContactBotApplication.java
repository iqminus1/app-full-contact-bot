package uz.pdp.appfullcontactbot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.commons.codec.CharEncoding;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;

@EnableScheduling
@EnableAsync
@SpringBootApplication
public class AppFullContactBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppFullContactBotApplication.class, args);
    }

    @Bean
    public ResourceBundleMessageSource messageSourceResourceBundle() {
        ResourceBundleMessageSource resourceBundleMessageSource = new ResourceBundleMessageSource();
        resourceBundleMessageSource.setBasename("classpath:messages");
        resourceBundleMessageSource.setFallbackToSystemLocale(false);
        resourceBundleMessageSource.setDefaultEncoding(CharEncoding.UTF_8);
        return resourceBundleMessageSource;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }


    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    @Bean
    public DecimalFormat decimalFormat() {
        return new DecimalFormat("###,###,###");
    }

    @Bean
    public DateTimeFormatter dateTimeFormatter() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    }
}
