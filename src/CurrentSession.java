import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import entity.MyUrl;
import entity.User;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class CurrentSession {
    private static final String FILE_PATH = "C:\\Users\\Tsyba\\Downloads\\Chat\\src\\session.json";
    private Gson gson = new GsonBuilder().create();

    private static CurrentSession session;
    private static User currentUser;

    private CurrentSession() {
        if (checkPermission()) {
            connect();
        } else scanInputData();
    }

    public static synchronized CurrentSession getSession() {
        if (session == null) {
            return session = new CurrentSession();
        } else
            return session;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Если пустой файл "(Your source path) ... \Chat\src\authorization\session.json"
     * Тогда считываем входящие данные (логин и пароль), потом конектимся
     **/
    private void scanInputData() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter your login:");
        currentUser.setLogin(scanner.nextLine());

        System.out.println("Enter your password:");
        currentUser.setPassword(scanner.nextLine());

        connect();
    }

    /**
     * Проверяем файл "(Your source path) ... \Chat\src\authorization\session.json"
     * на наличность данных
     **/
    private boolean checkPermission() {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(FILE_PATH)))) {
            currentUser = gson.fromJson(bufferedReader, User.class);
            if (currentUser == null) currentUser = new User();
            return currentUser.getLogin() != null && currentUser.getPassword() != null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Завершаем текущую сесию пользователя...
     * в 2 этапа
     * 1. удаляем данные с файла поьзователя (deleteFromFile)
     * 2. удаляемся с сервера =) убираем статус онлайна... (quitFromServer)
     **/
    public void closeSession() {
        disconnect();
        currentUser = null;
        writeToFile();
        session = null;
    }

    /**
     * Если в файле "(Your source path) ... \Chat\src\authorization\session.json"
     * есть данные, то конектимся и получаем свежые данные о юзере (мало ли что-то изменилось + данные на сервере храняться)
     **/
    private void connect() {
        try {
            String params = "?"
                    + "login=" + currentUser.getLogin()
                    + "&"
                    + "password=" + currentUser.getPassword();

            URL url = new URL(MyUrl.getURL() + "/auth" + params);

            String response = getResponse(url);
            currentUser = gson.fromJson(response, User.class);
            writeToFile();
            System.out.println("You joined as: " + currentUser.getLogin());

        } catch (IOException e) {
            System.out.println(e.getMessage());
            scanInputData();
        }
    }

    /**
     * Отключаемся от сервера
     */
    private void disconnect() {
        try {
            URL url = new URL(MyUrl.getURL()
                    + "/logout?"
                    + "login=" + CurrentSession.getSession().getCurrentUser().getLogin());
            String response = getResponse(url);
            System.out.println(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Disconnect/Connect from server and receive string response
     *
     * @param url - our URL
     * @return String response
     * @throws IOException if somethin went wrong
     */
    private String getResponse(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        InputStream inputStream = connection.getInputStream();
        byte[] buf = BodyToArray.requestBodyToArray(inputStream);
        return new String(buf, StandardCharsets.UTF_8);
    }

    public void changeRoom(String room) {
        try {
            URL url = new URL(MyUrl.getURL()
                    + "/changeroom?"
                    + "room=" + room
                    + "&login=" + CurrentSession.getSession().getCurrentUser().getLogin());
            String response = getResponse(url);
            System.out.println(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showUsers(String status) {
        try {
            URL url = new URL(MyUrl.getURL()
                    + "/showusers?"
                    + "usersStatus=" + status);
            String response = getResponse(url);
            System.out.println(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showRooms(){
        try {
            URL url = new URL(MyUrl.getURL()
                    + "/showrooms?all=true");
            String response = getResponse(url);
            System.out.println(response);
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    private void writeToFile() {
        try (Writer writer = new FileWriter(FILE_PATH)) {
            gson.toJson(currentUser, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
