package com.awin.recruitment.transfer;

import com.awin.recruitment.transfer.consumer.Consumer;
import com.awin.recruitment.transfer.producer.Producer;
import org.apache.log4j.Logger;

public class DataTransferServiceRunner<T> {

    private Consumer<T> consumer;
    private Producer<T> producer;

    private static final Logger logger = Logger.getLogger(DataTransferServiceRunner.class);

    public DataTransferServiceRunner(Consumer<T> consumer, Producer<T> producer) {
        this.consumer = consumer;
        this.producer = producer;
    }

    public void consume(Iterable<T> messages){

        consumer.consume(messages);

        Thread consumerThread = new Thread((Runnable)consumer);
        Thread producerThread = new Thread((Runnable)producer);

        producerThread.start();
        consumerThread.start();

        try {

            consumerThread.join();
            logger.info("Consumer thread finished.");
            producerThread.join();
            logger.info("Producer thread finished.");

        } catch (InterruptedException e) {

            logger.info("Threads were interrupted, failed to complete operation");

        }

    }

    public Consumer<T> getConsumer() {
        return consumer;
    }

    public Producer<T> getProducer() {
        return producer;
    }
}
