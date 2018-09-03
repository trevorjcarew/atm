package com.tc.banking.atm.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tc.banking.atm.entity.BankNoteEntity;
import com.tc.banking.atm.exception.AtmException;
import com.tc.banking.atm.repository.BankNoteRepository;
import com.tc.banking.atm.response.BankNoteResponse;
import com.tc.banking.atm.util.BankNoteEnum;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BankNoteService {

	@Autowired
	private BankNoteRepository bankNoteRepository;

	private double tempAmount;

	public Double checkAtmBalance() {
		//sum update the values of all notes in db
		List<BankNoteEntity> notes = bankNoteRepository.findAll();

		Double balance = 0.0;

		for (BankNoteEntity note : notes) {
			balance += note.getValue() * note.getQuantity();
		}

		return balance;
	}

	public List<BankNoteResponse> retrieveRequestedAmount(Double amount) {
		List<BankNoteResponse> notesResponse = new ArrayList<>();
		//tempAmount lets us know how many more notes we need to get.
		//it is reduced until we have balance retrieved or run out of notes
		tempAmount = amount;
		for (BankNoteEnum noteEnum : BankNoteEnum.values()) {
			notesResponse.add(getAmountInNotes(noteEnum));
		}

		if (tempAmount != 0.0) {
			log.error("cannot make up remaining amount with available notes, amount - " + tempAmount);
			throw new AtmException("Unable to dispense requested amount - Choose amount in different multiples");
			// TODO work out multiples so that better error can be provided
		}
		//only update the notes in the db when we can complete the transaction
		updateNoteEntities(notesResponse);

		return notesResponse;
	}

	private BankNoteResponse getAmountInNotes(BankNoteEnum noteEnum) {
		BankNoteEntity noteEntity = bankNoteRepository.findByName(noteEnum.getName());
		BankNoteResponse noteResponse = new BankNoteResponse();
		noteResponse.setNote(noteEntity.getName());
		int remainingQty = noteEntity.getQuantity();
		int noteCount = 0;

		//this allows us to return minimum amount of notes
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
			//don't try to update if we don't have any note quantity
			if (note.getNumberOfNotes() != 0) {
				BankNoteEntity entity = bankNoteRepository.findByName(note.getNote());
				entity.setQuantity(entity.getQuantity() - note.getNumberOfNotes());
				bankNoteRepository.save(entity);
			}
		}
	}
}
