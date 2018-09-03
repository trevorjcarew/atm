package com.tc.banking.atm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tc.banking.atm.response.AccountCheckResponse;
import com.tc.banking.atm.response.WithdrawCashResponse;
import com.tc.banking.atm.service.AccountService;

@RestController
@RequestMapping("/atm")
public class AccountController {
	
	@Autowired
	private AccountService accountService;
	
	@GetMapping("/checkFunds")
	public ResponseEntity<AccountCheckResponse> checkFunds(@RequestParam Integer pin, @RequestParam Integer accountNumber) {
		AccountCheckResponse response = accountService.retrieveFunds(pin, accountNumber);	
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@PutMapping("/withdrawFunds")
	public ResponseEntity<WithdrawCashResponse> withdrawFunds(@RequestParam Integer pin, @RequestParam Integer accountNumber, @RequestParam Double amount) {
		WithdrawCashResponse response =  accountService.withdrawFunds(pin, accountNumber, amount);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
