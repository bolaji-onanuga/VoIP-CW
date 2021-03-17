package com.company;

/*
    Liam Sinclair
*/

import CMPC3M06.AudioPlayer;
import uk.ac.uea.cmp.voip.*;
import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.zip.CRC32;
import static com.company.CustomPackets.*;
import static com.company.NetworkCoursework.SocketType;

public class VoIPController {

    private final boolean processing = false, interleave = false;
    private final int interleaveNumber = 16, sortPeriodNumber = 8;
    private boolean isFirst = true;
    private DatagramSocket sendingSocket, receivingSocket;

    private AudioPlayer audioPlayer;
    private SocketType socketType;

    private int squareRoot = (int) Math.sqrt(interleaveNumber);
    private int sequenceNumber = 1, index = 1, sequence = 0, i = squareRoot - 1, j = 0, savedNumber = 0;

    private CustomPackets[] arraySort = new CustomPackets[interleaveNumber];
    private ArrayList<CustomPackets> packetsReceived = new ArrayList<CustomPackets>();
    private byte[] empty = new byte[512];

    private CustomPackets emptypackets = new CustomPackets(0, empty);
    private CustomPackets previous = null;
    private CustomPackets[][] packetArray = new CustomPackets[squareRoot][squareRoot];
    private CustomPackets previous1 = new CustomPackets(0, new byte[512]);

    public VoIPController(SocketType socketType) {

        this.socketType = socketType;
    }

    public void VoiceTransmission(int PORT, byte[] buffer, InetAddress clientIP, int number) throws IOException {

        if (socketType == SocketType.Type4) {

            CRC32 validator = new CRC32();
            validator.update(buffer);
            long sum = validator.getValue();
            buffer = sendNumberToBuffer(buffer, number);

            CustomPackets pendingPackets = new CustomPackets(retrieveNumberFromBuffer(buffer, socketType), buffer);
            byte[] bufferSum = longToByteArray(sum), pendingBuffer = mergingArrays(bufferSum, buffer);
            pendingPackets.definePacketData(pendingBuffer);
            DatagramPacket freshPacket = pendingPackets.retrievePacket(clientIP, PORT);

            sendingSocket.send(freshPacket);
        } else if (interleave && socketType == SocketType.Type2) {

            buffer = sendNumberToBuffer(buffer, number);

            CustomPackets pendingPackets = new CustomPackets(retrieveNumberFromBuffer(buffer, socketType), buffer);

            packetArray[i][j] = pendingPackets;

            if (i >= 0) {

                i--;
            }

            if (i == -1) {

                i = squareRoot - 1;
                j++;
            }

            if (j == squareRoot) {

                j = 0;
            }

            if ((number % interleaveNumber == 0) && interleave) {

                for (CustomPackets[] packetArray1 : packetArray) {

                    for (int l = 0; l < packetArray.length; l++) {

                        DatagramPacket datagramPacket = packetArray1[l].retrievePacket(clientIP, PORT);
                        sendingSocket.send(datagramPacket);
                    }
                }
            }
        } else {

            buffer = sendNumberToBuffer(buffer, number);
            DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length, clientIP, PORT);
            sendingSocket.send(datagramPacket);
        }
    }

    public void VoiceReceiver(byte[] buffer, int bufferSize) throws IOException {

        DatagramPacket datagramPacket = new DatagramPacket(buffer, 0, bufferSize);
        receivingSocket.receive(datagramPacket);
        voiceFix(socketType, datagramPacket);
    }

    public void defineType(SocketType socketType) {

        this.socketType = socketType;
    }

    public SocketType retrieveType() {

        return this.socketType;
    }

    public void initialiseSocket(SocketType socketType, char a) throws SocketException, LineUnavailableException {

        audioPlayer = new AudioPlayer();

        switch (socketType) {

            case Type1:

                if (a == 's') {

                    sendingSocket = new DatagramSocket();
                } else {

                    receivingSocket = new DatagramSocket(55555);
                }
                break;

            case Type2:

                if (a == 's') {

                    sendingSocket = new DatagramSocket2();
                } else {

                    receivingSocket = new DatagramSocket2(55555);
                }
                break;

            case Type3:

                if (a == 's') {

                    sendingSocket = new DatagramSocket3();
                } else {

                    receivingSocket = new DatagramSocket3(55555);
                }
                break;

            case Type4:

                if (a == 's') {

                    sendingSocket = new DatagramSocket4();
                } else {

                    receivingSocket = new DatagramSocket4(55555);
                }
                break;
        }
    }

    public void voiceFix(SocketType socketType, DatagramPacket datagramPacket) throws IOException {

        long totalNumber = retrieveNumberFromBuffer(datagramPacket.getData(), socketType);
        byte[] pendingArray = packetStrip(datagramPacket.getData(), socketType);
        CRC32 validator = new CRC32();
        validator.update(pendingArray);

        long comparison = validator.getValue();

        CustomPackets current = new CustomPackets(retrieveNumberFromBuffer(datagramPacket.getData(), socketType), pendingArray);

        switch (socketType) {

            case Type1:

                audioPlayer.playBlock(current.retrievePacketData());
                break;

            case Type2:

                if (processing) {

                    if (isFirst) {

                        sequenceNumber = (int) (current.packetID / interleaveNumber);
                        isFirst = false;
                    }

                    if (!interleave) {

                        audioPlayer.playBlock(current.retrievePacketData());
                    } else {

                        if (current.packetID <= (sequenceNumber * interleaveNumber)) {

                            arraySort[(int) (current.packetID - (((sequenceNumber - 1) * interleaveNumber)) - 1)] = current;
                        } else {

                            for (int k = 0; k < arraySort.length; k++) {

                                if (arraySort[k] != null) {

                                    audioPlayer.playBlock(arraySort[k].packetData);
                                } else {

                                    if (k > 0 && arraySort[k - 1] != null) {

                                        audioPlayer.playBlock(arraySort[k - 1].packetData);
                                    } else if (k > 1 && arraySort[k - 2] != null) {

                                        audioPlayer.playBlock(arraySort[k - 2].packetData);
                                    } else if (k > 2 && arraySort[k - 3] != null) {

                                        audioPlayer.playBlock(arraySort[k - 3].packetData);
                                    } else {

                                        audioPlayer.playBlock((emptypackets.packetData));
                                    }
                                }
                            }

                            sequenceNumber++;
                            arraySort = new CustomPackets[interleaveNumber];

                            if (current.packetID > ((sequenceNumber) * interleaveNumber)) {
                                sequenceNumber++;
                            }

                            arraySort[(int) (current.packetID - (((sequenceNumber - 1) * interleaveNumber)) - 1)] = current;
                        }
                    }
                } else {

                    audioPlayer.playBlock(current.packetData);
                }

                break;

            case Type3:

                if (processing) {

                    packetsReceived.add(current);

                    if (packetsReceived.size() % sortPeriodNumber == 0) {

                        Collections.sort(packetsReceived, new CustomPackets.PacketComparator());

                        for (int i = 0; i < packetsReceived.size(); i++) {

                            if (packetsReceived.get(i).packetID >= savedNumber) {

                                audioPlayer.playBlock(packetsReceived.get(i).packetData);
                            } else if (i > 0) {

                                if (packetsReceived.get(i - 1) != null) {

                                    audioPlayer.playBlock(packetsReceived.get(i - 1).packetData);
                                } else if (i == 0) {

                                    audioPlayer.playBlock(emptypackets.packetData);
                                }

                                if (i == packetsReceived.size() - 1) {

                                    savedNumber = (int)(current.packetID);
                                }
                            }

                            packetsReceived.clear();
                            sequence++;
                        }
                    } else {

                        audioPlayer.playBlock(current.packetData);
                    }

                    break;
                    /*
                    case Type4:

                        if (processing) {

                            if (totalNumber == comparison) {

                                audioPlayer.playBlock(current.packetData);
                                previous = current;
                            } else if (previous != null) {

                                audioPlayer.playBlock(previous.packetData);
                            }
                        } else {

                            audioPlayer.playBlock(current.packetData);
                        }*/
                }
        }
    }
}


























