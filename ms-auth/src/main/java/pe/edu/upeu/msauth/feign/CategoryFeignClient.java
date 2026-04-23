package pe.edu.upeu.msauth.feign;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import pe.edu.upeu.msauth.dto.CategoryRequestDTO;
import pe.edu.upeu.msauth.dto.CategoryResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;


@FeignClient(name = "ms-categories-service")
public interface CategoryFeignClient {

    @PostMapping("/categories")
    CategoryResponseDTO createCategory(@RequestBody CategoryRequestDTO request);
}
