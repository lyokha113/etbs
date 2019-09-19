package fpt.capstone.etbs.service.impl;

import fpt.capstone.etbs.constant.RoleEnum;
import fpt.capstone.etbs.model.Account;
import fpt.capstone.etbs.model.Role;
import fpt.capstone.etbs.payload.RegisterRequest;
import fpt.capstone.etbs.repository.AccountRepository;
import fpt.capstone.etbs.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public Account getAccountByEmail(String email) {
        return accountRepository.getByEmail(email).orElse(null);
    }

    @Override
    public void registerAccount(RegisterRequest request) {
        Role role = new Role();
        role.setId(RoleEnum.USER.getId());
        Account account = setAccountFromRequest(request, role);
        accountRepository.save(account);
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
