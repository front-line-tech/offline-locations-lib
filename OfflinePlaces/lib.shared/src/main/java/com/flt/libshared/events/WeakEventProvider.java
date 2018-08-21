package com.flt.libshared.events;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

public class WeakEventProvider<EventType> {

    protected List<WeakReference<Listener<EventType>>> listeners;
    protected EventType sticky;

    public WeakEventProvider() {
        this.listeners = new LinkedList<>();
    }

    public synchronized void notifyListenersSticky(EventType event) {
        this.sticky = event;
        notifyListeners(event);
    }

    public synchronized  void clearSticky() {
        this.sticky = null;
    }

    public synchronized void notifyListeners(EventType event) {
        List<WeakReference<Listener<EventType>>> toRemove = new LinkedList<>();
        for (WeakReference<Listener<EventType>> reference : listeners) {
            Listener<EventType> listener = reference.get();
            if (listener != null) {
                boolean completed = listener.onNotifyEvent(event);
                if (completed) {
                    toRemove.add(reference);
                }
            } else {
                toRemove.add(reference);
            }
        }
        listeners.removeAll(toRemove);
    }

    public synchronized void cleanDeadRefs() {
        List<WeakReference<Listener<EventType>>> toRemove = new LinkedList<>();
        for (WeakReference<Listener<EventType>> reference : listeners) {
            if (reference.get() == null) {
                toRemove.add(reference);
            }
        }
        listeners.removeAll(toRemove);
    }

    public void requestSticky(Listener<EventType> listener) {
        if (hasSticky()) { listener.onNotifyEvent(sticky); }
    }

    public synchronized void addListener(Listener<EventType> listener) {
        boolean alreadyListening = false;
        for (WeakReference<Listener<EventType>> reference : listeners) {
            if (reference.get() == listener) {
                alreadyListening = true;
                break;
            }
        }

        if (!alreadyListening) {
            listener.reset();
            listeners.add(new WeakReference<Listener<EventType>>(listener));
        }

        // always notify with the sticky state - even if the add is redundant
        if (hasSticky()) { listener.onNotifyEvent(sticky); }
    }

    public boolean hasSticky() { return sticky != null; }

    public synchronized void removeListener(Listener<EventType> listener) {
        List<WeakReference<Listener<EventType>>> toRemove = new LinkedList<>();
        for (WeakReference<Listener<EventType>> reference : listeners) {
            if (reference.get() == listener || reference.get() == null) {
                toRemove.add(reference);
                break;
            }
        }
        listeners.removeAll(toRemove);
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

        public void reset() {
            completed = false;
        }
    }
}

