//Raja Hammad Mehmood

import java.awt.Color;
import java.awt.*;
import javax.swing.*;
public abstract class Snake {
    protected SnakeSegment[] segment_;
    protected int row_; // 
    protected int column_;//
    protected Color color_;
    protected boolean alive_;
    protected int length_;
    
  /**
     * Constructor for snake 
     * @param1 is  the row
     * @param2 is the column
     * @param3 is the color
     * @param4 is the length
     */

    public Snake(int row, int column, Color color, int length) {
        row_=row;
        column_=column;
        color_=color;
        alive_=true;
        length_=length;
    }

    
   /**
     * Constructor for snake 
     * @param1 is the color
     * @param2 is the segment of type SnakeSegment[]
     */
    public Snake( Color color, SnakeSegment[] segment) {
        segment_=segment;
        color_=color;
        length_=segment.length;
        alive_=true;
        
        
    }
    /**
     * getter for length
     * @return is the length
     */
    public int getLength() {
        return length_;
    }

   /**
     * getter for the head segment
     * @return is the head segment
     */

    public SnakeSegment getHeadSegment() {
        return segment_[0];
    }

    /**
     * getter for the tail segment
     * @return is the tail segment
     */
    public SnakeSegment getTailSegment() {
        return (segment_.length) -1;
    }
    
    /**
     * getter for the ith segment
     * @return is the ith segment
     */
    public SnakeSegment getIthSegment(int i) {
        return segment_[i];
    }
    
     /**
     * checks if the snake is alive
     * @return is true if alive
     */
    public boolean isAlive() {
        return alive_;
    }   
 
   /**
     * abstract method to get the next move.
     * @param1 is the den
     * @return is the direction
     */
    public abstract int getNextMove(Den den);
    
    /**
     * checks if it can do the next move
     * @param1 is the den
     * @param2 is the direction
     * @return is true if it can
     */
    public boolean canMove(Den den, int direction){
        int row;
        int column;
        if(direction== Paint.LEFT){
            row=getHeadSegment().getRow();
            column=getHeadSegment().getColumn()-1;
        }
        else if(directiom== Paint.RIGHT){
            row=getHeadSegment().getRow();
            column=getHeadSegment().getColumn()+1;
        }
        else if(directiom== Paint.TOP){
            row=getHeadSegment().getColumn()+1;
            column=getHeadSegment().getColumn();
        }
        else if(direction== Paint.BOTTOM){
            row=getHeadSegment().getRow()-1;
            column=getHeadSegment().getColumn();
        }
        return(den.getContent(row,column)!=2 && den.withinBound(row,column)==true);
        
    }
    
    
     /**
     * moves the snake
     * @param1 is the den
     * @param2 is the direction
     */
    public void moveSnake(Den den){
       if(canMove(den,direction)==true){
        int direc=getNextMove(den);
        if(direction== Paint.LEFT){
            column=getHeadSegment().getColumn()-1;
        }
        else if(directiom== Paint.RIGHT){
            column=getHeadSegment().getColumn()+1;
        }
        else if(directiom== Paint.TOP){
            row=getHeadSegment().getColumn()-1;
        }
        else if(direction== Paint.BOTTOM){
            row=getHeadSegment().getRow()+1;
          
        }
       }
        
     
        
    }
 
}