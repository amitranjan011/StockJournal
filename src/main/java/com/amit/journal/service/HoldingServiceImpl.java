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
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class HoldingServiceImpl implements HoldingService {
    private static final Logger LOG = LogManager.getLogger(HoldingServiceImpl.class);

    @Autowired
    private HoldingDAOImpl holdingDAOImpl;

    @Override
    public void saveFile(MultipartFile file, LocalDate holdingDate, double cash, double newFundAdded) {
        CSVHelper.saveFile(file, Constants.HOLDING_DIR);
        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {

            CsvToBean<HoldingItem> csvToBean = new CsvToBeanBuilder(reader)
                    .withType(HoldingItem.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            List<HoldingItem> holdingItems = csvToBean.parse();
            Holding holding = mapHoldingObject(holdingItems, holdingDate, cash, newFundAdded);
            populateDayChange(holding);

            LOG.info("Successfully saved the holding file and populated holding objects for file : {}", file.getOriginalFilename());
            holdingDAOImpl.persist(holding);
            Holding holdingWeek = getHoldingWeekObject(holding);
            holdingDAOImpl.persist(holdingWeek, CollectionsName.HOLDING_WEEK);
            LOG.info("Successfully saved  the holding objects in db for file : {}", file.getOriginalFilename());
            Holding holdingMonth = getHoldingMonthObject(holding);
            holdingDAOImpl.persist(holdingMonth, CollectionsName.HOLDING_MONTH);
        } catch (Exception ex) {
            LOG.error("Exception while saving file for holding upload for file: {} : {}"
                    , file.getOriginalFilename(), ExceptionUtils.getStackTrace(ex));
            throw new RuntimeException(ex);
        }
    }

    @Override
    public List<Holding> getAllHoldings() {
        List<Holding> holdings = holdingDAOImpl.getLImitedHoldings(25);
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

    @Override
    public Holding getLatestWeeklyHolding() {
        Holding holding = holdingDAOImpl.getLatestHolding();
        return holding;
    }

    @Override
    public List<Holding> getAllWeekHoldings() {
        List<Holding> holdings = holdingDAOImpl.getAllWeeklyHoldings();
        return holdings;
    }

    @Override
    public List<Holding> getAlMonthHoldings() {
        List<Holding> holdings = holdingDAOImpl.getAllMonthlyHoldings();
        return holdings;
    }

    private Holding mapHoldingObject(List<HoldingItem> holdingItems, LocalDate holdingDate, double cash, double newFundAdded) {
        LocalDate holdingDateUpdated = LocalDate.now();
        Holding holding = new Holding();
        holding.setCash(cash);
        holding.setNewFundAdded(newFundAdded);
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
        double profitPct = (profit / totalBuyVal) * 100;

        holding.setTotalBuyValue(totalBuyVal);
        holding.setTotalCurrValue(totalCurrVal);
        holding.setProfit(profit);
        holding.setProfitPct(profitPct);
        holding.setTotalPortfolioValue();

        return holding;
    }

    private void populateDayChange(Holding holding) {
        LocalDate previousDay = LocalDate.now().minusDays(1);
        String previousDayId = CommonUtil.generateId(UserContext.getUserId(), CommonUtil.getStartOfDay(previousDay));
        Holding holdingDB = holdingDAOImpl.findByFieldId(Constants.ID, previousDayId, CollectionsName.HOLDING);
        if (CommonUtil.isObjectNullOrEmpty(holdingDB)) holdingDB = getLatestHolding();
        if (!CommonUtil.isObjectNullOrEmpty(holdingDB) && !holdingDB.getId().equals(holding.getId())) {
            double dayChange = holding.getTotalPortfolioValue() - holdingDB.getTotalPortfolioValue();
            double dayChgPct = (dayChange / holdingDB.getTotalPortfolioValue()) * 100;
            holding.setDayChange(CommonUtil.round(dayChange, 2));
            holding.setDayChgPct(CommonUtil.round(dayChgPct, 1));
        }
    }

    private void populateWeekChange(Holding holding) {

        LocalDate minus7Day = LocalDate.now().minusDays(7);
        String previousWeekId = CommonUtil.generateId(UserContext.getUserId(), CommonUtil.getStartOfWeek(minus7Day));
        Holding holdingDB = holdingDAOImpl.findByFieldId(Constants.ID, previousWeekId, CollectionsName.HOLDING_WEEK);
        if (CommonUtil.isObjectNullOrEmpty(holdingDB)) holdingDB = getLatestWeeklyHolding();
        populateChange(holding, holdingDB);
    }

    private void populateChange(Holding holding, Holding holdingDB) {
        holding.setDayChange(CommonUtil.round(0, 2));
        holding.setDayChgPct(CommonUtil.round(0, 1));
        if (isDifferentHolding(holdingDB, holding)) {
            double weekChange = holding.getTotalPortfolioValue() - holdingDB.getTotalPortfolioValue();
            double weekChgPct = (weekChange / holdingDB.getTotalPortfolioValue()) * 100;
            holding.setDayChange(CommonUtil.round(weekChange, 2));
            holding.setDayChgPct(CommonUtil.round(weekChgPct, 1));
            holding.setNewFundAdded(holding.getNewFundAdded() + holdingDB.getNewFundAdded());
        }
    }

    private Holding getHoldingWeekObject(Holding holdingNew) {
        populateWeekChange(holdingNew);
        holdingNew.setId(CommonUtil.generateId(UserContext.getUserId(), CommonUtil.getStartOfWeek(holdingNew.getDate())));
        return holdingNew;
    }
    private Holding getHoldingMonthObject(Holding holdingNew) {
        try {
            populateMonthChange(holdingNew);
            holdingNew.setId(CommonUtil.generateId(UserContext.getUserId(), CommonUtil.getStartOfMonth(holdingNew.getDate())));
            return holdingNew;
        } catch (Exception ex) {
            LOG.error("Exception while updating monthly holding: {}"
                    , ExceptionUtils.getStackTrace(ex));
            throw new RuntimeException(ex);
        }
    }
    private void populateMonthChange(Holding holding) {
        holding.setDayChange(CommonUtil.round(0, 2));
        holding.setDayChgPct(CommonUtil.round(0, 1));
        List<Holding> holdingsDB = holdingDAOImpl.getLImitedHoldings(2);
        Holding previousMonthHolding = null;
        if (holdingsDB != null && holdingsDB.size() > 0) {
            List<Holding> filteredList = holdingsDB.stream().filter(holdingDB -> !holdingDB.getId().equals(holding.getId())).collect(Collectors.toList());
            if (filteredList != null && filteredList.size() > 0) {
//                if (filteredList.size() == 1)
                    previousMonthHolding = filteredList.get(0);
//                else
            }
        }
        populateChange(holding, previousMonthHolding);
    }

    private boolean isDifferentHolding(Holding previous, Holding current) {
        return !CommonUtil.isObjectNullOrEmpty(previous) && !previous.getId().equals(current.getId());
    }
}
