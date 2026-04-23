package com.example.mswallet.Controller;

import com.example.mswallet.Dto.TransactionRequestDTO;
import com.example.mswallet.Dto.TransactionResponseDTO;
import com.example.mswallet.Dto.TransactionToWalletDTO;
import com.example.mswallet.Service.TransactionService;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    // ======================================================
    // 1. CREAR TRANSACCIÓN NORMAL
    // ======================================================
    @PostMapping
    public ResponseEntity<TransactionResponseDTO> createTransaction(
            @RequestBody TransactionRequestDTO request) {

        TransactionResponseDTO response = transactionService.createTransaction(request);
        return ResponseEntity.ok(response);
    }

    // ======================================================
    // 2. LISTAR TRANSACCIONES POR USUARIO
    // ======================================================
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TransactionResponseDTO>> getUserTransactions(
            @PathVariable Long userId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<TransactionResponseDTO> transactions =
                transactionService.getUserTransactions(userId, startDate, endDate);

        return ResponseEntity.ok(transactions);
    }

    // ======================================================
    // ⭐ 3. REGISTRAR TRANSACCIÓN QUE VIENE DE MS-OPERACIONES
    // ======================================================
    @PostMapping("/from-operaciones")
    public ResponseEntity<Void> registrarDesdeOperacion(
            @RequestBody TransactionToWalletDTO dto
    ) {
        transactionService.registrarDesdeOperacion(dto);
        return ResponseEntity.ok().build();
    }
}