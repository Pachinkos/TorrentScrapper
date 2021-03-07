package Scrappers;

import java.util.List;

public class ScrapController {
    private String mode;

    public static final String MODE_FULL_DUMP = "fulldump";
    public static final String MODE_ULTRA_HD_DUMP = "ultrahd";

    public ScrapController(String mode) {
        this.mode = mode;
    }

    public void start() {
        switch (mode){
            case MODE_FULL_DUMP:
                fullDump();
                break;
            case MODE_ULTRA_HD_DUMP:
                fullUltraHdDump();
                break;
            default:
                System.out.println("No mode");
                break;
        }
    }

    private void fullDump(){
        System.out.println("Dumping films");
        new Thread(() -> dumpByCategory(PctExtractor.CATEGORY_FILMS)).start();

        System.out.println("Dumping filmsHD");
        new Thread(() -> dumpByCategory(PctExtractor.CATEGORY_FILMSHD)).start();

        System.out.println("Dumping films3D");
        new Thread(() -> dumpByCategory(PctExtractor.CATEGORY_FILMS3D)).start();

        System.out.println("Dumping filmsUltraHD");
        new Thread(() -> dumpByCategory(PctExtractor.CATEGORY_FILMS_ULTRAHD)).start();
    }

    private void fullUltraHdDump(){
        System.out.println("Dumping filmsUltraHD");
        new Thread(() -> dumpByCategory(PctExtractor.CATEGORY_FILMS_ULTRAHD)).start();
    }

    private void dumpByCategory(String category) {
        int failures = 0;
        int empty = 0;
        PctExtractor pctExtractor = new PctExtractor(category);
        TorrentCompleter torrentCompleter = new TorrentCompleter();
        List<MetaTorrent> torrents;

        int i = 0;
        while (failures < 2 && empty < 2) {
            torrents = null;
            try {
                System.out.println("Dumping page " + i);
                torrents = pctExtractor.extractPageTorrents(i+1);
                System.out.println("Dumping " + torrents.size() + " " + category+ " films");
                torrents.forEach(torrent ->  torrentCompleter.buildCompleteTorrent(torrent).save());
                failures = 0;
                i++;
            }catch (Exception e){
                failures++;
                System.out.println("Dump failure");
            }finally {
                if( torrents == null || torrents.size() == 0) {
                   empty++;
                }
            }
        }
    }
}
