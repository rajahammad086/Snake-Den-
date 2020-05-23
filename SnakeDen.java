//Raja Hammad Mehmood
//Making the snake den class
import java.util.Scanner;
import java.awt.*;
import javax.swing.*;

public class SnakeDen{
    public static void main(String[] args){
        Paint.buildWindow("Snake Den",100,100,600,400,Color.LIGHT_GRAY);
        Den den= new Den();
        den.paint();
        SnakeSegment segment= new SnakeSegment(6,7);
        segment.paintSegment(Color.RED);
        segment.highlightSegment();
        
    }
}