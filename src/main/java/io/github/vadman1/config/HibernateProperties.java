package io.github.vadman1.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class HibernateProperties {

    private final String driver;
    private final String url;
    private final String username;
    private final String password;
    private final String dialect;
    private final String hbm2ddl;
    private final String currentSessionContextClass;
    private final String showSql;
    private final String formatSql;

    public HibernateProperties(
            @Value("${hibernate.connection.driver_class}") String driver,
            @Value("${hibernate.connection.url}") String url,
            @Value("${hibernate.connection.username}") String username,
            @Value("${hibernate.connection.password}") String password,
            @Value("${hibernate.dialect}") String dialect,
            @Value("${hibernate.hbm2ddl.auto}") String hbm2ddl,
            @Value("${hibernate.current_session_context_class}") String currentSessionContextClass,
            @Value("${hibernate.show_sql}") String showSql,
            @Value("${hibernate.format_sql}") String formatSql
    ) {
        this.driver = driver;
        this.url = url;
        this.username = username;
        this.password = password;
        this.dialect = dialect;
        this.hbm2ddl = hbm2ddl;
        this.currentSessionContextClass = currentSessionContextClass;
        this.showSql = showSql;
        this.formatSql = formatSql;
    }

    public String getDriver() {
        return driver;
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getDialect() {
        return dialect;
    }

    public String getHbm2ddl() {
        return hbm2ddl;
    }

    public String getCurrentSessionContextClass() {
        return currentSessionContextClass;
    }

    public String getShowSql() {
        return showSql;
    }

    public String getFormatSql() {
        return formatSql;
    }
}