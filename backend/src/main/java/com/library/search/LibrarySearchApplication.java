package com.library.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LibrarySearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibrarySearchApplication.class, args);
        System.out.println("==========================================================");
        System.out.println(" DIGITAL LIBRARY SEARCH SYSTEM (B.Tech CSE DSA-3) STARTED");
        System.out.println(" Backend API Server : http://localhost:8080/api");
        System.out.println(" H2 Database Console: http://localhost:8080/h2-console");
        System.out.println(" Swagger / Endpoints: GET /api/search, GET /api/documents");
        System.out.println("==========================================================");
    }
}
