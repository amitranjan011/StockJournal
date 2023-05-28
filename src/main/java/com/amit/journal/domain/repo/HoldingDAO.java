package com.amit.journal.domain.repo;

import com.amit.journal.model.Holding;
import com.amit.journal.model.TransactionSummary;

import java.time.LocalDate;
import java.util.List;

public interface HoldingDAO {
    List<Holding> getHoldingsByDate(LocalDate startDate, LocalDate endDate);

    Holding getLatestHolding();

    Holding getLatestWeeklyHolding();

    List<Holding> getAllHoldings();

    List<Holding> getAllWeeklyHoldings();

    List<Holding> getAllMonthlyHoldings();

    List<Holding> getLImitedHoldings(int number);
}
