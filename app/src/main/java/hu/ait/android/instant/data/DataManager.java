package hu.ait.android.instant.data;

public class DataManager {

    private DataManager() {
        data = "";
    }

    private static DataManager instance = null;

    public static DataManager getInstance() {
        if(instance == null) {
            instance = new DataManager();
        }

        return instance;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    private String data;

    public void destroy() {
        data = "";
    }
}
