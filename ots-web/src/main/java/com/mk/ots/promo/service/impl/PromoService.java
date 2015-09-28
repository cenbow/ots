package com.mk.ots.promo.service.impl;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.mk.framework.exception.MyErrorEnum;
import com.mk.framework.schedule.ScheduleService;
import com.mk.framework.util.RandomSelectionUtils;
import com.mk.framework.util.StringUtils;
import com.mk.framework.util.XMLUtils;
import com.mk.ots.activity.dao.IBActiveCDKeyDao;
import com.mk.ots.activity.dao.IBActiveChannelDao;
import com.mk.ots.activity.dao.IBActivityDao;
import com.mk.ots.activity.dao.IUActiveCDKeyLogDao;
import com.mk.ots.activity.model.BActiveCDKey;
import com.mk.ots.activity.model.BActiveChannel;
import com.mk.ots.activity.model.BActivity;
import com.mk.ots.activity.model.PromotionGenTypeEnum;
import com.mk.ots.common.enums.*;
import com.mk.ots.common.utils.DateUtils;
import com.mk.ots.member.dao.IUPromotionLogDao;
import com.mk.ots.member.model.UMember;
import com.mk.ots.member.model.UPromotionLog;
import com.mk.ots.member.service.IMemberService;
import com.mk.ots.message.model.MessageType;
import com.mk.ots.message.service.IMailService;
import com.mk.ots.message.service.IMessageService;
import com.mk.ots.promo.dao.IBPromotionDao;
import com.mk.ots.promo.dao.IBPromotionPriceDao;
import com.mk.ots.promo.model.BPromotion;
import com.mk.ots.promo.model.BPromotionPrice;
import com.mk.ots.promo.model.CommonXmlPromo;
import com.mk.ots.promo.service.IPromoService;
import com.mk.ots.ticket.dao.BPrizeDao;
import com.mk.ots.ticket.dao.BPrizeStockDao;
import com.mk.ots.ticket.dao.IUPrizeRecordDao;
import com.mk.ots.ticket.dao.UTicketDao;
import com.mk.ots.ticket.model.*;
import com.mk.ots.ticket.service.ITicketService;
import com.mk.ots.ticket.service.parse.SimplesubTicket;
import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.elasticsearch.common.base.Strings;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @author Administrator
 */
@Service
public class PromoService implements IPromoService {
    final Logger logger = LoggerFactory.getLogger(PromoService.class);
    private final String TPL_SMS_DEF = "";
    private final String TPL_PUSH_DEF = "";
    @Autowired
    private IBPromotionDao iBPromotionDao;
    @Autowired
    private BPrizeDao iBPrizeDao;
    @Autowired
    private UTicketDao uTicketDao;
    @Autowired
    private IBPromotionPriceDao iBPromotionPriceDao;
    @Autowired
    private IMemberService iMemberService;
    @Autowired
    private IMessageService iMessageService;
    @Autowired
    private ScheduleService scheduleService;
    @Autowired
    private IUPromotionLogDao iUPromotionLogDao;
    @Autowired
    private IBActiveChannelDao iBActiveChannelDao;
    @Autowired
    private IBActiveCDKeyDao iBActiveCDKeyDao;
    @Autowired
    private ServletContext servletContext;
    @Autowired
    private IMailService iMailService;
    @Autowired
    private IBActivityDao ibActivityDao;
    @Autowired
    private ITicketService iTicketService;
    @Autowired
    private BPrizeStockDao bPrizeStockDao;
    @Autowired
    private IUPrizeRecordDao iUPrizeRecordDao;
    @Autowired
    private IUActiveCDKeyLogDao iUActiveCDKeyLogDao;

    private static String genRandomCode(int length) {
        String base = "abcdefghijklmnopqrstuvwxyz123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    @Override
    public List<BPromotion> findByActiveId(Long activeid) {
        return this.iBPromotionDao.findByActiveId(activeid);
    }

    @Override
    public void genTicketByAllRegNewMember(Long mid) {
        //判断新注册用户是否为切客注册
        Optional<UMember> ofMember = iMemberService.findMemberById(mid);
        if (ofMember.isPresent()) {

            UMember um = ofMember.get();
            /*业务码表
            QK	切客场景
			SC	市场
			HD	活动
			QD  渠道*/
            if (!Strings.isNullOrEmpty(um.getComefromtype())
                    && ComefromtypeEnum.qudao.getType().equals(um.getComefromtype())
                    && !Strings.isNullOrEmpty(um.getComefrom())) {
                logger.info("直客注册则直接发放券并通知");

                // 若是从重庆渠道注册的，需要发送一张50元优惠券
                // 1.限制app使用
                // 2.有效期3个月
                if (um.getComefrom().indexOf("CQ_") == 0
                        || um.getComefrom().indexOf("cq_") == 0) {
                    // 用户通过重庆渠道进行注册
                    genTicketByActive(25L, mid);
                } else if ("200001".equals(um.getComefrom())) { //华夏鑫彩渠道
                    //直客注册则直接发放券并通知
                    genTicketByActive(1L, mid);
                }
                logger.info("渠道注册用户发放新手券");
            } else {
                logger.info("非渠道注册用户不再发放新手券");
            }

            /**
             * modify by tankai  切客用户和非渠道用户不再方法优惠券，故此注释掉该段代码
             * if(!Strings.isNullOrEmpty(um.getComefromtype()) && ComefromtypeEnum.qieke.getType().equals(um.getComefromtype()) && !Strings.isNullOrEmpty(um.getComefrom())){
             logger.info("切客注册用户则延后二小时后发放新用户礼包并通知");
             //切客注册用户则延后三十分钟后发放新用户礼包并通知
             LocalDateTime datetime = LocalDateTime.fromDateFields(um.getCreatetime()).plusHours(2);
             logger.info("预计发放时间:{}", datetime.toDate());
             scheduleService.addScheduleOnce(UUID.randomUUID().toString(), "genTicketByAllRegNewMember", null, null, datetime.toDate(), new GenNewMemberTicketScheduleAfter30Min(mid));
             }else{
             logger.info("直客注册则直接发放券并通知");
             //直客注册则直接发放券并通知
             genTicketByActive(1l, mid);
             genTicketByNewMember(mid);
             logger.info("渠道注册用户发放新手券");

             }
             */


        }
    }

    @Override
    public void genCouponCode(BActiveChannel bActiveChannel, String batchno, Long gennum, String email) {
        logger.info("genCouponCode()... batchno:{}, gennum:{}, email:{}, bActiveChannel:{}", batchno, gennum, email, bActiveChannel.toString());

        //1. 根据活动编码和渠道编码确定一条优惠券定义
        Long promotionid = bActiveChannel.getPromotionid();
        BPromotion bPromotion = this.iBPromotionDao.findById(promotionid);
        if (bPromotion == null) {
            logger.error("genCouponCode>>> 优惠券定义有误. promotionid:{} ", promotionid);
            throw MyErrorEnum.customError.getMyException("优惠券定义有误.");
        }
        int num = bPromotion.getNum(); //券定义总数量
        logger.info("genCouponCode()>>> 总共可生成码数量:{}", num);
        if (num != -1) {
            Long alreadyGenNum = this.iBActiveCDKeyDao.getNotUseCDKeyNum(bActiveChannel.getActiveid(), bActiveChannel.getChannelid(), bActiveChannel.getPromotionid());       //已生成码数量
            logger.info("genCouponCode()>>> activeid:{}, channelid:{} 已生成优惠券:{}", bActiveChannel.getActiveid(), bActiveChannel.getChannelid(), alreadyGenNum);
            if ((gennum + alreadyGenNum) > num) {
                logger.error("已超出券定义数量.");
                throw MyErrorEnum.customError.getMyException("已超出券定义数量.");
            }
        }


        //2. 根据优惠券定义批量生成优惠码
        List<String> codeList = Lists.newArrayList();
        int codeLength = 8;
        for (int i = 0, n = gennum.intValue(); i < n; i++) {
            codeList.add(genRandomCode(codeLength));
        }
        logger.info("genCouponCode()>>> 生在优惠码，总计{}个.", gennum);
        this.iBActiveCDKeyDao.batchGenCode(batchno, bActiveChannel.getActiveid(), bActiveChannel.getChannelid(), bActiveChannel.getPromotionid(), bActiveChannel.getExpiration(), codeList);

        //3. 获取此批次的所有码生成xls文件
        List<BActiveCDKey> itemKeys = this.iBActiveCDKeyDao.getCDKeys(batchno, bActiveChannel.getActiveid(), bActiveChannel.getChannelid());
        if (itemKeys != null && itemKeys.size() > 0) {
            HSSFWorkbook hssfworkbook = createHSSFWorkbook(bActiveChannel, itemKeys);
            File xlsFile = new File(servletContext.getRealPath("/promotion/" + batchno + ".xls"));
            try {
                hssfworkbook.write(new FileOutputStream(xlsFile));
            } catch (IOException e) {
                logger.error("生成优惠码文件发生错误.");
                e.printStackTrace();
            }
            //4. 将优惠券xls文件发到指定邮箱
            String subject = "批次号：" + batchno + "优惠码发放";
            String content = "批次号：" + batchno + " 渠道：" + bActiveChannel.getChannelid() + " 共生成优惠码：" + itemKeys.size() + "个.";
            String[] attachment = new String[]{xlsFile.getAbsolutePath()};
            logger.info("subject:{}, content:{}...", subject, content);
            logger.info("发送生成文件({})到指定邮箱({}). >>>Begin", xlsFile.getAbsolutePath(), email);
            boolean issend = this.iMailService.sendAttachment(subject, content, attachment, new String[]{email});
            logger.info("发送生成文件({})到指定邮箱({}). status:{} >>>End.", xlsFile.getAbsolutePath(), email, issend);
        } else {
            logger.error("未生成优惠券.");
            throw MyErrorEnum.customError.getMyException("未生成优惠券.");
        }
    }

    /**
     * 生成优惠码文档
     *
     * @param bActiveChannel
     * @param itemKeys
     * @return
     */
    private HSSFWorkbook createHSSFWorkbook(BActiveChannel bActiveChannel, List<BActiveCDKey> itemKeys) {
        BPromotion bp = this.iBPromotionDao.findById(bActiveChannel.getPromotionid());
        List<CommonXmlPromo> cxpList = null;
        if (!Strings.isNullOrEmpty(bp.getInfo())) {
            try {
                cxpList = parse(bp.getInfo());
            } catch (Exception e) {
                logger.error("解析优惠券实例出错.promotionid:" + bActiveChannel.getPromotionid(), e);
                throw MyErrorEnum.errorParm.getMyException("解析优惠券实例出错.券实例:" + bActiveChannel.getPromotionid());
            }
            if (cxpList == null || cxpList.size() == 0) {
                logger.error("券实例定义中无券明细.券实例:{}", bActiveChannel.getPromotionid());
                throw MyErrorEnum.customError.getMyException("券实例定义中无券明细.券实例:" + bActiveChannel.getPromotionid());
            }
        } else {
            logger.error("券实例定义内容为空.券实例:{}", bActiveChannel.getPromotionid());
            throw MyErrorEnum.customError.getMyException("券实例定义内容为空.券实例:" + bActiveChannel.getPromotionid());
        }
        int promotionnum = cxpList.size();
        double promotionprice = cxpList.get(0).getOnlineprice().doubleValue();
        double totalprice = promotionprice * promotionnum;

        HSSFWorkbook workBook = new HSSFWorkbook();
        HSSFSheet sheet = workBook.createSheet();

        //1. 行标题
        HSSFRow titleRow = sheet.createRow(0);
        String[] titles = new String[]{"渠道", "券", "券张数", "券金额", "总计金额", "使用期", "使用限制"};
        HSSFCellStyle createHeaderStyle = createHeaderStyle(workBook);
        for (int i = 0, n = titles.length; i < n; i++) {
            HSSFCell titleCell = titleRow.createCell(i);
            titleCell.setCellStyle(createHeaderStyle);
            titleCell.setCellType(HSSFCell.CELL_TYPE_STRING);
            titleCell.setCellValue(titles[i]);
        }

        //2. 组织内容
        HSSFCellStyle createContextStyle = createContextStyle(workBook, HSSFCellStyle.ALIGN_LEFT);
        HSSFCellStyle createContextStyle2 = createContextStyle(workBook, HSSFCellStyle.ALIGN_RIGHT);
        for (int i = 0, n = itemKeys.size(); i < n; i++) {
            BActiveCDKey tmpKey = itemKeys.get(i);
            HSSFRow recordRow = sheet.createRow(i + 1);

            HSSFCell recordCell = recordRow.createCell(0);
            recordCell.setCellType(HSSFCell.CELL_TYPE_STRING);
            recordCell.setCellValue(bActiveChannel.getChannelname());
            recordCell.setCellStyle(createContextStyle);

            recordCell = recordRow.createCell(1);
            recordCell.setCellType(HSSFCell.CELL_TYPE_STRING);
            recordCell.setCellValue(tmpKey.getCode());
            recordCell.setCellStyle(createContextStyle);

            recordCell = recordRow.createCell(2);
            recordCell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
            recordCell.setCellValue(promotionnum);
            recordCell.setCellStyle(createContextStyle2);

            recordCell = recordRow.createCell(3);
            recordCell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
            recordCell.setCellValue(promotionprice);
            recordCell.setCellStyle(createContextStyle2);


            recordCell = recordRow.createCell(4);
            recordCell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
            recordCell.setCellValue(totalprice);
            recordCell.setCellStyle(createContextStyle2);

            recordCell = recordRow.createCell(5);
            recordCell.setCellType(HSSFCell.CELL_TYPE_STRING);
            recordCell.setCellValue(DateUtils.getStringFromDate(tmpKey.getExpiration(), "yyyy年MM月dd日 HH:mm"));
            recordCell.setCellStyle(createContextStyle);

            recordCell = recordRow.createCell(6);
            recordCell.setCellType(HSSFCell.CELL_TYPE_STRING);
            recordCell.setCellValue("仅限在线支付");
            recordCell.setCellStyle(createContextStyle);
        }
        return workBook;
    }

    private HSSFCellStyle createContextStyle(HSSFWorkbook wb, short align) {
        HSSFCellStyle cellstyle = wb.createCellStyle();
        cellstyle.setAlignment(align);
        return cellstyle;
    }

    private HSSFCellStyle createHeaderStyle(HSSFWorkbook wb) {
        HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 指定单元格居中对齐
        cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 指定单元格垂直居中对齐
        cellStyle.setWrapText(true);// 指定单元格自动换行
        cellStyle.setBorderBottom((short) 1);
        cellStyle.setBorderLeft((short) 1);
        cellStyle.setBorderRight((short) 1);
        cellStyle.setBorderTop((short) 1);
        // 单元格字体
        HSSFFont font = wb.createFont();
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        font.setFontName("宋体");
        font.setFontHeight((short) 250);
        cellStyle.setFont(font);

        // 设置单元格背景色
        cellStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
        cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        return cellStyle;
    }

    /**
     *
     */
    @Override
    public void genTicketByMemberOrderNumEveryDay() {
        Optional<BPromotion> of = findCommonPromotionDefine();
        if (of.isPresent()) {
            BPromotion bPromotion = of.get();
            List<Map> mList = uTicketDao.queryCurMonthUTicketNumAndOrderNum();
            for (Map<String, Object> tmp : mList) {
                long mid = Long.parseLong(String.valueOf(tmp.get("mid")));
                int ordernum = Integer.parseInt(String.valueOf(tmp.get("ordernum")));
                int ticketnum = Integer.parseInt(String.valueOf(tmp.get("ticketnum")));
                if (ordernum > 0 && ticketnum == 0) {
                    List<Long> genList = genCGTicket(bPromotion.getId(), mid, null, null, PromotionMethodTypeEnum.AUTO, null, null, "");
                    boolean isSuccess = genList != null && genList.size() > 0;

                    logger.info("genTicketByMemberOrderNumEveryDay >>>生成用户优惠券：mid:{}, status:{}, 券实例:{}", mid, isSuccess, bPromotion.toString());
                    if (isSuccess) {
                        String phone = getMemberPhoneByMid(mid);
//						iMessageService.sendMsg(phone, StringUtils.getMessage(TPL_SMS_DEF,""), MessageTypeEnum.normal);
                        iMessageService.pushMsg(phone, "", StringUtils.getMessage(TPL_PUSH_DEF, ""), "");
                    }
                }
            }
        }
    }

    @Override
    public void genTicketByMemberOrderNumAt25() {
        Optional<BPromotion> of = findCommonPromotionDefine();
        if (of.isPresent()) {
            BPromotion bPromotion = of.get();
            List<Map> mList = uTicketDao.queryCurMonthUTicketNumAndOrderNum();
            for (Map<String, String> tmp : mList) {
                long mid = Long.parseLong(String.valueOf(tmp.get("mid")));
                int ordernum = Integer.parseInt(String.valueOf(tmp.get("ordernum")));
                int ticketnum = Integer.parseInt(String.valueOf(tmp.get("ticketnum")));
                if (ordernum >= 2 && ticketnum == 1) {
                    List<Long> genList = genCGTicket(bPromotion.getId(), mid, null, null, PromotionMethodTypeEnum.AUTO, null, null, "");
                    boolean isSuccess = genList != null && genList.size() > 0;

                    logger.info("genTicketByMemberOrderNumAt25 >>>生成用户优惠券：mid:{}, status:{}, 券实例:{}", mid, isSuccess, bPromotion.toString());
                    if (isSuccess) {
                        String phone = getMemberPhoneByMid(mid);
//						iMessageService.sendMsg(phone, StringUtils.getMessage(TPL_SMS_DEF,""), MessageTypeEnum.normal);
                        iMessageService.pushMsg(phone, "", StringUtils.getMessage(TPL_PUSH_DEF, ""), "");
                    }
                }
            }
        }
    }

    @Override
    public void genTicketByActiveMember() {
        Optional<BPromotion> of = findCommonPromotionDefine();
        if (of.isPresent()) {
            BPromotion bPromotion = of.get();
            List<Long> mList = uTicketDao.queryActiveMemberRuleList();
            for (Long mid : mList) {
                List<Long> genList = genCGTicket(bPromotion.getId(), mid, null, null, PromotionMethodTypeEnum.AUTO, null, null, "");
                boolean isSuccess = genList != null && genList.size() > 0;

                logger.info("genTicketByActiveMember >>>生成用户优惠券：mid:{}, status:{}, 券实例:{}", mid, isSuccess, bPromotion.toString());
                if (isSuccess) {
                    String phone = getMemberPhoneByMid(mid);
//					iMessageService.sendMsg(phone, StringUtils.getMessage(TPL_SMS_DEF,""), MessageTypeEnum.normal);
                    iMessageService.pushMsg(phone, "", StringUtils.getMessage(TPL_PUSH_DEF, ""), "");
                }
            }
        }
    }

    @Override
    public void genTicketByUnActiveMember() {
        Optional<BPromotion> of = findCommonPromotionDefine();
        if (of.isPresent()) {
            BPromotion bPromotion = of.get();
            List<Long> mList = uTicketDao.queryUnActiveMemberRuleList();
            for (Long mid : mList) {
                List<Long> genList = genCGTicket(bPromotion.getId(), mid, null, null, PromotionMethodTypeEnum.AUTO, null, null, "");
                boolean isSuccess = genList != null && genList.size() > 0;
                logger.info("genTicketByUnActiveMember >>>生成用户优惠券：mid:{}, status:{}, 券实例:{}", mid, isSuccess, bPromotion.toString());
                if (isSuccess) {
                    String phone = getMemberPhoneByMid(mid);
                    iMessageService.pushMsg(phone, "", StringUtils.getMessage(TPL_PUSH_DEF, ""), "");
                }
            }
        }
    }

    @Override
    public List<Long> genTicketByActive(Long activeid, Long mid) {
        return genTicketByActive(activeid, mid, "");
    }


    @Override
    public List<Long> genTicketByActive(Long activeid, Long mid, String hardwarecode) {
        logger.info(">>>根据活动发放优惠券----------------------//开始");
        List<Long> rtnList = Lists.newArrayList();

        BActivity bActivity = ibActivityDao.findById(activeid);
        List<BPromotion> proList = this.iBPromotionDao.findByActiveId(activeid);
        logger.info(">>>根据活动发放优惠券----------------------//1. 查询活动对应的优惠券定义 {}", proList);

        if (PromotionGenTypeEnum.ALL.equals(bActivity.getGentype())) {
            if (proList != null && proList.size() <= 4) {
                logger.info(">>>根据活动发放优惠券----------------------//2. 将活动对应的优惠券定义进行实例并绑定");
                for (BPromotion tmp : proList) {
                    rtnList.addAll(genCGTicket(tmp.getId(), mid, bActivity.getBegintime(), bActivity.getEndtime(), bActivity.getPromotionmethodtype(), null, null, hardwarecode));
                }
            } else {
                logger.error(">>>根据活动发放优惠券----------------------//警告A：活动[{}]定义券超出限制[4]. ", activeid);
                logger.error(">>>根据活动发放优惠券----------------------//警告B：活动[{}]定义券超出限制[4]. ", activeid);
                logger.error(">>>根据活动发放优惠券----------------------//警告C：活动[{}]定义券超出限制[4]. ", activeid);
            }
        } else if (PromotionGenTypeEnum.RANDOM.equals(bActivity.getGentype())) {
            logger.info(">>>根据活动发放优惠券----------------------//2. 随机生成优惠券");
            List<RandomWeightItem<BPromotion>> rwiList = Lists.newArrayList();
            for (BPromotion tmp : proList) {
                if (tmp.getNum() == -1 || tmp.getNum() > 0) {
                    rwiList.add(new RandomWeightItem<BPromotion>(tmp, tmp.getWeight()));
                }
            }
            if (rwiList == null || rwiList.size() == 0) {
                throw MyErrorEnum.customError.getMyException("优惠券已发放完毕.");
            }
            List<RandomWeightItem<BPromotion>> selection = RandomSelectionUtils.randomSelect(rwiList, 1);
            if (selection != null && selection.size() > 0) {
                BPromotion bp = selection.get(0).getEntity();
                rtnList.addAll(genCGTicket(bp.getId(), mid, bActivity.getBegintime(), bActivity.getEndtime(), bActivity.getPromotionmethodtype(), null, null, hardwarecode));
            }
        } else {
            throw MyErrorEnum.customError.getMyException("此活动不允许领取优惠券.");
        }
        boolean issuccess = rtnList != null && rtnList.size() > 0;
        logger.info("生成用户优惠券：mid:{}, status:{}, 券实例:{}", mid, issuccess, rtnList);
        if (issuccess) {
            String phone = getMemberPhoneByMid(mid);

            //TODO 待加入活动通知模板
            if (1l == activeid) {
                //新用户礼包
                iMessageService.pushMsg(phone, "新手礼包", "40元新手优惠券礼包已经放入您的账户，点击查看。", MessageType.USER.getId());
            } else if (2l == activeid) {
                //老用户回馈活动详情

            } else if (3l == activeid) {
                //611活动

            } else if (5l == activeid) {
                //10+1活动

            } else if (6l == activeid) {
                //15元体验券

            }
        }
        logger.info(">>>根据活动发放优惠券----------------------//结束");
        return rtnList;
    }

    /*
     * 奖品随机抽奖券生成
     */
    @Override
    public List<BPrizeInfo> tryLuckByActive(Long activeid, Long mid, String ostype) {
        logger.info(">>>根据活动发放优惠券----------------------//开始");
        List<BPrizeInfo> rtnList = Lists.newArrayList();
        //此处判断该用户当天是否有抽奖机会
        boolean isPrizeChance = iTicketService.checkUserLuckyDraw(mid, activeid, ostype);
        if (isPrizeChance) {
            logger.info("用户 " + mid + "开始抽奖！");
            BActivity bActivity = ibActivityDao.findById(activeid);
            //判断该用户是否领取过实物
            boolean isMaterialObject = false;
            Long countLong = iUPrizeRecordDao.findMaterialCountByMidAndAct(mid, activeid, PrizeTypeEnum.material.getId());
            if (countLong >= 1) {
                isMaterialObject = true;
            }
            List<BPrize> proList = this.iBPrizeDao.findBPrizeByActiveid(activeid, isMaterialObject);
            //查询第三方库存，确保prize表定义券数目与库存一致
            if (CollectionUtils.isNotEmpty(proList)) {
                for (BPrize bPrize : proList) {
                    if (PrizeTypeEnum.thirdparty.getId().equals(bPrize.getType())) {
                        Long actNum = bPrizeStockDao.findStockCountByPrizeIDAndStatus(bPrize.getId(), 0l);
                        if (bPrize.getNum() != actNum) {
                            bPrize.setNum(actNum);
                            this.iBPrizeDao.saveOrUpdate(bPrize);
                            logger.info("奖品id为" + bPrize.getId() + "定义奖品数目与库存表不一致，现更新为库存值：" + actNum);
                        }
                    }
                }
            }
            logger.info(">>>根据活动发放优惠券----------------------//1. 查询活动对应的优惠券定义 {}", proList);

            if (PromotionGenTypeEnum.PRIZERANDOM.equals(bActivity.getGentype())) {
                logger.info(">>>根据活动发放奖品----------------------//2. 随机抽取奖品");
                List<RandomWeightItem<BPrize>> rwiList = Lists.newArrayList();
                for (BPrize tmp : proList) {
                    if (tmp.getNum() == -1 || tmp.getNum() > 0) {
                        rwiList.add(new RandomWeightItem<BPrize>(tmp, Integer.parseInt(tmp.getWeight().toString())));
                    }
                }
                if (rwiList == null || rwiList.size() == 0) {
                    throw MyErrorEnum.customError.getMyException("优惠券已发放完毕.");
                }
                List<RandomWeightItem<BPrize>> selection = RandomSelectionUtils.randomSelect(rwiList, 1);
                if (selection != null && selection.size() > 0) {
                    //根据随机结果生成奖品券
                    BPrize bp = selection.get(0).getEntity();
                    if (PrizeTypeEnum.mike.getId().equals(bp.getType())) {
                        logger.info("开始生成眯客券!");
                        List<BPromotion> list = this.iBPromotionDao.findByActiveId(activeid);
                        if (CollectionUtils.isNotEmpty(list)) {
                            for (BPromotion bPromotion : list) {//当眯客券有多张时，获取随机抽取的那张券的promotionid
                                if (bPromotion.getName().equals(bp.getName())) {
                                    List<Long> temp = genCGTicket(bPromotion.getId(), mid, bActivity.getBegintime(), bActivity.getEndtime(), bActivity.getPromotionmethodtype(), null, null, "");
                                    if (CollectionUtils.isNotEmpty(temp)) {
                                        for (int i = 0; i < temp.size(); i++) {
                                            BPrizeInfo bpi = new BPrizeInfo();
                                            bpi.setId(temp.get(i));
                                            bpi.setType(bp.getType());
                                            rtnList.add(bpi);
                                        }
                                    }
                                }
                            }

                        } else {
                            logger.info("没有可用眯客优惠券！");
                        }

                    } else if (PrizeTypeEnum.thirdparty.getId().equals(bp.getType())) {
                        logger.info("开始生成第三方券!");
                        Date nowDate = new Date();
                        BPrizeStock bps = bPrizeStockDao.findBPrizeStockByPrizeid(bp.getId(), activeid);
                        if (bps == null) {
                            throw MyErrorEnum.customError.getMyException("奖品已被领取完了！");
                        }
                        bps.setStatus(PrizeStatusEnum.received.getId());//0为未使用状态；1为已领取状态；2为已使用状态
                        //bps.setBegintime(nowDate);
                        //bps.setEndtime(DateUtils.addMonths(nowDate, 1));
                        bPrizeStockDao.saveOrUpdate(bps);
                        logger.info("更新第三方券" + bps.getId() + "为使用状态！");

                        BPrizeInfo bpi = new BPrizeInfo();
                        bpi.setCode(bps.getCode());
                        bpi.setMerchantid(bp.getMerchantid());
                        bpi.setId(bps.getId());
                        bpi.setType(bp.getType());
                        rtnList.add(bpi);
                    } else if (PrizeTypeEnum.material.getId().equals(bp.getType())) {
                        logger.info("开始生成实物券!");
                        BPrizeInfo bpi = new BPrizeInfo();
                        bpi.setMerchantid(bp.getMerchantid());
                        bpi.setId(bp.getId());
                        bpi.setType(bp.getType());
                        rtnList.add(bpi);
                    }
                    if (CollectionUtils.isNotEmpty(rtnList)) {
                        UPrizeRecord uPrizeRecord = new UPrizeRecord();
                        uPrizeRecord.setActiveid(bp.getActiveid());
                        uPrizeRecord.setCreatetime(new Date());
                        uPrizeRecord.setMid(mid);
                        uPrizeRecord.setOstype(Long.parseLong(ostype));
                        uPrizeRecord.setCreatetime(new Date());
                        //uPrizeRecord.setPrizeinfo(rtnList.get(0).getCode());
                        uPrizeRecord.setPrizetype(Long.parseLong(bp.getType().toString()));
                        //uPrizeRecord.setPrizeid(rtnList.get(0).getId());
                        //如果是眯客券，prizeinfo字段记录promotionid；如果是第三方券或者实物，则记录code
                        if (PrizeTypeEnum.mike.getId().equals(bp.getType())) {
                            uPrizeRecord.setPrizeinfo(rtnList.get(0).getId().toString());
                        } else {
                            uPrizeRecord.setPrizeinfo(rtnList.get(0).getCode());
                        }
                        uPrizeRecord.setPrizeid(bp.getId());
                        iUPrizeRecordDao.saveOrUpdate(uPrizeRecord);
                        if (uPrizeRecord.getId() == null) {
                            throw MyErrorEnum.customError.getMyException("插入奖品记录出错!");
                        }
                        rtnList.get(0).setCreatetime(DateUtils.formatDateTime(uPrizeRecord.getCreatetime()));
                        if (PrizeTypeEnum.mike.getId().intValue() != rtnList.get(0).getType().intValue()) {
                            rtnList.get(0).setPrizeRecordId(uPrizeRecord.getId());
                        }
                        logger.info("保存抽奖记录，奖品id为：" + uPrizeRecord.getId());
                        Long numLong = bp.getNum();
                        if (numLong >= 1) {
                            bp.setNum(--numLong);
                        } else {
                            throw MyErrorEnum.customError.getMyException("该类型奖品已发放完毕，无剩余库存！");
                        }
                        iBPrizeDao.saveOrUpdate(bp);
                        logger.info("更新奖品数目,当前库中奖品数为：" + bp.getNum());
                    } else {
                        throw MyErrorEnum.customError.getMyException("生成优惠券失败！");
                    }

                }
            } else {
                throw MyErrorEnum.customError.getMyException("此活动不允许领取优惠券.");
            }
        } else {
            logger.info("用户 " + mid + "今天已抽过奖！");
            throw MyErrorEnum.customError.getMyException("用户今天已抽过奖！");
        }

        logger.info("生成用户优惠券：mid:{}, status:{}, 券实例:{}", mid, true, rtnList);
        logger.info(">>>根据活动发放优惠券----------------------//结束");
        return rtnList;
    }

    @Override
    public List<Long> genCGTicket(long promotionid, long mid, Date starttime, Date endtime, PromotionMethodTypeEnum promotionMethodType, String sourcecdkey, Long channelid, String hardwarecode) {
        BPromotion bp = this.iBPromotionDao.findById(promotionid);
        Long genpromotionid = null;
        List<Long> genList = Lists.newArrayList();
        try {
            //1. 校验券定义
            if (bp == null) {
                logger.error("{},券定义不存在.", promotionid);
                throw MyErrorEnum.customError.getMyException("券定义不存在.");
            }
            if (!bp.getIsinstance()) {
                logger.error("{},此券非实例定义.", promotionid);
                throw MyErrorEnum.customError.getMyException("此券非实例定义.");
            }
            if (PromotionTypeEnum.qieke.equals(bp.getType()) || PromotionTypeEnum.yijia.equals(bp.getType())) {
                logger.error("{},切客券或议价券不可生成用户券.", promotionid);
                throw MyErrorEnum.customError.getMyException("切客券或议价券不可生成用户券.");
            }
            Date current = new Date();
            if (current.before(bp.getBegintime()) || current.after(bp.getEndtime())) {
                logger.error("{},此券定义未在有效时间范围内. 时间范围: {} - {}", promotionid, bp.getBegintime(), bp.getEndtime());
                throw MyErrorEnum.customError.getMyException("此券定义未在有效时间范围内.");
            }
            if (bp.getNum() != -1 && (bp.getNum() == 0 || bp.getNum() < 0)) {
                logger.error("{},此券已被领完.", promotionid);
                throw MyErrorEnum.customError.getMyException("此券已被领完.");
            }

            //代码重构: 保留现有逻辑
            if (bp.getOnlineprice() != null || bp.getOfflineprice() != null) {
                logger.info(">>>生成优惠券［新］业务逻辑---------------------->>开始");
                // 最新逻辑
                logger.info(">>>生成优惠券［新］业务逻辑---------------------->>1. 检查优惠券定义[promotionid:{}]可发放数量[num:{}]张", promotionid, bp.getNum());
                //1. 优惠券数据减1
                if (bp.getNum() != -1) { //如若此券定义为非无限发放，则需要更新券余量
                    BPromotion instance = new BPromotion();
                    instance.setId(promotionid);
                    instance.setNum(bp.getNum() - 1);
                    this.iBPromotionDao.saveOrUpdate(instance);
                    logger.info(">>>生成优惠券［新］业务逻辑---------------------->>2. 更新优惠券可用数量,num: before={}, after={}", bp.getNum(), bp.getNum() - 1);
                } else {
                    logger.info(">>>生成优惠券［新］业务逻辑---------------------->>2. 无需更新优惠券可用数量,num:{}", bp.getNum());
                }

                //2. 生成新优惠券
                BPromotion newp = new BPromotion();
                newp.setName(bp.getName());
                newp.setDescription(bp.getDescription());
                newp.setCreatetime(new Date());

                LocalDate begintime = LocalDate.now();
                if (bp.getEffectivetype().equals(EffectiveTypeEnum.TOMORROW)) {
                    begintime = begintime.plusDays(1);
                }
                newp.setBegintime(begintime.toDate());
                Date enddate = null;
                if (bp.getExpiretype().equals(ExpireTypeEnum.LOCK)) {
                    enddate = bp.getEndtime();
                } else {
                    org.joda.time.LocalDateTime.Property e = LocalDateTime.fromDateFields(begintime.plusDays(bp.getExpiredaynum()).toDate()).hourOfDay();
                    enddate = e.withMaximumValue().withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59).withMillisOfSecond(59).toDate();
                }
                newp.setEndtime(enddate);

                newp.setType(bp.getType());
                newp.setIsticket(bp.getIsticket());
                newp.setNum(1);
                newp.setInfo("");
                newp.setClassname(SimplesubTicket.class.getName());
                newp.setIsota(bp.getIsota());
                newp.setOtapre(bp.getOtapre());
                newp.setActivitiesid(bp.getActivitiesid());
                ;
                newp.setNote(bp.getNote());
                newp.setPic(bp.getPic());
                newp.setVersion(bp.getVersion());
                newp.setIsinstance(false);
                newp.setWeight(bp.getWeight());
                newp.setTotalnum(bp.getTotalnum());
                newp.setPlannum(bp.getPlannum());
                newp.setProtype(bp.getProtype());
                newp.setOnlineprice(bp.getOnlineprice());
                newp.setOfflineprice(bp.getOfflineprice());
                newp.setExpiretype(bp.getExpiretype());
                newp.setExpiredaynum(bp.getExpiredaynum());
                newp.setEffectivetype(bp.getEffectivetype());
                newp.setPlatformtype(bp.getPlatformtype());
                newp.setSourcecdkey(sourcecdkey);
                newp.setChannelid(channelid);
                this.iBPromotionDao.saveOrUpdate(newp);
                genpromotionid = newp.getId();
                logger.info(">>>生成优惠券［新］业务逻辑---------------------->>3. 生成新优惠券:{}", newp);

                //3. 绑定新券至用户
                UTicket newTicket = new UTicket();
                if (genpromotionid != null) {
                    newTicket.setMid(mid);
                    newTicket.setPromotionid(String.valueOf(genpromotionid));
                    if (PromotionMethodTypeEnum.HAND_NEEDACTIVE.equals(promotionMethodType)) {
                        newTicket.setStatus(TicketStatusEnum.unactive.getId());
                    } else {
                        newTicket.setStatus(TicketStatusEnum.unused.getId());
                    }
                    newTicket.setActivityid(newp.getActivitiesid());
                    newTicket.setPromotiontime(newp.getCreatetime());
                    newTicket.setPromotionmethodtype(promotionMethodType);
                    logger.info("绑定券: 用户id[{}],券定义[{}]", mid, genpromotionid);
                    this.uTicketDao.insert(newTicket);    //关联系统券给指定用户
                    logger.info(">>>生成优惠券［新］业务逻辑---------------------->>4. 绑定新优惠券[{}]给用户[{}]", genpromotionid, mid);
                    if (newTicket.getId() == null) {
                        logger.error("系统优惠券[{}]绑定用户[{}]时发生错误.券实例:{}.", genpromotionid, mid, promotionid);
                        throw MyErrorEnum.customError.getMyException("系统优惠券[" + genpromotionid + "]绑定用户[" + mid + "]时发生错误.券实例:" + promotionid);
                    }
                    //4. 记录用户领券记录
                    logMemberGetPromotion(mid, bp.getActivitiesid(), bp.getId(), genpromotionid, "", "", true, hardwarecode);
                    logger.info(">>>生成优惠券［新］业务逻辑---------------------->>5. 记录用户[{}]领券记录", mid);
                    genList.add(genpromotionid);
                }
                logger.info(">>>生成优惠券［新］业务逻辑---------------------->>结束");
            } else {
                logger.info(">>>生成优惠券［旧］业务逻辑---------------------->>开始");
                // 原先逻辑
                genList = oldGenTicket(bp.getId(), bp, starttime, endtime, mid, promotionMethodType, sourcecdkey, channelid, hardwarecode);
                logger.info(">>>生成优惠券［旧］业务逻辑---------------------->>开始");
            }
            return genList;
        } catch (Exception e) {
            logger.error("genCGTicket(long promotionid, long mid) occur error.", e);
            logMemberGetPromotion(mid, bp.getActivitiesid(), promotionid, null, "", e.getMessage(), false, hardwarecode);
        }
        return genList;
    }

    @Deprecated
    private List<Long> oldGenTicket(Long promotionid, BPromotion bp, Date starttime, Date endtime, Long mid, PromotionMethodTypeEnum promotionMethodType, String sourcecdkey, Long channelid, String hardwarecode) {
        List<Long> genList = Lists.newArrayList();

        //2. 解析券
        logger.info(">>>生成优惠券［旧］业务逻辑---------------------->>1. 解析优惠券定义,info:{}", bp.getInfo());
        List<CommonXmlPromo> cxpList = null;
        if (!Strings.isNullOrEmpty(bp.getInfo())) {
            try {
                cxpList = parse(bp.getInfo());
            } catch (Exception e) {
                logger.error("解析优惠券实例出错.promotionid:" + promotionid, e);
                throw MyErrorEnum.errorParm.getMyException("解析优惠券实例出错.券实例:" + promotionid);
            }
            if (cxpList == null || cxpList.size() == 0) {
                logger.error("券实例定义中无券明细.券实例:{}", promotionid);
                throw MyErrorEnum.customError.getMyException("券实例定义中无券明细.券实例:" + promotionid);
            }
        } else {
            logger.error("券实例定义内容为空.券实例:{}", promotionid);
            throw MyErrorEnum.customError.getMyException("券实例定义内容为空.券实例:" + promotionid);
        }

        //3. 生成用户券
        logger.info(">>>生成优惠券［旧］业务逻辑---------------------->>2. 循环解析优惠券定义列表,cxpList:{}", cxpList);
        for (CommonXmlPromo cxp : cxpList) {
            bp = this.iBPromotionDao.findById(promotionid);
            if (bp.getNum() == -1 || bp.getNum() > 0) {
                logger.info(">>>生成优惠券［旧］业务逻辑---------------------->>2.1 检查优惠券定义[promotionid:{}]可发放数量[num:{}]张", promotionid, bp.getNum());
                //3.1 维护券实例发放券数量
                if (bp.getNum() > 0) {
                    BPromotion instance = new BPromotion();
                    instance.setId(promotionid);
                    instance.setNum(bp.getNum() - 1);
                    this.iBPromotionDao.saveOrUpdate(instance);
                    logger.info(">>>生成优惠券［旧］业务逻辑---------------------->>2.2 更新优惠券可用数量,num: before={}, after={}", bp.getNum(), bp.getNum() - 1);
                } else {
                    logger.info(">>>生成优惠券［旧］业务逻辑---------------------->>2.2 无需更新优惠券可用数量, num:{}", bp.getNum());
                }

                //3.2 生成系统券
                bp.setId(null);
                Document doc = new Document(new Element("root"));
                doc.getRootElement().addContent(new Element("subprice").setAttribute("value", String.valueOf(cxp.getOnlineprice())));
                doc.getRootElement().addContent(new Element("offlinesubprice").setAttribute("value", String.valueOf(cxp.getOfflineprice())));
                bp.setInfo(XMLUtils.XMLtoString(doc));

                bp.setCreatetime(new Date());

                LocalDate begindate = LocalDate.now().plusDays(cxp.getEffective());
                bp.setBegintime(begindate.toDate());
                if (starttime != null && endtime != null) {
                    bp.setEndtime(endtime);
                } else {
                    org.joda.time.LocalDateTime.Property e = LocalDateTime.fromDateFields(begindate.plusDays(cxp.getLimitdaynum()).toDate()).hourOfDay();
                    Date enddate = e.withMaximumValue().withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59).withMillisOfSecond(59).toDate();
                    bp.setEndtime(enddate);
                }

                bp.setNum(1);
                bp.setClassname(SimplesubTicket.class.getName());
                bp.setIsinstance(false);
                bp.setType(bp.getType());
                // add by zyj 20150619 start
                bp.setOnlineprice(cxp.getOnlineprice());
                bp.setOfflineprice(cxp.getOfflineprice());
                // add by zyj 20150619 end
                bp.setSourcecdkey(sourcecdkey);
                bp.setChannelid(channelid);
                this.iBPromotionDao.saveOrUpdate(bp);  //生成系统立减券
                Long genpromotionid = bp.getId();
                logger.info(">>>生成优惠券［旧］业务逻辑---------------------->>3. 生成新优惠券 {}", bp);

                if (genpromotionid == null) {
                    logger.error("生成系统优惠券发生错误.券实例:{}", promotionid);
                    throw MyErrorEnum.customError.getMyException("生成系统优惠券发生错误.券实例:" + promotionid);
                }
                //3.2 系统券关联用户
                UTicket newTicket = new UTicket();
                if (genpromotionid != null) {
                    newTicket.setMid(mid);
                    newTicket.setPromotionid(String.valueOf(genpromotionid));
                    if (PromotionMethodTypeEnum.HAND_NEEDACTIVE.equals(promotionMethodType)) {
                        newTicket.setStatus(TicketStatusEnum.unactive.getId());
                    } else {
                        newTicket.setStatus(TicketStatusEnum.unused.getId());
                    }
                    // add by zyj 20150619 start
                    newTicket.setActivityid(bp.getActivitiesid());
                    newTicket.setPromotiontime(bp.getCreatetime());
                    // add by zyj 20150619 end
                    newTicket.setPromotionmethodtype(promotionMethodType);
                    this.uTicketDao.insert(newTicket);    //关联系统券给指定用户
                    logger.info(">>>生成优惠券［旧］业务逻辑---------------------->>4. 绑定新优惠券[{}]给用户[{}]. ", genpromotionid, mid);

                    if (newTicket.getId() == null) {
                        logger.error("系统优惠券[{}]绑定用户[{}]时发生错误.券实例:{}.", genpromotionid, mid, promotionid);
                        throw MyErrorEnum.customError.getMyException("系统优惠券[" + genpromotionid + "]绑定用户[" + mid + "]时发生错误.券实例:" + promotionid);
                    }
                    logger.info(">>>生成优惠券［旧］业务逻辑---------------------->>5. 记录用户[{}]领券记录", mid);
                    logMemberGetPromotion(mid, bp.getActivitiesid(), promotionid, genpromotionid, "", "", true, hardwarecode);
                    genList.add(genpromotionid);
                }
            }
        }
        return genList;
    }

    /**
     * 记录用户领券日志
     *
     * @param mid
     * @param activeid
     * @param promoinstanceid
     * @param noticetype
     * @param errormsg
     * @param success
     */
    private void logMemberGetPromotion(Long mid, Long activeid, Long promoinstanceid, Long promotionid, String noticetype, String errormsg, boolean success, String hardwarecode) {
        UPromotionLog log = new UPromotionLog();
        log.setActiveid(activeid);
        log.setCreatetime(new Date());
        log.setErrormsg(errormsg);
        log.setMid(mid);
        log.setNoticetype(noticetype);
        log.setPromoinstanceid(promoinstanceid);
        log.setPromotionid(promotionid);
        log.setSuccess(success);
        log.setHardwarecode(hardwarecode);
        logger.info("记录用户领券日志,{}", log.toString());
        iUPromotionLogDao.insert(log);
    }

    /**
     * 查询新用户礼包券
     *
     * @return
     */
    public Optional<BPromotion> findNewMemberPromotionDefine() {
        List<BPromotion> list = findByPromotionType(PromotionTypeEnum.XLB);
        if (list != null && list.size() == 1) {
            return Optional.fromNullable(list.get(0));
        } else {
            logger.error("未发现新用户礼包券定义.type:{}, name:{}", PromotionTypeEnum.XLB.getId(), PromotionTypeEnum.XLB.getName());
        }
        return Optional.absent();
    }

    /**
     * 查询常规券
     *
     * @return
     */
    public Optional<BPromotion> findCommonPromotionDefine() {
        List<BPromotion> list = findByPromotionType(PromotionTypeEnum.CGJ);
        if (list != null && list.size() == 1) {
            return Optional.fromNullable(list.get(0));
        } else {
            logger.error("未发现常规券定义.type:{}, name:{}", PromotionTypeEnum.CGJ.getId(), PromotionTypeEnum.CGJ.getName());
        }
        return Optional.absent();

    }

    @Override
    public void saveOrUpdate(BPromotionPrice bPromotionPrice) {
        this.iBPromotionPriceDao.saveOrUpdate(bPromotionPrice);
    }

    @Override
    public List<BPromotion> findByPromotionType(
            PromotionTypeEnum promotionTypeEnum) {
        return this.iBPromotionDao.findByPromotionType(promotionTypeEnum);
    }

    /**
     * 根据mid获取用户手机号
     *
     * @param mid
     * @return
     */
    public String getMemberPhoneByMid(Long mid) {
        Optional<UMember> member = iMemberService.findMemberById(mid);
        if (member.isPresent()) {
            UMember uMember = member.get();
            return Strings.isNullOrEmpty(uMember.getPhone()) ? uMember.getLoginname() : uMember.getPhone();
        }
        return "";
    }

    /**
     * 解析券实例定义，仅限于新用户礼包及常规券的定义
     *
     * @param xml
     * @return
     * @throws JDOMException
     * @throws IOException
     */
    public List<CommonXmlPromo> parse(String xml) throws JDOMException, IOException {
        List<CommonXmlPromo> rtnList = Lists.newArrayList();
        Document doc = (Document) XMLUtils.StringtoXML(xml);
        Element tickets = doc.getRootElement().getChild("tickets");
        if (tickets != null && tickets.getChildren().size() > 0) {
            for (Element ticket : (List<Element>) tickets.getChildren()) {
                BigDecimal onlineprice = new BigDecimal(Double.valueOf(ticket.getChildText("onlineprice")));
                BigDecimal offlineprice = new BigDecimal(Double.valueOf(ticket.getChildText("offlineprice")));
                Integer limitdaynum = Integer.valueOf(ticket.getChildText("limit-daynum"));
                Integer effective = Integer.valueOf(ticket.getChildText("effective"));
                rtnList.add(new CommonXmlPromo(onlineprice, offlineprice, limitdaynum, effective));
            }
        }
        return rtnList;
    }

    @Override
    public boolean checkFirstOrderByOrderId(Long orderId) {
        boolean result = false;
        List<BPromotion> bPromotions = iBPromotionDao.queryPromotionByOrderId(orderId);
        if (CollectionUtils.isNotEmpty(bPromotions)) {
            for (BPromotion bPromotion : bPromotions) {
                if (PromotionTypeEnum.shoudan.getId().equals(bPromotion.getType())) {
                    result = true;
                    return result;
                }
            }
        }
        return result;
    }


    @Override
    public boolean isGetFirstOrderPromotion(String hardwarecode) {
        List rsList = this.iBPromotionDao.findFirstOrderPromotionByHardwarecode(hardwarecode);
        return rsList != null && rsList.size() > 0;
    }
}
