package com.awin.recruitment

import com.awin.recruitment.data.EnrichedTransaction
import com.awin.recruitment.data.Product
import com.awin.recruitment.data.Transaction
import com.awin.recruitment.transfer.DataTransferServiceRunner
import com.awin.recruitment.transfer.producer.TransactionProducer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import java.util.function.Predicate

@ContextConfiguration(locations = "classpath:/di.test.xml")
@ActiveProfiles("test")
class RecruitmentAppTest extends Specification {

    @Autowired
    ApplicationContext applicationContext

    def "Test eqauls methods of Transaction, EnrichedTransaction and Product classes"(){

        setup:

        Transaction transaction1 = applicationContext.getBean("transaction1")
        Transaction transaction11 = applicationContext.getBean("transaction1")
        Transaction transaction2 = applicationContext.getBean("transaction2")

        EnrichedTransaction enrichedTransaction1 = new EnrichedTransaction(transaction1,656.25)
        EnrichedTransaction enrichedTransaction11 = new EnrichedTransaction(transaction1,656.25)
        EnrichedTransaction enrichedTransaction2 = new EnrichedTransaction(transaction2,1355.74)
        Transaction enrichedTransaction = new EnrichedTransaction(transaction1,656.25)

        Product product1 = applicationContext.getBean("ordinaryShoes")
        Product product11 = applicationContext.getBean("ordinaryShoes")
        Product product2 = applicationContext.getBean("funnyShoes")

        expect:

        transaction1.equals(transaction1)
        transaction1.equals(transaction11)
        transaction11.equals(transaction1)
        !transaction1.equals(transaction2)
        enrichedTransaction.equals(enrichedTransaction1)
        enrichedTransaction1.equals(enrichedTransaction)
        enrichedTransaction1.equals(enrichedTransaction11)
        !enrichedTransaction2.equals(enrichedTransaction1)
        !product1.equals(product2)
        product1.equals(product1)
        product1.equals(product11)

    }

    def "Implementations of consumer and producer refer to the same BlockingQueue ('data buffer')"() {

        setup: "Creating consumer and producer"

        def consumer = applicationContext.getBean("consumer")
        def producer = applicationContext.getBean("producer")

        when: "Getting data buffer as blocking queue from both consumer and producer"

        def consumerBlockingQueue = consumer.getDataBuffer()
        def producerBlockingQueue = producer.getDataBuffer()

        then: "Refers to the same BlockingQueue"

        consumerBlockingQueue == producerBlockingQueue


    }

    def "Consumer consumes all messages and puts them into the 'data buffer' - transactionQueue"() {

        given: "Setting up consumer and data buffer"

        def consumer = applicationContext.getBean("consumer")

        def messages = applicationContext.getBean("transactionsList")

        //empty data buffer
        def messagesDataBuffer = applicationContext.getBean("transactionQueue")

        when: "Consumer consumes given messages (transactions) and transfers them to the buffer"

        consumer.consume(messages)

        def consumerThread = new Thread((Runnable)consumer)
        consumerThread.start()

        consumerThread.join()

        ArrayList transferedMessages = Arrays.asList(messages)
        ArrayList bufferedMessages = Arrays.asList(messagesDataBuffer)

        //removing exit transaction

        bufferedMessages.removeIf(new Predicate() {
            @Override
            boolean test(Object o) {
                return ((Transaction)o).getTransactionId().equals(-1L)
            }
        })

        then: "Data buffer contains all the transfered messages"

        transferedMessages.equals(bufferedMessages)

    }

    def "Creating enriched transaction"(){

        setup: "Setting up producer with enrichment ability"

        def producer = applicationContext.getBean("producer")
        def transaction = applicationContext.getBean("transaction2")
        def totalCost = 1355.74

        expect: "Total cost of example transaction is consistent with the given value"

        totalCost.doubleValue() == producer.getEnrichedTransaction(transaction).getTotalCost().doubleValue()

    }

    def "Producer processes and enriches all messages put by the consumer"(){

        setup: "Setting up producer with enrichment ability"

        def producer = applicationContext.getBean("producer")
        def transactionsList = applicationContext.getBean("transactionsList")
        def totalCost1 = 656.25
        def totalCost2 = 1355.74

        when: "Calling method enriching passed transactions and returning them to variables"

        producer.produce(transactionsList)

        def enrichedTransactionsOutput = producer.getOutputTransactions()

        def outputEnrichedTransaction1 = enrichedTransactionsOutput.poll()
        def outputEnrichedTransaction2 = enrichedTransactionsOutput.poll()

        then: "Total costs of transactions should return given values"

        outputEnrichedTransaction1.getTotalCost().doubleValue() == totalCost1.doubleValue()
        outputEnrichedTransaction2.getTotalCost().doubleValue() == totalCost2.doubleValue()

    }

    def "Producer and consumer actions combined together - at the end - buffer empty, producer returns enriched transactions"(){

        setup: "Using DataTransferServiceRunner and defining some transactions to compare with the result of processing"

        DataTransferServiceRunner<Transaction> dataTransferServiceRunner = (DataTransferServiceRunner<Transaction>) applicationContext.getBean("dataTransferServiceRunner");
        def messagesDataBuffer = applicationContext.getBean("transactionQueue")

        def transaction1 = applicationContext.getBean("transaction1")
        def transaction2 = applicationContext.getBean("transaction2")

        def enrichedTransaction1 = new EnrichedTransaction(transaction1,656.25)
        def enrichedTransaction2 = new EnrichedTransaction(transaction2,1355.74)

        when: "Consuming and Producing threads started simultaneously by dataTransferServiceRunner, getting enriched transactions output"

        dataTransferServiceRunner.consume((ArrayList<Transaction>)applicationContext.getBean("transactionsList"));

        TransactionProducer transactionProducer = dataTransferServiceRunner.getProducer()
        def outputTransactions = transactionProducer.getOutputTransactions()

        then: "Data Buffer is empty after process, output enriched transactions are equal given ones"

        messagesDataBuffer.isEmpty()
        outputTransactions.poll().equals(enrichedTransaction1)
        outputTransactions.poll().equals(enrichedTransaction2)

    }
}
