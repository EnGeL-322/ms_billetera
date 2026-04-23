package com.example.mswallet.Feign;

import com.example.mswallet.Dto.AuthUserDto;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-auth-service", path = "/auth")
public interface UserFeignClient {

    @GetMapping("/{id}")
    @CircuitBreaker(name = "userByIdCB", fallbackMethod = "fallbackGetUserById")
    AuthUserDto getUserById(@PathVariable Long id);

    default AuthUserDto fallbackGetUserById(Long id, Throwable e) {
        System.err.println(" CircuitBreaker: ms-auth no disponible (ID: " + id + ")");
        return AuthUserDto.builder()
                .userName("usuario_desconocido")
                .build();
    }
}
