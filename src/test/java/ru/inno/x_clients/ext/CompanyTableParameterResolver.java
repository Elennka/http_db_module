package ru.inno.x_clients.ext;

import org.junit.jupiter.api.extension.*;
import ru.inno.x_clients.db.CompanyRepository;
import ru.inno.x_clients.db.CompanyRepositoryJDBC;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class CompanyTableParameterResolver implements ParameterResolver, AfterAllCallback, BeforeAllCallback {
    private Connection connection;

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().equals(CompanyRepository.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return new CompanyRepositoryJDBC(connection);
    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {

        String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        String appConfigPath = rootPath + "env.properties";

        Properties properties = new Properties();
        properties.load(new FileInputStream(appConfigPath));

        String connectionString = properties.getProperty("db.connection_string");
        String username = properties.getProperty("db.user");
        String password = properties.getProperty("db.pass");

        connection = DriverManager.getConnection(connectionString, username, password);
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        if (connection != null) {
            connection.close();
        }
    }
}
