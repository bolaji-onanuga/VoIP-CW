package com.company;

/*
@bolaji onanuga 14/3/2021
 */

public class ThreadedSoundDuplex {

    public static void main(String[] args){
        ThreadedSoundSender sender = new ThreadedSoundSender(NetworkCoursework.SocketType.Type1);
        ThreadedSoundReceiver rec  = new ThreadedSoundReceiver(NetworkCoursework.SocketType.Type1);

        sender.start();
        rec.start();
    }
}
