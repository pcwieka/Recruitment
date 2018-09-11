package com.awin.recruitment.transfer.producer;

import com.awin.recruitment.data.EnrichedTransaction;
import com.awin.recruitment.data.Product;
import com.awin.recruitment.data.Transaction;
import org.apache.log4j.Logger;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class TransactionProducer implements Producer<Transaction>,Runnable{

    private Long transactionsExitId;
    private Integer threadSleepMillis;
    private BlockingQueue<Transaction> transactionQueue;
    private final Queue<Transaction> outputTransactions = new LinkedList<>();
    private final AtomicBoolean isProducerThreadRunning = new AtomicBoolean(false);
    private static final Logger logger = Logger.getLogger(TransactionProducer.class);

    public TransactionProducer(BlockingQueue<Transaction> transactionQueue) {
        this.transactionQueue = transactionQueue;
    }

    @Override
    public void produce(Iterable<Transaction> messages) {

        try{

            Transaction transaction;

            while(!(transaction = transactionQueue.take()).getTransactionId().equals(transactionsExitId)){

                Thread.sleep(threadSleepMillis);

                EnrichedTransaction enrichedTransaction = getEnrichedTransaction(transaction);

                outputTransactions.add(enrichedTransaction);

                logger.info("Produced: transaction ID: " + enrichedTransaction.getTransactionId() + ", products total cost: " + enrichedTransaction.getTotalCost());

            }

            transactionQueue.clear();

        }catch(InterruptedException e) {

            Thread.currentThread().interrupt();
            logger.info("Thread was interrupted, failed to complete operation.");
        }

    }

    @Override
    public void run() {

        isProducerThreadRunning.set(true);

        produce(outputTransactions);

        isProducerThreadRunning.set(false);

        logger.info("Producing finished.");

    }

    public EnrichedTransaction getEnrichedTransaction(Transaction transaction){

        BigDecimal totalCost = new BigDecimal(0);

        for(Product product : transaction.getProducts()){

            totalCost = totalCost.add(product.getCost());

        }

        return new EnrichedTransaction(transaction,totalCost);

    }

    public void setTransactionsExitId(Long transactionsExit) {
        this.transactionsExitId = transactionsExit;
    }

    public AtomicBoolean isProducerThreadRunning() {
        return isProducerThreadRunning;
    }

    public void setThreadSleepMillis(Integer threadSleepMillis) {
        this.threadSleepMillis = threadSleepMillis;
    }

    public BlockingQueue<Transaction> getDataBuffer() {
        return transactionQueue;
    }

    public Queue<Transaction> getOutputTransactions() {
        return outputTransactions;
    }
}
