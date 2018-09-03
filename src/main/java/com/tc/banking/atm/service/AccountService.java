package com.tc.banking.atm.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tc.banking.atm.entity.AccountEntity;
import com.tc.banking.atm.exception.AtmException;
import com.tc.banking.atm.repository.AccountRepository;
import com.tc.banking.atm.response.AccountCheckResponse;
import com.tc.banking.atm.response.BankNoteResponse;
import com.tc.banking.atm.response.WithdrawCashResponse;

@Service
public class AccountService {

	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private BankNoteService bankNoteService;	

	public AccountCheckResponse retrieveFunds(int pin, int accountNumber) {
		AccountCheckResponse response = new AccountCheckResponse();
		AccountEntity entity = retrieveAccount(accountNumber);
		validatePin(pin, entity.getPin());
		response.setBalance(entity.getBalance() + "");
		response.setAvailableFunds(entity.getBalance() + entity.getOverdraft() + "");
		return response;
	}

	public WithdrawCashResponse withdrawFunds(int pin, int accountNumber, double amount) {		
		AccountEntity accountEntity = retrieveAccount(accountNumber);		
		validatePin(pin, accountEntity.getPin());		
		verifyUserBalance(amount, accountEntity.getBalance() + accountEntity.getOverdraft());		
		verifyAtmBalance(amount);		
		List<BankNoteResponse> retrievedNotes = bankNoteService.retrieveRequestedAmount(amount);		
		double newBalance = updateAccountAndGetBalance(accountEntity, amount);
		WithdrawCashResponse response = formWithdrawelResponse(retrievedNotes, newBalance);
		return response;
	}

	private double updateAccountAndGetBalance(AccountEntity accountEntity, double amount) {
		double balance = accountEntity.getBalance();
		double overdraft = accountEntity.getOverdraft();

		//when overdrawn set balance to 0 and update overdraft amount
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
	
	private AccountEntity retrieveAccount(int accountNumber) {
		AccountEntity entity = accountRepository.findByAccountNumber(accountNumber);
		if (entity == null) {			
			throw new AtmException("Account does not exist");
		}
		return entity;
	}

	private WithdrawCashResponse formWithdrawelResponse(List<BankNoteResponse> retrievedNotes, double newBalance) {
		WithdrawCashResponse response  = new WithdrawCashResponse();
		
		response.setNewBalance(newBalance);
		
		//remove notes with a quantity of 0 from response
		for (int i = 0; i < retrievedNotes.size(); i++) {
			if(retrievedNotes.get(i).getNumberOfNotes() == 0) {
				retrievedNotes.remove(i);
			}
		}
		response.setNotes(retrievedNotes);
		
		return response;
	}
	
	private void verifyAtmBalance(double requestedAmount) {
		//get all notes from the db and sum up their total
		Double atmBalance = bankNoteService.checkAtmBalance();
		if (requestedAmount > atmBalance) {			
			throw new AtmException("Unable to dispense this amount");
		}
	}

	private void validatePin(int pin, int entityPin) {
		//check if pin provided matches pin in db
		if (pin != entityPin) {			
			throw new AtmException("Invalid pin number entered");
		}
	}

	private void verifyUserBalance(double amount, double availableFunds) {
		//check that the have enough funds in their account
		if (amount > availableFunds) {
			throw new AtmException("Insufficient funds");
		}
	}
}
