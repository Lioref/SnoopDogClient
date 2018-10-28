package SnoopDogClient;

import javax.sound.sampled.*;
import java.io.*;
import java.net.Socket;

public class ClientConnection extends Thread {

    Socket socket;
    DataInputStream inFromServer;
    DataOutputStream outToServer;
    boolean shouldRun = true;
    Client client;


    public ClientConnection(Socket socket, Client client) {
        this.socket = socket;
        this.client = client;
    }


    public void sendStringToServer(String text) {
        try {
            outToServer.writeBytes(text + '\n');
            outToServer.flush();
        } catch (IOException e) {
            e.printStackTrace();
            close();
        }
    }

    public void close() {
        try {
            outToServer.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseInput() {

    }


    @Override
    public void run() {
        try {
            AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 2, 4, 44100, false);
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            final SourceDataLine sourceLine = (SourceDataLine) AudioSystem.getLine(info);
            sourceLine.open(format);


            this.outToServer = new DataOutputStream(socket.getOutputStream());
            this.inFromServer = new DataInputStream(socket.getInputStream());
            while (shouldRun) {
                try {
                    while (inFromServer.available() == -1) { /* No message from server */
                        Thread.sleep(1);
                    }

                    /* Parse input */
                    int input = inFromServer.readInt();
                    if (input == 0) { /* A message is being sent */
                        /* Display bark message however convenient */
                        System.out.println("Bark!");
                    }
                    else if (input == 1) { /* Audio is being sent */
                        sourceLine.start(); /* starting an already started line does nothing */
                        int length = inFromServer.readInt(); /* Length of byteArray is sent first */
                        byte[] audio = new byte[length];
                        inFromServer.read(audio, 0, length);
                        sourceLine.write(audio, 0, length);

                    }

                } catch (IOException e) { /* clean up */
                    e.printStackTrace();
                    sourceLine.close();
                    close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

