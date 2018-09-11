package com.awin.recruitment.transfer.consumer;

import com.awin.recruitment.data.Transaction;
import org.apache.log4j.Logger;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class TransactionConsumer implements Consumer<Transaction>,Runnable{

    private Long transactionsExitId;
    private Integer threadSleepMillis;
    private BlockingQueue<Transaction> transactionQueue;
    private final Queue<Transaction> inputTransactions = new LinkedList<>();
    private final AtomicBoolean isConsumerThreadRunning = new AtomicBoolean(false);
    private static final Logger logger = Logger.getLogger(TransactionConsumer.class);

    public TransactionConsumer(BlockingQueue<Transaction> transactionQueue) {
        this.transactionQueue = transactionQueue;
    }

    @Override
    public void consume(Iterable<Transaction> messages) {

        messages.forEach(transaction -> inputTransactions.add(transaction));

    }

    @Override
    public void run() {

        isConsumerThreadRunning.set(true);

        inputTransactions.forEach(transaction -> {

            try {

                Thread.sleep( (int)(Math.random() * threadSleepMillis));

                transactionQueue.put(transaction);

                logger.info("Consumed: transaction ID: " + transaction.getTransactionId());

            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();
                logger.info("Thread was interrupted, failed to complete operation.");
            }

        });

        Transaction exitTransaction = new Transaction(transactionsExitId,null,null);

        try {

            transactionQueue.put(exitTransaction);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.info("Thread was interrupted, failed to put exit transaction.");
        }

        isConsumerThreadRunning.set(false);

        logger.info("Consuming finished.");

    }

    public AtomicBoolean isConsumerThreadRunning() {
        return isConsumerThreadRunning;
    }

    public void setTransactionsExitId(Long transactionsExitId) {
        this.transactionsExitId = transactionsExitId;
    }

    public void setThreadSleepMillis(Integer threadSleepMillis) {
        this.threadSleepMillis = threadSleepMillis;
    }

    public BlockingQueue<Transaction> getDataBuffer() {
        return transactionQueue;
    }

}
