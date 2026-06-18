package com.example.main.config;

public record DatabaseConfig(
        String host,
        int port,
        String database,
        boolean integratedSecurity,
        boolean trustServerCertificate,
        boolean sendStringParametersAsUnicode
) {
    public static DatabaseConfig fromEnvironment() {
        String host = env("DB_HOST", "localhost");
        int port = Integer.parseInt(env("DB_PORT", "1433"));
        String database = env("DB_NAME", "sort_algorithm_present");
        boolean integratedSecurity = Boolean.parseBoolean(env("DB_INTEGRATED_SECURITY", "true"));
        boolean trust = Boolean.parseBoolean(env("DB_TRUST_SERVER_CERTIFICATE", "true"));
        boolean sendUnicode = Boolean.parseBoolean(env("DB_SEND_STRING_PARAMETERS_AS_UNICODE", "true"));
        return new DatabaseConfig(host, port, database, integratedSecurity, trust, sendUnicode);
    }

    private static String env(String key, String defaultValue) {
        String value = System.getenv(key);
        return value == null || value.isBlank() ? defaultValue : value;
    }
}
