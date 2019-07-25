package com.app.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.HibernateCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.app.model.Product;

@Configuration
@EnableTransactionManagement
@EnableBatchProcessing
public class BatchConfig {
 
	@Value("${hibernate.dialect}")
	private String DIALECT;
 
	@Value("${hibernate.show_sql}")
	private String SHOW_SQL;
 
	@Value("${hibernate.hbm2ddl.auto}")
	private String HBM2DDL_AUTO;
 
	@Value("${entitymanager.packagesToScan}")
	private String PACKAGES_TO_SCAN;
 
	@Autowired
	private DataSource dataSource;
	
	@Autowired
	private JobBuilderFactory jf;
	
	@Autowired
    private StepBuilderFactory sf;
 
	@Bean
	public LocalSessionFactoryBean sessionFactory() {
		LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
		sessionFactory.setDataSource(dataSource);
		sessionFactory.setPackagesToScan(PACKAGES_TO_SCAN);		
		sessionFactory.setHibernateProperties(props()); 
		return sessionFactory;
	}
	@Bean
	public Properties props() {
		
		Properties hibernateProperties = new Properties();
		hibernateProperties.put("hibernate.dialect", DIALECT);
		hibernateProperties.put("hibernate.show_sql", SHOW_SQL);
		hibernateProperties.put("hibernate.hbm2ddl.auto", HBM2DDL_AUTO);
		return hibernateProperties;
	}	

	//Job
		
		@Bean
		public Job j1() throws Exception  {
			return jf.get("j1").incrementer(new RunIdIncrementer()).start(s1()).build();
		}
		
		//step
		@Bean
		public Step s1() throws Exception {
			return sf.get("s1")
					.<Product,Product>chunk(1)
					.reader(reader())
					.processor(processor())
					.writer(writer())
					.build();
		}
	
	//1. ItemReader, ItemProcessor, ItemWriter , LocalSessionFactoryBean
	
	@Bean
	public  ItemReader<Product> reader() throws Exception{	

		  
		  String hsqlQuery = " from com.app.model.Product";
		  HibernateCursorItemReader<Product> reader = new HibernateCursorItemReader<>();
		  reader.setQueryString(hsqlQuery);
		  reader.setSessionFactory(sessionFactory().getObject());
		  reader.setUseStatelessSession(true);
		  reader.setFetchSize(3);
		  reader.afterPropertiesSet();
		  reader.setSaveState(true);
		  return reader;
		
		
	}
	
	@Bean
	public ItemProcessor<Product,Product> processor(){
		return (p)->{
			return p;};
	}
	
	@Bean
	public ItemWriter<Product> writer(){			
		//Create writer instance
        FlatFileItemWriter<Product> writer = new FlatFileItemWriter<>();         
        //Set output file location
        writer.setResource(new FileSystemResource("D://sample/sample.csv"));               
        //All job repetitions should "append" to same output file
        writer.setAppendAllowed(true);         
        //Name field values sequence based on object properties
        writer.setLineAggregator(new DelimitedLineAggregator<Product>() {
            {
                setDelimiter(",");
                setFieldExtractor(new BeanWrapperFieldExtractor<Product>() {
                    {
                        setNames(new String[] { "id", "code", "cost","disc","gst" });
                    }
                });
            }
        });
        return writer;
	}
	
	
	
}
