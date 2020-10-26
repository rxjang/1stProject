package com.bit.project1;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;


public class NumberClient extends Frame{
   Socket sock;
   Panel pCenter;
   Panel pEast;
   Panel pSouth;
   ImagePanel pTitle;
   Panel pButtons;
   Panel pManual;
   JButton btns[];
   Label lTimer;
   Dialog diaGameStart;
   Dialog diaInputName;
   JLabel lsign;
   JLabel lname;
   TextField txtName;
   
//   int difficulty;
   int[] correct = new int[25]; // 정답 순서 배열
   int iter = 1; // 배열 화살표
//   int[] correct = new int[difficulty];
   int gameCount = 0;
//   int[] correct = new int[difficulty];
   Button btnReady;
   Button btnExit;
   TextArea ta;
   Label luser1;
   Label luser2;
   JLabel stateUser1;
   JLabel stateUser2;
   static String msg = "";
   String state;
   String hostName;
   String name; // 사용자 지정 이름 (공백일 경우는 hostName)
   String timeBuffer;
   long startTime;   
   Timer fTimer;
   TimerTask task;
   
   OutputStream os = null;
   OutputStreamWriter osw = null;
   BufferedWriter bw = null;
   
   NumberClient(Socket sock) {
      
      this.sock = sock;
      // 게임 타이틀
      Panel pNorth = new Panel(); 
      // 타이틀 라벨
      Label lTitle = new Label("누가 25를 먼저 눌렀을까?");
      pNorth.add(lTitle);
      
      // 게임 실행 버튼
      pCenter = new Panel();
      pCenter.setLayout(new GridLayout(5,5)); // 5 X 5
      // 버튼 생성 (1 ~ 25)
//      btns = new JButton[difficulty];   
      btns = new JButton[25];
      for(int i=0; i<btns.length; i++) {
         btns[i] = new JButton((i+1)+"");
         pCenter.add(btns[i]);
         btns[i].setEnabled(false);
         btns[i].setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 30));
      }   
      
      // 게임제어버튼과 타이머
      pEast = new Panel();
      pEast.setLayout(new BorderLayout());
      Panel pEast_North = new Panel();
      btnReady = new Button("준비");
      btnReady.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            state = "/ready";
            try {
               os = sock.getOutputStream();
               osw = new OutputStreamWriter(os);
               bw = new BufferedWriter(osw);
               bw.write(state);
               bw.newLine();
               bw.flush();
            } catch (IOException e1) {
               e1.printStackTrace();
            }
         }
      });
      
      btnExit = new Button("종료");
      btnExit.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            state = "/exit";
            try {
               os = sock.getOutputStream();
               osw = new OutputStreamWriter(os);
               bw = new BufferedWriter(osw);
               bw.write(state);
               bw.newLine();
               bw.flush();

            } catch (IOException e1) {
               e1.printStackTrace();
            }
            dispose();
         }
      });
      // 타이머 UI
      lTimer = new Label("   Timer   ");

      pEast_North.add(lTimer);
      pEast_North.add(btnReady);
      pEast_North.add(btnExit);
      pEast.add(BorderLayout.NORTH, pEast_North);
      
      Panel pEastUser1 = new Panel();
      Panel pEastUser2 = new Panel();
      
      luser1 = new Label();
      stateUser1 = new JLabel();
      pEastUser1.add(luser1);
      pEastUser1.add(stateUser1);
      stateUser1.setHorizontalAlignment(JLabel.CENTER);
      
      luser2 = new Label();
      stateUser2 = new JLabel();
      pEastUser2.add(luser2);
      pEastUser2.add(stateUser2);
      stateUser2.setHorizontalAlignment(JLabel.CENTER);
      
      luser1.setVisible(false);
      stateUser1.setVisible(false);
      
      luser2.setVisible(false);
      stateUser2.setVisible(false);
      pEast.add(BorderLayout.CENTER, pEastUser1);
      pEast.add(BorderLayout.SOUTH, pEastUser2);
      
      // 게임상황 notice
      pSouth = new Panel(); 
      ta = new TextArea();
      ta.setSize(80, 50);
      ta.setEditable(false);
      ta.setText(msg);

      pSouth.add(ta);

      
      // 닫기 버튼
      addWindowListener(new WindowAdapter() {
         
         @Override
         public void windowClosing(WindowEvent e) {
            state = "/exit";
            try {
               os = sock.getOutputStream();
               osw = new OutputStreamWriter(os);
               bw = new BufferedWriter(osw);
               bw.write(state);
               bw.newLine();
               bw.flush();

            } catch (IOException e1) {
               e1.printStackTrace();
            }
            dispose();
         }
         
      });
      
      add(BorderLayout.NORTH, pNorth);
      add(BorderLayout.CENTER, pCenter);
      add(BorderLayout.EAST, pEast);
      add(BorderLayout.SOUTH, pSouth);
      
      pCenter.setVisible(false);
      pEast.setVisible(false);
      pSouth.setVisible(false);
      
      pTitle = new ImagePanel(); // title
      pButtons  = new Panel();  // default true
      pManual = new Panel(); // default false
      Button btnBack = new Button("돌아가기");
      btnBack.addActionListener(new ActionListener() {

         @Override
         public void actionPerformed(ActionEvent e) {
            pManual.setVisible(false);
            pTitle.setVisible(true);
            pButtons.setVisible(true);
            add(BorderLayout.CENTER, pTitle);
            add(BorderLayout.SOUTH, pButtons);
            
            
         }
         
      });
      Panel pBack = new Panel();
      Panel pImage = new Panel();
      Toolkit kit = Toolkit.getDefaultToolkit();
      Image img = kit.createImage("bono.jpg");
      Icon icon = new ImageIcon(img);
      JButton btnBono = new JButton(icon);
//      btnBono.setEnabled(false);

      pBack.add(btnBack);
      pImage.add(btnBono);
      pManual.add(BorderLayout.NORTH, pBack);
      pManual.add(BorderLayout.CENTER, pImage);
      
      pTitle.setVisible(true);
      pButtons.setVisible(true);
      pManual.setVisible(false);
      Button btnManual = new Button("게임방법");
      btnManual.addActionListener(new ActionListener() {

         @Override
         public void actionPerformed(ActionEvent e) {
            pTitle.setVisible(false);
            pManual.setVisible(true);
            pButtons.setVisible(false);
            add(BorderLayout.CENTER, pManual);
         }
         
      });
      Button btnEnterGame = new Button("게임 시작");
      btnEnterGame.addActionListener(new ActionListener() {

         @Override
         public void actionPerformed(ActionEvent e) {
            pTitle.setVisible(false);
            pButtons.setVisible(false);
            
            pCenter.setVisible(true);
            pEast.setVisible(true);
            pSouth.setVisible(true);
            diaInputName.setVisible(true);
            add(BorderLayout.CENTER,pCenter);
            add(BorderLayout.EAST, pEast);
            add(BorderLayout.SOUTH, pSouth);
         }
         
      });
      pButtons.add(BorderLayout.NORTH,btnManual);
      pButtons.add(BorderLayout.SOUTH,btnEnterGame);
      
      add(BorderLayout.CENTER, pManual);
      add(BorderLayout.CENTER, pTitle);
      add(BorderLayout.SOUTH, pButtons);
      
      setSize(700,700);
      setLocation(1920/2 - 700/2, 1080/2 - 700/2);
      setVisible(true);
      
      diaGameStart = new Dialog(this, "게임 시작 알리미");
      lsign = new JLabel();

      diaGameStart.add(lsign);
      lsign.setHorizontalAlignment(JLabel.CENTER);
      diaGameStart.setLocation(1920/2 - getX()/2, 1080/2 - getY()/2);
      diaGameStart.setSize(400, 200);
      diaGameStart.setVisible(false);
      
      diaGameStart.addWindowListener(new WindowAdapter(){
         @Override
         public void windowClosing(WindowEvent e) {
            diaGameStart.dispose();
         }
      });
      
      diaInputName = new Dialog(this, "사용자 지정 이름 입력");
      diaInputName.setLayout(new FlowLayout());
      lname = new JLabel("사용자 지정 이름");
      txtName = new TextField(30);
      
      Button btnSubmit = new Button("입력");
      diaInputName.add(lname);
      diaInputName.add(txtName);
      diaInputName.add(btnSubmit);
      diaInputName.setVisible(false);
      diaInputName.setLocation(1920/2 - getX()/2, 1080/2 - getY()/2);
      diaInputName.setSize(400, 100);
      
      diaInputName.addWindowListener(new WindowAdapter(){
         @Override
         public void windowClosing(WindowEvent e) {
            diaInputName.dispose();
         }
      });
      
      btnSubmit.addActionListener(new ActionListener() {

         @Override
         public void actionPerformed(ActionEvent e) {
               try {
                  os = sock.getOutputStream();
                  osw = new OutputStreamWriter(os);
                  bw = new BufferedWriter(osw);
                  bw.write("/setUserName");
                  bw.newLine();
                  bw.write(txtName.getText());
                  bw.newLine();
                  bw.flush();
                  
                  if(!txtName.getText().equals("") && !txtName.getText().equals(null) && !txtName.getText().isEmpty())
                     name = txtName.getText();
                  
                  luser1.setText(name);
                  luser1.setVisible(true);
                  stateUser1.setText("대기중");
                  stateUser1.setBackground(Color.BLUE);
                  stateUser1.setVisible(true);
                  
                  revalidate();
                  
               } catch (IOException e1) {
                  e1.printStackTrace();
               }
               diaInputName.dispose();
            }

      });
   }

   void mix() {
      
      for(int i=0; i<correct.length; i++) {
         this.correct[i] = i+1;
      }   

      if(gameCount > 0) {
         iter = 1;
         this.remove(pCenter);
         pCenter = new Panel();
         pCenter.setLayout(new GridLayout(5,5));
         btns = new JButton[25];
//         btns = new JButton[difficulty];
         for(int i=0; i<btns.length; i++) {
            btns[i] = new JButton((i+1)+"");
            pCenter.add(btns[i]);
            btns[i].setEnabled(false);
            btns[i].setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 30));
         }
         add(BorderLayout.CENTER, pCenter);
         
      }
      
      JButton temp = new JButton();
      int tmp;
      for(int i=0; i<1000; i++) {
         Random ran = new Random();
         int su = ran.nextInt(25); // 1 ~ 25
         temp = btns[0];
         tmp = correct[0];
         btns[0] = btns[su];
         correct[0] = correct[su];
         btns[su] = temp;
         correct[su] = tmp;
      }
//         int su = ran.nextInt(difficulty) + 1; // 1 ~ difficulty
      
      for(int i=0; i<btns.length; i++) {
         pCenter.add(btns[i]);
         
         int btnNum = i;
         btns[i].addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               if(correct[btnNum] == iter && iter < 25) { 
                  iter++;
                  btns[btnNum].setEnabled(false);
                  
               }
               else if(correct[btnNum] == iter && iter == 25){
                  try { // 게임을 종료 시킴
                     state = "/endgame";
                     os = sock.getOutputStream();
                     osw = new OutputStreamWriter(os);
                     bw = new BufferedWriter(osw);
                     bw.write(state);
                     bw.newLine();
                     bw.flush();
                     
                     btns[btnNum].setEnabled(false);
                     iter = 0;
                     
                  } catch (IOException e1) {
                     e1.printStackTrace();
                  }
               }
            }
         });
      }

      revalidate(); // 화면 갱신
      gameCount++;
   }
   
    private void toTime(long time){
        int m = (int)(time / 1000.0 / 60.0);
        int s = (int)(time % (1000.0 * 60) / 1000.0);
        int ms = (int)(time % 1000 / 10.0);

        timeBuffer= String.format("%02d : %02d : %02d", m, s, ms);
        }
     
     public void timer(){
        startTime = System.currentTimeMillis();
        fTimer=new Timer();
        task=new TimerTask(){
           public void run() {
              if(state.equals("start")){
                 toTime(System.currentTimeMillis()-startTime);
                 lTimer.setText(timeBuffer);
              }else if(state.equals("fin")){fTimer.cancel();}
           }
        };
        
        fTimer.schedule(task, 0, 50);
     }
   
   public static  void main(String[] args) {
      
      // 서버정보
      String ip = "192.000.0.0"; //서버컴퓨터의 ip주소를 입력해 주세요 
      int port = 8080;
      
      Socket sock = null;
      InputStream is = null;
      InputStreamReader isr = null;
      BufferedReader br = null;
      
      try {
         // 서버 접속
         sock = new Socket(ip, port);
         
         // UI 생성
         NumberClient me = new NumberClient(sock);
         is = sock.getInputStream();
         isr = new InputStreamReader(is);
         br = new BufferedReader(isr);
         while(true) {
            //접속
            String temp = br.readLine();
            System.out.println(temp);

            if(temp.equals("/setUserName")) {
               temp="";
               me.name = br.readLine();
               me.txtName.setText(me.name);
//               me.diaInputName.setVisible(true);
               
            }
            if(temp.equals("/player")) {
               temp = "";
               me.luser2.setText(br.readLine()); // 상대플레이어 이름
               me.luser2.setVisible(true);
               
               String playerState = br.readLine();
               if(playerState.equals("/notready")) {
                  me.stateUser2.setText("대기중");
                  me.stateUser2.setBackground(Color.BLUE);
               }
               else if(playerState.equals("/already")){
                  me.stateUser2.setText("준비완료");
                  me.stateUser2.setBackground(Color.RED);
               }
               me.stateUser2.setVisible(true);
               me.revalidate(); // 화면 갱신
            }
            if(temp.equals("/ready")) {
               temp = "";
               String readyPlayer = br.readLine();
               System.out.println("readyPlayer, me.name" + readyPlayer + ", " + me.name);
               if(readyPlayer.equals(me.name)) {
                  me.luser1.setText(readyPlayer);
                  me.stateUser1.setText("준비완료");
                  me.stateUser1.setBackground(Color.RED);
                  me.stateUser1.setVisible(true);
               }
               else {
                  me.luser2.setText(readyPlayer);
                  me.stateUser2.setText("준비완료");
                  me.stateUser2.setBackground(Color.RED);
                  me.stateUser2.setVisible(true);
               }
               me.revalidate(); // 화면갱신
               
            }
            if(temp.equals("/start")) {
               // 게임시작
               temp ="";
               me.btnReady.setEnabled(false);
               me.btnExit.setEnabled(false);
//               me.mix();
            }
            if(temp.equals("/timer")) {
               //타이머 사용하지 않음
               temp="";
               String timer = br.readLine();
//               me.tfTimer.setText(timer);
            }
            if(temp.equals("/signal")) {
               // 게임시작 3초전 알림창
               temp="";
               String signal = br.readLine();
               me.lsign.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 30));
               me.lsign.setText(signal);
               me.diaGameStart.setTitle("게임 시작 알리미");
               me.diaGameStart.setVisible(true);
               
               if(signal.equals("Game start!")) {
                  me.lsign.setText(signal);
                  me.diaGameStart.dispose();
                  
                  me.mix();
                  for(int i=0; i<me.btns.length; i++) {
                     me.btns[i].setEnabled(true);
                  }
                  me.state = "start";
                  me.timer();
               }
            }
            if(temp.equals("/endgame")) {
               // 게임 1세트 종료
               temp="";
               me.state = "fin";
               String result = br.readLine();
               me.lsign.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
               me.lsign.setText(result);
               me.diaGameStart.setTitle("게임 결과");
               me.diaGameStart.setVisible(true);
               temp = result;
               me.btnReady.setEnabled(true);
               me.btnExit.setEnabled(true);
               String winner = br.readLine();
               if(me.name.equals(winner)) {
                  
               }
               me.stateUser1.setText("대기중");               
               me.stateUser1.setBackground(Color.BLUE);
               me.stateUser2.setText("대기중");
               me.stateUser2.setBackground(Color.BLUE);
               me.revalidate(); // 화면 갱신
               
            }
            if(temp.equals("/exit")) {
               // 다른유저 게임 종료
               temp="";
               me.state = "fin";
               
               me.luser2.setVisible(false);
               me.stateUser2.setVisible(false);
            }
            
            if(temp.equals("/timer")) temp="";
            if(!temp.isEmpty()) temp+="\n";
            msg+=temp;
            me.ta.setText(msg); // 서버에서 텍스트보낼 때, '\n' 개행문자 포함할 것
         }
         
         
      } catch (UnknownHostException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      } catch (java.lang.NullPointerException e) {
         System.out.println("----------- null  -----------");
      } finally {
         System.out.println("----------- 끝 -----------");
         try {
            if(br != null) br.close();
            if(isr != null) isr.close();
            if(is != null)is.close();
         } catch (IOException e) {
            e.printStackTrace();
         }
      }

   }

}