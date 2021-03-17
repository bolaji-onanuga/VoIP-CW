package com.company;

/*
 bolaji onanuga 14/3/2021
 */

import CMPC3M06.AudioPlayer;
import uk.ac.uea.cmp.voip.DatagramSocket2;
import uk.ac.uea.cmp.voip.DatagramSocket3;
import uk.ac.uea.cmp.voip.DatagramSocket4;
import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;
import java.lang.Runnable;
import java.net.*;
import java.nio.ByteBuffer;


public class ThreadedSoundReceiver implements Runnable {

    static DatagramSocket receiving_socket;
    NetworkCoursework.SocketType socketType;

    public ThreadedSoundReceiver(NetworkCoursework.SocketType socketType){
        this.socketType = socketType;
    }

    public void start(){
        Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run(){
        //Initialize the port we are receiving on
        int port = 55555;

        //Open a  socket to receive from

        // Create & initialise a AudioRecorder
        AudioPlayer player = null;
        try {
            player = new AudioPlayer();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }

        //Create DatagramPacket to put received data into.
        byte[] buffer = new byte[512];
        DatagramPacket packet = new DatagramPacket(buffer, 0, 512);
        int cipher = 196157828;

        switch (socketType){
            case Type1:
                //Open the socket to receive from
                try {
                    receiving_socket = new DatagramSocket(port);
                } catch (SocketException e) {
                    System.out.println("ERROR: TextReceiver: Could not open socket to receive from.");
                    e.printStackTrace();
                    System.exit(0);
                }

                try {
                    receiving_socket.receive(packet);
                    player.playBlock(packet.getData());
                } catch (IOException e) {
                    System.out.println("ERROR: TextReceiver: Some random IO error occured!");
                    e.printStackTrace();
                }

                receiving_socket.close();
                break;


            case Type2: //DatagramSocket2
                //Open the socket to receive from
                try {
                    receiving_socket = new DatagramSocket2(port);
                } catch (SocketException e) {
                    System.out.println("ERROR: TextReceiver: Could not open socket to receive from.");
                    e.printStackTrace();
                    System.exit(0);
                }

                receiving_socket.close();
                break;


            case Type3: //DatagramSocket3
                //Open the socket to receive from
                try {
                    receiving_socket = new DatagramSocket3(port);
                } catch (SocketException e) {
                    System.out.println("ERROR: TextReceiver: Could not open socket to receive from.");
                    e.printStackTrace();
                    System.exit(0);
                }

                receiving_socket.close();
                break;


            case Type4: //DatagramSocket4 (DGS1 w/ encryption)
                //Open the socket to receive from
                try {
                    receiving_socket = new DatagramSocket4(port);
                } catch (SocketException e) {
                    System.out.println("ERROR: TextReceiver: Could not open socket to receive from.");
                    e.printStackTrace();
                    System.exit(0);
                }

                try {
                    receiving_socket.receive(packet);
                    ByteBuffer unwrapDecrypt = ByteBuffer.allocate(buffer.length);
                    ByteBuffer encryptedAudio = ByteBuffer.wrap(buffer);

                    for (int i = 0; i < buffer.length/4; i++) {
                        int fourByte = encryptedAudio.getInt();
                        fourByte = fourByte ^ cipher;
                        unwrapDecrypt.putInt(fourByte);
                    }

                    //Convert into byte array and play sound.
                    byte[] decryptedSound = unwrapDecrypt.array();
                    player.playBlock(decryptedSound);

                } catch (IOException e) {
                    System.out.println("ERROR: TextReceiver: Some random IO error occured!");
                    e.printStackTrace();
                }

                receiving_socket.close();
                break;

        }//End of switch statement
    }
}
