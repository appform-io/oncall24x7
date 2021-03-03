package io.appform.oncall24x7;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Secrets for slack connectivity
 */
@Data
@NoArgsConstructor
public class SlackSecrets {
    @NotEmpty
    private String clientId;

    @NotEmpty
    private String clientSecret;

    @NotEmpty
    private String signingSecret;
}
