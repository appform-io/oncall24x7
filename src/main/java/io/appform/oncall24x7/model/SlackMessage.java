package io.appform.oncall24x7.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Value;

/**
 *
 */
@Value
@Builder
public class SlackMessage {
    String text;
    JsonNode blocks;
}
