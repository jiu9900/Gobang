package com.example.wuzi;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import static com.example.wuzi.Configuration.Csize;

public class Point extends StackPane{
    private int x,y;
    private boolean vis;
    Circle stone;
    public String toString(){return String.format("(%d,%d)",x,y);}
    Point(int x,int y){
        this.x=x;this.y=y;
        setPrefSize(Csize,Csize);
        setLayoutX((x-1.5)*Csize);
        setLayoutY((y-1.5)*Csize);
        stone=new Circle(Csize/2.0);
        stone.setVisible(false);
        getChildren().add(stone);

        setOnMouseClicked(this::Click);
    }
    void adjust(){
        setPrefSize(Csize,Csize);
        setLayoutX((x-1.5)*Csize);
        setLayoutY((y-1.5)*Csize);
        stone.setRadius(Csize/2.0);
        toFront();
    }
    void Click(MouseEvent event){
        if(vis||!Board.clickable) return;
        vis=true;
        Board.stk.push(this);
        Board.clickable=false;
        System.out.printf("%d %d\n",x,y);
        Board.out.printf("%d %d\n",x,y);Board.out.flush();
        if(Board.col){
            stone.setFill(Color.WHITE);
            Board.a[x][y]=1;
        }else{
            stone.setFill(Color.BLACK);
            Board.a[x][y]=0;
        }
        stone.setVisible(true);
        if(Wuziqi.board.check(x,y)||Wuziqi.board.full()){
            Wuziqi.board.end();
            return;
        }
        Board.col=!Board.col;
    }
    void Click(){
//        if(vis) return;
        vis=true;Board.stk.push(this);
        Board.clickable=true;
        System.out.printf("%d %d\n",x,y);
        if(Board.col){
            stone.setFill(Color.WHITE);
            Board.a[x][y]=1;
        }else{
            stone.setFill(Color.BLACK);
            Board.a[x][y]=0;
        }
        stone.setVisible(true);
        Board.col=!Board.col;
    }
}
