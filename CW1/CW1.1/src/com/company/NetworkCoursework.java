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
    private static VoIPController voipController;
    private static InetAddress clientIP;

    public enum SocketType {
        Type1, Type2, Type3, Type4;
    }
}
