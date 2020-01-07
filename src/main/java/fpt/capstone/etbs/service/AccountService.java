package fpt.capstone.etbs.service;

import fpt.capstone.etbs.model.Account;
import fpt.capstone.etbs.payload.AccountCreateRequest;
import fpt.capstone.etbs.payload.AccountUpdateRequest;
import fpt.capstone.etbs.payload.RegisterRequest;
import java.util.List;
import java.util.UUID;

public interface AccountService {

  List<Account> getAccounts();

  Account getAccount(UUID uuid);

  Account getAccountByEmail(String email);

  Account registerAccount(RegisterRequest request);

  Account createAccount(AccountCreateRequest request);

  Account createGoogleAccount(String email, String name, String avatarURL);

  Account updateAccount(UUID uuid, AccountUpdateRequest request);

  Account updateAccount(UUID uuid, Boolean active);

  Account updateGoogleAccount(Account account, String name, String avatarURL);
}
