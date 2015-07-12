package nebhistory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan
@EnableAutoConfiguration
public class NebhistoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(NebhistoryApplication.class, args);
    }
}
