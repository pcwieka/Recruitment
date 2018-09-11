package com.awin.recruitment.transfer.producer;

public interface Producer<T> {

    void produce(
        Iterable<T> messages
    );
}
