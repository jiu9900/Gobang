package com.example.wuzi;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.Alert;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Iterator;
import java.util.Optional;
import java.util.Stack;

import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import static com.example.wuzi.Configuration.Bsize;
import static com.example.wuzi.Configuration.Csize;
import static com.example.wuzi.Configuration.WinCount;


public class Board extends Pane{
    public static boolean col=false,clickable=true;
    public static int[][] a;
    public static Point[][] pt;
    private final Rectangle background;
    public static Socket socket;
    public static BufferedReader in;
    public static PrintWriter out;
    public static Stack<Point> stk=new Stack<>();
    Board(int X,double Y){
        try{
            socket=new Socket("172.20.10.4",Server.PORT);
            in=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out=new PrintWriter(socket.getOutputStream());
        }catch(IOException e){
            e.printStackTrace();
        }
        new Thread(()->{
            try{
                String move;
                while((move=in.readLine())!=null){
                    System.out.println("收到信息：" + move);
                    if(move.equals("reset")){askSave(this::reset);continue;}
                    if(move.equals("exit")){askSave(()->{Platform.exit();System.exit(0);});}
                    else{
                        String[] xy=move.split(" ");
                        int x=Integer.parseInt(xy[0]),y=Integer.parseInt(xy[1]);
                        pt[x][y].Click();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        Bsize=X;
        Csize=Y;
        background=new Rectangle((X-1)*Y,(X-1)*Y);
        background.setFill(Color.web("#FFE4C4"));
        getChildren().add(background);
        drawLines(X,Y);
        BuildPoints();
    }
    String status;
    String getStatus(){
        if(status!=null) return status;
        for(int i=1;i<=Bsize;++i) for(int j=1;j<=Bsize;++j) if(a[i][j]!=-1&&check(i,j)){
            if(a[i][j]==1) return status="白棋获胜！";
            return status="黑棋获胜！";
        }
        return status="平局！";
    }
    boolean full(){return stk.size()==Bsize*Bsize;}
    void adjust(){//call when Csize changes
        getChildren().removeIf(node -> node instanceof Line);
        background.setWidth((Bsize-1)*Csize);background.setHeight((Bsize-1)*Csize);
        drawLines(Bsize,Csize);
        for(int i=1;i<=Bsize;++i) for(int j=1;j<=Bsize;++j) pt[i][j].adjust();
    }
    void drawLines(int X,double Y){
        for(int i=1;i<=X;++i) for(int j=1;j<=X;++j){
            getChildren().add(new Line(0,(i-1)*Y,(X-1)*Y,(i-1)*Y));
            getChildren().add(new Line((j-1)*Y,(X-1)*Y,(j-1)*Y,0));
        }
    }
    void BuildPoints(){
        a=new int[Bsize+1][Bsize+1];
        pt=new Point[Bsize+1][Bsize+1];
        for(int i=1;i<=Bsize;++i) for(int j=1;j<=Bsize;++j){
            a[i][j]=-1;
            getChildren().add(pt[i][j]=new Point(i,j));
        }
    }
    void saveGame(){
        FileChooser fileChooser=new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text files (*.txt)", "*.txt"));
        File file=fileChooser.showSaveDialog(new Stage());
        if(file==null) return;
        try(PrintWriter writer=new PrintWriter(new FileWriter(file))){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String formattedDate = sdf.format(new Date());
            writer.println(formattedDate);
            writer.printf("共 %d 手 %s\n",stk.size(),getStatus());
            for(Point i:stk) writer.println(i);
        }catch (IOException e){e.printStackTrace();}
    }
    void askSave(Runnable callback){
        System.out.println("do askSave");
        Platform.runLater(()->{
            Alert query=new Alert(Alert.AlertType.CONFIRMATION);
            query.setTitle("我是奶龙！");
            query.setHeaderText("是否保存棋谱？");
            Optional<ButtonType> result=query.showAndWait();
            if(result.isPresent()&&result.get()==ButtonType.OK) saveGame();
            callback.run();
        });
    }
    void reset(){
        System.out.println("do reset");
        Platform.runLater(()->{
            status=null;
            stk.clear();
            col=false;clickable=true;
            getChildren().clear();
            getChildren().add(background);
            drawLines(Bsize,Csize);
            BuildPoints();
        });
    }
    void end(){
        Alert qwq=new Alert(Alert.AlertType.CONFIRMATION);
        qwq.setTitle("Game Over!");
        qwq.setHeaderText(getStatus());
//        if(stk.size()==Bsize*Bsize) qwq.setHeaderText("平局！");
//        else if(col) qwq.setHeaderText("白棋获胜！");
//        else qwq.setHeaderText("黑棋获胜！");
        qwq.setContentText("再来一盘？");
        Optional<ButtonType> result=qwq.showAndWait();

        if(result.get()!=ButtonType.OK){
            askSave(()->{out.println("exit");out.flush();Platform.exit();System.exit(0);});
        }else{
            askSave(this::reset);
            out.println("reset");
            out.flush();
        }
    }
    boolean check(int x,int y){
        int c1,c2;

        c1=c2=0;
        for(int xx=x,yy=y;xx<=Bsize&&yy<=Bsize;xx++,yy++){
            if(a[xx][yy]!=a[x][y]) break;
            c1++;
        }
        for(int xx=x,yy=y;xx>0&&yy>0;xx--,yy--){
            if(a[xx][yy]!=a[x][y]) break;
            c2++;
        }
        if(c1+c2>WinCount) return true;

        c1=c2=0;
        for(int xx=x,yy=y;xx<=Bsize&&yy>0;xx++,yy--){
            if(a[xx][yy]!=a[x][y]) break;
            c1++;
        }
        for(int xx=x,yy=y;xx>0&&yy<=Bsize;xx--,yy++){
            if(a[xx][yy]!=a[x][y]) break;
            c2++;
        }
        if(c1+c2>WinCount) return true;

        c1=c2=0;
        for(int i=x;i>0;i--){
            if(a[i][y]!=a[x][y]) break;
            c1++;
        }
        for(int i=x;i<=Bsize;i++){
            if(a[i][y]!=a[x][y]) break;
            c2++;
        }
        if(c1+c2>WinCount) return true;

        c1=c2=0;
        for(int i=y;i>0;i--){
            if(a[x][i]!=a[x][y]) break;
            c1++;
        }
        for(int i=y;i<=Bsize;i++){
            if(a[x][i]!=a[x][y]) break;
            c2++;
        }
        if(c1+c2>WinCount) return true;
        return false;
    }
}
