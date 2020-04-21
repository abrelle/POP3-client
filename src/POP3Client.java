import com.sun.org.apache.bcel.internal.generic.POP;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.*;
import java.util.Vector;

/**
 * Basic POP3 client implementation
 *
 * Author Gabriele Buivydaite
 * Date: 2020-04-17
 *
 * Possible future improvements:
 * 1) Add an option to choose a host
 * 2) Not enough testing has been done, better error handling
 * 3) Receiving formatted messages
 */


public class POP3Client {

    // private String host = "pop.gmail.com";

    public static final int DEFAULT_SSL_PORT = 995;
    public static final int DEFAULT_PORT = 110; //not used right now, but is in RFC
    private String username;
    private String password;
    private Socket socketSSL = null;
    private String host;
    private BufferedReader reader;
    private BufferedWriter writer;


    POP3Client(String host, int port) throws Exception {  //not used right now, only 995 port

        if(port != DEFAULT_PORT && port != DEFAULT_SSL_PORT){
            System.out.println("Wrong port number.");
            throw new Exception("PORT error.");
        }
        SocketFactory socketFactory = SSLSocketFactory.getDefault();
        socketSSL = socketFactory.createSocket(host, port);
        reader = new BufferedReader(
                new InputStreamReader(socketSSL.getInputStream()));
        writer = new BufferedWriter(
                new OutputStreamWriter(socketSSL.getOutputStream()));
        checkServer();
    }

    POP3Client(String host) throws Exception {

        SocketFactory socketFactory = SSLSocketFactory.getDefault();
        socketSSL = socketFactory.createSocket(host, DEFAULT_SSL_PORT);
        reader = new BufferedReader(
                new InputStreamReader(socketSSL.getInputStream()));
        writer = new BufferedWriter(
                new OutputStreamWriter(socketSSL.getOutputStream()));
        try {
            checkServer();
        }
        catch(Exception e){
            throw e;
        }
    }


    public void checkServer() throws Exception {        //prints if connection is established
        String response = reader.readLine();
        if(response.startsWith("+OK"))
            System.out.println("Connected to POP3 server successfully.");
        else
            throw new Exception("Could not connect to server");
    }

    public boolean is_connected() {
        return this.socketSSL != null && this.socketSSL.isConnected();
    }

    public void disconnect() throws IOException {
        if (this.is_connected()){
            this.socketSSL.close();
            reader = null;
            writer = null;
        }

        System.out.println("You are disconnected.");
    }



    public boolean login(String usr, String pass) throws IOException {
        try{
            this.user(usr);
            this.pass(pass);
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }

    }


    private String sendCommand(String command) throws IOException {
        writer.write(command + "\n");
        writer.flush();
        return reader.readLine();
    }


//---------------------------------------------------------------------------------------------------------------------
    public void user(String usr) throws Exception {   //USER command passes username and checks if +OK or -ERR
        String response = sendCommand("USER " + usr);
        /*if(response.startsWith("+OK"))
            System.out.println("Correct username.");   //pointless part
        else
            throw new Exception("Wrong username.");*/

    }

    public void pass(String pass) throws Exception {   //PASS command passes password and checks if +OK or -ERR
        String response = sendCommand("PASS " + pass);
        if(response.startsWith("+OK"))
            System.out.println("Correct password.");
        else
            throw new Exception("Wrong password.");

    }

    public void stat() throws IOException{      //STAT command returns 1)+OK [number of mails] [number of octets], 2) -ERR

        String response =  sendCommand("Stat");
        String [] args;
        if(response.startsWith("+OK")) {
            args = response.split(" ", 0);   //parse string into three parts args[] = {"+ok", "number of mails", "number of octets"}
            System.out.println("Number of mails: " + args[1]);
        }
        else
           System.out.println("Could not get number of mails.");

    }

    public void quit() throws IOException {

            String response;
            response = sendCommand("QUIT");
            if(response.startsWith("+OK"))
                System.out.println("Logged out.");
            else
                System.out.println("Haven't logged out.");

    }

    public void noop() throws IOException {   //NOOP basically does nothing, returns +OK or -ERR
        String response;
        response = sendCommand("NOOP");
        System.out.println(response);
    }

    public void delete(int num) throws IOException {  //DELE command marks message as deleted, but does not delete it until UPDATE state
                                                       // returns +OK if successful, -ERR if not
        if(num > 0){
            String response;
            response = sendCommand("DELE " + num);
            if(response.startsWith("+OK"))
                System.out.println("Message marked as deleted.");
            else
                System.out.println("Message could not be deleted.");
        }
        else
            System.out.println("Wrong number input");

    }

    public void reset() throws IOException {    //RSET command - any message marked as deleted is unmarked  +OK if success, -ERR otherwise
        String response;
        response = sendCommand("RSET");
        if(response.startsWith("+OK"))
            System.out.println("All messages are unmarked.");
        else
            System.out.println("Could not reset messages.");
    }

    private Vector<String> getMultiLines(String command) {
        Vector<String> lines = new Vector<String>();
        try {
            String result = sendCommand(command);
            lines.addElement(result);

            if (!result.startsWith("+OK")){
                System.out.println(result);
                return lines;                          //single line
            }

            boolean ifLinesLeft = true;
            String newLine = null;
            while (ifLinesLeft) {
                newLine = reader.readLine();
                lines.addElement(newLine);

                if (".".equals(newLine)) {//when sending multi lines the last line is CRLF.CRLF
                    ifLinesLeft = false;
                    return lines;
                }
                else
                    System.out.println(newLine);

            }

        } catch (Exception e) {
        }
        return lines;
    }

    public void retrieveMsg(int num) throws IOException {
        if(num > 0){
           Vector<String> multi = getMultiLines("RETR " + num);
        }
        else
            System.out.println("Wrong number input");
    }

    public void list() throws IOException {
        Vector<String> multi = getMultiLines("LIST");

    }

    public void top(int message, int lines){
        if(lines > 0 && message > 0){
            Vector<String> multi = getMultiLines("TOP " + message + " " + lines);
        }
        else
            System.out.println("Wrong number input");
    }

    public void uidl(int num) throws IOException {
        if(num == 0){
            Vector<String> multi = getMultiLines("UIDL");
        }
        else if(num > 0){
            String response = sendCommand("UIDL " + num);
            String [] args;
            if(response.startsWith("+OK")) {
                args = response.split(" ", 0);   //parse string into three parts args[] = {"+ok", "index", "unique id"}
                System.out.println("Unique ID of a message: " + args[2]);
            }
            else
                System.out.println("Could not get unique ID of a message.");
        }
        else
            System.out.println("Wrong number input");
    }

}
