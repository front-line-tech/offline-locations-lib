package com.flt.liblookupclient.events;

import java.util.LinkedList;
import java.util.List;

public class StrongEventProvider<EventType> {

    protected List<Listener<EventType>> listeners;
    protected EventType sticky;

    public StrongEventProvider() {
        this.listeners = new LinkedList<>();
    }

    public synchronized void notifyListenersSticky(EventType event) {
        this.sticky = event;
        notifyListeners(event);
    }

    public synchronized void notifyListeners(EventType event) {
        List<Listener<EventType>> toRemove = new LinkedList<>();
        for (Listener<EventType> listener : listeners) {
            if (listener != null) {
                boolean finished = listener.onNotifyEvent(event);
                if (finished) { toRemove.add(listener); }
            } else {
                toRemove.add(listener);
            }
        }
        listeners.removeAll(toRemove);
    }

    public synchronized void cleanDeadRefs() {
        List<Listener<EventType>> toRemove = new LinkedList<>();
        for (Listener<EventType> listener : listeners) {
            if (listener.completed) {
                toRemove.add(listener);
            }
        }
        listeners.removeAll(toRemove);
    }

    public void requestSticky(Listener<EventType> listener) {
        if (hasSticky()) { listener.onNotifyEvent(sticky); }
    }

    public synchronized void addListener(Listener<EventType> listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
            if (hasSticky()) { listener.onNotifyEvent(sticky); }
        }
    }

    public boolean hasSticky() { return sticky != null; }

    public synchronized void removeListener(Listener<EventType> listener) {
        listeners.remove(listener);
    }

    public static abstract class Listener<E> {
        public boolean completed = false;

        public boolean onNotifyEvent(E event) {
            if (!completed) {
                completed = parseEvent(event);
            }
            return completed;
        }

        /**
         * @return true if this event was accepted and the listener is completed now
         */
        protected abstract boolean parseEvent(E event);
    }
}

