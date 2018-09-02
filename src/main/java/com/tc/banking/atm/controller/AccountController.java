package com.tc.banking.atm.controller;

import org.springframework.beans.factory.annotation.Autowired;
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
	public AccountCheckResponse checkFunds(@RequestParam Integer pin, @RequestParam Integer accountNumber) {
		return accountService.retrieveFunds(pin, accountNumber);		
	}
	
	@PutMapping("/withdrawFunds")
	public WithdrawCashResponse withdrawFunds(@RequestParam Integer pin, @RequestParam Integer accountNumber, @RequestParam Double amount) {
		return accountService.withdrawFunds(pin, accountNumber, amount);
	}

}
