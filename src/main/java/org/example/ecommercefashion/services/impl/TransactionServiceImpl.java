package org.example.ecommercefashion.services.impl;

import org.example.ecommercefashion.entities.Transaction;
import org.example.ecommercefashion.repositories.TransactionRepository;
import org.example.ecommercefashion.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public Transaction create(Transaction transaction) {
       Transaction transactions = new Transaction();
       transaction.setCode(transaction.getCode());
       transaction.setBody(transaction.getBody());
       transaction.setAmountIn(transaction.getAmountIn());
       transaction.setReferenceNumber(transaction.getReferenceNumber());
       transaction.setTransactionDate(transaction.getTransactionDate());
       transaction.setTransactionContent(transaction.getTransactionContent());
       return transactionRepository.save(transactions);
    }
}
