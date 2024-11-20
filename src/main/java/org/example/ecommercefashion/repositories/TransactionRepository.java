package org.example.ecommercefashion.repositories;

import org.example.ecommercefashion.dtos.response.BankTransactionResponse;
import org.example.ecommercefashion.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {}
