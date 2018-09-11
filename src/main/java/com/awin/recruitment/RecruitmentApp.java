package com.awin.recruitment;

import com.awin.recruitment.data.Transaction;
import com.awin.recruitment.infrastructure.spring.ClassPathXmlApplicationContextFactory;
import com.awin.recruitment.transfer.DataTransferServiceRunner;
import com.awin.recruitment.transfer.consumer.Consumer;
import com.awin.recruitment.transfer.consumer.TransactionConsumer;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

@SuppressWarnings("unchecked")
public final class RecruitmentApp {

    private RecruitmentApp() { }


    public static void main(
        String[] args
    ) {
        ClassPathXmlApplicationContext applicationContext = ClassPathXmlApplicationContextFactory.create();

        DataTransferServiceRunner<Transaction> dataTransferServiceRunner = (DataTransferServiceRunner<Transaction>) applicationContext.getBean("dataTransferServiceRunner");

        dataTransferServiceRunner.consume((ArrayList<Transaction>)applicationContext.getBean("transactionsList"));

    }
}
