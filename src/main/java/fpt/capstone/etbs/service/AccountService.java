package fpt.capstone.etbs.service;

import fpt.capstone.etbs.model.Account;
import fpt.capstone.etbs.payload.AccountUpdateRequest;
import fpt.capstone.etbs.payload.LoginRequest;
import fpt.capstone.etbs.payload.RegisterRequest;

import java.util.UUID;

public interface AccountService {
    Account getAccountByEmail(String email);
    Account createAccount(RegisterRequest request, int roleId);
    Account updateAccount(UUID uuid, AccountUpdateRequest request);
    Account getAccount(UUID uuid);
    Account loginByGoogle(LoginRequest loginRequest);
}
