package top.ayang818.pfstudio;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("top.ayang818.pfstudio.mapper")
@SpringBootApplication
public class PfstudioApplication {

    public static void main(String[] args) {
        SpringApplication.run(PfstudioApplication.class, args);
    }

}
