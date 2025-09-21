package ru.sakhapov;

import lombok.Getter;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;

import java.util.Properties;


public class SessionFactoryMaker {
    @Getter
    private static SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            return new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static void init(String jdbcUrl, String username, String password) {
        if (sessionFactory != null) {
            sessionFactory.close();
        }

        Configuration configuration = new Configuration();
        configuration.addAnnotatedClass(User.class);

        Properties settings = new Properties();
        settings.put(AvailableSettings.JAKARTA_JDBC_DRIVER, "org.postgresql.Driver");
        settings.put(AvailableSettings.JAKARTA_JDBC_URL, jdbcUrl);
        settings.put(AvailableSettings.JAKARTA_JDBC_USER, username);
        settings.put(AvailableSettings.JAKARTA_JDBC_PASSWORD, password);
        settings.put(AvailableSettings.DIALECT, "org.hibernate.dialect.PostgreSQLDialect");
        settings.put(AvailableSettings.HBM2DDL_AUTO, "update");

        configuration.setProperties(settings);

        StandardServiceRegistryBuilder registryBuilder =
                new StandardServiceRegistryBuilder()
                        .applySettings(configuration.getProperties());

        sessionFactory = configuration.buildSessionFactory(registryBuilder.build());
    }

    public static void shutdown() {
        getSessionFactory().close();
    }
}