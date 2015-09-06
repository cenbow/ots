package com.mk.ots.manager;

import java.net.MalformedURLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.winhoo.pms.webout.service.IPmsOutService;
import cn.com.winhoo.pms.webout.service.bean.CancelOrder;
import cn.com.winhoo.pms.webout.service.bean.CustomerResult;
import cn.com.winhoo.pms.webout.service.bean.HotelInfoResult;
import cn.com.winhoo.pms.webout.service.bean.OrderResult;
import cn.com.winhoo.pms.webout.service.bean.PmsAddPay;
import cn.com.winhoo.pms.webout.service.bean.PmsCancelPay;
import cn.com.winhoo.pms.webout.service.bean.PmsCheckIn;
import cn.com.winhoo.pms.webout.service.bean.PmsCheckOut;
import cn.com.winhoo.pms.webout.service.bean.PmsContinuedToLive;
import cn.com.winhoo.pms.webout.service.bean.PmsGiveRoomCard;
import cn.com.winhoo.pms.webout.service.bean.PmsOtaAddOrder;
import cn.com.winhoo.pms.webout.service.bean.PmsOtaChangePrice;
import cn.com.winhoo.pms.webout.service.bean.PmsRoomPrice;
import cn.com.winhoo.pms.webout.service.bean.PmsUpdateOrder;
import cn.com.winhoo.pms.webout.service.bean.ReturnObject;

import com.caucho.hessian.client.HessianProxyFactory;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import com.mk.framework.exception.MyErrorEnum;
import com.mk.framework.exception.PmsException;
import com.mk.framework.util.MySessionUtils;
import com.mk.orm.plugin.hessian.HessianHelper;

/**
 *
 * @author BaiJin
 *
 */

public class HotelPMSManager {
	private static HotelPMSManager instance;

	private static Logger logger = LoggerFactory.getLogger(HotelPMSManager.class);
	private IPmsOutService service = null;
	private IPmsOutService pmsOutServiceProxy = null;

	private void init() throws MalformedURLException {
		HessianProxyFactory factory = new HessianProxyFactory();
		// PMS访问地址，改成读取配置hessian_urls.properties的方式
		String url = HessianHelper.getUrl(HessianHelper.hessian_soap, "/pmsOutService");
		// String url =
		// SysConfig.getInstance().getSysValueByKey(Constant.mikewebPMSUrl);
		this.service = (IPmsOutService) factory.create(IPmsOutService.class, url);
		this.pmsOutServiceProxy = new MkPmsOutServiceProxy(this.service);
	}

	public Boolean returnError(ReturnObject<?> returnObject) {
		if ((returnObject != null) && returnObject.getIsError()) {
			// PmsErrorEnum.findPmsErrorEnumByCode(returnObject.getErrorCode()).getErrorMessage();
			if (returnObject.getErrorMessage() != null) {
				HotelPMSManager.logger.info("PMS错误:" + returnObject.getErrorMessage());
				MySessionUtils.setPmsexception(new PmsException(returnObject.getErrorMessage()));
			} else {
				HotelPMSManager.logger.info("PMS内部错误.");
				MySessionUtils.setPmsexception(MyErrorEnum.Pms.getMyException());
			}
			return true;
		} else {
			return false;
		}
	}

	private HotelPMSManager() {
		try {
			this.init();
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	public static HotelPMSManager getInstance() {
		// 无需严格的单例
		if (HotelPMSManager.instance == null) {
			HotelPMSManager.instance = new HotelPMSManager();
		}
		return HotelPMSManager.instance;
	}

	public IPmsOutService getService() {
		return this.pmsOutServiceProxy;
	}

	class MkPmsOutServiceProxy implements IPmsOutService {

		IPmsOutService pmsOutService = null;

		public MkPmsOutServiceProxy(IPmsOutService servie) {
			this.pmsOutService = servie;
		}

		@Override
		public ReturnObject<Object> submitPrice(Long hotelId, List<PmsRoomPrice> priceList) {

			ReturnObject<Object> result = null;
			Transaction t = Cat.newTransaction("PmsHessianCall", "submitPrice");
			try {
				result = pmsOutService.submitPrice(hotelId, priceList);
				t.setStatus(Transaction.SUCCESS);
			} catch (Exception ex) {
				t.setStatus(ex);
				throw ex;
			} finally {
				t.complete();
			}
			return result;
		}

		@Override
		public ReturnObject<List<PmsOtaAddOrder>> submitAddOrder(Long hotelId, List<PmsOtaAddOrder> list) {

			ReturnObject<List<PmsOtaAddOrder>> result = null;
			Transaction t = Cat.newTransaction("PmsHessianCall", "submitAddOrder");
			try {
				result = pmsOutService.submitAddOrder(hotelId, list);
				t.setStatus(Transaction.SUCCESS);
			} catch (Exception ex) {
				t.setStatus(ex);
				throw ex;
			} finally {
				t.complete();
			}
			return result;
		}

		@Override
		public ReturnObject<Object> changePrice(Long hotelId, PmsOtaChangePrice changePrice) {

			ReturnObject<Object> result = null;
			Transaction t = Cat.newTransaction("PmsHessianCall", "changePrice");
			try {
				result = pmsOutService.changePrice(hotelId, changePrice);
				t.setStatus(Transaction.SUCCESS);
			} catch (Exception ex) {
				t.setStatus(ex);
				throw ex;
			} finally {
				t.complete();
			}
			return result;
		}

		@Override
		public ReturnObject<List<PmsOtaAddOrder>> redownOrder(Long hotelId, PmsCancelPay cancelpay, CancelOrder cancelOrder, List<PmsOtaAddOrder> list, PmsAddPay pay) {

			ReturnObject<List<PmsOtaAddOrder>> result = null;
			Transaction t = Cat.newTransaction("PmsHessianCall", "redownOrder");
			try {
				result = pmsOutService.redownOrder(hotelId, cancelpay, cancelOrder, list, pay);
				t.setStatus(Transaction.SUCCESS);
			} catch (Exception ex) {
				t.setStatus(ex);
				throw ex;
			} finally {
				t.complete();
			}
			return result;
		}

		@Override
		public ReturnObject<List<PmsOtaAddOrder>> updateImportantOrder(Long hotelId, List<PmsOtaAddOrder> list) {

			ReturnObject<List<PmsOtaAddOrder>> result = null;
			Transaction t = Cat.newTransaction("PmsHessianCall", "updateImportantOrder");
			try {
				result = pmsOutService.updateImportantOrder(hotelId, list);
				t.setStatus(Transaction.SUCCESS);
			} catch (Exception ex) {
				t.setStatus(ex);
				throw ex;
			} finally {
				t.complete();
			}
			return result;
		}

		@Override
		public ReturnObject<Object> cancelOrder(Long hotelId, CancelOrder cancelOrder) {

			ReturnObject<Object> result = null;
			Transaction t = Cat.newTransaction("PmsHessianCall", "cancelOrder");
			try {
				result = pmsOutService.cancelOrder(hotelId, cancelOrder);
				t.setStatus(Transaction.SUCCESS);
			} catch (Exception ex) {
				t.setStatus(ex);
				throw ex;
			} finally {
				t.complete();
			}
			return result;
		}

		@Override
		public ReturnObject<Object> updateOrder(Long hotelId, PmsUpdateOrder order) {

			ReturnObject<Object> result = null;
			Transaction t = Cat.newTransaction("PmsHessianCall", "updateOrder");
			try {
				result = pmsOutService.updateOrder(hotelId, order);
				t.setStatus(Transaction.SUCCESS);
			} catch (Exception ex) {
				t.setStatus(ex);
				throw ex;
			} finally {
				t.complete();
			}
			return result;
		}

		@Override
		public ReturnObject<Object> checkIn(Long hotelId, PmsCheckIn checkIn) {

			ReturnObject<Object> result = null;
			Transaction t = Cat.newTransaction("PmsHessianCall", "checkIn");
			try {
				result = pmsOutService.checkIn(hotelId, checkIn);
				t.setStatus(Transaction.SUCCESS);
			} catch (Exception ex) {
				t.setStatus(ex);
				throw ex;
			} finally {
				t.complete();
			}
			return result;
		}

		@Override
		public ReturnObject<Object> continuedToLive(Long hotelId, PmsContinuedToLive toLive) {

			ReturnObject<Object> result = null;
			Transaction t = Cat.newTransaction("PmsHessianCall", "continuedToLive");
			try {
				result = pmsOutService.continuedToLive(hotelId, toLive);
				t.setStatus(Transaction.SUCCESS);
			} catch (Exception ex) {
				t.setStatus(ex);
				throw ex;
			} finally {
				t.complete();
			}
			return result;
		}

		@Override
		public ReturnObject<Object> checkOut(Long hotelId, PmsCheckOut checkOut) {

			ReturnObject<Object> result = null;
			Transaction t = Cat.newTransaction("PmsHessianCall", "checkOut");
			try {
				result = pmsOutService.checkOut(hotelId, checkOut);
				t.setStatus(Transaction.SUCCESS);
			} catch (Exception ex) {
				t.setStatus(ex);
				throw ex;
			} finally {
				t.complete();
			}
			return result;
		}

		@Override
		public ReturnObject<Object> pmsGiveRoomCard(Long hotelId, PmsGiveRoomCard pmsGiveRoomCard) {

			ReturnObject<Object> result = null;
			Transaction t = Cat.newTransaction("PmsHessianCall", "pmsGiveRoomCard");
			try {
				result = pmsOutService.pmsGiveRoomCard(hotelId, pmsGiveRoomCard);
				t.setStatus(Transaction.SUCCESS);
			} catch (Exception ex) {
				t.setStatus(ex);
				throw ex;
			} finally {
				t.complete();
			}
			return result;
		}

		@Override
		public ReturnObject<Object> recoveryRoomCard(Long hotelId, PmsGiveRoomCard pmsGiveRoomCard) {

			ReturnObject<Object> result = null;
			Transaction t = Cat.newTransaction("PmsHessianCall", "recoveryRoomCard");
			try {
				result = pmsOutService.recoveryRoomCard(hotelId, pmsGiveRoomCard);
				t.setStatus(Transaction.SUCCESS);
			} catch (Exception ex) {
				t.setStatus(ex);
				throw ex;
			} finally {
				t.complete();
			}
			return result;
		}

		@Override
		public ReturnObject<PmsAddPay> payToPms(Long hotelId, PmsAddPay pay) {

			ReturnObject<PmsAddPay> result = null;
			Transaction t = Cat.newTransaction("PmsHessianCall", "payToPms");
			try {
				result = pmsOutService.payToPms(hotelId, pay);
				t.setStatus(Transaction.SUCCESS);
			} catch (Exception ex) {
				t.setStatus(ex);
				throw ex;
			} finally {
				t.complete();
			}
			return result;
		}

		@Override
		public ReturnObject<PmsCancelPay> cancelPayToPms(Long hotelId, PmsCancelPay pay) {

			ReturnObject<PmsCancelPay> result = null;
			Transaction t = Cat.newTransaction("PmsHessianCall", "cancelPayToPms");
			try {
				result = pmsOutService.cancelPayToPms(hotelId, pay);
				t.setStatus(Transaction.SUCCESS);
			} catch (Exception ex) {
				t.setStatus(ex);
				throw ex;
			} finally {
				t.complete();
			}
			return result;
		}

		@Override
		public ReturnObject<List<OrderResult>> selectOrder(Long hotelId, List<String> orderid) {

			ReturnObject<List<OrderResult>> result = null;
			Transaction t = Cat.newTransaction("PmsHessianCall", "selectOrder");
			try {
				result = pmsOutService.selectOrder(hotelId, orderid);
				t.setStatus(Transaction.SUCCESS);
			} catch (Exception ex) {
				t.setStatus(ex);
				throw ex;
			} finally {
				t.complete();
			}
			return result;
		}

		@Override
		public ReturnObject<List<CustomerResult>> selectCustomerno(Long hotelId, List<String> customerno) {

			ReturnObject<List<CustomerResult>> result = null;
			Transaction t = Cat.newTransaction("PmsHessianCall", "selectCustomerno");
			try {
				result = pmsOutService.selectCustomerno(hotelId, customerno);
				t.setStatus(Transaction.SUCCESS);
			} catch (Exception ex) {
				t.setStatus(ex);
				throw ex;
			} finally {
				t.complete();
			}
			return result;
		}

		@Override
		public ReturnObject<HotelInfoResult> selectHotel(Long hotelId) {

			ReturnObject<HotelInfoResult> result = null;
			Transaction t = Cat.newTransaction("PmsHessianCall", "selectHotel");
			try {
				result = pmsOutService.selectHotel(hotelId);
				t.setStatus(Transaction.SUCCESS);
			} catch (Exception ex) {
				t.setStatus(ex);
				throw ex;
			} finally {
				t.complete();
			}
			return result;
		}

		@Override
		public ReturnObject<Boolean> checkOnline(Long hotelId) {

			ReturnObject<Boolean> result = null;
			Transaction t = Cat.newTransaction("PmsHessianCall", "checkOnline");
			try {
				result = pmsOutService.checkOnline(hotelId);
				t.setStatus(Transaction.SUCCESS);
			} catch (Exception ex) {
				t.setStatus(ex);
				throw ex;
			} finally {
				t.complete();
			}
			return result;
		}

	}
}
