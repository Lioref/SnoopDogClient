package SnoopDogClient;

import java.io.*;
import java.net.*;
import java.util.Scanner;

import static java.lang.Thread.*;

public class Client {


    ClientConnection cc;
    private static final int PORTNUM = 3333;

    public static void main(String[] args) {
        new Client();
    }

    public Client(){
        try {
            Socket socket = new Socket("localHost", PORTNUM);
            cc = new ClientConnection(socket, this);
            cc.start();

            listenForInput();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listenForInput() {
        Scanner console = new Scanner(System.in);
        while (true) {
            while(!console.hasNextLine()) {
                try {
                    sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            String input = console.nextLine();
            if (input.toLowerCase().equals("quit")) {
                break;
            }
            cc.sendStringToServer(input); /* sends typed string to server */
        }
        cc.close();
    }
}
