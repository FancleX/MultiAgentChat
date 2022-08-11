package com.neu.client.ui;

import com.neu.client.communication.CommunicationAPI;
import com.neu.client.communication.CommunicationAPIImpl;
import com.neu.client.restClient.RestClient;
import com.neu.client.sharableResource.SharableResource;
import com.neu.node.Node;
import com.neu.node.NodeChannel;
import com.neu.protocol.GeneralType;
import com.neu.protocol.leaderElectionProtocol.LeaderElectionProtocol;
import com.neu.protocol.leaderElectionProtocol.LeaderElectionType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * UI for user to interact. Should be located in a different thread.
 * Use system out print to distinguish the output with system log.
 */
@Slf4j
public class UI implements Runnable {

    private final CommunicationAPI writer;

    // for user signup and login
    private final RestClient restClient;

    private final UIHelper uiHelper;

    private final BufferedReader bufferedReader;

    public UI() {
        this.writer = new CommunicationAPIImpl();
        this.restClient = new RestClient();
        this.bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        this.uiHelper = new UIHelper();
    }


    public void userInterface() throws IOException {
        uiHelper.printTitle("Welcome to P2P multi agent chat application");
        leve1MenuEventHandler();

    }



    /**
     * The level 1 menu should take care of guidelines for user signup and login.
     */
    public void level1Menu() {
        uiHelper.printTitle("Signup / Login");
        uiHelper.printSystemMessage("Please choose a command number to continue");
        uiHelper.printSystemMessage("<1> signup");
        uiHelper.printSystemMessage("<2> login");
        uiHelper.printSystemMessage("<3> exit");
        uiHelper.printLineBreaker();
    }

    public void leve1MenuEventHandler() throws IOException {
        // print menu
        level1Menu();
        // get user input
        int rightInput = getRightInput(1, 3);
        switch (rightInput) {
            // signup
            case 1:
                signupHandler();
                uiHelper.printEnd();
                // signup successfully call login
                loginHandler();
                uiHelper.printEnd();
                break;
            // login
            case 2:
                loginHandler();
                uiHelper.printEnd();
                break;
            // exit
            case 3:
                onExit();
                break;
        }
        uiHelper.printEnd();
    }

    public void level2Menu() {

    }


    public void signupHandler() throws IOException {
        uiHelper.printTitle("Signup");
        // get email
        String email = getRightInput(0, "email");
        uiHelper.printLineBreaker();
        // get password
        String password = getRightInput(1, "password");
        uiHelper.printLineBreaker();
        // get nickname
        String nickname = getRightInput(2, "your nickname");
        uiHelper.printLineBreaker();
        try {
            // call signup
            String response = restClient.signup(nickname, email, password);
            uiHelper.printSystemMessage(response);
        } catch (HttpClientErrorException hcee) {
            uiHelper.printSystemMessage(hcee.getResponseBodyAsString());
            // recall the method to restart
            signupHandler();
        } catch (HttpServerErrorException | ResourceAccessException e) {
            log.error("Network or server traffic");
            uiHelper.printSystemMessage("Server is unreachable now, please use the application later");
            onExit();
        }
    }

    public void loginHandler() throws IOException {
        uiHelper.printTitle("Login");
        // get email
        String email = getRightInput(0, "email");
        uiHelper.printLineBreaker();
        // get password
        String password = getRightInput(1, "password");
        uiHelper.printLineBreaker();
        // hostname
        String myHostname = SharableResource.myHostname;
        // port
        int myPort = SharableResource.myPort;
        try {
            // call login
            Map<String, Object> login = restClient.login(email, password, myHostname, myPort);
            // parse the response of login
            Long id = Long.valueOf((Integer) login.get("id"));
            String nickname = (String) login.get("nickname");
            String hostname = (String) login.get("hostname");
            int port = (int) login.get("port");
            uiHelper.printSystemMessage("Welcome back " + nickname);
            // construct my node info
            SharableResource.myNode = new Node(id, nickname, false, SharableResource.myHostname, SharableResource.myPort);
            uiHelper.printSystemMessage("Connecting to the p2p network ...");
            // check if the node is the leader node
            if (hostname.equals(SharableResource.serverHostname) && port == SharableResource.serverPort) {
                log.info("The node has become the leader node");
                SharableResource.myNode.setLeader(true);
                try {
                    SharableResource.server = SharableResource.group.connect(hostname, port);
                    LeaderElectionProtocol leaderElectionProtocol = new LeaderElectionProtocol(GeneralType.LEADER_ELECTION, LeaderElectionType.CLIENT_REPORT, SharableResource.myNode);
                    SharableResource.server.writeAndFlush(leaderElectionProtocol);
                    SharableResource.server.close();
                    System.out.println(SharableResource.server);
                } catch (SocketTimeoutException e) {
                    log.error("Failed to connect to server p2p service");
                    restClient.logout(id);
                    uiHelper.printSystemMessage("Server service is current unavailable, please try later");
                    onExit();
                }
            } else {
                // TODO: connect to the leader node and call join and leave api
            }


        } catch (HttpClientErrorException hcee) {
            uiHelper.printSystemMessage(hcee.getResponseBodyAsString());
            // recall the method to restart
            loginHandler();
        } catch (HttpServerErrorException | ResourceAccessException e) {
            log.error("Network or server traffic");
            uiHelper.printSystemMessage("Server is unreachable now, please use the application later");
            onExit();
        }

    }

    /**
     * Loop until get right input from user.
     *
     * @param status status code for call different methods
     * 0 -> email check; 1 -> password check; 2 -> general string check
     * @param guidelines to guide user input
     * @return the valid input from user
     */
    public String getRightInput(int status, String guidelines) throws IOException {
        switch (status) {
            case 0:
                while (true) {
                    uiHelper.printSystemMessage("Please input your " + guidelines + ": ");
                    String email = bufferedReader.readLine();
                    if (uiHelper.checkEmail(email)) {
                        return email;
                    }
                    uiHelper.printSystemMessage("Invalid format of " + guidelines + ", please check and input again");
                }
            case 1:
                while (true) {
                    uiHelper.printSystemMessage("Please input your " + guidelines + ": ");
                    String password = bufferedReader.readLine();
                    if (uiHelper.checkPassword(password)) {
                        return password;
                    }
                    uiHelper.printSystemMessage("Invalid format of " + guidelines + ", please check and input again");
                }
            case 2:
                while (true) {
                    uiHelper.printSystemMessage("Please input "  + guidelines + ": ");
                    String input = bufferedReader.readLine();
                    if (uiHelper.checkInputString(input)) {
                        return input;
                    }
                    uiHelper.printSystemMessage("Invalid format of "  + guidelines + ", please check and input again");
                }
            default:
                return null;
        }
    }


    /**
     * Loop until the user Integer input a valid input or exit
     * return the right input
     *
     * @param lowerBound lower bound
     * @param upperBound upper bound
     * @return the correct input
     * @throws IOException io of system terminal
     */
    public int getRightInput(int lowerBound, int upperBound) throws IOException {
        while (true) {
            String input = bufferedReader.readLine();
            if (uiHelper.integerInputChecker(input, lowerBound, upperBound)) {
                return Integer.parseInt(input);
            } else {
                uiHelper.printSystemMessage("Invalid input, please try again");
            }
        }
    }

    /**
     * Display all online users.
     */
    public void displayOnlineUsers() {
        uiHelper.printTitle("Current Online Users");
        Iterator<NodeChannel> allNodes = SharableResource.liveNodeList.getAllNodes();
        while (allNodes.hasNext()) {
            NodeChannel next = allNodes.next();
            System.out.println("[" + next.getId() + "] " + next.getNickname());
        }
        uiHelper.printEnd();
    }

    public static void main(String[] args) {
        UIHelper uiHelper1 = new UIHelper();
        uiHelper1.printTitle("Current Online Users");
        uiHelper1.printTitle("Login");
    }


    public void test() {
        Map<String, Object> login = restClient.login("123@gmail.com", "123456", SharableResource.myHostname, SharableResource.myPort);
        Long id = Long.valueOf((Integer) login.get("id"));
        String nickname = (String) login.get("nickname");
        System.out.println("My id: " + id);
        System.out.println("My nickname: " + nickname);
        // parse hostname and port
        String hostname = (String) login.get("hostname");
        int port = (int) login.get("port");
        System.out.println("hostname: " + hostname);
        System.out.println("port: " + port);



        // if it is the sever info
        if (hostname.equals(SharableResource.serverHostname) && port == SharableResource.serverPort) {
            System.out.println("Current node is leader node");
            SharableResource.myNode = new Node(id, nickname, true, SharableResource.myHostname, SharableResource.myPort);
            try {
                SharableResource.server = SharableResource.group.connect(hostname, port);
                LeaderElectionProtocol leaderElectionProtocol = new LeaderElectionProtocol(GeneralType.LEADER_ELECTION, LeaderElectionType.CLIENT_REPORT, SharableResource.myNode);
                SharableResource.server.writeAndFlush(leaderElectionProtocol);
                SharableResource.server.close();
                System.out.println(SharableResource.server);
            } catch (SocketTimeoutException e) {
                throw new RuntimeException(e);
            }
        }

    }

    /**
     * For user exit the program, this should shut down the whole system.
     */
    public void onExit() {
        log.info("System exited successfully");
        uiHelper.printSystemMessage("System exited ...");
        System.exit(0);
    }

    @Override
    public void run() {
        try {
            userInterface();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}

/**
 * The helper class of ui to do some operations.
 *
 */
class UIHelper {

    String emailRegexPattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
            + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";

    /**
     * A general check input string is neither empty nor composed of spaces.
     *
     * @param str the string to be checked
     * @return true if the string passed the check (neither empty nor composed of spaces), otherwise false
     */
    boolean checkInputString(String str) {
        return !str.isBlank();
    }

    /**
     * Check password is not empty, all spaces, or less than 6 characters.
     *
     * @param password the password to be checked
     * @return true if the password is valid, otherwise false
     */
    boolean checkPassword(String password) {
        return checkInputString(password) && password.length() > 5;
    }

    /**
     * Check email if it matches the regex pattern.
     *
     * @param email the email to be checked
     * @return true if the email matched, otherwise false
     */
    boolean checkEmail(String email) {
        return Pattern
                .compile(emailRegexPattern)
                .matcher(email)
                .matches();
    }

    /**
     * Check integer input to test if they are valid and in the right interval.
     *
     * @param input the input string
     * @param lowerBound the lower bound
     * @param upperBound the upper bound
     * @return true if the string can be parsed to an integer and in the interval [lower bound, upper bound], otherwise false
     */
    boolean integerInputChecker(String input, int lowerBound, int upperBound) {
        boolean preCheck = checkInputString(input);
        if (!preCheck) {
            return false;
        }
        int parseInt;
        try {
            parseInt = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return false;
        }
        return parseInt >= lowerBound && parseInt <= upperBound;
    }

    /**
     * Print the title of a menu with a 50 length string, if the string is less than the length will be filled with "=".
     *
     * @param title the title to be printed
     */
    void printTitle(String title) {
        // wrap the title by two spaces
        String wrappedTitle = " " + title + " ";
        int length = wrappedTitle.length();
        String result;
        if (length < 50) {
            // filled the rest spots with "="
            int restSpots = 50 - length;
            int half = restSpots / 2;
            result = "=".repeat(half) + wrappedTitle + "=".repeat(restSpots - half);
        } else {
            result = wrappedTitle;
        }
        System.out.println(result);
    }

    /**
     * Print the end of the title.
     */
    void printEnd() {
        System.out.println("=".repeat(50));
    }

    /**
     * Print the line breaker.
     */
    void printLineBreaker() {
        System.out.println("-".repeat(50));
    }

    /**
     * Print client system message to user.
     *
     * @param msg message
     */
    void printSystemMessage(String msg) {
        System.out.println("[System message]: " + msg);
    }

}
