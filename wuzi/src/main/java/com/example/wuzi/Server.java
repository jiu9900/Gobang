package com.example.wuzi;
import javafx.application.Platform;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server{
    public static int PORT=11451;
    private static Socket player1,player2;
    public static void main(String[] args){
        try{
            ServerSocket server = new ServerSocket(PORT);
            System.out.println("Waiting for clients……");
            player1 = server.accept();System.out.println("玩家1已连接");
            player2 = server.accept();System.out.println("玩家2已连接");
            BufferedReader in1=new BufferedReader(new InputStreamReader(player1.getInputStream()));
            BufferedReader in2=new BufferedReader(new InputStreamReader(player2.getInputStream()));
            PrintWriter out1=new PrintWriter(player1.getOutputStream());
            PrintWriter out2=new PrintWriter(player2.getOutputStream());

            new Thread(() -> handlePlayer(in1,out2,"玩家1")).start();
            new Thread(() -> handlePlayer(in2,out1,"玩家2")).start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    static void handlePlayer(BufferedReader thisIn,PrintWriter otherOut,String name){
        try{
            String move=thisIn.readLine();
            while(move!=null){
                System.out.println("111");
                otherOut.println(move);otherOut.flush();
                if(move.equals("exit")) Platform.exit();
                move= thisIn.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
