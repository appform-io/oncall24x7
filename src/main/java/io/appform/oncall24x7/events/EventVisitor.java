package io.appform.oncall24x7.events;

/**
 *
 */
public interface EventVisitor<T> {
    T visit(AuthCodeReceivedEvent authCodeReceivedEvent);
}
