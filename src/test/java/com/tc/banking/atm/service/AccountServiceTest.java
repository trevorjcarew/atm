package com.tc.banking.atm.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.tc.banking.atm.entity.AccountEntity;
import com.tc.banking.atm.exception.AtmException;
import com.tc.banking.atm.repository.AccountRepository;
import com.tc.banking.atm.response.AccountCheckResponse;
import com.tc.banking.atm.response.BankNoteResponse;
import com.tc.banking.atm.response.WithdrawCashResponse;

public class AccountServiceTest {
	
	private static final String NOTE_NAME = "AnyNote";
	private static final int NUMBER_NOTES = 5;
	private static final int PIN = 1234;
	private static final int ACCOUNT = 123456789;
	private static final double BALANCE = 100.0;
	private static final double OVERDRAFT = 50.0;
	
	@Mock
	private AccountRepository accountRepository;
	
	@Mock
	private BankNoteService bankNoteService;
	
	@InjectMocks
	private AccountService accountService;
	
	@Before
    public void setup() {
    	MockitoAnnotations.initMocks(this);    	
    }
	
	@Test
	public void testRetrieveFundsIsSuccessful() {
		when(accountRepository.findByAccountNumber(anyInt())).thenReturn(getAccountEntity());		
		AccountCheckResponse response = accountService.retrieveFunds(PIN, ACCOUNT);
		assertEquals(response.getAvailableFunds(), 150.00 + "");
		assertEquals(response.getBalance(), 100.00 + "");
	}
	
	@Test (expected = AtmException.class)
	public void testAccountNotFoundThrowsAtmException() {
		when(accountRepository.findByAccountNumber(anyInt())).thenReturn(null);		
		AccountCheckResponse response = accountService.retrieveFunds(PIN, ACCOUNT);
	}
	
	@Test (expected = AtmException.class)
	public void testIncorrectPinThrowsAtmException() {
		when(accountRepository.findByAccountNumber(anyInt())).thenReturn(getAccountEntity());		
		AccountCheckResponse response = accountService.retrieveFunds(0000, ACCOUNT);		
	}
	
	@Test
	public void testWithdrawFundsIsSuccessful() {
		when(accountRepository.findByAccountNumber(anyInt())).thenReturn(getAccountEntity());
		when(bankNoteService.retrieveRequestedAmount(anyDouble())).thenReturn(getBankNotesList());
		when(bankNoteService.checkAtmBalance()).thenReturn(200.0);
		
		WithdrawCashResponse response = accountService.withdrawFunds(PIN, ACCOUNT, 10.0);
		assertEquals(response.getNewBalance(), 90.0, 10.0);
		assertEquals(response.getNotes().size(), 1);
		assertEquals(response.getNotes().get(0).getNote(), NOTE_NAME);
		assertEquals(response.getNotes().get(0).getNumberOfNotes(), NUMBER_NOTES);
		
	}
	
	@Test (expected = AtmException.class)
	public void testUserBalanceInsufficient() {
		when(accountRepository.findByAccountNumber(anyInt())).thenReturn(getAccountEntity());
		when(bankNoteService.retrieveRequestedAmount(anyDouble())).thenReturn(getBankNotesList());
		when(bankNoteService.checkAtmBalance()).thenReturn(200.0);
		
		WithdrawCashResponse response = accountService.withdrawFunds(PIN, ACCOUNT, 160.0);		
	}
	
	@Test (expected = AtmException.class)
	public void testAtmBalanceInsufficient() {
		when(accountRepository.findByAccountNumber(anyInt())).thenReturn(getAccountEntity());
		when(bankNoteService.retrieveRequestedAmount(anyDouble())).thenReturn(getBankNotesList());
		when(bankNoteService.checkAtmBalance()).thenReturn(50.0);
		
		WithdrawCashResponse response = accountService.withdrawFunds(PIN, ACCOUNT, 80.0);		
	}
	
	@Test
	public void testUserCanUseOverdraft() {
		when(accountRepository.findByAccountNumber(anyInt())).thenReturn(getAccountEntity());
		when(bankNoteService.retrieveRequestedAmount(anyDouble())).thenReturn(getBankNotesList());
		when(bankNoteService.checkAtmBalance()).thenReturn(200.0);
		
		WithdrawCashResponse response = accountService.withdrawFunds(PIN, ACCOUNT, 120.0);
		assertEquals(response.getNewBalance(), 0, 100.0);
		assertEquals(response.getNotes().size(), 1);
		assertEquals(response.getNotes().get(0).getNote(), NOTE_NAME);
		assertEquals(response.getNotes().get(0).getNumberOfNotes(), NUMBER_NOTES);
		
	}
	
	

	private AccountEntity getAccountEntity() {
		AccountEntity entity = new AccountEntity();
		entity.setAccountNumber(ACCOUNT);
		entity.setBalance(BALANCE);
		entity.setOverdraft(OVERDRAFT);
		entity.setPin(PIN);
		return entity;
	}
	
	private List<BankNoteResponse> getBankNotesList(){
		List<BankNoteResponse> bankNoteList = new ArrayList<>();
		
		BankNoteResponse bankNote = new BankNoteResponse();
		bankNote.setNote(NOTE_NAME);
		bankNote.setNumberOfNotes(NUMBER_NOTES);
		bankNoteList.add(bankNote);
		return bankNoteList;
	}

}
