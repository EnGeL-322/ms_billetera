package com.example.mswallet.Controller;

import com.example.mswallet.Entity.Bank;
import com.example.mswallet.Service.BankService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bancos")
@RequiredArgsConstructor
public class BankController {

    private final BankService bankService;

    // Crear banco (Yape, BBVA, Plin, etc.)
    @PostMapping
    public ResponseEntity<Bank> createBank(@RequestBody Bank bank) {
        return ResponseEntity.ok(bankService.createBank(bank));
    }

    // Listar todos los bancos
    @GetMapping
    public ResponseEntity<List<Bank>> getAllBanks() {
        return ResponseEntity.ok(bankService.getAllBanks());
    }

    // Obtener banco por ID (opcional, por si necesitas)
    @GetMapping("/{id}")
    public ResponseEntity<Bank> getBankById(@PathVariable Long id) {
        return ResponseEntity.ok(bankService.getBankById(id));
    }
}
