package com.company;

/*
@bolaji onanuga 14/3/2021
 */

public class ThreadedSoundDuplex {

    public  static void main(String[] args){
        ThreadedSoundReceiver rec  = new ThreadedSoundReceiver(NetworkCoursework.SocketType.Type2);
        ThreadedSoundSender sender = new ThreadedSoundSender(NetworkCoursework.SocketType.Type2);
        sender.start();
        rec.start();

    }
}
