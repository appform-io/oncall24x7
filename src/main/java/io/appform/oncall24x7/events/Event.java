package io.appform.oncall24x7.events;

import lombok.Data;

/**
 *
 */
@Data
public abstract class Event {
    private final EventType type;

    public abstract <T> T accept(final EventVisitor<T> visitor);
}
