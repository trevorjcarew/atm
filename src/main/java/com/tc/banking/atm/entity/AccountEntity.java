package com.tc.banking.atm.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name="account")
@Data
public class AccountEntity {
	
	@Id
	@Column(name="account_number")
	private int accountNumber;
	
	@Column(name="pin")
	private int pin;
	
	@Column(name="balance")
	private double balance;
	
	@Column(name="overdraft")
	private double overdraft;

}
