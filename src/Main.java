
import entity.Message;
import entity.MyUrl;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        CurrentSession.getSession();

        Thread thread = startGettingMsg();

        Scanner scanner = new Scanner(System.in);
        System.out.println("To see commands write '/help' and push enter.");
        while (scanner.hasNextLine()) {

            String tag = scanner.nextLine();

            if (tag.equals("/logout")) {
                thread.interrupt();
                logout();
                thread = startGettingMsg();
            } else if (tag.equals("/help")) {
                showHelp();
            } else if (tag.equals("/exit")) {
                exitFromProgram(thread);
                break;
            } else if (checkPrivateMsg(tag)) {
                sendPrivateMsg(tag);
            } else if (checkChangeRoom(tag)) {
                requestToChangeRoom(tag);
            } else if (checkShowUsers(tag)) {
                requestForUsersStatus(tag);
            } else if (checkShowRooms(tag)) {
                requestForRooms();
            }
        }
        scanner.close();
    }

    private static void showHelp() {
        String help =
                "/p 'login of person you want to write' 'text of message'   - Example: '/p admin hello world'\n" +
                "/change-room 'room name'                                   - change your channel. Example: '/change-room music'\n" +
                "/show-user 'status'                                        - show users with input status 'online/offline/all'. Example: '/show-users online'\n" +
                "/show-rooms                                                - will show you all exist rooms.\n" +
                "/logout                                                    - will logout you from server but u still can get back.\n" +
                "/exit                                                      - exit from the program\n";
        System.out.println(help);
    }

    private static void requestForRooms() {
        CurrentSession.getSession().showRooms();
    }

    private static boolean checkShowRooms(String tag) {
        if (tag.length() >= 11)
            return tag.substring(0, 11).equals("/show-rooms");
        else return false;
    }

    private static void requestForUsersStatus(String tag) {
        CurrentSession.getSession().showUsers(tag.substring(12));
    }

    private static boolean checkShowUsers(String tag) {
        if (tag.length() >= 12)
            return tag.substring(0, 12).equals("/show-users ");
        else return false;
    }

    private static Thread startGettingMsg() {
        Thread thread = new Thread(new GetMessagesThread());
        thread.setDaemon(true);
        thread.start();
        return thread;
    }

    private static void requestToChangeRoom(String tag) {
        CurrentSession.getSession().changeRoom(tag.substring(13));
    }

    private static boolean checkChangeRoom(String tag) {
        if (tag.length() >= 13)
            return tag.substring(0, 13).equals("/change-room ");
        else return false;
    }

    private static void sendPrivateMsg(String tag) {
        Message message = new Message();
        String to = tag.substring(3, tag.indexOf(" ", 3));
        message.setMsgTxt(tag.substring(tag.indexOf(to) + to.length() + 1));
        message.setFrom(CurrentSession.getSession().getCurrentUser());
        message.setTo(to);
        try {
            message.send(MyUrl.getURL() + "/add");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean checkPrivateMsg(String tag) {
        return tag.substring(0, 3).equals("/p ");
    }

    private static void exitFromProgram(Thread thread) {
        CurrentSession.getSession().closeSession();
        thread.interrupt();
    }

    private static void logout() {
        CurrentSession.getSession().closeSession();
        CurrentSession.getSession();
    }
}
