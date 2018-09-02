package com.tc.banking.atm.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name="bank_note")
@Data
public class BankNoteEntity {
	
	@Id
	@Column(name="id")
	private int id;
	
	@Column(name="name")
	private String name;
	
	@Column(name="value")
	private double value;
	
	@Column(name="quantity")
	private int quantity;

}
