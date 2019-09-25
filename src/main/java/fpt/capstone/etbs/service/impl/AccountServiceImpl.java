package fpt.capstone.etbs.service.impl;

import fpt.capstone.etbs.model.Account;
import fpt.capstone.etbs.model.Role;
import fpt.capstone.etbs.payload.AccountUpdateRequest;
import fpt.capstone.etbs.payload.CreateAccountRequest;
import fpt.capstone.etbs.payload.RegisterRequest;
import fpt.capstone.etbs.repository.AccountRepository;
import fpt.capstone.etbs.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public Account getAccount(UUID uuid) {
        return accountRepository.findById(uuid).orElse(null);
    }

    @Override
    public Account getAccountByEmail(String email) {
        return accountRepository.getByEmail(email).orElse(null);
    }

    @Override
    public Account createAccount(RegisterRequest request, int roleId) {
        Account account = getAccountByEmail(request.getEmail());
        if (account == null) {
            Role role = new Role();
            role.setId(roleId);
            account = setAccountFromRequest(request, role);
            account = accountRepository.save(account);
            return account;
        }
        return null;
    }

    @Override
    public Account createAccount(CreateAccountRequest request) {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .email(request.getEmail())
                .fullName(request.getFullName())
                .password(request.getPassword())
                .build();
        return createAccount(registerRequest, request.getRoleId());
    }

    @Override
    public Account updateAccount(UUID uuid, AccountUpdateRequest request) {
        Account account = getAccount(uuid);
        if (account != null) {
            account.setFullName(request.getFullName());
            account.setPassword(request.getPassword());
            accountRepository.save(account);
            return account;
        }
        return null;
    }

    @Override
    public boolean changeStatusAccount(UUID uuid, boolean active) {
        Account account = getAccount(uuid);
        if (account != null) {
            account.setActive(active);
            accountRepository.save(account);
            return true;
        }
        return false;
    }

    private Account setAccountFromRequest(RegisterRequest request, Role role) {
        Account account = new Account();
        account.setPassword(request.getPassword());
        account.setFullName(request.getFullName());
        account.setEmail(request.getEmail());
        account.setRole(role);
        account.setActive(true);
        return account;
    }
}
