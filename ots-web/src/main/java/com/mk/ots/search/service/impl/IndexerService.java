package com.mk.ots.search.service.impl;

import com.mk.ots.common.utils.Constant;
import com.mk.ots.hotel.service.HotelService;
import com.mk.ots.mapper.THotelMapper;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by kirinli on 15/12/8.
 */
@Service
public class IndexerService {
    /**
     * 更新 ES 索引
     */
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    protected SqlSessionFactory sqlSessionFactory;
    @Autowired
    private HotelService hotelService;

    private final AtomicInteger indexerCounter = new AtomicInteger(0);

    public void updateHotelEsIndexer(String hotelid){
        hotelService.readonlyInitPmsHotel(Constant.STR_CITYID_SHANGHAI, hotelid);

        try {
            Thread.currentThread().sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        hotelService.updateEsMikePrice(Long.valueOf(hotelid));

    }

    public String batchUpdateEsIndexer(){
        final int coreNum = 10;
        ExecutorService exService = Executors.newFixedThreadPool(10);
        SqlSession session = sqlSessionFactory.openSession();
        THotelMapper mapper = session.getMapper(THotelMapper.class);

        final List<Long> hotelIdArr = mapper.findAllHotelIds();
        System.out.println("%%%%%%%%%%%%%%%%%%%"+hotelIdArr.size());
        final int hotelCounter = hotelIdArr.size() / coreNum;
        final List<Future<?>> hotelIndexerFutures = new ArrayList<Future<?>>();

        for (int i = 0; i < coreNum; i++) {
            final int index = i;
            final int currentIndex = i * hotelCounter;
            Future<?> hotelIndexerFuture = exService.submit(new Runnable() {
                private int beginIndex = currentIndex;

                @Override
                public void run() {
                    for (int j = beginIndex; j < ((index == coreNum - 1) ? hotelIdArr.size()
                            : beginIndex + hotelCounter); j++) {
                        System.out.println("@@@@@@@@@@@@"+ beginIndex);
                        if (hotelIdArr.get(j) != null) {

                            System.out.println(String.format("%d . init hotel:%d start ====",j,hotelIdArr.get(j)));
                            hotelService.readonlyInitPmsHotel(Constant.STR_CITYID_SHANGHAI, hotelIdArr.get(j).toString());
                            System.out.println(String.format("%d . init hotel:%d end ====", j, hotelIdArr.get(j)));
                            try {
                                Thread.currentThread().sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            System.out.println(String.format("%d . update hotel:%d 's mikeprice start ====",j,hotelIdArr.get(j)));
                            hotelService.updateEsMikePrice(hotelIdArr.get(j));
                            System.out.println(String.format("%d . update hotel:%d 's mikeprice end ====",j,hotelIdArr.get(j)));
                            int hotelIndexerCounterTmp = indexerCounter.incrementAndGet();
                            if (hotelIndexerCounterTmp % 100 == 0 && logger.isInfoEnabled()) {
                                logger.info("{} indexer and mkprice has been updated", hotelIndexerCounterTmp);
                            }
                        }

                        if (logger.isInfoEnabled()) {
                            logger.info("updating indexer and mkprice  with index {}, hotelId {}", j, hotelIdArr.get(j));
                        }
                    }
                }
            });
            hotelIndexerFutures.add(hotelIndexerFuture);
        }

        Thread waitThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (logger.isInfoEnabled()) {
                        logger.info("waiting for indexer init and mkprice  update to complete...");
                    }

                    for (Future<?> mkFuture : hotelIndexerFutures) {
                        mkFuture.get();

                        if (logger.isInfoEnabled()) {
                            logger.info("1 indexer init and mkpriceworker finished...");
                        }
                    }

                    if (logger.isInfoEnabled()) {
                        logger.info("all indexer and mkprice {} have been updated...", hotelIdArr.size());
                    }
                } catch (Exception e) {
                    logger.error("failed to wait for mkFuture to complete...", e);
                }
            }
        });
        waitThread.start();



        return "success";
    }

}
