package io.appform.oncall24x7.events;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

/**
 *
 */
@Value
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AuthCodeReceivedEvent extends Event {

    String authCode;

    public AuthCodeReceivedEvent(String authCode) {
        super(EventType.AUTH_CODE_RECEIVED);
        this.authCode = authCode;
    }

    @Override
    public <T> T accept(EventVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
