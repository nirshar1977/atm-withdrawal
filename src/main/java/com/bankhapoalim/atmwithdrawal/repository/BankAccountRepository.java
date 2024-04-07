package com.bankhapoalim.atmwithdrawal.repository;

import com.bankhapoalim.atmwithdrawal.entity.BankAccount;
import com.bankhapoalim.atmwithdrawal.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {
    BankAccount findByCards(Card card);
}


