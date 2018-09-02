package com.tc.banking.atm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tc.banking.atm.entity.BankNoteEntity;

public interface BankNoteRepository extends JpaRepository<BankNoteEntity, Integer> {

	BankNoteEntity findByName(String name);

}
