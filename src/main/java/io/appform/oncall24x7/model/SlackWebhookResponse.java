package io.appform.oncall24x7.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

/**
 *
 */
@Value
@Builder
public class SlackWebhookResponse {
    public enum ResponseType {
        ;
        public static final String EPHEMERAL = "ephemeral";
        public static final String IN_CHANNEL = "in_channel";
    }

    @JsonProperty("text")
    String text;

    @JsonProperty("response_type")
    String responseType;
}
