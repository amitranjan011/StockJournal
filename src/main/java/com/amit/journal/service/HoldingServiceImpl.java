package com.amit.journal.service;

import com.amit.journal.constants.CollectionsName;
import com.amit.journal.constants.Constants;
import com.amit.journal.csv.helper.CSVHelper;
import com.amit.journal.domain.repo.HoldingDAOImpl;
import com.amit.journal.interceptor.UserContext;
import com.amit.journal.model.Holding;
import com.amit.journal.model.HoldingItem;
import com.amit.journal.util.CommonUtil;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Service
public class HoldingServiceImpl implements HoldingService {
    private static final Logger LOG = LogManager.getLogger(HoldingServiceImpl.class);

    @Autowired
    private HoldingDAOImpl holdingDAOImpl;

    @Override
    public void saveFile(MultipartFile file, LocalDate holdingDate, double cash) {
        CSVHelper.saveFile(file, Constants.HOLDING_DIR);
        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {

            CsvToBean<HoldingItem> csvToBean = new CsvToBeanBuilder(reader)
                    .withType(HoldingItem.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            List<HoldingItem> holdingItems = csvToBean.parse();
            Holding holding = mapHoldingObject(holdingItems, holdingDate, cash);
            populateDayChange(holding);

            LOG.info("Successfully saved the holding file and populated holding objects for file : {}", file.getOriginalFilename());
            holdingDAOImpl.persist(holding);
            Holding holdingWeek = getHoldingWeekObject(holding);
            holdingDAOImpl.persist(holdingWeek, CollectionsName.HOLDING_WEEK);
            LOG.info("Successfully saved  the holding objects in db for file : {}", file.getOriginalFilename());
        } catch (Exception ex) {
            LOG.error("Exception while saving file for holding upload for file: {} : {}"
                    ,file.getOriginalFilename(), ExceptionUtils.getStackTrace(ex));
            throw new RuntimeException(ex);
        }
    }

    @Override
    public List<Holding> getAllHoldings() {
        List<Holding> holdings = holdingDAOImpl.findAll();
        holdings.sort(Comparator.comparing(Holding::getDate));
        return holdings;
    }

    @Override
    public List<Holding> getHoldingsByDateRange(LocalDate startDate, LocalDate endDate) {
        return holdingDAOImpl.getHoldingsByDate(startDate, endDate);
    }
    @Override
    public Holding getLatestHolding() {
        Holding holding = holdingDAOImpl.getLatestHolding();
        return holding;
    }

    private Holding mapHoldingObject(List<HoldingItem> holdingItems, LocalDate holdingDate, double cash) {
        LocalDate holdingDateUpdated = LocalDate.now();
        Holding holding = new Holding();
        holding.setCash(cash);

        if (!CommonUtil.isObjectNullOrEmpty(holdingDate)) {
            holdingDateUpdated = holdingDate;
        }
        holding.setDate(holdingDateUpdated);

        holding.setEntries(holdingItems);
        holding.setId(CommonUtil.generateId(UserContext.getUserId(), CommonUtil.getStartOfDay(holdingDateUpdated)/*, CommonUtil.getHourMinuteString(today)*/));
        Supplier<Stream<HoldingItem>> holdingStream = holdingItems::stream;

        double totalBuyVal = holdingStream.get().mapToDouble(holdingEntry -> holdingEntry.getQuantity() * holdingEntry.getAvgCost()).sum();
        double totalCurrVal = holdingStream.get().mapToDouble(holdingEntry -> holdingEntry.getQuantity() * holdingEntry.getLastPrice()).sum();
        double profit = totalCurrVal - totalBuyVal;
        double profitPct = (profit/totalBuyVal) * 100;

        holding.setTotalBuyValue(totalBuyVal);
        holding.setTotalCurrValue(totalCurrVal);
        holding.setProfit(profit);
        holding.setProfitPct(profitPct);
        holding.setTotalPortfolioValue();

        return holding;
    }

    private void populateDayChange(Holding holding) {
        Holding holdingDB = getLatestHolding();
        if (!CommonUtil.isObjectNullOrEmpty(holdingDB) && !holdingDB.getId().equals(holding.getId())) {
            double dayChange = holding.getTotalPortfolioValue() - holdingDB.getTotalPortfolioValue();
            double dayChgPct = (dayChange/holdingDB.getTotalPortfolioValue()) * 100;
            holding.setDayChange(dayChange);
            holding.setDayChgPct(dayChgPct);
        }
    }

    private Holding getHoldingWeekObject(Holding holding) {
        holding.setId(CommonUtil.generateId(UserContext.getUserId(), CommonUtil.getStartOfWeek(holding.getDate())));
        return holding;
    }
}
