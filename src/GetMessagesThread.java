import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import entity.Message;
import entity.Messages;
import entity.MyUrl;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class GetMessagesThread implements Runnable {
    private Gson gson = new GsonBuilder().create();
    private int indexRoom;
    private int indexPrivate;

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                URL url = new URL(MyUrl.getURL()
                        + "/get?"
                        + "user=" + CurrentSession.getSession().getCurrentUser().getLogin()
                        + "&indexRoom=" + indexRoom
                        + "&indexPrivate=" + indexPrivate);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = connection.getInputStream();

                try {
                    byte[] buf = BodyToArray.requestBodyToArray(inputStream);
                    String strBuf = new String(buf, StandardCharsets.UTF_8);

                    Messages list = gson.fromJson(strBuf, Messages.class);
                    if (list != null) {
                        for (Message m : list.getMessages()) {
                            System.out.println(m);
                            indexRoom++;
                            indexPrivate++;
                        }
                    }
                } finally {
                    inputStream.close();
                }

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // restore interrupted status
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}