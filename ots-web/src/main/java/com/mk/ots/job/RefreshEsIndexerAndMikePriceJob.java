package com.mk.ots.job;


import com.mk.framework.AppUtils;
import com.mk.ots.search.service.impl.IndexerService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * 每天00:05执行一次，全量更新索引和眯客价
 * @author kirin
 *
 */
public class RefreshEsIndexerAndMikePriceJob extends QuartzJobBean {
	private static Logger logger = LoggerFactory.getLogger(RefreshEsIndexerAndMikePriceJob.class);
	private IndexerService indexerService = AppUtils.getBean(IndexerService.class);
	
	@Override
	protected void executeInternal(JobExecutionContext arg0) throws JobExecutionException {
		logger.info("RefreshEsIndexerAndMikePriceJob::start");
		try {
			indexerService.batchUpdateEsIndexer();

		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("RefreshEsIndexerAndMikePriceJob::end");
	}
}
