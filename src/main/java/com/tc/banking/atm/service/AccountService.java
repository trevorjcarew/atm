package com.tc.banking.atm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tc.banking.atm.entity.AccountEntity;
import com.tc.banking.atm.repository.AccountRepository;
import com.tc.banking.atm.response.AccountCheckResponse;

@Service
public class AccountService {
	
	@Autowired
	private AccountRepository accountRepository;

	public AccountCheckResponse retrieveFunds(Integer pin, Integer accountNumber) {
		AccountCheckResponse response = new AccountCheckResponse();
		AccountEntity entity = accountRepository.findByAccountNumber(accountNumber);
		response.setBalance(entity.getBalance() + "");
		response.setAvailableFunds(entity.getBalance() + entity.getOverdraft() + "");
		return response;
	}

}
