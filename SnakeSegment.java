//Raja Hammad Mehmood
// Making Snake segment class
import java.awt.Color;
public class SnakeSegment  {
    
    private int row_; // row for the snake segment
    private int column_;// column for the snake segment
    /**
     * Constructor for snake segment
     * @param1 is  the row
     * @param2 is the column
     */

    public SnakeSegment(int row, int column) {
        row_=row;
        column_=column;
    }

    /**
     * getter for row
     * @return is the row
     */

    public int getRow() {
        return row_;
    }

     /**
     * getter for column
     * @return is the column
     */

    public int getColumn() {
        return column_;
    }

    /**
     * paints the segment
     * @param1 is  color
     */

    public void paintSegment(Color color) {
        Paint.setColor(color);
        Paint.fillRoundRect(row_*20,column_*20,20,20,14,14);
      
    }

    /**
     * highlights the segment
     */
    public void highlightSegment() {
        Paint.setColor(Color.WHITE);
       Paint.drawRoundRect(row_*20,column_*20,20,20,14,14);
      
    }
   
        

}