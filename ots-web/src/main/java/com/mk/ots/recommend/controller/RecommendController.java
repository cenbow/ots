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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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


        Integer platformValue = null;


        try {
            if (StringUtils.isNotBlank(platform)) {
                platformValue = Integer.valueOf(platform);
            }
            List<RecommendList> banners = new ArrayList<>();

            List<TRecommenditem> list;

            HashMap<Integer, TRecommenditem> recommenditemHashMap = new HashMap<>();


            Integer cityId = StringUtils.isNoneBlank(cityid) ? Integer.valueOf(cityid) : Constant.RECOMMEND_GLOBAL;

            List<TRecommendItemArea> cityItemAreaList = recommendService.selectItemAreaByCityId(cityId);

            if (position.equals("921A") || position.equals("921C")) {
                list = recommendService.queryRecommendItem(position, platformValue);

                for (TRecommenditem recommenditem : list) {
                    recommenditemHashMap.put(recommenditem.getId().intValue(), recommenditem);
                }

                int city_recommend_count = 0;


                if (StringUtils.isNotBlank(cityid) && cityItemAreaList != null) {

                    for (int i = 0; i < cityItemAreaList.size(); i++) {
                        if (cityItemAreaList.get(i) != null && city_recommend_count <= Constant.CITY_RECOMMEND_ITEM_LIMT) {
                            TRecommenditem tRecommendItem = recommenditemHashMap.get(cityItemAreaList.get(i).getItemid());


                            if (tRecommendItem != null) {

                                if (Constant.WEIXIN_CALLMETHOD.equals(callmethod) && tRecommendItem.getViewtype() == Constant.TONIGHT_PROMO_VIEWTYPE) {
                                    continue;
                                }

                                if (city_recommend_count <= Constant.CITY_RECOMMEND_ITEM_LIMT) {
                                    RecommendList recommendList = new RecommendList();
                                    recommendList.setName(tRecommendItem.getTitle());
                                    recommendList.setDescription(tRecommendItem.getDescription());
                                    recommendList.setImgurl(tRecommendItem.getImageurl());
                                    recommendList.setUrl(tRecommendItem.getLink());
                                    recommendList.setDetailid(tRecommendItem.getDetailid());
                                    recommendList.setQuerytype(tRecommendItem.getViewtype());
                                    recommendList.setCreatetime(tRecommendItem.getCreatetime());

                                    banners.add(recommendList);
                                    city_recommend_count++;
                                }


                            }
                        }

                    }
                }

                List<TRecommendItemArea> itemAreaList = recommendService.selectItemAreaByCityId(Constant.RECOMMEND_GLOBAL);
                if (itemAreaList != null) {
                    int global_count = 0;
                    for (int i = 0; i < itemAreaList.size(); i++) {
                        if (itemAreaList != null && itemAreaList.get(i) != null) {
                            TRecommenditem tRecommendItem = recommenditemHashMap.get(itemAreaList.get(i).getItemid());


                            if (tRecommendItem != null && global_count <= Constant.RECOMMEND_ITEM_LIMT - city_recommend_count) {

                                if (Constant.WEIXIN_CALLMETHOD.equals(callmethod) && tRecommendItem.getViewtype() == Constant.TONIGHT_PROMO_VIEWTYPE) {
                                    continue;
                                }

                                if (StringUtils.isNotBlank(cityid)){
                                    if (Constant.PROMO_BANNER_PROMO.equals(tRecommendItem.getViewtype()) || Constant.PROMO_BANNER_ONE_PROMO.equals(tRecommendItem.getViewtype())){
                                        continue;
                                    }
                                }


                                if (global_count <= Constant.RECOMMEND_ITEM_LIMT - city_recommend_count) {

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

            } else {
                list = recommendService.queryRecommendItem(position, platformValue);

                for (TRecommenditem recommenditem : list) {
                    recommenditemHashMap.put(recommenditem.getId().intValue(), recommenditem);
                }

                if (StringUtils.isNotBlank(cityid)) {
                    for (TRecommendItemArea tRecommendItemArea : cityItemAreaList) {
                        TRecommenditem tRecommendItem = recommenditemHashMap.get(tRecommendItemArea.getItemid());


                        if (tRecommendItem != null) {
                            if (Constant.WEIXIN_CALLMETHOD.equals(callmethod) && tRecommendItem.getViewtype() == Constant.TONIGHT_PROMO_VIEWTYPE) {
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
                }


                List<TRecommendItemArea> itemAreaList = recommendService.selectItemAreaByCityId(Constant.RECOMMEND_GLOBAL);

                for (TRecommendItemArea tRecommendItemArea : itemAreaList) {
                    TRecommenditem tRecommendItem = recommenditemHashMap.get(tRecommendItemArea.getItemid());


                    if (tRecommendItem != null) {

                        if (Constant.WEIXIN_CALLMETHOD.equals(callmethod) && tRecommendItem.getViewtype() == Constant.TONIGHT_PROMO_VIEWTYPE) {
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

            }

            rtnMap.put("banners", banners);
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


}
