package io.appform.oncall24x7;


import io.dropwizard.Configuration;
import io.dropwizard.client.JerseyClientConfiguration;
import io.dropwizard.db.DataSourceFactory;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class AppConfig extends Configuration {

    @Valid
    @NotNull
    @Getter
    private DataSourceFactory db;

    @Valid
    @NotNull
    private JerseyClientConfiguration httpClient = new JerseyClientConfiguration();

    @Valid
    @NotNull
    private SlackSecrets slackSecrets;
}
