package com.tc.banking.atm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tc.banking.atm.response.AccountCheckResponse;
import com.tc.banking.atm.service.AccountService;

@RestController
@RequestMapping("/atm")
public class AccountController {
	
	@Autowired
	private AccountService accountService;
	
	@GetMapping("/checkFunds")
	public AccountCheckResponse account(@RequestParam Integer pin, @RequestParam Integer accountNumber) {
		AccountCheckResponse response = accountService.retrieveFunds(pin, accountNumber);
		
		return response;
	}

}
