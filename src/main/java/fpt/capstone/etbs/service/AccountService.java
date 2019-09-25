package fpt.capstone.etbs.service;

import fpt.capstone.etbs.model.Account;
import fpt.capstone.etbs.payload.AccountUpdateRequest;
import fpt.capstone.etbs.payload.RegisterRequest;

import java.util.UUID;

public interface AccountService {
    Account getAccountByEmail(String email);
    void registerAccount(RegisterRequest request);
    boolean updateAccount(UUID uuid, AccountUpdateRequest request);
    Account getAccount(UUID uuid);
}
