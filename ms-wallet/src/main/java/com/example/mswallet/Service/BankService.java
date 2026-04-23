package com.example.mswallet.Service;

import com.example.mswallet.Entity.Bank;
import com.example.mswallet.Exceptions.ResourceNotFoundException;
import com.example.mswallet.Repository.BankRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BankService {

    private final BankRepository bankRepository;

    public Bank createBank(Bank bank) {
        return bankRepository.save(bank);
    }

    public List<Bank> getAllBanks() {
        return bankRepository.findAll();
    }

    public Bank getBankById(Long id) {
        return bankRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Banco no encontrado"));
    }
}
