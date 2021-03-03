package io.appform.oncall24x7.events;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

/**
 *
 */
public class Bus {
    private final EventBus eventBus;
    private final SlackcallEventVisitor visitor;

    public Bus(SlackcallEventVisitor visitor) {
//        this.eventBus = new AsyncEventBus(Executors.newSingleThreadExecutor());
        this.eventBus = new EventBus();
        this.visitor = visitor;

        this.eventBus.register(this);
    }

    public void publish(Event event) {
        eventBus.post(event);
    }

    @Subscribe
    private void handleEvent(Event event) {
        event.accept(visitor);
    }

}
