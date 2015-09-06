package com.mk.framework.configuration;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.stereotype.Repository;

/**
 * ElasticsearchConfiguration
 * 
 * @author chuaiqing.
 *
 */
@Configuration
@PropertySource("classpath:elasticsearch.properties")
@EnableElasticsearchRepositories(basePackages = { "com.mk.es.repository" })
@Repository
public class ElasticsearchConfiguration {
	@Resource
	private Environment env;

	@Bean
	public Client client() {
		Map<String, String> settingMap = new HashMap<String, String>();
		String clusterHosts = this.env.getProperty("elasticsearch.clusterhosts", "localhost:9300");
		// settingMap.put("node.client", "false");
		// settingMap.put("node.data", "true");
		// settingMap.put("node.local", "true");
		settingMap.put("cluster.name", this.env.getProperty("elasticsearch.clustername", "ota2-test-elastic"));
		Settings settings = ImmutableSettings.settingsBuilder().put(settingMap).build();
		TransportClient client = new TransportClient(settings);
		String[] hostsSplit = clusterHosts.split(",");
		if (hostsSplit != null) {
			for (String hostInfo : hostsSplit) {
				int flgPos = hostInfo.indexOf(":");
				if (flgPos > -1) {
					String host = hostInfo.substring(0, flgPos).trim();
					int port = Integer.parseInt(hostInfo.substring(flgPos + 1).trim());
					client.addTransportAddress(new InetSocketTransportAddress(host, port));
				}
			}
		}
		return client;
	}

	@Bean
	public ElasticsearchOperations elasticsearchTemplate() {
		return new ElasticsearchTemplate(this.client());
	}
}
