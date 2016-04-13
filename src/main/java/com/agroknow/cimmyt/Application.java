package com.agroknow.cimmyt;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Configuration
@ComponentScan({"com.agroknow.cimmyt.controllers"})
@EnableAutoConfiguration
public class Application extends SpringBootServletInitializer{

	private static Client client;
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application)
	{
		return application.sources(Application.class);
	}
	
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        
        Settings settings = ImmutableSettings.settingsBuilder()
		        .put("cluster.name", "cimmyt").build();
		
		client = new TransportClient(settings)
		        .addTransportAddress(new InetSocketTransportAddress("localhost", 9300));
		        //.addTransportAddress(new InetSocketTransportAddress("host2", 9300));
		System.out.println("Status:"+client.settings().toString());
		// on shutdown
		
		GetResponse response = client.prepareGet("cimmyt", "object", "1403840553")
		        .execute()
		        .actionGet();

		System.out.println("Response:"+response.getSourceAsString());
		
		//client.close();
        
    }
}
/*
@RestController
class GreetingController {
    
    @RequestMapping("/hello/{name}")
    String hello(@PathVariable String name) {
        
    	Settings settings = ImmutableSettings.settingsBuilder()
		        .put("cluster.name", "cimmyt").build();
    	
    	Client client = new TransportClient(settings)
		        .addTransportAddress(new InetSocketTransportAddress("localhost", 9300));
		        //.addTransportAddress(new InetSocketTransportAddress("host2", 9300));
		System.out.println("Status:"+client.settings().toString());
		// on shutdown
		
		GetResponse response = client.prepareGet("cimmyt", "object", name)
		        .execute()
		        .actionGet();
    	
    	return "Hello, " + name + "!"+response.getSourceAsString();
        
    }
}*/