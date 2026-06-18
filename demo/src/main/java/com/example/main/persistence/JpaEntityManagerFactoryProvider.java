package com.example.main.persistence;

import com.example.main.config.DatabaseConfig;
import com.example.main.config.DataSourceFactory;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.Map;

public final class JpaEntityManagerFactoryProvider {
    private static volatile EntityManagerFactory factory;

    private JpaEntityManagerFactoryProvider() {
    }

    public static EntityManagerFactory get() {
        if (factory == null) {
            synchronized (JpaEntityManagerFactoryProvider.class) {
                if (factory == null) {
                    DatabaseConfig config = DatabaseConfig.fromEnvironment();
                    factory = Persistence.createEntityManagerFactory("algorithmPresentPU", Map.of(
                            "jakarta.persistence.nonJtaDataSource", DataSourceFactory.create(config),
                            "hibernate.dialect", "org.hibernate.dialect.SQLServerDialect"
                    ));
                }
            }
        }
        return factory;
    }

    public static void close() {
        if (factory != null) {
            synchronized (JpaEntityManagerFactoryProvider.class) {
                if (factory != null) {
                    factory.close();
                    factory = null;
                }
            }
        }
    }
}
