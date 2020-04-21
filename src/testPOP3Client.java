import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Basic POP3 client test implementation
 *
 * Author: Gabriele Buivydaite
 * Date: 2020-04-17
 *
 * Possible future improvements:
 * 1) Fix entering a password : password => ********
 * 2) Not enough testing has been done, better error handling
 * 3) Improve main "thread"
 */



/**state values:
 * 0 - nothing
 * 1 - authentication
 * 2 - transaction
 */

public class testPOP3Client{

    public static void main(String[] args) throws IOException{
        int state = 0;
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String host = "pop.gmail.com";
        try {
            POP3Client pop3Client = new POP3Client(host);
            state = 1;
            while(state == 1){
                System.out.println("Enter your username:");
                String username = in.readLine();
                System.out.println("Enter your password:");
                String password = in.readLine();
                if(pop3Client.login(username, password)){
                    System.out.println("You have logged in.");
                    state = 2;
                }
            }

            while(state == 2){
                System.out.println("Press key and enter to choose an option.\n\n");
                System.out.println("1. Read a message.");
                System.out.println("2. Mark a message as deleted.");
                System.out.println("3. List all messages.");
                System.out.println("4. Total number of messages.");
                System.out.println("5. Reset all messages marked as deleted.");
                System.out.println("6. Choose a message and select number of lines to read.");
                System.out.println("7. Unique-id of a message.");
                System.out.println("8. Exit.");
                String keyboardInput = in.readLine();
                if(!Character.isLetter(keyboardInput.charAt(0))){
                    int  choice = getNumber(keyboardInput);
                    int userNumberInt = 0;
                    //System.out.println(choice);
                    String userNumberInput = null;
                    switch(choice){
                        case 1:{
                            System.out.println("You selected to read a message. Please enter a number of selected message:");
                            userNumberInput = in.readLine();
                            userNumberInt = getNumber(userNumberInput);
                            if(userNumberInt != -1)
                                pop3Client.retrieveMsg(userNumberInt);
                            System.out.println("Press any key and enter to continue.");
                            String wait = in.readLine();
                            break;
                        }
                        case 2:{
                            System.out.println("You have selected to delete an email. Please enter number of an email:");
                            userNumberInput = in.readLine();
                            userNumberInt = getNumber(userNumberInput);
                            if(userNumberInt != -1)
                                pop3Client.delete(userNumberInt);
                            System.out.println("Press any key and enter to continue.");
                            String wait = in.readLine();
                            break;
                        }
                        case 3:{
                            System.out.println("List of emails.");
                            pop3Client.list();
                            System.out.println("Press any key and enter to continue.");
                            String wait = in.readLine();
                            break;
                        }
                        case 4:{
                            pop3Client.stat();
                            System.out.println("Press any key and enter to continue.");
                            String wait = in.readLine();
                            break;
                        }
                        case 5:{
                            System.out.println("You have selected to reset all emails marked as deleted.");
                            pop3Client.reset();
                            System.out.println("Press any key and enter to continue.");
                            String wait = in.readLine();
                            break;
                        }

                        case 6:{
                            System.out.println("Which message would you like to read:");
                            userNumberInput = in.readLine();
                            userNumberInt = getNumber(userNumberInput);

                            System.out.println("How many lines of body would you like to read:");
                            String userNumberInput2 = in.readLine();
                            int userNumberInt2 = getNumber(userNumberInput);

                            if(userNumberInt != -1 && userNumberInt2 != -1)
                                pop3Client.top(userNumberInt, userNumberInt2);

                            System.out.println("Press any key and enter to continue.");
                            String wait = in.readLine();
                            break;
                        }

                        case 7:{
                            System.out.println("Type a number of a message and press enter. For all messages type 0:");
                            userNumberInput = in.readLine();
                            userNumberInt = getNumber(userNumberInput);
                            if(userNumberInt != -1)
                                pop3Client.uidl(userNumberInt);
                            System.out.println("Press any key and enter to continue.");
                            String wait = in.readLine();
                            break;
                        }

                        case 8:{
                            System.out.println("You have selected to quit.");
                            pop3Client.quit();
                            pop3Client.disconnect();
                            System.out.println("Goodbye.");
                            System.exit(0);
                        }

                        default:{
                            System.out.print("Error.Try again.");
                        }
                    }
                }
            }

        } catch(Exception out){
            System.out.println(out.getMessage());
        }

    }

    private static int getNumber(String str){
        try {
            int temp = Integer.parseInt(str);
            return temp;
        }
        catch (NumberFormatException e)
        {
            System.out.println("Wrong input. Try Again.");
            return -1;
        }
    }

}
