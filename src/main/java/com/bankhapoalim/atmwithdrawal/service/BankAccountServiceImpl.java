package com.bankhapoalim.atmwithdrawal.service;

import com.bankhapoalim.atmwithdrawal.entity.BankAccount;
import com.bankhapoalim.atmwithdrawal.entity.Card;
import com.bankhapoalim.atmwithdrawal.repository.BankAccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@Slf4j
public class BankAccountServiceImpl implements  BankAccountService {

    //TODO: Can be exposed to config file as well
    private static final long CACHE_EXPIRATION = 10; // TTL - Cache expiration time in seconds

    private final BankAccountRepository bankAccountRepository;

    @Autowired
    private RedisTemplate<String, BankAccount> redisTemplate;

    @Autowired
    public BankAccountServiceImpl(BankAccountRepository bankAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
    }

    /**
     * Retrieves a bank account from cache if available, otherwise fetches it from the database.
     * Caching is based on the cardNumber parameter.
     *
     * @param card object which contains the number to look up the bank account.
     * @return The bank account associated with the card number, or null if not found.
     */
    @Override
    @Cacheable(value = "bankAccountCache", key = "#card.cardNumber")
    public BankAccount getAccountFromCache(Card card) {
        String cardNumber = card.getCardNumber();
        log.info("Checking cache for bank account with card number: {}", cardNumber);
        Optional<BankAccount> cachedAccount = Optional.ofNullable(redisTemplate.opsForValue().get(cardNumber));
        if (cachedAccount.isPresent()) {
            log.debug("Retrieved bank account from cache for card number: {}", cardNumber);
            return cachedAccount.get();
        } else {
            log.info("Bank account not found in cache for card number: {}. Fetching from database.",cardNumber);
            BankAccount account = bankAccountRepository.findByCards(card);
            if (account != null) {
                log.info("Fetched bank account from database for card number: {}", cardNumber);
                redisTemplate.opsForValue().set(cardNumber, account, CACHE_EXPIRATION);
                log.info("Cached bank account with card number: {}", cardNumber);
            } else {
                log.warn("Bank account not found in database for card number: {}", cardNumber);
            }
            return account;
        }
    }

    /**
     * Reverses a balance deduction by adding a specified amount to the account balance.
     *
     * @param bankAccount The account to be balanced.
     * @param amountToAdd The amount to add back to the account balance.
     * @param reason      The reason or description for the balance reversal.
     * @throws IllegalArgumentException if the Bank account is null.
     */
    @Override
    public void reverseBalance(BankAccount bankAccount, BigDecimal amountToAdd, String reason) {
        if ( bankAccount!=null) {
            BigDecimal currentBalance =  bankAccount.getBalance();
            BigDecimal updatedBalance = currentBalance.add(amountToAdd);
            bankAccount.setBalance(updatedBalance);
            bankAccountRepository.save(bankAccount);
            log.info("Balance reversed for account ID {} by adding {} due to: {}", bankAccount.getAccountId(), amountToAdd, reason);
        } else {
            log.warn("Null bank account encountered in reverseBalance method");
            throw new IllegalArgumentException("Bank account is null");
        }
    }
}

