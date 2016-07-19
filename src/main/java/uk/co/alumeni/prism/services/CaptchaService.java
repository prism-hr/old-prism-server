package uk.co.alumeni.prism.services;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.fileupload.util.Streams;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

@Service
public class CaptchaService {

    private static final Logger logger = getLogger(CaptchaService.class);

    public boolean verifyCaptcha(String captchaResponse) {
        HttpClient captchaClient = HttpClientBuilder.create().build();
        try {
            HttpPost httpPost = new HttpPost("https://www.google.com/recaptcha/api/siteverify");
            List<NameValuePair> captchaParameters = Arrays.asList(
                    new BasicNameValuePair("secret", "6LeUOSITAAAAAB3I1E80w-w5x-Ic2hvClIX8Zu4v"),
                    new BasicNameValuePair("response", captchaResponse));
            httpPost.setEntity(new UrlEncodedFormEntity(captchaParameters));
            InputStream response = captchaClient.execute(httpPost).getEntity().getContent();
            Type mapType = new TypeToken<Map<String, Object>>() {
            }.getType();
            Map<String, Object> responseMap = new Gson().fromJson(Streams.asString(response), mapType);
            Boolean success = (Boolean) responseMap.get("success");
            if (!success) {
                List<String> codes = (List<String>) responseMap.get("error-codes");
                logger.error("Captcha errors: " + codes.stream().collect(Collectors.joining(", ")));
            }
            return success;
        } catch (IOException e) {
            throw new Error("Could not connect to Google");
        }

    }
}
