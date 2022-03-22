import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHeaders;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;


public class Main {

    public static final String NASA_URI = "https://api.nasa.gov/planetary/apod?api_key=3JM61ZvhAYYfwhObkFveRqCOLhFUmDCzAGO5uD9x";
    public static ObjectMapper mapper = new ObjectMapper();
    public static final String SAVE_CATALOG = "E:/Учеба/";

    public static void main(String[] args) throws IOException {

        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)    // максимальное время ожидание подключения к серверу
                        .setSocketTimeout(30000)    // максимальное время ожидания получения данных
                        .setRedirectsEnabled(false) // возможность следовать редиректу в ответе
                        .build())
                .build();

        HttpGet request = new HttpGet(NASA_URI);
        request.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());

        CloseableHttpResponse response = httpClient.execute(request);
        Arrays.stream(response.getAllHeaders()).forEach(System.out::println);

        Nasa nasaJsonFile = mapper.readValue(response.getEntity().getContent(), new TypeReference<>() { });
        System.out.println(nasaJsonFile);

        URL patch = new URL(nasaJsonFile.getUrl());
        String nameSaveFile = new File(patch.getPath()).getName();

        try (BufferedInputStream inputStream = new BufferedInputStream(patch.openStream());
             FileOutputStream file = new FileOutputStream(SAVE_CATALOG + nameSaveFile)) {
            byte data[] = new byte[1024];
            int byteContent;
            while ((byteContent = inputStream.read(data, 0, 1024)) != -1) {
                file.write(data, 0, byteContent);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}