package io.bit.busnaeryeo;

import io.bit.busnaeryeo.repository.DiaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
public class BusnaeryeoApplication {

	public static void main(String[] args) {

		SpringApplication.run(BusnaeryeoApplication.class, args);

	}

}
