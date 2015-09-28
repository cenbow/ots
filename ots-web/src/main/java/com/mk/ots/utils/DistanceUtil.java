package com.mk.ots.utils;


public class DistanceUtil {
    /**
     * 计算地球上任意两点(经纬度)距离
     * 
     * @param long1
     *            第一点经度
     * @param lat1
     *            第一点纬度
     * @param long2
     *            第二点经度
     * @param lat2
     *            第二点纬度
     * @return 返回距离 单位：米
     */
    public static double distance(double long1, double lat1, double long2, double lat2) {
        double a, b, R;
        R = 6378137; // 地球半径
        lat1 = lat1 * Math.PI / 180.0;
        lat2 = lat2 * Math.PI / 180.0;
        a = lat1 - lat2;
        b = (long1 - long2) * Math.PI / 180.0;
        double d;
        double sa2, sb2;
        sa2 = Math.sin(a / 2.0);
        sb2 = Math.sin(b / 2.0);
        d = 2 * R * Math.asin(Math.sqrt(sa2 * sa2 + Math.cos(lat1) * Math.cos(lat2) * sb2 * sb2));
        return d;
    }
    
    /**
     * 计算两点之间距离
     * @param start
     * @param end
     * @return 米
     */
    public static double getDistance(double lon1, double lat1, double lon2, double lat2 /*LatLng start,LatLng end*/ ){
//        double lat1 = (Math.PI/180)*start.latitude;
//        double lat2 = (Math.PI/180)*end.latitude;
//        
//        double lon1 = (Math.PI/180)*start.longitude;
//        double lon2 = (Math.PI/180)*end.longitude;
        lat1 = (Math.PI/180)*lat1;
        lat2 = (Math.PI/180)*lat2;
        
        lon1 = (Math.PI/180)*lon1;
        lon2 = (Math.PI/180)*lon2;
        
//      double Lat1r = (Math.PI/180)*(gp1.getLatitudeE6()/1E6);
//      double Lat2r = (Math.PI/180)*(gp2.getLatitudeE6()/1E6);
//      double Lon1r = (Math.PI/180)*(gp1.getLongitudeE6()/1E6);
//      double Lon2r = (Math.PI/180)*(gp2.getLongitudeE6()/1E6);
        
        //地球半径
        double R = 6371;
        
        //两点间距离 km，如果想要米的话，结果*1000就可以了
        double d =  Math.acos(Math.sin(lat1)*Math.sin(lat2)+Math.cos(lat1)*Math.cos(lat2)*Math.cos(lon2-lon1))*R;
        
        return d*1000;
    }
    
    public static void main(String[] args) {
        double lon1 = 121.39716055492;
        lon1 = 121.397218d;  //万丽酒店
        
        double lat1 = 31.164681496697;
        lat1 = 31.165181d;
        
        double lon2 = 121.313851;
        lon2 = 121.4692688d;  // 微信地图
        
        double lat2 = 31.149234;
        lat2 = 31.2381763d;
        
        double dis = distance(lon1, lat1, lon2, lat2);
        System.out.println(dis / 1000 + "千米");
        
        float ff = calculateLineDistance(lon1, lat1, lon2, lat2);
        System.out.println(ff / 1000 + " 千米");
        
        double getDis = getDistance(lon1, lat1, lon2, lat2);
        System.out.println(getDis + "米");
        
        //1.将两个经纬度点转成投影点
        //MAMapPoint point1 = MAMapPointForCoordinate(CLLocationCoordinate2DMake(39.989612,116.480972));
        //MAMapPoint point2 = MAMapPointForCoordinate(CLLocationCoordinate2DMake(39.990347,116.480441));
        //2.计算距离
        //CLLocationDistance distance = MAMetersBetweenMapPoints(point1,point2);
    }
    
    /**
     * copy高德地图SDK中的计算距离方法
     * @param d2
     * @param d3
     * @param d4
     * @param d5
     * @return
     */
    public static float calculateLineDistance(double d2, double d3, double d4, double d5) {
        double d1 = 0.01745329251994329D;
//        double d2 = paramLatLng1.longitude;
//        double d3 = paramLatLng1.latitude;
//        double d4 = paramLatLng2.longitude;
//        double d5 = paramLatLng2.latitude;
        d2 *= 0.01745329251994329D;
        d3 *= 0.01745329251994329D;
        d4 *= 0.01745329251994329D;
        d5 *= 0.01745329251994329D;
        double d6 = Math.sin(d2);
        double d7 = Math.sin(d3);
        double d8 = Math.cos(d2);
        double d9 = Math.cos(d3);
        double d10 = Math.sin(d4);
        double d11 = Math.sin(d5);
        double d12 = Math.cos(d4);
        double d13 = Math.cos(d5);
        double[] arrayOfDouble1 = new double[3];
        double[] arrayOfDouble2 = new double[3];
        arrayOfDouble1[0] = (d9 * d8);
        arrayOfDouble1[1] = (d9 * d6);
        arrayOfDouble1[2] = d7;
        arrayOfDouble2[0] = (d13 * d12);
        arrayOfDouble2[1] = (d13 * d10);
        arrayOfDouble2[2] = d11;
        double d14 = Math.sqrt((arrayOfDouble1[0] - arrayOfDouble2[0])
                * (arrayOfDouble1[0] - arrayOfDouble2[0])
                + (arrayOfDouble1[1] - arrayOfDouble2[1])
                * (arrayOfDouble1[1] - arrayOfDouble2[1])
                + (arrayOfDouble1[2] - arrayOfDouble2[2])
                * (arrayOfDouble1[2] - arrayOfDouble2[2]));

        return (float) (Math.asin(d14 / 2.0D) * 12742001.579854401D);
    }
}
