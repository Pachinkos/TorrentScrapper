package Scrappers;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import misc.Eventt;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PctExtractor {
    private String category;
    private HashMap<String,String> categoryMap;
    private HashMap<String,String> qualityMap;

    public static String CATEGORY_FILMSHD = "peliculas-x264-mkv";
    public static String CATEGORY_FILMS = "peliculas";
    public static String CATEGORY_FILMS3D = "peliculas-3d";
    public static String CATEGORY_FILMS_ULTRAHD = "peliculas-hd";
    public static String CATEGORY_TV_SHOWSHD = "series-hd";
    public static String CATEGORY_TV_SHOWS = "series";
    public static String CATEGORY_SEARCH = "search";

    public PctExtractor(String category) {
        this.category = category;
        categoryMap = new HashMap<>();
        qualityMap = new HashMap<>();
        categoryMap.put(CATEGORY_FILMS, "films");
        qualityMap.put(CATEGORY_FILMS, "microhd");

        categoryMap.put(CATEGORY_FILMSHD, "films");
        qualityMap.put(CATEGORY_FILMSHD, "bluerayrip");

        categoryMap.put(CATEGORY_FILMS3D, "films");
        qualityMap.put(CATEGORY_FILMS3D, "3d");

        categoryMap.put(CATEGORY_FILMS_ULTRAHD, "films");
        qualityMap.put(CATEGORY_FILMS_ULTRAHD, "ultrahd");
    }

    public void extractTorrentPages(Eventt<List<MetaTorrent>> onPageFinish){
        int page = 1;
        try{
            //String json = Jsoup.connect("https://api6.ipify.org?format=json").ignoreContentType(true).execute().body();
            while(true){
                onPageFinish.toDo(extractPageTorrents(page));
                page++;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public List<MetaTorrent> extractTorrentFromSearch(String something) throws IOException {
        String jsonSearch = Jsoup.connect("https://pctmix.com/get/result/")
                .header("Content-Type","application/x-www-form-urlencoded; charset=UTF-8")
                .header("Host", "pctmix.com")
                .requestBody("categoryIDR=&categoryID=&idioma=&calidad=&ordenar=Lo+Ultimo&inon=Descendente&s="+something)
                .ignoreContentType(true)
                .post().body().text();

        List<String> filmLandings = new ArrayList<>();
        List<String> tvshowsLandings = new ArrayList<>();

        List<MetaTorrent> torrents = new ArrayList<>();

        String landing = null;
        try{
            for (Map.Entry<String,JsonElement> entry : new JsonParser().parse(jsonSearch).getAsJsonObject().getAsJsonObject("data").getAsJsonObject("torrents").getAsJsonObject("0").entrySet()) {
                landing = "https://pctmix.com/" + entry.getValue().getAsJsonObject().get("guid").getAsString();
                if(landing.contains("/series/")){
                    tvshowsLandings.add(landing);
                }else{
                    filmLandings.add(landing);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        for(String filmLanding : filmLandings){
            try{
                torrents.add(extractTorrent(filmLanding));
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        return torrents;
    }

    public List<MetaTorrent> extractMetaTorrents(int page) throws IOException {
        List<MetaTorrent> metaTorrents = new ArrayList<>();
        System.out.println("Extracting page "+"https://pctmix.com/"+category+"/pg/"+page);
        Document doc = Jsoup.connect("https://pctmix.com/"+category+"/pg/"+page).get();
        MetaTorrent metaTorrent;
        Element aux;
        for (Element element : doc.body().getElementsByClass("pelilist").first().getElementsByTag("li")){
            metaTorrent = new MetaTorrent();
            aux = element.getElementsByTag("a").first();
            metaTorrent.setImageUrl(aux.getElementsByTag("img").first().attr("src").replace("//", "https://"));
            metaTorrent.setName(aux.getElementsByTag("h2").first().text());
            metaTorrent.setTorrentUrl(aux.attr("href"));

            metaTorrents.add(metaTorrent);
        }

        return metaTorrents;
    }

    public List<MetaTorrent> extractPageTorrents(int page) throws IOException {
        List<MetaTorrent> torrents = new ArrayList<>();
        System.out.println("Extracting page "+"https://pctmix.com/"+category+"/pg/"+page);
        Document doc = Jsoup.connect("https://pctmix.com/"+category+"/pg/"+page).get();
        for (Element element : doc.body().getElementsByClass("pelilist").first().getElementsByTag("li")){
            String landing =  element.getElementsByTag("a").attr("href");
            try{
                torrents.add(extractTorrent(landing));
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        return torrents;
    }

    public MetaTorrent extractTorrent(String filmUrl) throws IOException {
        Document doc = Jsoup.connect(filmUrl).get();
        String rawScript = doc.getElementById("btn-download-torrent").parent().getElementsByTag("script").first().html();

        MetaTorrent torrent = new MetaTorrent();
        torrent.setTorrentUrl("https://"+StringUtils.substringBetween(rawScript, "\"//", "\";"));
        torrent.setImageUrl(doc.getElementsByClass("entry-left").first().getElementsByTag("img").first().attr("src").replace("//", "https://"));
        torrent.setName(doc.getElementById("content-ficha").getElementsByClass("page-box").first().getElementsByTag("h1").first().getElementsByTag("strong").first().text());

        doc.getElementById("content-ficha").getElementsByTag("strong").forEach(element -> {
            if (element.text().endsWith("Size:")){
                torrent.setSize(element.parent().ownText());
            }
        });

        torrent.setYear(extractYearFromName(torrent.getName()));
        torrent.setName(cleanTorrentName(torrent.getName(), torrent.getYear()));
        torrent.setCategory(categoryMap.get(category));
        torrent.setQuality(qualityMap.get(category));

        return torrent;
    }

    private int extractYearFromName(String torrentName){
        String maybeYear = StringUtils.substringBetween(torrentName, "(", ")");
        int year = 0;
        try {
            int i = Integer.parseInt(maybeYear);
            if(i < 2050 && i > 1900){
                year = i;
            }
        }catch (Exception e){}

        return year;
    }

    private String cleanTorrentName(String uglyName, int year) {
        return uglyName.replace("("+year+")", "").trim();
    }
}
