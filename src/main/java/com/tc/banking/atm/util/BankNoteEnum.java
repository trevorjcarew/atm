package com.tc.banking.atm.util;

public enum BankNoteEnum {
	FIFTY("Fifty Euro Note", 50.00), TWENTY("Twenty Euro Note", 20.00), TEN("Ten Euro Note", 10.00),
	FIVE("Five Euro Note", 5.00);

	private String name;
	private double value;

	BankNoteEnum(String name, double value) {
		this.name = name;
		this.value = value;
	}
	
	public String getName() {
		return name;
	}
	
	public double getValue() {
		return value;
	}
}
