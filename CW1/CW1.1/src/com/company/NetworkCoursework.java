package com.company;

/*
    Liam Sinclair
*/

import com.company.*;
import java.io.*;
import java.util.*;
import java.net.*;
import javax.sound.sampled.LineUnavailableException;

public class NetworkCoursework {

    private static final int PORT = 8000;
    private static final AudioController audioController = new AudioController();
    private static VoIPController voipController;
    //private static ThreadedVoiceSender threadedVoiceSender;
    private static ThreadedVoiceReceiver threadedVoiceReceiver;
    private static InetAddress clientIP;

    public enum SocketType {
        Type1, Type2, Type3, Type4;
    }

    public static void main(String[] args) throws LineUnavailableException, IOException {

        //InitialiseThreads();
        //RecordTest();
        //StartThreadedVoice();
        //RecordTest();
        //TestPackets();
    }

    /*
    private static void StartThreadedVoice() {

        threadedVoiceReceiver.start();
        threadedVoiceSender.start();
    }
    */

    static void InitialiseThreads(SocketType socketType) {

        voipController = new VoIPController(socketType);
        threadedVoiceReceiver = new ThreadedVoiceReceiver(socketType);
       // threadedVoiceSender = new ThreadedVoiceSender(socketType);
    }

    static void RecordTest() throws LineUnavailableException, IOException {

        Vector<byte[]> recordedAudio = audioController.AudioRecord(5);

        for (byte[] recordedAudio1 : recordedAudio) {

            for (int i = 0; i < recordedAudio1.length; i++) {

                System.out.println(recordedAudio1[i]);
            }

            System.out.println("/n");
        }
    }
}
