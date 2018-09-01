package com.tc.banking.atm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tc.banking.atm.entity.AccountEntity;

public interface AccountRepository extends JpaRepository<AccountEntity, Integer> {

	AccountEntity findByAccountNumber(int accountNumber);

}
