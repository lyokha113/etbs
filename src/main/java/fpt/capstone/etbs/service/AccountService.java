package fpt.capstone.etbs.service;

import fpt.capstone.etbs.model.Account;
import fpt.capstone.etbs.payload.AccountRequest;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import javax.mail.MessagingException;

public interface AccountService {

  List<Account> getAccounts();

  Account getAccount(UUID uuid);

  Account getAccountByEmail(String email);

  Account registerAccount(AccountRequest request);

  Account createAccount(AccountRequest request);

  Account updateAccount(UUID uuid, AccountRequest request) throws IOException;

  Account updateProfile(UUID uuid, AccountRequest request) throws IOException;

  Account updateInvite(UUID uuid, Boolean allow) throws IOException;

  Account updateAccountStatus(UUID uuid, Boolean active);

  Account setGoogleAccount(String email, String name, String avatar);

  void confirmRecovery(UUID uuid) throws IOException, MessagingException;

  void confirmAccount(UUID uuid);

}
