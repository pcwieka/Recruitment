package com.awin.recruitment

import com.awin.recruitment.data.Transaction
import com.awin.recruitment.transfer.consumer.Consumer
import com.awin.recruitment.transfer.producer.Producer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

import java.util.concurrent.BlockingQueue
import java.util.function.Predicate

@ContextConfiguration(locations = "classpath:/di.test.xml")
@ActiveProfiles("test")
class RecruitmentAppTest extends Specification {

    @Autowired
    ApplicationContext applicationContext

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

        def transferedMessages = Arrays.asList(messages)
        def bufferedMessages = Arrays.asList(messagesDataBuffer)

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

        setup:

        def producer = applicationContext.getBean("producer")
        def transaction1 = applicationContext.getBean("transaction1")
        def transaction2 = applicationContext.getBean("transaction2")
        def totalCost1 = 656.25
        def totalCost2 = 1355.74

        when:



        then:

        1!=1


    }
}
