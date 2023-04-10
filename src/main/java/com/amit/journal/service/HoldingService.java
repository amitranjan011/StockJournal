package com.amit.journal.service;

import com.amit.journal.model.Holding;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public interface HoldingService {
    void saveFile(MultipartFile file, LocalDate holdingDate, double cash, double newFundAdded);

    List<Holding> getAllHoldings();

    List<Holding> getHoldingsByDateRange(LocalDate startDate, LocalDate endDate);

    Holding getLatestHolding();
}
