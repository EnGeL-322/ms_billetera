package com.example.mswallet.Repository;

import com.example.mswallet.Entity.UserBankBalance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface UserBankBalanceRepository extends JpaRepository<UserBankBalance, Long> {

    Optional<UserBankBalance> findByUserIdAndBankId(Long userId, Long bankId);

    List<UserBankBalance> findByUserId(Long userId);
}
