package com.tc.banking.atm.response;

import java.util.List;

import lombok.Data;

@Data
public class WithdrawCashResponse {
	
	private double newBalance;
	private List<BankNoteResponse> notes;

}
