package com.basamadco.opxi.callmanager.queue;

import com.basamadco.opxi.callmanager.call.CallService;

/**
 * A priority queue data structure for manipulating OpxiCallManager sip calls
 *
 * @author Jrad
 */
public class CallQueue {

    private QueueList _queue;

    CallQueue() {
        this._queue = new QueueList();
    }

    public CallService peek() {
        return (CallService) _queue.peek();
    }

    public void queue( CallService call ) {
        _queue.enqueue( call );
    }

    public CallService dequeue() {
        return (CallService) _queue.dequeue();
    }

    public boolean dequeue( CallService call ) {
        if ( !_queue.isEmpty() ) {
            return _queue.dequeue( call );
        }
        return false;
    }

    public int size() {
        return _queue.size();
    }

    public int callIndex( String callId ) {
        return _queue.indexOf( callId );
    }

    public Object[] readOnlyCallList() {
        return _queue.readOnlyList();
    }

    void clear() {
        _queue.empty();
        _queue = null;
    }

}