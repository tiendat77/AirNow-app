package uit.thesis.airnow.retrofit;

public class APIUtils {
    public static final String Base_Url = "http://13.59.35.198:8000/api/";

    public static DataClient getData() {
        return RetrofitClient.getClient(Base_Url).create(DataClient.class);

    }//nhan va gui data
}
