package models;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "torrent")
public class Torrent extends BaseModel implements BaseTorrent {
    private String name;
    private String type;
    private String category;
    private String quality;
    private byte[] image;
    private byte[] torrent;
    private String size;
    private int year;

    public Torrent() {
    }

    public Torrent(BaseTorrent t) {
        name = t.getName();
        category = t.getCategory();
        quality = t.getQuality();
        size = t.getSize();
        year = t.getYear();
    }

    @Override
    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public byte[] getTorrent() {
        return torrent;
    }

    public void setTorrent(byte[] torrent) {
        this.torrent = torrent;
    }

    @Override
    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
