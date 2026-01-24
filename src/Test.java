import globl.Data;
import tools.AppUtils;
import tools.FileService;

import java.io.File;

class Test {

    public static void main(String[] args) {
        testAppUtils();
        //testNBTParsingPlayer();
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
//        float dayTime = Float.parseFloat(AppUtils.getDataFromNBT(new File("G:\\My Servers\\Krumpli\\world\\level.dat"),"DayTime"));
//        dayTime = dayTime / 24000;
//        System.out.println((int)dayTime + "day");
        File player = new File("G:\\\\My Servers\\\\Krumpli\\\\world\\\\level.dat");
        byte[] data = FileService.readGzipBytes(player);
        byte[] gamerules = AppUtils.getCompoundFromNBT(data, "game_rules");

        String keepinv = AppUtils.getDataFromNBT(gamerules,"minecraft:keep_inventory");
        System.out.println(keepinv);
    } //todo: server settings motd && picture || motd settings & world generation settings || game_rule settings || update server file_settings || whitelist OP BANNED players editor

    public static void testNBTParsingPlayer() {
        File player = new File("G:\\My Servers\\Krumpli\\world\\playerdata\\testPlayer.dat");
        byte[] nbt = FileService.readGzipBytes(player);
//        float health = Float.parseFloat(AppUtils.getDataFromNBT(nbt,"Health"));
//        System.out.println("Health: " + health);

        //String pos = AppUtils.getDataFromNBT(nbt, "pos");
        //System.out.println("Pos: " + pos);

        String UUID = AppUtils.getDataFromNBT(nbt, "UUID");
        System.out.println("uuid: " + UUID);

        byte[] lastDeath = AppUtils.getCompoundFromNBT(nbt,"LastDeathLocation");
        System.out.println(lastDeath.length);
        String deathPos = AppUtils.getDataFromNBT(lastDeath, "pos");
        String deathWorld = AppUtils.getDataFromNBT(lastDeath, "dimension");
        System.out.println("Last Death Location: " + deathPos + " : " + deathWorld);

    }

}