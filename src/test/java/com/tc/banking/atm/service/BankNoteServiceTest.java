package com.tc.banking.atm.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.tc.bank.atm.exception.AtmException;
import com.tc.banking.atm.entity.BankNoteEntity;
import com.tc.banking.atm.repository.BankNoteRepository;
import com.tc.banking.atm.response.BankNoteResponse;
import com.tc.banking.atm.util.BankNoteEnum;

public class BankNoteServiceTest {
	
	@Mock
	private BankNoteRepository bankNoteRepository;
	
	@InjectMocks
	private BankNoteService bankNoteService;
	
	@Before
    public void setup() {
    	MockitoAnnotations.initMocks(this);    	
    }
	
	@Test
	public void testCheckAtmBalanceReturnsCorrectAmount() {
		when(bankNoteRepository.findAll()).thenReturn(getBankNoteEntityList());		
		double atmBalance = bankNoteService.checkAtmBalance();
		assertEquals(atmBalance, 170.0, 0);
	}
	
	@Test
	public void testRetrieveRequestedAmountIsSuccessful() {
		setupMockResponses();
		List<BankNoteResponse> response = bankNoteService.retrieveRequestedAmount(85.0);
		assertEquals(response.size(), 4);
		assertEquals(response.get(0).getNote(), "Fifty Euro Note");
		assertEquals(response.get(1).getNote(), "Twenty Euro Note");
		assertEquals(response.get(2).getNote(), "Ten Euro Note");
		assertEquals(response.get(3).getNote(), "Five Euro Note");
		assertEquals(response.get(0).getNumberOfNotes(), 1);
		assertEquals(response.get(1).getNumberOfNotes(), 1);
		assertEquals(response.get(2).getNumberOfNotes(), 1);
		assertEquals(response.get(3).getNumberOfNotes(), 1);		
	}
	
	@Test (expected = AtmException.class)
	public void testRetrieveRequestedAmountDoesntHaveSufficientAmoutOfNotes() {
		setupMockResponses();
		List<BankNoteResponse> response = bankNoteService.retrieveRequestedAmount(180.0);		
	}
	
	@Test 
	public void testRetrieveRequestedAmountProvidesMinNumberOfNotes() {
		setupMockResponses();
		List<BankNoteResponse> response = bankNoteService.retrieveRequestedAmount(70.0);
		assertEquals(response.get(0).getNote(), "Fifty Euro Note");
		assertEquals(response.get(1).getNote(), "Twenty Euro Note");
		assertEquals(response.get(2).getNote(), "Ten Euro Note");
		assertEquals(response.get(3).getNote(), "Five Euro Note");
		assertEquals(response.get(0).getNumberOfNotes(), 1);
		assertEquals(response.get(1).getNumberOfNotes(), 1);
		assertEquals(response.get(2).getNumberOfNotes(), 0);
		assertEquals(response.get(3).getNumberOfNotes(), 0);
	}
	
	private void setupMockResponses() {
		when(bankNoteRepository.findByName(BankNoteEnum.FIFTY.getName())).thenReturn(getNoteEntity(1, "Fifty", 50.0));
		when(bankNoteRepository.findByName(BankNoteEnum.TWENTY.getName())).thenReturn(getNoteEntity(2, "Twenty", 20.0));
		when(bankNoteRepository.findByName(BankNoteEnum.TEN.getName())).thenReturn(getNoteEntity(1, "Ten", 10.0));
		when(bankNoteRepository.findByName(BankNoteEnum.FIVE.getName())).thenReturn(getNoteEntity(1, "Five", 5.0));
		
	}

	private List<BankNoteEntity> getBankNoteEntityList() {
		List<BankNoteEntity> bankNoteList = new ArrayList<>();
		bankNoteList.add(getNoteEntity(1, "Fifty", 50.0));
		bankNoteList.add(getNoteEntity(2, "Twenty", 20.0));
		bankNoteList.add(getNoteEntity(3, "Ten", 10.0));
		bankNoteList.add(getNoteEntity(4, "Five", 5.0));
		
		return bankNoteList;
	}
	
	private BankNoteEntity getNoteEntity(int id, String noteName, double value) {
		BankNoteEntity entity = new BankNoteEntity();
		entity.setId(id);
		entity.setName(noteName + " Euro Note");
		entity.setValue(value);
		entity.setQuantity(2);		
		return entity;
	}
}
