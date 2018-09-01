package com.tc.banking.atm.response;

import lombok.Data;

@Data
public class AccountCheckResponse {
	
	private String balance;
	private String availableFunds;

}
