package com.example.spring.service;

import com.example.spring.entity.transaction.*;
import com.example.spring.repository.TransactionLogRepository;
import com.example.spring.repository.TransactionLogSpareRepository;
import com.google.gson.Gson;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Log4j2
@Service
public class TestService {
    @Autowired
    TransactionLogRepository logRepo;
    @Autowired
    TransactionLogSpareRepository spareRepo;

    public void saveSpare() {
        String path = "C:\\Users\\ADMIN\\Documents\\PaymentAPI log\\all_log_20250312.txt";

        try {
            Scanner scanner = new Scanner(new File(path));

            while (scanner.hasNextLine()) {
                String messageBody = scanner.nextLine();
                Gson gson = new Gson();
                TransactionLogDto l = gson.fromJson(messageBody, TransactionLogDto.class);

                TransactionLogSpare transaction = TransactionLogSpare.builder()
                        .mode(l.getMode())
                        .amount(l.getAmount())
                        .chargeTime(l.getChargeTime())
                        .channel(l.getChannel())
                        .serviceId(l.getServiceId())
                        .msisdn(l.getMsisdn())
                        .params(l.getParams())
                        .type(l.getType())
                        .command(l.getCommand())
                        .transactionId(l.getTransactionId())
                        .build();
                spareRepo.save(transaction);
            }

            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void test0(String file1, String file2) {
        String path = "C:\\Users\\ADMIN\\Documents\\PaymentAPI log\\";
        List<TransactionLogDto> extractList1 = extractFile(path + "log-" + file1 + ".txt");
        List<TransactionLogDto> extractList2 = extractFile(path + "Untitled-" + file1 + ".txt");

        int count = 0;

        for (TransactionLogDto l : extractList1) {
            boolean check = extractList2.stream().noneMatch(dto -> dto.getMsisdn().equals(l.getMsisdn())
                    && dto.getChannel().equals(l.getChannel())
                    && dto.getServiceId().equals(l.getServiceId())
                    && dto.getChargeTime().equals(l.getChargeTime())
                    && dto.getTransactionId().equals(l.getTransactionId()));

            if (check) {
//                log.info(l);
                TransactionLog transaction = TransactionLog.builder()
                        .mode(l.getMode())
                        .amount(l.getAmount())
                        .chargeTime(l.getChargeTime())
                        .channel(l.getChannel())
                        .serviceId(l.getServiceId())
                        .msisdn(l.getMsisdn())
                        .params(l.getParams())
                        .type(l.getType())
                        .command(l.getCommand())
                        .transactionId(l.getTransactionId())
                        .build();
                logRepo.save(transaction);
//                log.info(transaction);
                count = count + 1;
            }
        }

        log.info("Count: " + count);
    }

    private List<TransactionLogDto> extractFile(String url) {
        List<TransactionLogDto> result = new ArrayList<>();

        try {
            Scanner scanner = new Scanner(new File(url));

            while (scanner.hasNextLine()) {
                String messageBody = scanner.nextLine();
                Gson gson = new Gson();
                TransactionLogDto dto = gson.fromJson(messageBody, TransactionLogDto.class);
//                log.info("Dto" + dto);
                result.add(dto);
            }

            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return result;
    }

//    public String test1() throws IOException {
//        List<String> list = logRepo.getTransaction();
//
//        Path filePath = Paths.get("Downloads\\local-save.txt");
//        Files.deleteIfExists(filePath);
//        Files.createFile(filePath);
//        for (String str : list) {
//            Files.writeString(filePath, str + System.lineSeparator(),
//                    StandardOpenOption.APPEND);
//        }
//        return filePath.toString();
//    }
}
