package fpt.capstone.etbs.service;

import fpt.capstone.etbs.model.Account;
import fpt.capstone.etbs.payload.RegisterRequest;

public interface AccountService {
    Account getAccountByEmail(String email);
    void registerAccount(RegisterRequest request);
}
