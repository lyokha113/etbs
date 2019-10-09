package fpt.capstone.etbs;

import fpt.capstone.etbs.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@EnableConfigurationProperties(AppProperties.class)
@SpringBootApplication
public class EtbsApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(EtbsApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(EtbsApplication.class);
    }
}
