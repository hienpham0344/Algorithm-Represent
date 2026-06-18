package com.example.main.config;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;

import javax.sql.DataSource;

public final class DataSourceFactory {
    private DataSourceFactory() {
    }

    public static DataSource create(DatabaseConfig config) {
        SQLServerDataSource dataSource = new SQLServerDataSource();
        dataSource.setServerName(config.host());
        dataSource.setPortNumber(config.port());
        dataSource.setDatabaseName(config.database());
        dataSource.setIntegratedSecurity(config.integratedSecurity());
        dataSource.setTrustServerCertificate(config.trustServerCertificate());
        dataSource.setSendStringParametersAsUnicode(config.sendStringParametersAsUnicode());
        return dataSource;
    }
}
