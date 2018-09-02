package com.tc.banking.atm.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tc.banking.atm.entity.AccountEntity;
import com.tc.banking.atm.repository.AccountRepository;
import com.tc.banking.atm.service.BankNoteService;

@Component
public class AccountValidation {
	
	@Autowired
	private BankNoteService bankNoteService;
	@Autowired
	private AccountRepository accountRepository;
	
	public void verifyAtmBalance(double requestedAmount) {
		Double atmBalance = bankNoteService.checkAtmBalance();

		if (requestedAmount > atmBalance) {
			// throw unable to dispense this amount error
		}

	}

	public AccountEntity retrieveAccount(int accountNumber) {
		AccountEntity entity = accountRepository.findByAccountNumber(accountNumber);
		if (entity == null) {
			// throw invalid account number error
		}
		return entity;
	}

	public void validatePin(int pin, int entityPin) {
		if (pin != entityPin) {
			// throw invalid pin error
		}

	}

	public void verifyUserBalance(double amount, double availableFunds) {
		if (amount > availableFunds) {
			// throw insufficient balance error
		}

	}

}
