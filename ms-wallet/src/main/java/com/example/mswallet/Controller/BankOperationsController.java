package com.example.mswallet.Controller;

import com.example.mswallet.Dto.BankIncomeRequestDTO;
import com.example.mswallet.Dto.BankIncomeResponseDTO;
import com.example.mswallet.Dto.BankTransferDTO;
import com.example.mswallet.Dto.TransactionResponseDTO;
import com.example.mswallet.Entity.UserBankBalance;
import com.example.mswallet.Service.BankServiceLogic;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bank-ops")
@RequiredArgsConstructor
public class BankOperationsController {

    private final BankServiceLogic bankService;

    // 1) AGREGAR DINERO AL BANCO
    @PostMapping("/add-income")
    public ResponseEntity<?> addIncome(@RequestBody BankIncomeRequestDTO dto) {
        return ResponseEntity.ok(bankService.addIncome(dto));
    }

    // 2) OBTENER HISTORIAL
    @GetMapping("/history/{userId}/{bankId}")
    public ResponseEntity<List<BankIncomeResponseDTO>> getHistory(
            @PathVariable Long userId,
            @PathVariable Long bankId) {

        return ResponseEntity.ok(bankService.getIncomeHistory(userId, bankId));
    }

    // 3) TRANSFERIR A WALLET
    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponseDTO> transfer(
            @RequestBody BankTransferDTO dto) {

        return ResponseEntity.ok(bankService.transferToWallet(dto));
    }

    // 4) OBTENER SALDO ACTUAL DEL BANCO
    @GetMapping("/balance/{userId}/{bankId}")
    public ResponseEntity<UserBankBalance> getBalance(
            @PathVariable Long userId,
            @PathVariable Long bankId) {

        return ResponseEntity.ok(bankService.getBankBalance(userId, bankId));
    }

}
