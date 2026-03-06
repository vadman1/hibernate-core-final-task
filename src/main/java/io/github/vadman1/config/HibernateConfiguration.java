package io.github.vadman1.config;

import io.github.vadman1.account.Account;
import io.github.vadman1.user.User;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HibernateConfiguration {

    private final HibernateProperties hibernateProperties;

    public HibernateConfiguration(HibernateProperties hibernateProperties) {
        this.hibernateProperties = hibernateProperties;
    }

    @Bean
    public SessionFactory sessionFactory() {
        org.hibernate.cfg.Configuration configuration = new org.hibernate.cfg.Configuration();

        configuration
                .addAnnotatedClass(User.class)
                .addAnnotatedClass(Account.class)
                .addPackage("io.github.vadman1")
                .setProperty("hibernate.connection.driver_class", hibernateProperties.getDriver())
                .setProperty("hibernate.connection.url", hibernateProperties.getUrl())
                .setProperty("hibernate.connection.username", hibernateProperties.getUsername())
                .setProperty("hibernate.connection.password", hibernateProperties.getPassword())
                .setProperty("hibernate.dialect", hibernateProperties.getDialect())
                .setProperty("hibernate.hbm2ddl.auto", hibernateProperties.getHbm2ddl())
                .setProperty("hibernate.current_session_context_class", hibernateProperties.getCurrentSessionContextClass())
                .setProperty("hibernate.show_sql", hibernateProperties.getShowSql())
                .setProperty("hibernate.format_sql", hibernateProperties.getFormatSql());

        return configuration.buildSessionFactory();
    }
}
