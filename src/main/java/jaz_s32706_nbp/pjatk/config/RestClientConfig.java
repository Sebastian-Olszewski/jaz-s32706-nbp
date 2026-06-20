package jaz_s32706_nbp.pjatk.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient nbpRestClient(RestClient.Builder builder, @Value("${nbp.api.base-url}") String nbpBaseUrl) {
        return builder.baseUrl(nbpBaseUrl).build();
    }
}
