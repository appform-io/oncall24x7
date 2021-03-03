package io.appform.oncall24x7;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import org.apache.commons.text.StrSubstitutor;

import java.util.Map;

/**
 *
 */
public enum Message {
    BOT_ADDED_MESSAGE("<@${owner}> has added Oncall24x7 to this channel."),
    BOT_MENTIONED("<${oncall}> please note that <@${sender}> has sent you a message"),
    ONCALL_NOT_SET("Sorry <@${sender}>, this channel does not have a designated on-call person.")
    ;

    private final String template;

    Message(String template) {
        this.template = template;
    }

    public static String text(final Message message, final Map<String, Object> context) {
        return StrSubstitutor.replace(message.template, context);
    }

    public static JsonNode markdown(
            final Message message,
            final Map<String, Object> context,
            ObjectMapper mapper,
            String quote) {
        final String text = text(message, context);
        final String messageTxt = Strings.isNullOrEmpty(quote)
                                  ? text
                                  : (text + "\n>" + quote);
        return mapper.createArrayNode()
                .add(mapper.createObjectNode()
                             .put("type", "section")
                             .set("text", mapper.createObjectNode()
                                     .put("type", "mrkdwn")
                                     .put("text", messageTxt)));
    }
}
