package com.awin.recruitment.transfer.consumer;

public interface Consumer<T>{

    void consume(
        Iterable<T> messages
    );


}