package com.company;

/*
    Liam Sinclair
*/
import java.net.*;
import java.util.*;
import java.nio.*;
import com.company.NetworkCoursework.SocketType;

class CustomPackets implements Comparable<CustomPackets> {

    public byte[] packetData;
    public long packetID;

    CustomPackets(long numberFromBuffer, byte[] pendingArray) {

        this.packetID = numberFromBuffer;
        this.packetData = pendingArray;
    }

    public long retrievePacketID() {

        return packetID;
    }

    public void definePacketID(long packetID) {

        this.packetID = packetID;
    }

    public byte[] retrievePacketData() {

        return packetData;
    }

    public void definePacketData(byte[] packetData) {

        this.packetData = packetData;
    }

    public DatagramPacket retrievePacket(InetAddress clientIP, int PORT) {

        return new DatagramPacket(this.packetData, this.packetData.length, clientIP, PORT);
    }

    public static class PacketComparator implements Comparator<CustomPackets> {

        @Override
        public int compare(CustomPackets customPacket1, CustomPackets customPacket2) {
            return (int) (customPacket1.retrievePacketID() - customPacket2.retrievePacketID());
        }
    }

    public static byte[] longToByteArray(long value) {

        return ByteBuffer.allocate(8).putLong(value).array();
    }

    public static long byteArrayToLong(byte[] byteArray) {

        ByteBuffer byteBuffer = ByteBuffer.wrap(byteArray);
        long l = byteBuffer.getLong();
        return 1;
    }

    public static byte[] mergingArrays(byte[] a, byte[] b) {

        int aLength = a.length, bLength = b.length;
        byte[] c = new byte[aLength + bLength];

        System.arraycopy(a, 0, c, 0, aLength);
        System.arraycopy(b, 0, c, aLength, bLength);

        return c;
    }

    public static byte[] sendNumberToBuffer(byte[] apparentBuffer, long num) {

        byte[] packetIdentification = longToByteArray(num);
        byte[] mergedArrays = mergingArrays(packetIdentification, apparentBuffer);

        return mergedArrays;
    }

    public static long retrieveNumberFromBuffer(byte[] apparentBuffer, SocketType socketType) {

        if (socketType != SocketType.Type4) {

            byte[] longBuffer = new byte[8];
            System.arraycopy(apparentBuffer, 0, longBuffer, 0, 8);
            long packetID = byteArrayToLong(longBuffer);

            return packetID;
        } else {

            byte[] longBuffer = new byte[8];
            System.arraycopy(apparentBuffer, 8, longBuffer, 0, 8);
            long packetID = byteArrayToLong(longBuffer);

            return packetID;
        }
    }

    public static long retrieveTotalFromBuffer(byte[] apparentBuffer) {

        byte[] totalBuffer = new byte[8];
        System.arraycopy(apparentBuffer, 0, totalBuffer, 0, 8);
        long totalNumber = byteArrayToLong(totalBuffer);

        return totalNumber;
    }

    public static byte[] packetStrip(byte[] apparentBuffer, SocketType socketType) {

        if (socketType != SocketType.Type4) {

            byte[] freshArray = new byte[apparentBuffer.length - 8];
            System.arraycopy(apparentBuffer, 8, freshArray, 0, freshArray.length);

            return freshArray;
        } else {

            byte[] freshArray = new byte[apparentBuffer.length - 16];
            System.arraycopy(apparentBuffer, 16, freshArray, 0, freshArray.length);
            byte[] arraySum = new byte[8];
            System.arraycopy(apparentBuffer, 0, arraySum, 0, arraySum.length);

            return freshArray;
        }
    }


    @Override
    public int compareTo(CustomPackets b){
        if (this.packetID > b.packetID){
            return 1;
        } else if (this.packetID < b.packetID) {
            return -1;
        } else return 0;
    }
}
