package code.project.springbootjwt.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.*;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.datasource.embedded.*;
import org.springframework.orm.jpa.*;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.sql.Driver;

@Configuration
@EnableTransactionManagement
@ComponentScan("code.project.springbootjwt.model")
@EnableJpaRepositories("code.project.springbootjwt.repository")
public class JPAConfig {

    public static final String DB_USERNAME = "digital";
    public static final String DB_PASSWORD = "digital";
    public static final String DB_URL = "jdbc:hsqldb:file:./src/main/resources/embeddedDB;";

    @Bean(name = "dataSource")
    public DataSource dataSource() {

        return new EmbeddedDatabaseBuilder().setDataSourceFactory(new DataSourceFactory() {
            private final SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
            @Override
            public ConnectionProperties getConnectionProperties() {
                return new ConnectionProperties() {
                    @Override
                    public void setDriverClass(Class<? extends Driver> aClass) {
                        dataSource.setDriverClass(org.hsqldb.jdbcDriver.class);
                    }

                    @Override
                    public void setUrl(String s) {
                        dataSource.setUrl(DB_URL);
                    }

                    @Override
                    public void setUsername(String s) {
                        dataSource.setUsername(DB_USERNAME);
                    }

                    @Override
                    public void setPassword(String s) {
                        dataSource.setPassword(DB_PASSWORD);
                    }
                };
            }

            @Override
            public DataSource getDataSource() {
                return dataSource;
            }
        })
                .addScript("classpath:schema.sql")
                .build();
    }


    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean() {
        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setDataSource(dataSource());
        factoryBean.setPackagesToScan(new String[] { "code.project.springbootjwt.model" });
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setShowSql(true);
        factoryBean.setJpaVendorAdapter(vendorAdapter);
        return factoryBean;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactoryBean().getObject());
        return transactionManager;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }
    


}
