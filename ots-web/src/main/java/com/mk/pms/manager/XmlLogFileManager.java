package com.mk.pms.manager;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mk.framework.util.XMLUtils;
import com.mk.ots.common.utils.SysConfig;
import com.mk.pms.bean.PmsLog;
import com.mk.pms.myenum.PmsLogTypeEnum;
import com.mk.pms.myenum.PmsResultEnum;

/**
 *
 * @author shellingford
 * @version 2015年2月11日
 */
public class XmlLogFileManager {

	private static final Logger logger = LoggerFactory.getLogger(XmlLogFileManager.class);

	private final static XmlLogFileManager instance = new XmlLogFileManager();

	public void writeXmlLogFile(PmsLog pmslog) {
		if (DebugManager.isSaveXmlFile()) {
			try {
				String dir = SysConfig.getInstance().getProjectPath() + "WEB-INF" + File.separatorChar + "pmsxml" + File.separatorChar;
				dir = dir + pmslog.getHotelId() + "_" + pmslog.getPmsno() + File.separatorChar;
				Calendar ca2 = Calendar.getInstance();
				ca2.setTime(pmslog.getTime());
				dir = dir + ca2.get(Calendar.YEAR) + File.separatorChar + (ca2.get(Calendar.MONTH) + 1) + File.separatorChar
						+ ca2.get(Calendar.DAY_OF_MONTH);
				File fdir = new File(dir);
				fdir.mkdirs();
				SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss_SSS_");
				Random random = new Random();
				String baseName = null;
				if (PmsLogTypeEnum.send.getId().equals(pmslog.getType())) {
					baseName = "S" + format.format(pmslog.getTime()) + random.nextInt(999);
				} else {
					baseName = "R" + format.format(pmslog.getTime()) + random.nextInt(999);
				}
				File file = new File(fdir, baseName + ".xml");
				XMLUtils.XMLtoFile(pmslog.getDoc(), file);
				File file2 = null;
				if (PmsResultEnum.success.toString().equals(pmslog.getReslut())) {
					file2 = new File(fdir, baseName + "_success.properties");
				} else {
					file2 = new File(fdir, baseName + "_error.properties");
				}
				PropertiesUtil prop = new PropertiesUtil();
				if (PmsResultEnum.success.toString().equals(pmslog.getReslut())) {

				} else {
					prop.setValue("errormsg", pmslog.getErrorMsg() == null ? "" : pmslog.getErrorMsg());
				}
				prop.saveFile(file2, "resultlog");
			} catch (Exception e) {
				XmlLogFileManager.logger.error("", e);
			}

		}
	}

	private XmlLogFileManager() {
	}

	public static XmlLogFileManager getInstance() {
		return XmlLogFileManager.instance;
	}
}
