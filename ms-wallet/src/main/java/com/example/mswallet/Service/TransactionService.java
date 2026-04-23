package com.example.mswallet.Service;

import com.example.mswallet.Dto.*;
import com.example.mswallet.Entity.Transaction;
import com.example.mswallet.Entity.Wallet;
import com.example.mswallet.Exceptions.ResourceNotFoundException;
import com.example.mswallet.Feign.CategoryFeignClient;
import com.example.mswallet.Feign.EventFeignClient;
import com.example.mswallet.Feign.GoalFeignClient;
import com.example.mswallet.Repository.TransactionRepository;
import com.example.mswallet.Repository.WalletRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final CategoryFeignClient categoryClient;
    private final EventFeignClient eventClient;
    private final GoalFeignClient goalClient;

    @Autowired
    public TransactionService(
            TransactionRepository transactionRepository,
            WalletRepository walletRepository,
            CategoryFeignClient categoryClient,
            EventFeignClient eventClient,
            GoalFeignClient goalClient
    ) {
        this.transactionRepository = transactionRepository;
        this.walletRepository = walletRepository;
        this.categoryClient = categoryClient;
        this.eventClient = eventClient;
        this.goalClient = goalClient;
    }

    // =====================================================================
    // CREATE
    // =====================================================================
    @Transactional
    public TransactionResponseDTO createTransaction(TransactionRequestDTO request) {

        // ------------------------------
        // 1. Validar Wallet del usuario
        // ------------------------------
        Wallet wallet = walletRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Wallet no encontrada"));

        // ------------------------------
        // 2. Obtener categoría por ID
        // ------------------------------
        CategoryDTO category = null;
        SubcategoryDTO subcategory = null;

        if (request.getCategoryId() != null) {
            try {
                category = categoryClient.getCategoryById(request.getCategoryId());
            } catch (Exception e) {
                log.warn("⚠ La categoría {} no existe.", request.getCategoryId());
            }
        }

        if (request.getCategoryId() != null && request.getSubcategoryId() != null) {
            try {
                subcategory = categoryClient.getSubcategoryById(
                        request.getCategoryId(),
                        request.getSubcategoryId()
                );
            } catch (Exception e) {
                log.warn("⚠ La subcategoría {} no existe.", request.getSubcategoryId());
            }
        }

        // ------------------------------
        // 3. Validar EVENTO opcional
        // ------------------------------
        EventDTO event = null;
        if (request.getEventId() != null) {
            try {
                event = eventClient.getEventById(request.getEventId());
            } catch (Exception e) {
                log.warn("⚠ Evento {} no encontrado.", request.getEventId());
            }
        }

        // ------------------------------
        // 4. Crear transacción
        // ------------------------------
        Transaction transaction = new Transaction();
        transaction.setWalletId(wallet.getId());
        transaction.setUserId(request.getUserId());
        transaction.setCategoryId(request.getCategoryId());
        transaction.setSubcategoryId(request.getSubcategoryId());
        transaction.setEventId(request.getEventId());
        transaction.setType(request.getType());
        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription());
        transaction.setTransactionDate(LocalDateTime.now());

        transaction = transactionRepository.save(transaction);

        // ------------------------------
        // 5. Actualizar balance
        // ------------------------------
        if (request.getType() == Transaction.TransactionType.INCOME) {
            wallet.setBalance(wallet.getBalance().add(request.getAmount()));
        } else {
            wallet.setBalance(wallet.getBalance().subtract(request.getAmount()));
        }

        walletRepository.save(wallet);

        // ------------------------------
        // 6. Aportar a meta (GOAL)
        // ------------------------------
        if (request.getGoalId() != null &&
                request.getType() == Transaction.TransactionType.EXPENSE) {

            try {
                goalClient.updateGoalAmount(request.getGoalId(), request.getAmount());
                log.info("✔ Meta {} actualizada con {}", request.getGoalId(), request.getAmount());
            } catch (Exception e) {
                log.warn("⚠ No se pudo actualizar la meta {}", request.getGoalId());
            }
        }

        // ------------------------------
        // 7. Respuesta final
        // ------------------------------
        return buildTransactionResponse(transaction, category, subcategory, event);
    }

    // =====================================================================
    // LISTAR TRANSACCIONES
    // =====================================================================
    public List<TransactionResponseDTO> getUserTransactions(Long userId, LocalDate startDate, LocalDate endDate) {

        List<Transaction> transactions;

        if (startDate != null && endDate != null) {
            LocalDateTime start = startDate.atStartOfDay();
            LocalDateTime end = endDate.atTime(23, 59, 59);
            transactions = transactionRepository.findByUserIdAndTransactionDateBetween(userId, start, end);
        } else {
            transactions = transactionRepository.findByUserId(userId);
        }

        return transactions.stream()
                .map(this::enrichTransaction)
                .collect(Collectors.toList());
    }

    // =====================================================================
    // ENRIQUECER TRANSACCIÓN (evitar errores)
    // =====================================================================
    private TransactionResponseDTO enrichTransaction(Transaction tx) {

        CategoryDTO category = null;
        SubcategoryDTO subcategory = null;
        EventDTO event = null;

        if (tx.getCategoryId() != null) {
            try {
                category = categoryClient.getCategoryById(tx.getCategoryId());
            } catch (Exception ignored) {}
        }

        if (tx.getCategoryId() != null && tx.getSubcategoryId() != null) {
            try {
                subcategory = categoryClient.getSubcategoryById(
                        tx.getCategoryId(),
                        tx.getSubcategoryId()
                );
            } catch (Exception ignored) {}
        }

        if (tx.getEventId() != null) {
            try {
                event = eventClient.getEventById(tx.getEventId());
            } catch (Exception ignored) {}
        }

        return buildTransactionResponse(tx, category, subcategory, event);
    }

    // =====================================================================
    // ARMAR RESPUESTA FINAL
    // =====================================================================
    private TransactionResponseDTO buildTransactionResponse(
            Transaction tx,
            CategoryDTO category,
            SubcategoryDTO subcategory,
            EventDTO event
    ) {
        TransactionResponseDTO dto = new TransactionResponseDTO();
        dto.setId(tx.getId());
        dto.setWalletId(tx.getWalletId());
        dto.setUserId(tx.getUserId());
        dto.setCategory(category);
        dto.setSubcategory(subcategory);
        dto.setEvent(event);
        dto.setType(tx.getType());
        dto.setAmount(tx.getAmount());
        dto.setDescription(tx.getDescription());
        dto.setTransactionDate(tx.getTransactionDate());
        return dto;
    }
    @Transactional
    public void registrarDesdeOperacion(TransactionToWalletDTO op) {

        Wallet wallet = walletRepository.findByUserId(op.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Wallet no encontrada"));

        // Registrar transacción
        Transaction tx = new Transaction();
        tx.setUserId(op.getUserId());
        tx.setWalletId(wallet.getId());
        tx.setAmount(op.getAmount());
        tx.setDescription(op.getDescription());
        tx.setTransactionDate(LocalDateTime.now());

        if (op.getType().equals("INCOME")) {
            tx.setType(Transaction.TransactionType.INCOME);
            wallet.setBalance(wallet.getBalance().add(op.getAmount()));
        } else {
            tx.setType(Transaction.TransactionType.EXPENSE);

            if (wallet.getBalance().compareTo(op.getAmount()) < 0) {
                throw new IllegalStateException("Fondos insuficientes en la billetera");
            }
            wallet.setBalance(wallet.getBalance().subtract(op.getAmount()));
        }

        walletRepository.save(wallet);
        transactionRepository.save(tx);
    }

}
