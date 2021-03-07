import Scrappers.ScrapController;

public class Main {
    public static void main(String[] args) {
        System.out.println("HelloScrapper");

        System.out.println("FullDump started");
        //new ScrapController(ScrapController.MODE_FULL_DUMP).start();
        new ScrapController(ScrapController.MODE_ULTRA_HD_DUMP).start();
        System.out.println("FullDump finished");
    }
}