package app;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class Model {
    private final int id;
    private final String name;
    private final double latitude;
    private final double longitude;
    private final String region;
    private final byte[] photo;

    public Model(int id, String name, double latitude, double longitude, String region, byte[] photo) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.region = region;
        this.photo = photo;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getRegion() {
        return region;
    }

    public byte[] getPhoto() {
        return photo;
    }
}
