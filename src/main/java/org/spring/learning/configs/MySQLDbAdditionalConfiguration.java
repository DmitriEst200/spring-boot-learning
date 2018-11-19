package org.spring.learning.configs;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.spi.LoggerContextFactory;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

@Configuration
@EnableJpaAuditing
public class MySQLDbAdditionalConfiguration {

    // annotation @Autowired provides ( by help of ioc )
    // injection of registered bean into property of class
    // MySqlDbAdditionalConfiguration which are tagged by this annotation

    @Autowired
    private LocalContainerEntityManagerFactoryBean lcem;

    // better to set unchanged configuration properties in the separate method
    // of configuration class, than in the configuration
    // file( although this depends of specific task )

    @PostConstruct
    private void loadUnchangedJPAProperties(){

        Map<String, Object>props = lcem.getJpaPropertyMap();

        props.put("spring.jpa.hibernate.naming.physical-strategy",
                  "org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl");

        lcem.setJpaPropertyMap(props);
    }
}
