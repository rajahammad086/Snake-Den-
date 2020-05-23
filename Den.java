//Raja Hammad Mehmood
//Making the den class
import java.util.Scanner;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
     
 public class Den {
    private int row_;//row for the snake segmentcolumn for the snake segment
    private int column_;//column for the snake segment
    private int[][] contentsOfDen_;// 2d array for the grid
    
     public Den(){ //constructor for den
        contentsOfDen_=new int [30][20];
        for(int count=0;count<30;count++){
         for (int i=0;i<20;i++){
            contentsOfDen_[count][i]=1; //setting all of them to dirt. Later the first layer would be overwritten by the rocks
            }  
        }
        
         for(int count=0;count<30;count++){ // rocks top layer
         for (int i=0;i<20;i=i+19){
            contentsOfDen_[count][i]=2; 
            }  
        }
        
        for(int count=0;count<30;count=count+29){ //rocks side layer
         for (int i=0;i<20;i++){
            contentsOfDen_[count][i]=2; 
            }  
        }
        
        for(int count=0;count<20;){ // random rocks 
         int x=(int)(Math.random()*29)+1;
         int y=(int)(Math.random()*19)+1;
            if ( contentsOfDen_[x][y]!=2 ){
            contentsOfDen_[x][y]=2;
            count++;
            }
        }
        
        
        for(int count=0;count<20;){ // random food
         int x=(int)(Math.random()*29)+1;
         int y=(int)(Math.random()*19)+1;
            if ( contentsOfDen_[x][y]!=2 &&  contentsOfDen_[x][y]!=3){
            contentsOfDen_[x][y]=3;
            count++;
            }
        }
        
     }
     
      /**
     * 
     */

    public int getContent(int a, int b) {
        return contentsOfDen_[a][b];
    }
    
    /**
     * 
     */

    public boolean withinBound(int row, int column) {
        if (row>=0 && row<=30 && column >=0 && column<=20){
            return true;
        }
        else{
            return false;
        }
    
    }
    
    
     /**
     
     */

    public void addSnakeInDen() {
        
    }
    
    
     /**
     * 
     */

    public void paint() {
        for(int count=0;count<30;count++){ 
         for (int i=0;i<20;i++){
           if (contentsOfDen_[count][i]==1){
            Paint.setColor(153,76,0);
            Paint.fillRect(count*20+5, i*20+5, 15, 15);
           }
           
           else if (contentsOfDen_[count][i]==2){
            Paint.setColor(Color.BLACK);
            Paint.fillRect(count*20+5, i*20+5, 15, 15);
           }
           
            else if (contentsOfDen_[count][i]==3){
            Paint.setColor(Color.GREEN);
            Paint.fillRect(count*20+5, i*20+5, 15, 15);
           }
           
            }  
        }
    }
    
    
 }