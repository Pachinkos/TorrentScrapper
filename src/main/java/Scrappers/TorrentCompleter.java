package Scrappers;

import models.Torrent;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class TorrentCompleter {

    public Torrent buildCompleteTorrent(MetaTorrent metaTorrent){
        Torrent torrent = new Torrent(metaTorrent);
        torrent.setImage(downloadFile(metaTorrent.getImageUrl()));
        torrent.setTorrent(downloadFile(metaTorrent.getTorrentUrl()));

        return torrent;
    }

    public byte[] downloadFile(String fileUrl){
        byte[] fileBytes = new byte[0];
        try {
            URLConnection connection = new URL(fileUrl).openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            connection.connect();
            try (InputStream in = connection.getInputStream()) {
                fileBytes = IOUtils.toByteArray(in);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return fileBytes;
    }
}
