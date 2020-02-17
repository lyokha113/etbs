import fpt.capstone.etbs.model.Account;
import fpt.capstone.etbs.model.Role;
import fpt.capstone.etbs.repository.AccountRepository;
import fpt.capstone.etbs.repository.RoleRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MockitoTest {

  @Mock
  private RoleRepository roleRepository;

  @Mock
  private AccountRepository accountRepository;

  @Test
  public void createAccount() {

    int roleId = 1;
    String roleName = "Admin";
    String email = "abcd@gmail.com";
    String password = "123456";

    // Mock role object
    Role mockResult = Role.builder().id(roleId).name(roleName).build();
    Mockito.when(roleRepository.getOne(1)).thenReturn(mockResult);

    Role role = roleRepository.getOne(1);
    Account account = Account.builder()
        .email(email).password(password).role(role).active(false)
        .build();

    // Mock inserted account object
    Mockito.when(accountRepository.save(account)).then(a -> {
      account.setActive(true);
      return account;
    });
    Account saved = accountRepository.save(account);

    Assert.assertTrue(saved.isActive());
    Assert.assertEquals(roleName, saved.getRole().getName());

    //  False
    Assert.assertEquals(2, saved.getRole().getId().intValue());
  }


}
