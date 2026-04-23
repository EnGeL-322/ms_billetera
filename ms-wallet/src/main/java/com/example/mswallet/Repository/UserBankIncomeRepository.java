package com.example.mswallet.Repository;

import com.example.mswallet.Entity.UserBankIncome;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserBankIncomeRepository extends JpaRepository<UserBankIncome, Long> {

    List<UserBankIncome> findByUserIdAndBankId(Long userId, Long bankId);
}
