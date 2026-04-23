package com.example.mswallet.Controller;

import com.example.mswallet.Dto.WalletRequestDTO;
import com.example.mswallet.Dto.WalletResponseDTO;
import com.example.mswallet.Service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    // ============================================================
    // 🟩 Crear Wallet
    // ============================================================
    @PostMapping
    public ResponseEntity<WalletResponseDTO> createWallet(@RequestBody WalletRequestDTO request) {
        WalletResponseDTO response = walletService.createWallet(request);
        return ResponseEntity.ok(response);
    }

    // ============================================================
    // 🟦 Obtener Wallet por ID
    // ============================================================
    @GetMapping("/{id}")
    public ResponseEntity<WalletResponseDTO> getWalletById(@PathVariable Long id) {
        WalletResponseDTO response = walletService.getWalletById(id);
        return ResponseEntity.ok(response);
    }

    // ============================================================
    // 🟨 Obtener Wallet por UserId
    // ============================================================
    @GetMapping("/user/{userId}")
    public ResponseEntity<WalletResponseDTO> getWalletByUserId(@PathVariable Long userId) {
        WalletResponseDTO response = walletService.getWalletByUserId(userId);
        return ResponseEntity.ok(response);
    }

    // ============================================================
    // 🟥 Eliminar Wallet por UserId
    // ============================================================
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Void> deleteWallet(@PathVariable Long userId) {
        walletService.deleteWallet(userId);
        return ResponseEntity.noContent().build();
    }

    // ============================================================
    // ⭐⭐⭐ 🔥 RESTAR SALDO DE LA WALLET (GASTO DESDE EVENTOS)
    // PATCH /wallets/{userId}/subtract
    // ============================================================
    @PatchMapping("/{userId}/subtract")
    public ResponseEntity<String> subtractFromWallet(
            @PathVariable Long userId,
            @RequestBody Map<String, Double> body
    ) {
        Double amount = body.get("amount");

        if (amount == null || amount <= 0) {
            return ResponseEntity.badRequest().body("Monto inválido");
        }

        walletService.subtractAmount(userId, amount);
        return ResponseEntity.ok("Monto descontado correctamente");
    }
}
