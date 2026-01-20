import globl.Data;
import tools.AppUtils;

class Test {

    public static void main(String[] args) {
        testAppUtils();
    }

    public static void testAppUtils() {
        //System.out.println(AppUtils.getExternalIp());
        //System.out.println(AppUtils.getInternalIp());
        Data data = new Data("NOWHERE");
        try {

            data.setMyServerPort(2555);
        } catch (Exception ERR) {
            System.out.println(ERR);
        }
        System.out.println(data.getMyServerPort());
    }

}