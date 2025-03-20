package com.example.wuzi;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.application.Application;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static com.example.wuzi.Configuration.Bsize;
import static com.example.wuzi.Configuration.Csize;
import static java.lang.Math.min;

public class Wuziqi extends Application {
    public static Board board;
    void adjust(Scene scene){
        Csize=min(scene.getWidth(),scene.getHeight())/(Bsize-1);
        board.adjust();
//            board=new Board(Bsize,Csize);
//            root.getChildren().clear();
//            root.getChildren().add(board);
        board.setLayoutX((scene.getWidth()-(Bsize-1)*Csize)/2);
        board.setLayoutY((scene.getHeight()-(Bsize-1)*Csize)/2);
    }
    public void start(Stage stage) throws Exception {

        Pane root=new Pane();
        board=new Board(Bsize,Csize);
        root.getChildren().add(board);
        Scene scene=new Scene(root,(Bsize-1)*Csize,(Bsize-1)*Csize);
        root.prefHeightProperty().bind(scene.heightProperty());
        root.prefWidthProperty().bind(scene.widthProperty());
        scene.widthProperty().addListener((obs,oldval,newval)-> adjust(scene));
        scene.heightProperty().addListener((obs,oldval,newval)-> adjust(scene));
        stage.setTitle("五子棋");
        stage.setScene(scene);
        stage.show();
    }
}
