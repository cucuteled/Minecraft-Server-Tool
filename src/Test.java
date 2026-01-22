import globl.Data;
import tools.AppUtils;

import java.io.File;

class Test {

    public static void main(String[] args) {
        testAppUtils();
    }

    public static void testAppUtils() {
        //System.out.println(AppUtils.getExternalIp());
        //System.out.println(AppUtils.getInternalIp());
//        Data data = new Data("NOWHERE");
//        try {
//
//            data.setMyServerPort(2555);
//        } catch (Exception ERR) {
//            System.out.println(ERR);
//        }
//        System.out.println(data.getMyServerPort());
        // 24000 tick / day
        float dayTime = Float.parseFloat(AppUtils.getDataFromNBT(new File("G:\\My Servers\\Krumpli\\world\\level.dat"),"DayTime"));
        dayTime = dayTime / 24000;
        System.out.println((int)dayTime + "day");
    }

}