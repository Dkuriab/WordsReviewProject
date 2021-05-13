package wordReview;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Translator {
    public static String getTranslate(String word) {
        try {
            URL url = new URL(
                    "https://translate.yandex.net/api/v1.5/tr/translate" +
                            "?key=trnsl.1.1.20200415T201249Z.c07c7376466d723c.ba9df976f8b2e19af2f997adb8c0f0abb984ac04" +
                            "&text=" + word +
                            "&lang=ru" +
                            "&format=plain" +
                            "&options=1"
            );

            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));
            reader.readLine();

            return reader.readLine().replaceAll("<[^>]*>", "").split("text")[0];

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
