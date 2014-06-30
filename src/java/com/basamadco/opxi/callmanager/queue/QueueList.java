package com.basamadco.opxi.callmanager.queue;

import com.basamadco.opxi.callmanager.call.CallService;

import java.util.*;

/**
 * A linkedlist queue implementation for concurrent clients
 *
 * @author Jrad
 */
final class QueueList {

    //     private LinkedList queue = Collections.synchronizedList( new LinkedList() );
    private final LinkedList queue = new LinkedList();

    void enqueue( Object o ) {
        synchronized ( queue ) {
            queue.addLast( o );
        }
    }

    Object dequeue() {
        try {
            synchronized ( queue ) {
                return queue.removeFirst();
            } // for using a sync. block in try/catch look @"http://www.artima.com/underthehood/threadP.html"
        } catch ( NoSuchElementException e ) {
            return null;
        }
    }

    Object peek() {
        synchronized ( queue ) {
            if ( !queue.isEmpty() ) {
                return queue.getFirst();
            }
        } // for using a sync. block in try/catch look @"http://www.artima.com/underthehood/threadP.html"
        return null;
    }

    int size() {
        synchronized ( queue ) {
            return queue.size();
        }
    }

    boolean isEmpty() {
        synchronized ( queue ) {
            return queue.isEmpty();
        }
    }

    public boolean dequeue( Object o ) {
        synchronized ( queue ) {
            return queue.remove( o );
        }
    }

    public Object[] readOnlyList() {
//        List clone = new ArrayList( queue.size() );
        return queue.toArray();
//        Collections.copy( clone , queue );
//        return clone;
    }

    public int indexOf( String key ) {
        synchronized ( queue ) {
            int index = 0;
            for ( Object o : queue ) {
                if ( ((CallService) o).getId().equals( key ) ) {
                    return index;
                }
                index++;
            }
            return -1;
        }
    }

    void empty() {
        synchronized ( queue ) {
            queue.clear();
//             nullifying the queue instance variable seems dangerous here
//             queue = null;
        }
    }
}