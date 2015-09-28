#济源市  抓取数据sql
delete from s_areainfo where citycode=419001;
        /****************************** 输出行政区SQL脚本: 开始 ***********************************/
        /****************************** 输出行政区SQL脚本: 结束 ***********************************/
delete from s_landmark where citycode=419001;
        /****************************** 输出景点SQL脚本: 开始 ***********************************/
        insert into s_landmark (landmarkid, landmarkname, pinyin, ltype, lat, lng, citycode, discode) values (7569,'沁园街道','qinyuanjiedao',1,35.084732,112.940532,'419001','');
        insert into s_landmark (landmarkid, landmarkname, pinyin, ltype, lat, lng, citycode, discode) values (12400,'信尧城市广场','xinyao',1,35.087735,112.57287,'419001','');
        insert into s_landmark (landmarkid, landmarkname, pinyin, ltype, lat, lng, citycode, discode) values (12401,'大商新世纪广场','dsxsj',1,0.0,0.0,'419001','');
        insert into s_landmark (landmarkid, landmarkname, pinyin, ltype, lat, lng, citycode, discode) values (12402,'凯旋城','kaixuan',1,0.0,0.0,'419001','');
        insert into s_landmark (landmarkid, landmarkname, pinyin, ltype, lat, lng, citycode, discode) values (12399,'源园广场','yuanyuan',1,0.0,0.0,'419001','');
        insert into s_landmark (landmarkid, landmarkname, pinyin, ltype, lat, lng, citycode, discode) values (12398,'时代购物中心','shidaigouw',1,35.08957,112.94677,'419001','');
        /****************************** 输出景点SQL脚本: 结束 ***********************************/

        