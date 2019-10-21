package fpt.capstone.etbs.service.impl;

import fpt.capstone.etbs.model.Account;
import fpt.capstone.etbs.model.Role;
import fpt.capstone.etbs.payload.AccountUpdateRequest;
import fpt.capstone.etbs.payload.CreateAccountRequest;
import fpt.capstone.etbs.payload.LoginRequest;
import fpt.capstone.etbs.payload.RegisterRequest;
import fpt.capstone.etbs.repository.AccountRepository;
import fpt.capstone.etbs.repository.RoleRepository;
import fpt.capstone.etbs.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;

import java.util.UUID;

@Service
public class AccountServiceImpl extends DefaultOAuth2UserService implements AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

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
            account = new Account();
            account.setEmail(request.getEmail());
            account.setFullName(request.getFullName());
            account.setPassword(passwordEncoder.encode(request.getPassword()));
            account.setActive(true);
            if (roleRepository.findById(roleId).isPresent()) {
                account.setRole(roleRepository.findById(roleId).get());
            }
            accountRepository.save(account);
        }
        return account;
    }


    @Override
    public Account updateAccount(UUID uuid, AccountUpdateRequest request) {
        Account account = getAccount(uuid);
        if (account != null) {
            account.setFullName(request.getFullName());
            account.setPassword(passwordEncoder.encode(request.getPassword()));
            accountRepository.save(account);
            return account;
        }
        return null;
    }
}
