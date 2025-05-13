package com.example.sessionmanager.repository;


import com.example.sessionmanager.model.Data;
import org.springframework.data.jpa.repository.JpaRepository;

//Permette di gestire la memorizzazione in DB attraverso Jpa
public interface DataRepository extends JpaRepository<Data, Long> {}