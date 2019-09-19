package fpt.capstone.etbs.service.impl;

import fpt.capstone.etbs.model.Account;
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
}
