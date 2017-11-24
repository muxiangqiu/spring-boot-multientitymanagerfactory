package org.malagu.multientitymanagerfactory;

import java.util.Map;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

import org.malagu.multidatasource.DataSourceAutoConfiguration;
import org.malagu.multidatasource.DataSources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.jta.JtaTransactionManager;


/**
 * @author Kevin Yang (mailto:muxiangqiu@gmail.com)
 * @since 2017年11月24日
 */
@Configuration
@ConditionalOnClass({ LocalContainerEntityManagerFactoryBean.class,
		EnableTransactionManagement.class, EntityManager.class })
@Conditional(HibernateJpaBaseConfiguration.HibernateEntityManagerCondition.class)
@ConditionalOnBean(name = DataSources.dataSource2)
@AutoConfigureAfter({ DataSourceAutoConfiguration.class })
public class HibernateJpa2AutoConfiguration extends HibernateJpaBaseConfiguration {

	@Autowired(required = false)
	@Qualifier(DataSources.dataSource2)
	private DataSource dataSource;
	
	private JpaProperties jpaProperties;
	
	
	@Autowired(required = false)
	@Qualifier(JtaTransactionManagers.jtaTransactionManager2)
	private JtaTransactionManager jtaTransactionManager;
	
	
	@Override
	protected DataSource getDataSource() {
		return dataSource;
	}

	@Override
	@Bean("jpa2Properties")
	@ConfigurationProperties(prefix = "spring.jpa2")
	public JpaProperties getJpaProperties() {
		this.jpaProperties = new JpaProperties();
		return jpaProperties;
	}
	
	@Override
	protected JtaTransactionManager getJtaTransactionManager() {
		return jtaTransactionManager;
	}
	
	@Bean
	@ConditionalOnMissingBean(name = TransactionManagers.transactionManager2)
	public PlatformTransactionManager transactionManager2() {
		JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
		jpaTransactionManager.setPersistenceUnitName(EntityManagerFactories.entityManagerFactory2);
		return jpaTransactionManager;
	}
	
	@Bean
	@ConditionalOnMissingBean(name = EntityManagerFactories.entityManagerFactory2)
	public LocalContainerEntityManagerFactoryBean entityManagerFactory2() {
		EntityManagerFactoryBuilder factoryBuilder = getEntityManagerFactoryBuilder();
		Map<String, Object> vendorProperties = getVendorProperties();
		customizeVendorProperties(vendorProperties);
		return factoryBuilder.dataSource(getDataSource()).packages(getPackagesToScan(jpaProperties.getPackagesToScan()))
				.properties(vendorProperties).jta(isJta()).persistenceUnit(EntityManagerFactories.entityManagerFactory2).build();
	}

}
