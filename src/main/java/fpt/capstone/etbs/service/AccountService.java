package fpt.capstone.etbs.service;

import fpt.capstone.etbs.model.Account;

public interface AccountService {
    Account getAccountByEmail(String email);
}
