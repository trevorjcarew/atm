package com.tc.banking.atm.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tc.banking.atm.entity.AccountEntity;
import com.tc.banking.atm.repository.AccountRepository;
import com.tc.banking.atm.response.AccountCheckResponse;
import com.tc.banking.atm.response.BankNoteResponse;
import com.tc.banking.atm.response.WithdrawCashResponse;
import com.tc.banking.atm.validation.AccountValidation;

@Service
public class AccountService {

	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private BankNoteService bankNoteService;
	
	@Autowired
	private AccountValidation accountValidation;

	public AccountCheckResponse retrieveFunds(int pin, int accountNumber) {
		AccountCheckResponse response = new AccountCheckResponse();
		AccountEntity entity = retrieveAccount(accountNumber);
		accountValidation.validatePin(pin, entity.getPin());
		response.setBalance(entity.getBalance() + "");
		response.setAvailableFunds(entity.getBalance() + entity.getOverdraft() + "");
		return response;
	}

	public WithdrawCashResponse withdrawFunds(int pin, int accountNumber, double amount) {		
		AccountEntity accountEntity = retrieveAccount(accountNumber);		
		accountValidation.validatePin(pin, accountEntity.getPin());		
		accountValidation.verifyUserBalance(amount, accountEntity.getBalance() + accountEntity.getOverdraft());		
		accountValidation.verifyAtmBalance(amount);		
		List<BankNoteResponse> retrievedNotes = bankNoteService.retrieveRequestedAmount(amount);		
		double newBalance = updateAccountAndGetBalance(accountEntity, amount);
		WithdrawCashResponse response = formWithdrawelResponse(retrievedNotes, newBalance);
		return response;
	}

	private double updateAccountAndGetBalance(AccountEntity accountEntity, double amount) {
		double balance = accountEntity.getBalance();
		double overdraft = accountEntity.getOverdraft();

		if (amount > balance) {
			double overdraftReduction = amount - balance;
			accountEntity.setOverdraft(overdraft - overdraftReduction);
			accountEntity.setBalance(0.0);
		} else {
			accountEntity.setBalance(balance - amount);
		}		
		accountRepository.save(accountEntity);
		
		return accountEntity.getBalance();
	}
	
	private AccountEntity retrieveAccount(Integer accountNumber) {
		AccountEntity entity = accountRepository.findByAccountNumber(accountNumber);
		if (entity == null) {
			// throw invalid account number error
		}
		return entity;
	}

	private WithdrawCashResponse formWithdrawelResponse(List<BankNoteResponse> retrievedNotes, double newBalance) {
		WithdrawCashResponse response  = new WithdrawCashResponse();
		
		response.setNewBalance(newBalance);
		
		for (int i = 0; i < retrievedNotes.size(); i++) {
			if(retrievedNotes.get(i).getNumberOfNotes() == 0) {
				retrievedNotes.remove(i);
			}
		}
		response.setNotes(retrievedNotes);
		
		return response;
	}
}
