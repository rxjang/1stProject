package com.bit.project1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class NumberServer extends Thread {
   // OutputStream 배열
   static ArrayList<OutputStream> list = new ArrayList<OutputStream>(); 
   // 접속한 hotNameList 배열
   // (사용자 지정 이름이 공란일 때, hostName으로 강제 설정)
   static ArrayList<String> hostNameList = new ArrayList<String>(); 
   // 접속한 유저들의 사용자 지정 이름 배열
   static ArrayList<String> userList = new ArrayList<String>(); 
   
   Socket sock;

   static TimerThread timer;
   static String hostName="";

   static int readyCnt;
   
   NumberServer(Socket sock) {
      this.sock = sock; 
   }
   
   public void run() {
      InputStream is = null;
      OutputStream os = null;
      InputStreamReader isr = null;
      OutputStreamWriter osw = null;
      BufferedReader br = null;
      BufferedWriter bw = null;
      
      
      try {
         InetAddress addr = sock.getInetAddress();
         String userName;
         is = sock.getInputStream();
         isr = new InputStreamReader(is);
         br = new BufferedReader(isr);
         os = sock.getOutputStream();
         list.add(os);
         hostNameList.add(addr.getHostName());
         userList.add(addr.getHostName());
         
         // Diagram 체로 사용자 지정 이름 값 받음.
         osw = new OutputStreamWriter(os);
         bw = new BufferedWriter(osw);
         bw.write("/setUserName");
         
         bw.newLine();
         bw.write(addr.getHostName());
         bw.newLine();
         bw.flush();
         
         
         while(true) {
            String msg = br.readLine();
            System.out.println(msg);
            
            if(msg.equals("/setUserName")) {
               userName = br.readLine();
               if(userName.isEmpty() || userName.equals(null) || userName.equals("")) {
                  userName = addr.getHostName();
               }
               
               int idx = -1;
               for(int i=0; i<userList.size(); i++) {
                  if(addr.getHostName().equals(userList.get(i))) {
                     userList.set(i, userName);
                     idx = i;
                  }
               }
               
               // 먼저 접속한 유저가 있다면, 상대 userName 과 상태 보내기.
               if(idx > 0) {
                  OutputStream info = list.get(idx);
                  osw = new OutputStreamWriter(info);
                  bw = new BufferedWriter(osw);
                  bw.write("/player");
                  bw.newLine();
                  bw.write(userList.get(idx-1));
                  bw.newLine();
                  if(hostName.isEmpty() || hostName.equals("") || hostName.equals(null))
                     bw.write("/notready");
                  else bw.write("/already");
                  bw.newLine();
                  bw.flush();
               }
               
               for(int i=0; i<list.size(); i++) {
                  OutputStream user = list.get(i);
                  osw = new OutputStreamWriter(user);
                  bw = new BufferedWriter(osw);
                  bw.write(userName+"님이 입장하셨습니다.");
                  bw.newLine();
                  bw.flush();
               }
            }
            
            if(msg.equals("/ready")) {
               if(readyCnt == 0) {
                  readyCnt++;

                  hostName = addr.getHostName();
                  
                  int idx = -1;
                  for(int i=0; i<hostNameList.size(); i++) { // String hostName;
                     if(hostName.equals(hostNameList.get(i)))
                        idx = i;
                  }

                  for(int i=0; i<list.size(); i++) {
                     OutputStream user = list.get(i);
                     osw = new OutputStreamWriter(user);
                     bw = new BufferedWriter(osw);
                     bw.write(userList.get(idx) + "님이 게임 준비를 마쳤습니다.");
                     bw.newLine();
                     bw.write("/ready");
                     bw.newLine();
                     bw.write(userList.get(idx));
                     bw.newLine();
                     bw.write("다른 사용자가 준비를 마치면 게임이 시작됩니다.\n");
                     
                     // 주석 처리 필요. (1인 테스트시 주석 풀기)
                     bw.write("잠시 후 게임이 시작됩니다.\n");
                     bw.newLine();
                     bw.write("/start");
                     bw.newLine();
                     
                     bw.flush();


                  }
               // 주석 처리 필요. (1인 테스트시 주석 풀기)
                  if(timer.equals(null)) {
                     timer = new TimerThread(list); 
                     timer.start();
                  }
                  else if(timer.getState().equals(Thread.State.TERMINATED)) {
                     timer = new TimerThread(list); 
                     timer.start();
                  }
                  else {
                     timer.start();
                     
                  }
                  
               }
               else if(!hostName.equals(addr.getHostName()) && !hostName.equals(null) && !hostName.isEmpty()) {
                  
//                  int idx = -1;
//                  for(int i=0; i<hostNameList.size(); i++) { // ArrayList hostNameList
//                     if(addr.getHostName().equals(hostNameList.get(i)))
//                        idx = i;
//                  }
                  int idx = getSenderIdx(addr);
                  
                  for(int i=0; i<list.size(); i++) {
                     OutputStream user = list.get(i);
                     osw = new OutputStreamWriter(user);
                     bw = new BufferedWriter(osw);
                     bw.write(userList.get(idx)+"님이 게임 준비를 마치셨습니다.");
                     bw.newLine();
                     bw.write("/ready");
                     bw.newLine();
                     bw.write(userList.get(idx));
                     bw.newLine();
                  
                     bw.write("잠시 후 게임이 시작됩니다.\n");
                     bw.newLine();
                     bw.write("/start");
                     bw.newLine();
                     bw.flush();

                  }

                  if(timer.equals(null)) {
                     timer = new TimerThread(list); 
                     timer.start();
                  }
                  else if(timer.getState().equals(Thread.State.TERMINATED)) {
                     timer = new TimerThread(list); 
                     timer.start();
                  }
                  else {
                     timer.start();
                  }
                  
               }
               else {
                  
//                  int idx = -1;
//                  for(int i=0; i<hostNameList.size(); i++) {  // ArrayList hostNameList
//                     if(addr.getHostName().equals(hostNameList.get(i)))
//                        idx = i;
//                  }
                  int idx = getSenderIdx(addr);
                  
                  for(int i=0; i<list.size(); i++) {
                     OutputStream user = list.get(i);
                     osw = new OutputStreamWriter(user);
                     bw = new BufferedWriter(osw);
                     bw.write(userList.get(idx)+"님이 게임 준비를 마치셨습니다.");
                     bw.newLine();
                     bw.write("/ready");
                     bw.newLine();
                     bw.write(userList.get(idx));
                     bw.newLine();
                     
                     bw.newLine();
                     bw.flush();
                  }
               }
            }
            
            if(msg.equals("/endgame")) {

//               int idx = -1;
//               for(int i=0; i<hostNameList.size(); i++) {  // ArrayList hostNameList
//                  if(addr.getHostName().equals(hostNameList.get(i)))
//                     idx = i;
//               }
               int idx = getSenderIdx(addr);
               
               for(int i=0; i<list.size(); i++) {
                  OutputStream user = list.get(i);
                  osw = new OutputStreamWriter(user);
                  bw = new BufferedWriter(osw);
                  bw.write("/endgame");
                  bw.newLine();
                  bw.write("\"" + userList.get(idx) + "\"님이 승리하셨습니다.");
                  bw.newLine();
                  bw.write(userList.get(idx));
                  bw.newLine();
                  bw.flush();
                  
                  readyCnt = 0;
                  hostName = null;
                  
               }
               
               timer.stop();
               
            }
            
            if(msg.equals("/exit")) {
               
//               int idx = -1;
//               for(int i=0; i<hostNameList.size(); i++) {  // ArrayList hostNameList
//                  if(addr.getHostName().equals(hostNameList.get(i)))
//                     idx = i;
//               }
               int idx = getSenderIdx(addr);
               
               for(int i=0; i<list.size(); i++) {
                  OutputStream user = list.get(i);
                  osw = new OutputStreamWriter(user);
                  bw = new BufferedWriter(osw);
                  System.out.println(userList.get(idx)+"님이 게임을 종료하셨습니다.");
                  if(hostName.equals(userList.get(idx)))
                     hostName = "";
                  bw.newLine();
                  bw.write("다른 사용자를 기다리는 중입니다...");
                  bw.newLine();
                  bw.flush();
                  
                  readyCnt = 0;
                  hostName = null;
                  
                  list.remove(idx);
                  hostNameList.remove(idx);
                  userList.remove(idx);
               }
            }
         
         }
         
      } catch (IOException e) {
//         e.printStackTrace();
      } catch (java.lang.NullPointerException e) {
         System.out.println(sock.getInetAddress().getHostAddress() + "님이 나가셨습니다.");
      } finally {
         try {
            if(bw != null) bw.close();
            if(br != null) br.close();
            if(osw != null) osw.close();
            if(isr != null) isr.close();
            if(os != null) os.close();
            if(is != null) is.close();
            if(sock != null) sock.close();
         } catch (IOException e) {
//            e.printStackTrace();
         }
      }
   
   }
   
   public static void main(String[] args) {
      int port = 8080;
      ServerSocket serv = null;
      System.out.println("********************** WELCOME **********************");
      try {
            serv = new ServerSocket(port);
            timer = new TimerThread(list);
            
            while(true) {
               Socket sock = serv.accept();
               NumberServer thr = new NumberServer(sock);
               thr.start();
            }
            
      } catch (IOException e) {
//         e.printStackTrace();
      } finally {
         try {
            if(serv != null) serv.close();
         } catch (IOException e) {
//            e.printStackTrace();
         }
      }

   }
   
   public int getSenderIdx(InetAddress addr) {
      int idx = -1;
      for(int i=0; i<hostNameList.size(); i++) { // ArrayList hostNameList
         if(addr.getHostName().equals(hostNameList.get(i)))
            idx = i;
      }
      return idx;
   }

}

class TimerThread extends Thread {
   static ArrayList<OutputStream> list = new ArrayList<OutputStream>();
   int timer = 0;
   
   TimerThread(ArrayList<OutputStream> list) {
      this.list = list;
   }
   public void run() {
      System.out.println("타이머 시작");
      int signal = 3;
      synchronized (this) {
         
         while(true) {
            try {
               Thread.sleep(1000);
               if(signal < 0) {
                  timer++;
                  
                  String send = timer + "";
                  for(int i=0; i<list.size(); i++) {
                     OutputStream user = list.get(i);
                     OutputStreamWriter osw = new OutputStreamWriter(user);
                     BufferedWriter bw = new BufferedWriter(osw);
                     if(timer < 10) {
                        send = "0" + timer;
                     }
                     else {
                        send = timer + "";
                     }
                     try {
                        bw.write("/timer");
                        bw.newLine();
                        
                        bw.write(send);
                        bw.newLine();
                        bw.flush();
                     } catch (IOException e) {
//                        e.printStackTrace();
                     }
                  }
                  System.out.println(send);
               }
               else{
                  for(int i=0; i<list.size(); i++) {
                     OutputStream user = list.get(i);
                     OutputStreamWriter osw = new OutputStreamWriter(user);
                     BufferedWriter bw = new BufferedWriter(osw);
                        try {
                           bw.write("/signal");
                           bw.newLine();
                           
                           if(signal > 0) bw.write(signal+"");
                           else bw.write("Game start!");
                           bw.newLine();
                           bw.flush();
                        } catch (IOException e) {
//                           e.printStackTrace();
                        }
                  }
                  
                  signal--;
               }
            } catch (InterruptedException e) {
//            e.printStackTrace();
            }
            
         }
      }
   }
}