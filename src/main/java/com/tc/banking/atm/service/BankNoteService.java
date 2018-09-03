package com.tc.banking.atm.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tc.bank.atm.exception.AtmException;
import com.tc.banking.atm.entity.BankNoteEntity;
import com.tc.banking.atm.repository.BankNoteRepository;
import com.tc.banking.atm.response.BankNoteResponse;
import com.tc.banking.atm.util.BankNoteEnum;

@Service
public class BankNoteService {

	@Autowired
	private BankNoteRepository bankNoteRepository;

	private double tempAmount;

	public Double checkAtmBalance() {
		List<BankNoteEntity> notes = bankNoteRepository.findAll();

		Double balance = 0.0;

		for (BankNoteEntity note : notes) {
			balance += note.getValue() * note.getQuantity();
		}

		return balance;
	}

	public List<BankNoteResponse> retrieveRequestedAmount(Double amount) {
		List<BankNoteResponse> notesResponse = new ArrayList<>();
		tempAmount = amount;
		for (BankNoteEnum noteEnum : BankNoteEnum.values()) {
			notesResponse.add(getAmountInNotes(amount, noteEnum));
		}

		if (tempAmount != 0.0) {
			throw new AtmException("Unable to dispense requested amount");
			// TODO work out multiples
		}
		updateNoteEntities(notesResponse);

		return notesResponse;
	}

	private BankNoteResponse getAmountInNotes(double amount, BankNoteEnum noteEnum) {
		BankNoteEntity noteEntity = bankNoteRepository.findByName(noteEnum.getName());
		BankNoteResponse noteResponse = new BankNoteResponse();
		noteResponse.setNote(noteEntity.getName());
		int remainingQty = noteEntity.getQuantity();
		int noteCount = 0;

		while (remainingQty > 0 && tempAmount >= noteEntity.getValue()) {
			remainingQty--;
			noteCount++;
			tempAmount = tempAmount - noteEntity.getValue();
		}
		noteResponse.setNumberOfNotes(noteCount);

		return noteResponse;
	}

	private void updateNoteEntities(List<BankNoteResponse> notesResponse) {
		for (BankNoteResponse note : notesResponse) {
			if (note.getNumberOfNotes() != 0) {
				BankNoteEntity entity = bankNoteRepository.findByName(note.getNote());
				entity.setQuantity(entity.getQuantity() - note.getNumberOfNotes());
				bankNoteRepository.save(entity);
			}
		}
	}
}
