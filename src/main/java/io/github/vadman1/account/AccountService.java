package io.github.vadman1.account;

import io.github.vadman1.user.User;
import io.github.vadman1.util.TransactionHelper;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountService {

    private final AccountProperties accountProperties;
    private final SessionFactory sessionFactory;
    private final TransactionHelper transactionHelper;

    public AccountService(
            AccountProperties accountProperties,
            SessionFactory sessionFactory,
            TransactionHelper transactionHelper
    ) {
        this.accountProperties = accountProperties;
        this.sessionFactory = sessionFactory;
        this.transactionHelper = transactionHelper;
    }

    public Account createAccount(User user) {
        if (user == null) {
            throw new IllegalArgumentException("user must not be null");
        }

        return transactionHelper.executeInTransactionOrJoin(() -> {
            Session session = sessionFactory.getCurrentSession();
            Account newAccount = new Account(accountProperties.getDefaultAmount());
            newAccount.setOwner(user);
            user.getAccountList().add(newAccount);
            session.persist(newAccount);
            return newAccount;
        });
    }

    public Optional<Account> findAccountById(Integer id) {
        validatePositiveId(id, "account id");

        Account account;
        try (Session session = sessionFactory.openSession()) {
            account = session.find(Account.class, id);
        }

        return Optional.ofNullable(account);
    }

    public void withdraw(Integer fromAccountId, Integer amount) {
        validatePositiveId(fromAccountId, "account id");
        validatePositiveAmount(amount);
        Account account = findAccountById(fromAccountId)
                .orElseThrow(() -> new IllegalArgumentException("No such account: id=%s".formatted(fromAccountId)));

        if (amount > account.getMoneyAmount()) {
            throw new IllegalArgumentException(
                    "insufficient funds on account id=%s, moneyAmount=%s, attempted withdraw=%s"
                            .formatted(account.getId(), account.getMoneyAmount(), amount)
            );
        }
        account.setMoneyAmount(account.getMoneyAmount() - amount);

        transactionHelper.executeInTransaction(session -> {
            session.merge(account);
        });
    }

    public void deposit(Integer toAccountId, Integer amount) {
        validatePositiveId(toAccountId, "account id");
        validatePositiveAmount(amount);
        Account account = findAccountById(toAccountId)
                .orElseThrow(() -> new IllegalArgumentException("No such account: id=%s".formatted(toAccountId)));

        account.setMoneyAmount(account.getMoneyAmount() + amount);

        transactionHelper.executeInTransaction(session -> {
            session.merge(account);
        });
    }

    public Account closeAccount(Integer accountId) {
        validatePositiveId(accountId, "account id");
        Account accountToClose = findAccountById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("No such account: id=%s".formatted(accountId)));

        User user = accountToClose.getOwner();
        var userAccounts = user.getAccountList();

        if (userAccounts.size() == 1) {
            throw new IllegalStateException("Can't close the only one account");
        }

        var accountToTransferMoney = userAccounts.stream()
                .filter(it -> it.getId() != accountId)
                .findFirst()
                .orElseThrow();

        var newAmount = accountToTransferMoney.getMoneyAmount() + accountToClose.getMoneyAmount();
        accountToTransferMoney.setMoneyAmount(newAmount);

        transactionHelper.executeInTransaction(session -> {
            session.remove(accountToClose);
            session.merge(accountToTransferMoney);
        });

        return accountToClose;
    }

    public void transfer(int fromAccountId, int toAccountId, int amount) {
        validatePositiveId(fromAccountId, "source account id");
        validatePositiveId(toAccountId, "target account id");
        validatePositiveAmount(amount);
        if (fromAccountId == toAccountId) {
            throw new IllegalArgumentException("source and target account id must be different");
        }
        Account accountFrom = findAccountById(fromAccountId)
                .orElseThrow(() -> new IllegalArgumentException("No such account: id=%s".formatted(fromAccountId)));
        Account accountTo = findAccountById(toAccountId)
                .orElseThrow(() -> new IllegalArgumentException("No such account: id=%s".formatted(toAccountId)));

        if (amount > accountFrom.getMoneyAmount()) {
            throw new IllegalArgumentException(
                    "insufficient funds on account id=%s, moneyAmount=%s, attempted transfer=%s"
                            .formatted(accountFrom.getId(), accountFrom.getMoneyAmount(), amount)
            );
        }
        accountFrom.setMoneyAmount(accountFrom.getMoneyAmount() - amount);

        int amountToTransfer = accountTo.getOwner().getId() == accountFrom.getOwner().getId()
                ? amount
                : (int) Math.round(amount * (1 - accountProperties.getTransferCommission()));
        accountTo.setMoneyAmount(accountTo.getMoneyAmount() + amountToTransfer);

        transactionHelper.executeInTransaction(session -> {
            session.merge(accountFrom);
            session.merge(accountTo);
        });
    }

    private void validatePositiveId(Integer id, String fieldName) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(fieldName + " must be > 0");
        }
    }

    private void validatePositiveAmount(Integer amount) {
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("amount must be > 0");
        }
    }
}