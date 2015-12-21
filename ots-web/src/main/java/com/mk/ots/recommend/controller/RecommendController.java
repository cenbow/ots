package com.mk.ots.recommend.controller;

import com.google.common.collect.Maps;
import com.mk.framework.exception.MyErrorEnum;
import com.mk.ots.common.utils.Constant;
import com.mk.ots.recommend.model.*;
import com.mk.ots.recommend.service.RecommendService;
import com.mk.ots.web.ServiceOutput;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.*;


@Controller
@RequestMapping(value = "/recommend", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
public class RecommendController {

    final Logger logger = LoggerFactory.getLogger(RecommendController.class);

    @Autowired
    private RecommendService recommendService;



    /**
     * 根据位置查询banner或者列表
     * http://127.0.0.1:8080/ots/recommend/query
     *
     * @param position
     * @return
     */
    @RequestMapping("/query")
    public ResponseEntity<Map<String, Object>> query(String position, String platform, String cityid, String callmethod) {
        Map<String, Object> rtnMap = Maps.newHashMap();
        if (StringUtils.isEmpty(position)) {
            throw MyErrorEnum.errorParm.getMyException();
        }

        Integer platformValue = getplatformValue(callmethod, platform);

        try {
            Integer cityLimit = null;
            Integer globleLimit = null;
            List<RecommendList> banners = new ArrayList<>();
            List<RecommendList> cityBanners;
            HashMap<Integer, TRecommenditem> recommenditemHashMap = genRecommenditemHashMap(position, platformValue);


            if (position.equals("921A") || position.equals("921C")) {
                cityLimit = Constant.CITY_RECOMMEND_ITEM_LIMIT;
                cityBanners = genCityRecommendLists(recommenditemHashMap, cityid, callmethod, cityLimit);

                globleLimit = Constant.RECOMMEND_ITEM_LIMIT - cityBanners.size();
            } else {
                cityBanners = genCityRecommendLists(recommenditemHashMap, cityid, callmethod, cityLimit);
            }


            banners.addAll(cityBanners);

            List<RecommendList> globleBanners = genGlobleRecommendLists(recommenditemHashMap, cityid, callmethod, globleLimit);
            banners.addAll(globleBanners);

            rtnMap.put("banners", reSort(banners));
            rtnMap.put("success", true);

        } catch (Exception e) {
            e.printStackTrace();
            rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, false);
            rtnMap.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
            rtnMap.put(ServiceOutput.STR_MSG_ERRMSG, e.getMessage());
            logger.error("【/recommend/query】 is error: {} ", e.getMessage());
        }
        return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
    }

    public List<RecommendList> bannersInfo(String position, Integer platform) {
        //banner页只返回5条数据
        List<TRecommenditem> list;
        if (position.equals("921A") || position.equals("921C")) {
            list = recommendService.queryRecommendItemLimit5(position, platform);
        } else {
            list = recommendService.queryRecommendItem(position, platform);
        }

        List<RecommendList> banners = new ArrayList<RecommendList>();


        if (CollectionUtils.isNotEmpty(list)) {
            for (TRecommenditem tRecommendItem : list) {

                if (Constant.WEIXIN_CALLMETHOD.equals(platform.toString()) && tRecommendItem.getViewtype() == Constant.TONIGHT_PROMO_VIEWTYPE) {
                    continue;
                }

                RecommendList recommendList = new RecommendList();
                recommendList.setName(tRecommendItem.getTitle());
                recommendList.setDescription(tRecommendItem.getDescription());
                recommendList.setImgurl(tRecommendItem.getImageurl());
                recommendList.setUrl(tRecommendItem.getLink());
                recommendList.setDetailid(tRecommendItem.getDetailid());
                recommendList.setQuerytype(tRecommendItem.getViewtype());
                recommendList.setCreatetime(tRecommendItem.getCreatetime());
                banners.add(recommendList);
            }
        }


        return banners;

    }

    /**
     * 根据发现详情页id 得到详情页信息
     *
     * @param
     * @return
     */
    @RequestMapping("/querydetail")
    public ResponseEntity<Map<String, Object>> querydetail(String detailid) {
        Map<String, Object> rtnMap = Maps.newHashMap();
        if (StringUtils.isEmpty(detailid)) {
            throw MyErrorEnum.errorParm.getMyException();
        }
        TRecommenddetail detail = recommendService.selectByPrimaryKey(Long.valueOf(detailid));
        if (detail != null) {
            RecommendDetail detail2 = new RecommendDetail();
            detail2.setImage(detail.getTopimage());
            detail2.setSubtitle(detail.getSubtitle());
            detail2.setTitle(detail.getTitle());
            detail2.setWord(detail.getContent());
            rtnMap.put("detail", detail2);
        }
        rtnMap.put("success", true);
        return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
    }



    /**
     *
     * App 首屏推荐
     *
     *
     */
    @RequestMapping("/queryloading")
    public ResponseEntity<Map<String, Object>> queryloading( String platform, String cityid, String callmethod) {
        Map<String, Object> rtnMap = Maps.newHashMap();
        String position = Constant.RECOMMEND_LOADDING_POSITION;

        Integer platformValue = getplatformValue(callmethod, platform);

        if (StringUtils.isBlank(cityid)){
            rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, false);
            rtnMap.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
            rtnMap.put(ServiceOutput.STR_MSG_ERRMSG, "参数cityid 不能为空");
            return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
        }




        try {
            Integer cityLimit =  Constant.CITY_RECOMMEND_ITEM_LIMIT;
            Integer globleLimit = null;
            List<RecommendList> banners = new ArrayList<>();
            List<RecommendList> cityBanners;
            HashMap<Integer, TRecommenditem> recommenditemHashMap = genRecommenditemHashMap(position, platformValue);

            cityBanners = genCityRecommendLists(recommenditemHashMap, cityid, callmethod, cityLimit);

            globleLimit = Constant.RECOMMEND_ITEM_LIMIT - cityBanners.size();

            banners.addAll(cityBanners);
            List<RecommendList> globleBanners = genGlobleRecommendLists(recommenditemHashMap, cityid, callmethod, globleLimit);
            banners.addAll(globleBanners);

            rtnMap.put("loading", reSort(banners));
            rtnMap.put("success", true);

        } catch (Exception e) {
            e.printStackTrace();
            rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, false);
            rtnMap.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
            rtnMap.put(ServiceOutput.STR_MSG_ERRMSG, e.getMessage());
            logger.error("【/recommend/queryloading】 is error: {} ", e.getMessage());
        }
        return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
    }


    /**
     *
     * App 首页快捷入口
     *
     *
     */
    @RequestMapping("/shortcut")
    public ResponseEntity<Map<String, Object>> shortCut( String platform, String cityid, String callmethod) {
        Map<String, Object> rtnMap = Maps.newHashMap();
        String position = Constant.RECOMMEND_HOMEPAGE_SHORTCUT_POSITION;

        Integer platformValue = getplatformValue(callmethod, platform);

        try {
            Integer cityLimit =  Constant.CITY_RECOMMEND_HOMEPAGE_SHORTCUT_LIMIT;
            Integer globleLimit = null;
            List<RecommendList> banners = new ArrayList<>();
            List<RecommendList> cityBanners;
            HashMap<Integer, TRecommenditem> recommenditemHashMap = genRecommenditemHashMap(position, platformValue);


            if (StringUtils.isBlank(cityid)){
                cityid = Constant.STR_CITYID_SHANGHAI;
            }

            cityBanners = genCityRecommendLists(recommenditemHashMap, cityid, callmethod, cityLimit);

            banners.addAll(cityBanners);
//            List<RecommendList> globleBanners = genGlobleRecommendLists(recommenditemHashMap, cityid, callmethod, globleLimit);
//            banners.addAll(globleBanners);

            rtnMap.put("shortcut", reSort(banners));
            rtnMap.put("success", true);

        } catch (Exception e) {
            e.printStackTrace();
            rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, false);
            rtnMap.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
            rtnMap.put(ServiceOutput.STR_MSG_ERRMSG, e.getMessage());
            logger.error("【/recommend/shortcut】 is error: {} ", e.getMessage());
        }
        return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
    }



    private static Integer getplatformValue(String callmethod, String platform) {
        Integer platformValue = null;
        if (StringUtils.isNotBlank(platform)) {
            platformValue = Integer.valueOf(platform);
        } else if (StringUtils.isNotBlank(callmethod)) {
            if (Constant.WEIXIN_CALLMETHOD.equals(callmethod)) {
                platformValue = Constant.WEIXIN_PLATFORM;
            } else if (Constant.ANDROID_CALLMETHOD.equals(callmethod)) {
                platformValue = Constant.ANDROID_PLATFORM;
            } else if (Constant.IOS_CALLMETHOD.equals(callmethod)) {
                platformValue = Constant.IOS_PLATFORM;
            }
        }

        return platformValue;
    }

    private HashMap<Integer, TRecommenditem> genRecommenditemHashMap(String position, Integer platformValue) {
        HashMap<Integer, TRecommenditem> recommenditemHashMap = new HashMap<>();

        List<TRecommenditem> list = recommendService.queryRecommendItem(position, platformValue);

        for (TRecommenditem recommenditem : list) {
            recommenditemHashMap.put(recommenditem.getId().intValue(), recommenditem);
        }

        return recommenditemHashMap;
    }

    private List<RecommendList> genCityRecommendLists(HashMap<Integer, TRecommenditem> recommenditemHashMap,
                                                      String cityid,
                                                      String callmethod,
                                                      Integer limit) {


        List<RecommendList> banners = new ArrayList<>();


        Integer cityId = StringUtils.isNoneBlank(cityid)
                ? Integer.valueOf(cityid)
                : Constant.RECOMMEND_GLOBAL;

        List<TRecommendItemArea> cityItemAreaList = recommendService.selectItemAreaByCityId(cityId);


        if (StringUtils.isNotBlank(cityid) && cityItemAreaList != null) {
            banners = buildBanners(cityItemAreaList,
                    recommenditemHashMap,
                    callmethod,
                    limit);
        }

        return banners;
    }


    private List<RecommendList> genGlobleRecommendLists(HashMap<Integer, TRecommenditem> recommenditemHashMap,
                                                        String cityid,
                                                        String callmethod,
                                                        Integer limit) {

        List<RecommendList> banners = new ArrayList<>();

        List<TRecommendItemArea> itemAreaList = recommendService.selectItemAreaByCityId(Constant.RECOMMEND_GLOBAL);

        if (itemAreaList != null) {
            int global_count = 0;
            for (int i = 0; i < itemAreaList.size(); i++) {
                if (itemAreaList != null && itemAreaList.get(i) != null) {
                    TRecommenditem tRecommendItem = recommenditemHashMap.get(itemAreaList.get(i).getItemid());


                    if (tRecommendItem != null && global_count <= limit) {

                        if (Constant.WEIXIN_CALLMETHOD.equals(callmethod)
                                && tRecommendItem.getViewtype() == Constant.TONIGHT_PROMO_VIEWTYPE) {
                            continue;
                        }

                        if (StringUtils.isNotBlank(cityid)) {
                            if (Constant.PROMO_BANNER_PROMO.equals(tRecommendItem.getViewtype())
                                    || Constant.PROMO_BANNER_ONE_PROMO.equals(tRecommendItem.getViewtype())) {
                                continue;
                            }
                        }


                        if (global_count <= limit) {

                            RecommendList recommendList = new RecommendList();
                            recommendList.setName(tRecommendItem.getTitle());
                            recommendList.setDescription(tRecommendItem.getDescription());
                            recommendList.setImgurl(tRecommendItem.getImageurl());
                            recommendList.setUrl(tRecommendItem.getLink());
                            recommendList.setDetailid(tRecommendItem.getDetailid());
                            recommendList.setQuerytype(tRecommendItem.getViewtype());
                            recommendList.setCreatetime(tRecommendItem.getCreatetime());


                            banners.add(recommendList);
                            global_count++;
                        }

                    }

                }
            }
        }

        return banners;
    }


    private List<RecommendList> buildBanners(List<TRecommendItemArea> areaList,
                                             HashMap<Integer, TRecommenditem> recommenditemHashMap,
                                             String callmethod,
                                             Integer limit) {

        List<RecommendList> banners = new ArrayList<>();
        Integer city_recommend_count = 0;
        for (int i = 0; i < areaList.size(); i++) {
            if (areaList.get(i) != null && (limit == null || city_recommend_count <= limit)) {
                TRecommenditem tRecommendItem = recommenditemHashMap.get(areaList.get(i).getItemid());

                if (tRecommendItem != null) {

                    if (Constant.WEIXIN_CALLMETHOD.equals(callmethod)
                            && tRecommendItem.getViewtype() == Constant.TONIGHT_PROMO_VIEWTYPE) {
                        continue;
                    }

                    if (limit == null || city_recommend_count <= limit) {
                        RecommendList recommendList = new RecommendList();
                        recommendList.setName(tRecommendItem.getTitle());
                        recommendList.setDescription(tRecommendItem.getDescription());
                        recommendList.setImgurl(tRecommendItem.getImageurl());
                        recommendList.setUrl(tRecommendItem.getLink());
                        recommendList.setDetailid(tRecommendItem.getDetailid());
                        recommendList.setQuerytype(tRecommendItem.getViewtype());
                        recommendList.setCreatetime(tRecommendItem.getCreatetime());
                        recommendList.setSort(tRecommendItem.getSort());
                        banners.add(recommendList);
                        city_recommend_count++;
                    }


                }
            }

        }

        return banners;
    }

    private List<RecommendList> reSort(List<RecommendList> banners){

        if (banners == null || banners.size() == 0){
            return banners;
        }

        Object[] bannberArr = banners.toArray();
        Arrays.sort(bannberArr, this.new RecommentComparator());

        banners.clear();

        for (int i = 0; i < bannberArr.length; i++) {
            if (bannberArr[i] instanceof RecommendList) {
                RecommendList  rl = (RecommendList) bannberArr[i];
                banners.add(rl);

            }
        }

        return banners;

    }
    /*
	 * 价格排序规则
	 */
    private class RecommentComparator implements Comparator<Object> {
        public int compare(Object obj1, Object obj2) {
            RecommendList banner1 = (RecommendList) obj1;
            RecommendList banner2 = (RecommendList) obj2;

            if (banner1.getSort().compareTo(banner2.getSort()) > 0) {
                return -1;
            } else {
                return 1;
            }

        }
    }

}
