package io.github.vadman1.user;

import io.github.vadman1.account.AccountService;
import io.github.vadman1.util.TransactionHelper;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final AccountService accountService;
    private final SessionFactory sessionFactory;
    private final TransactionHelper transactionHelper;

    public UserService(
            AccountService accountService,
            SessionFactory sessionFactory,
            TransactionHelper transactionHelper
    ) {
        this.accountService = accountService;
        this.sessionFactory = sessionFactory;
        this.transactionHelper = transactionHelper;
    }

    public User createUser(String login) {
        String normalizedLogin = validateLogin(login);
        if (isLoginTaken(normalizedLogin)) {
            throw new IllegalArgumentException("User already exists with login=%s".formatted(normalizedLogin));
        }

        return transactionHelper.executeInTransactionOrJoin(() -> {
            Session session = sessionFactory.getCurrentSession();

            var user = new User(normalizedLogin, new ArrayList<>());
            accountService.createAccount(user);

            session.persist(user);

            return user;
        });
    }

    public User findUserById(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("user id must be > 0");
        }

        User user;
        try (Session session = sessionFactory.openSession()) {
            user = session.find(User.class, id);
        }

        if (user == null) {
            throw new IllegalArgumentException("No such user with id=%s".formatted(id));
        }
        return user;
    }

    public List<User> findAll() {
        List<User> userList;
        try (Session session = sessionFactory.openSession()) {
            userList = session.createQuery("select u from User u join fetch u.accountList", User.class)
                    .list();
        }

        return userList;
    }

    private String validateLogin(String login) {
        if (login == null || login.isBlank()) {
            throw new IllegalArgumentException("login must not be blank");
        }
        return login.trim();
    }

    private boolean isLoginTaken(String login) {
        Set<String> logins = findAll()
                .stream()
                .map(User::getLogin)
                .collect(Collectors.toSet());

        return logins.contains(login);
    }
}