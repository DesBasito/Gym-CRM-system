package epam.gym.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.SharedEntityManagerCreator;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = {
        "epam.gym.infrastructure.repositories",
        "epam.gym.infrastructure.mappers",
        "epam.gym.domain.services",
        "epam.gym.infrastructure.security",
        "epam.gym.infrastructure.controller"
})
public class TestConfig {

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setPersistenceXmlLocation("classpath:META-INF/persistence.xml");
        em.setPersistenceUnitName("test");
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        return em;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager tm = new JpaTransactionManager();
        tm.setEntityManagerFactory(entityManagerFactory);
        return tm;
    }

    @Bean
    public EntityManager entityManager(EntityManagerFactory entityManagerFactory) {
        return SharedEntityManagerCreator.createSharedEntityManager(entityManagerFactory);
    }

    @Bean(initMethod = "migrate")
    public Flyway flyway() {
        Flyway flyway = Flyway.configure()
                .dataSource("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL", "sa", "")
                .locations("classpath:db/test-migrations")
                .cleanDisabled(false)
                .load();
        flyway.clean();
        return flyway;
    }
}
