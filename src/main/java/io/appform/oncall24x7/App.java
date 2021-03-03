package io.appform.oncall24x7;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import io.appform.oncall24x7.db.ChannelInfo;
import io.appform.oncall24x7.db.ChannelInfoDao;
import io.appform.oncall24x7.db.Oncall;
import io.appform.oncall24x7.db.OncallDao;
import io.appform.oncall24x7.events.Bus;
import io.appform.oncall24x7.events.SlackcallEventVisitor;
import io.appform.oncall24x7.resources.Root;
import io.dropwizard.Application;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.glassfish.jersey.client.ClientProperties;

/**
 * Main application
 */
@Slf4j
public class App extends Application<AppConfig> {

    private HibernateBundle<AppConfig> db = new HibernateBundle<AppConfig>(Oncall.class, ChannelInfo.class) {
        @Override
        public PooledDataSourceFactory getDataSourceFactory(AppConfig appConfig) {
            return appConfig.getDb();
        }
    };

    @Override
    public void initialize(Bootstrap<AppConfig> bootstrap) {
        super.initialize(bootstrap);
        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(bootstrap.getConfigurationSourceProvider(),
                                               new EnvironmentVariableSubstitutor(false)));
        bootstrap.addBundle(db);
    }

    @Override
    public void run(AppConfig configuration, Environment environment) throws Exception {
        val objectMapper = environment.getObjectMapper();
        objectMapper.registerModule(new ParameterNamesModule());
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        val httpClient = new JerseyClientBuilder(environment)
                .using(configuration.getHttpClient())
                .build(getName());
        httpClient.property(ClientProperties.CONNECT_TIMEOUT, 0);
        httpClient.property(ClientProperties.READ_TIMEOUT, 0);

        val channelInfoDao = new ChannelInfoDao(db.getSessionFactory());
        val oncallDao = new OncallDao(db.getSessionFactory());
        val bus = new Bus(new SlackcallEventVisitor(httpClient, objectMapper,
                                                    channelInfoDao));

        environment.jersey().register(
                new Root(objectMapper,
                         channelInfoDao,
                         oncallDao,
                         httpClient,
                         environment.lifecycle()
                                 .executorService("slack-sender-%d")
                                 .build(),
                         configuration.getSlackSecrets()));
    }

    public static void main(String[] args) throws Exception {
        val app = new App();
        app.run(args);
    }
}
