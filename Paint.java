import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Class for building a graphical window and drawing on it. Also listens for
 * keyboard events so that this class can be used for interactive games. This
 * class is intended for use in a Java programming class where objects are
 * introduced late. It exports several static methods, which other classes can
 * call to manipulate the window. These methods handle object creation and
 * manipulation, thus hiding it from the other classes. Window is drawn by
 * creating a window (JFrame) and a Paint object (Paint extends JPanel) and
 * setting the Paint object to be the content panel of the window. Paint
 * overrides paintComponent(), which then draws all of the shapes that have been
 * specified via calls to static methods below (e.g., fillRect). Although this
 * class does hide objects, it attempts to make the interface for drawing shapes
 * similar to the actual interface. So the methods are the same (although
 * static) and take the same kinds of arguments for the most part: fillOval,
 * drawOval, fillRect, drawRect, setColor, etc. There are a couple of
 * exceptions, however. setFont takes different arguments. In Java, setFont
 * takes a Font object. In the Paint class below it takes the three arguments to
 * the Font constructor: font class ("serif" or "sanserif"), font type (int,
 * e.g., Font.BOLD), and size (int 16). In addition, for convenience, in Paint
 * there are methods for drawing triangles. With the Graphics class, one must
 * use the drawPolygon or fillPolygon methods.
 * 
 * @author Marc Corliss, minor modifications Stina Bridgeman
 */
public class Paint extends JPanel implements KeyListener {
  /** Constant representing the maximum number of objects allowed */
  public static final int MAX_OBJECTS = 1000000;

  /** Constant representing no direction (for arrow input) */
  public static final int NONE = -1;

  /** Constant representing some other direction (for arrow input) */
  public static final int OTHER = -2;

  /** Constant representing the direction up (for arrow input) */
  public static final int UP = 0;

  /** Constant representing the direction down (for arrow input) */
  public static final int DOWN = 1;

  /** Constant representing the direction left (for arrow input) */
  public static final int LEFT = 2;

  /** Constant representing the direction right (for arrow input) */
  public static final int RIGHT = 3;

  /*
   * Note: variables below are 'static' so that programmers using this class do
   * not have to know about objects. Instead, they can make static calls to the
   * methods below, which then privately handle object creation and
   * manipulation.
   */

  /** Window to draw on */
  private static JFrame window;

  /** Content panel of window (a Paint panel) */
  private static Paint content;

  /** Array of objects (rectangles, ovals, strings, etc.) */
  private static DrawObject[] drawObjects;

  /** Number of drawn objects */
  private static int drawObjectsNum;

  /** Current color to paint with */
  private static Color currentColor;

  /** Current font to draw strings with */
  private static Font currentFont;

  /** A string buffer for holding text entered via keyboard */
  private static StringBuffer keyBuffer;

  /**
   * An integer holding the direction pushed for the last arrow (NONE means
   * arrow was not entered)
   */
  private static int arrow = NONE;

  /**
   * If true, every drawRect and similar command results in a repaint after the
   * command. If false, repaints do not occur until paint() is called (a
   * one-time repaint) or setAutoRepaint(true) is called.
   */
  private static boolean repaint_ = true;

  /**
   * Paint constructor Creates the panel and initializes all variables
   * 
   * @param bgColor
   *          the background color.
   */
  public Paint ( Color bgColor ) {
    // set this class to listen for keyboard events
    addKeyListener(this);
    // set the background color
    setBackground(bgColor);
    // copy the color to currentColor
    currentColor =
      new Color(bgColor.getRed(),bgColor.getGreen(),bgColor.getBlue());
    // set the default font
    currentFont = new Font("Dialog",Font.PLAIN,12);
    // initialize the objects array and initialize length
    drawObjects = new DrawObject[MAX_OBJECTS];
    drawObjectsNum = 0;
    // initialize the key buffer
    keyBuffer = new StringBuffer();
  }

  /**
   * Override paintComponent and draw panel Draws all the objects (rectangles,
   * ovals, string, etc.) on the window.
   * 
   * @param g
   *          a graphics object
   */
  public void paintComponent ( Graphics g ) {
    // call parent paintComponent method - draws background
    super.paintComponent(g);

    // draw shapes
    for ( int i = 0 ; i < drawObjectsNum ; i++ )
      drawObjects[i].draw(g);

    // request the focus for this panel to listen for keys entered
    requestFocusInWindow();
  }

  /**
   * Define the abstract keyReleased method Pushes key events onto the string
   * buffer
   * 
   * @param event
   *          the key event object
   */
  public void keyReleased ( KeyEvent event ) {
    if ( event.getKeyCode() == KeyEvent.VK_UP ) arrow = UP;
    else if ( event.getKeyCode() == KeyEvent.VK_DOWN ) arrow = DOWN;
    else if ( event.getKeyCode() == KeyEvent.VK_LEFT ) arrow = LEFT;
    else if ( event.getKeyCode() == KeyEvent.VK_RIGHT ) arrow = RIGHT;
    else {
      arrow = OTHER;
      keyBuffer.append(event.getKeyChar());
    }
    notifyKeypress();
  }

  private synchronized void notifyKeypress () {
    notifyAll();
  }

  /**
   * Define the abstract keyPressed method (not used)
   * 
   * @param event
   *          the key event object
   */
  public void keyPressed ( KeyEvent event ) {}

  /**
   * Define the abstract keyTyped method (not used)
   * 
   * @param event
   *          the key event object
   */
  public void keyTyped ( KeyEvent event ) {}

  /**
   * Pop the characters that were typed from the buffer. Input is buffered until
   * return is pressed.
   * 
   * @return string containing characters that were typed
   */
  private synchronized String popFromBuffer () {
    //		System.out.println("pop from buffer: " + keyBuffer.length());
    // return line of text stopping at a newline
    if ( keyBuffer.length() != 0 ) {
      String line = "";
      int index = keyBuffer.indexOf("\n");
      if ( index < 0 ) {
	return ""; // index = keyBuffer.length();
      }
      line = keyBuffer.substring(0,index);
      keyBuffer = keyBuffer.delete(0,index + 1); // delete includes \n
      return line;
    } else return "";
  }

  /**
   * Blocks until some input is given via keyboard and returns this input
   * 
   * @return input entered via the keyboard
   */
  private synchronized String waitForText () {
    // line to return
    String line = "";

    // loop infinitely until we get a line of text
    while ( true ) {
      line = content.popFromBuffer();
      if ( line.equals("") ) {
	try {
	  wait();
	} catch ( InterruptedException ignored ) {}
      } else break;
    }

    // return line
    return line;
  }

  /**
   * Blocks until a character is entered via keyboard and returns this input
   * 
   * @return character entered via the keyboard
   */
  private synchronized char waitForChar () {
    // wait until there is a character
    while ( true ) {
      if ( keyBuffer.length() == 0 ) {
	try {
	  wait();
	} catch ( InterruptedException ignored ) {}
      } else {
	break;
      }
    }
    char ch = keyBuffer.charAt(0);
    keyBuffer.delete(0,1);
    return ch;
  }

  /**
   * Get a line of typed text Blocks until it gets some input
   * 
   * @return line of typed text
   */
  public static String getln () {
    String line = content.waitForText();
    arrow = NONE; // ignore any arrows pressed while waiting
    return line;
  }

  /**
   * Get a single characters. Blocks until it gets some input.
   * 
   * @return character typed on keyboard
   */
  public static char getChar () {
    char ch = content.waitForChar();
    arrow = NONE; // ignore any arrows pressed while waiting
    return ch;
  }

  /**
   * Blocks until an arrow is entered is given via keyboard and returns the
   * direction of the arrow
   * 
   * @return input entered via the keyboard
   */
  private synchronized int waitForArrow () {
    int direction;

    // loop infinitely until arrow is not NONE
    while ( true ) {
      if ( arrow == NONE ) {
	try {
	  wait();
	} catch ( InterruptedException ignored ) {}
      } else break;
    }

    direction = arrow;
    arrow = NONE;

    // return direction
    return direction;
  }

  /**
   * Get an arrow key typed Blocks until an arrow key is typed
   * 
   * @return direction of arrow as an int (0-up, 1-down, 2-left, 3-right)
   */
  public static int getArrow () {
    return content.waitForArrow();
  }

  /**
   * Build a new window
   * 
   * @param title
   *          the title of the window
   * @param x
   *          starting x coordinate of upper lefthand corner of the window on
   *          the desktop (in pixels)
   * @param y
   *          starting y coordinate of upper lefthand corner of the window on
   *          the desktop (in pixels)
   * @param width
   *          width of the drawing area (in pixels)
   * @param height
   *          height of the drawing area (in pixels)
   * @param bgColor
   *          background color of the window
   */
  public static void buildWindow ( String title, int x, int y, int width,
				   int height, Color bgColor ) {
    // if window was already created then hide it until it's redrawn
    if ( window != null ) window.setVisible(false);

    // [ssb] made changes to window sizing so that the size of the drawing area
    // is width by height, to avoid having to account for the windoww title bar

    // create new window and display it
    window = new JFrame(title);
    window.setLocation(x,y);
    // window.setSize(width,height); // ssb
    window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    content = new Paint(bgColor);
    content.setPreferredSize(new Dimension(width,height)); // ssb
    content.setMinimumSize(new Dimension(width,height)); // ssb
    content.setSize(width,height); // ssb
    window.setContentPane(content);
    window.pack(); // ssb
    window.setVisible(true);
  }

  /**
   * Add an object to the window
   * 
   * @param o
   *          object to draw (e.g., rectangle)
   */
  private static void addObject ( DrawObject o ) {
    // check if more than MAX_OBJECTS and if so throw an exception
    // (usually happens when student has an infinite loop)
    if ( drawObjectsNum >= MAX_OBJECTS )
      throw new RuntimeException("Can create at most " + MAX_OBJECTS
				 + " objects (shapes, strings, etc.) with Paint");

    // check if drawObjects array has been initialized, if not then
    // buildWindow was not called
    if ( drawObjects == null )
      throw new RuntimeException("Must call Paint.buildWindow() before "
				 + "drawing any objects (shapes, strings, etc.) " + "with Paint");

    // otherwise add object to array
    drawObjects[drawObjectsNum++] = o;

    // repaint window
    if ( repaint_ ) {
      content.repaint();
    }
  }

  /**
   * Set the current color
   * 
   * @param c
   *          current color
   */
  public static void setColor ( Color c ) {
    currentColor = new Color(c.getRed(),c.getGreen(),c.getBlue());
  }

  /**
   * Set the current color
   * 
   * @param r
   *          red magnitude (0-255)
   * @param g
   *          green magnitude (0-255)
   * @param b
   *          blue magnitude (0-255)
   */
  public static void setColor ( int r, int g, int b ) {
    currentColor = new Color(r,g,b);
  }

  /**
   * Set the current font
   * 
   * @param name
   *          name of font ("serif" or "sanserif")
   * @param style
   *          style of font (Font.PLAIN, Font.BOLD, Font.ITALIC, or
   *          Font.BOLD+Font.ITALIC)
   * @param size
   *          size of font (e.g., 16)
   */
  public static void setFont ( String name, int style, int size ) {
    currentFont = new Font(name,style,size);
  }

  /**
   * Retrieve the font metrics for the current font.
   * 
   * @return the font metrics for the current font
   */
  public static FontMetrics getFontMetrics () {
    return content.getGraphics().getFontMetrics();
  }

  /**
   * Abstract class for an object on the window
   * 
   * @author Marc Corliss
   */
  private static abstract class DrawObject {
    /** Color of the string (initially set using current color) */
    protected Color c;

    // must define draw() method in subclasses...
    abstract public void draw ( Graphics g );
  }

  /**
   * Class for a string object
   * 
   * @author Marc Corliss
   */
  private static class DrawString extends DrawObject {
    /** String to draw */
    protected String str;

    /** x coordinate of lower, lefthand portion of the string */
    protected int x;

    /** y coordinate of lower, lefthand portion of the string */
    protected int y;

    /** Font of the string (initially set using current font) */
    protected Font f;

    /**
     * DrawString constructor
     * 
     * @param str
     *          string to draw
     * @param x
     *          x coordinate of lower, lefthand portion of the string
     * @param y
     *          y coordinate of lower, lefthand portion of the string
     */
    public DrawString ( String str, int x, int y ) {
      this.str = str;
      this.x = x;
      this.y = y;
      this.c = currentColor;
      this.f = currentFont;
    }

    /**
     * Draw the object on the window
     * 
     * @param g
     *          graphics object
     */
    public void draw ( Graphics g ) {
      g.setColor(c);
      g.setFont(f);
      g.drawString(str,x,y);
    }
  }

  /**
   * Draw a string object onto the window
   * 
   * @param str
   *          string to draw
   * @param x
   *          x coordinate of lower, lefthand portion of the string
   * @param y
   *          y coordinate of lower, lefthand portion of the string
   */
  public static void drawString ( String str, int x, int y ) {
    addObject((DrawObject) new DrawString(str,x,y));
  }

  /**
   * Class for a line object
   * 
   * @author Marc Corliss
   */
  private static class DrawLine extends DrawObject {
    /** x coordinate of first point */
    protected int x1;

    /** y coordinate of first point */
    protected int y1;

    /** x coordinate of second point */
    protected int x2;

    /** y coordinate of second point */
    protected int y2;

    /**
     * DrawLine constructor
     * 
     * @param x1
     *          x coordinate of first point
     * @param y1
     *          y coordinate of first point
     * @param x2
     *          x coordinate of second point
     * @param y2
     *          y coordinate of second point
     */
    public DrawLine ( int x1, int y1, int x2, int y2 ) {
      this.x1 = x1;
      this.y1 = y1;
      this.x2 = x2;
      this.y2 = y2;
      this.c = currentColor;
    }

    /**
     * Draw the object on the window
     * 
     * @param g
     *          graphics object
     */
    public void draw ( Graphics g ) {
      g.setColor(c);
      g.drawLine(x1,y1,x2,y2);
    }
  }

  /**
   * Draw a line object onto the window
   * 
   * @param x1
   *          x coordinate of first point
   * @param y1
   *          y coordinate of first point
   * @param x2
   *          x coordinate of second point
   * @param y2
   *          y coordinate of second point
   */
  public static void drawLine ( int x1, int y1, int x2, int y2 ) {
    addObject((DrawObject) new DrawLine(x1,y1,x2,y2));
  }

  /**
   * Class for a rectangle object
   * 
   * @author Marc Corliss
   */
  private static class DrawRect extends DrawObject {
    /** x coordinate of upper, lefthand corner of rectangle */
    protected int x;

    /** y coordinate of upper, lefthand corner of rectangle */
    protected int y;

    /** Width of rectangle */
    protected int width;

    /** Height of rectangle */
    protected int height;

    /**
     * DrawRect constructor
     * 
     * @param x
     *          x coordinate of upper, lefthand corner of rectangle
     * @param y
     *          y coordinate of upper, lefthand corner of rectangle
     * @param width
     *          Width of rectangle
     * @param height
     *          Height of rectangle
     */
    public DrawRect ( int x, int y, int width, int height ) {
      this.x = x;
      this.y = y;
      this.height = height;
      this.width = width;
      this.c = currentColor;
    }

    /**
     * Draw the object on the window
     * 
     * @param g
     *          graphics object
     */
    public void draw ( Graphics g ) {
      g.setColor(c);
      g.drawRect(x,y,width,height);
    }
  }

  /**
   * Draw a rectangle outline to the window
   * 
   * @param x
   *          x coordinate of upper, lefthand corner of rectangle
   * @param y
   *          y coordinate of upper, lefthand corner of rectangle
   * @param width
   *          Width of rectangle
   * @param height
   *          Height of rectangle
   */
  public static void drawRect ( int x, int y, int width, int height ) {
    addObject((DrawObject) new DrawRect(x,y,width,height));
  }

  /**
   * Abstract class for a filled-in rectangle object
   * 
   * @author Marc Corliss
   */
  private static class FillRect extends DrawRect {
    /**
     * FillRect constructor
     * 
     * @param x
     *          x coordinate of upper, lefthand corner of rectangle
     * @param y
     *          y coordinate of upper, lefthand corner of rectangle
     * @param width
     *          Width of rectangle
     * @param height
     *          Height of rectangle
     */
    public FillRect ( int x, int y, int width, int height ) {
      super(x,y,width,height);
    }

    /**
     * Draw the object on the window
     * 
     * @param g
     *          graphics object
     */
    public void draw ( Graphics g ) {
      g.setColor(c);
      g.fillRect(x,y,width,height);
    }
  }

  /**
   * Draw a filled-in rectangle to the window
   * 
   * @param x
   *          x coordinate of upper, lefthand corner of rectangle
   * @param y
   *          y coordinate of upper, lefthand corner of rectangle
   * @param width
   *          Width of rectangle
   * @param height
   *          Height of rectangle
   */
  public static void fillRect ( int x, int y, int width, int height ) {
    addObject((DrawObject) new FillRect(x,y,width,height));
  }

  /**
   * Class for an oval object
   * 
   * @author Marc Corliss
   */
  private static class DrawOval extends DrawObject {
    /** x coordinate of upper-lefthand corner of box that bounds oval */
    protected int x;

    /** y coordinate of upper-lefthand corner of box that bounds oval */
    protected int y;

    /** Width of box that bounds oval */
    protected int width;

    /** Height of box that bounds oval */
    protected int height;

    /**
     * DrawOval constructor
     * 
     * @param x
     *          x coordinate of upper-lefthand corner of box that bounds oval
     * @param y
     *          y coordinate of upper-lefthand corner of box that bounds oval
     * @param width
     *          width of box that bounds oval
     * @param height
     *          height of box that bounds oval
     */
    public DrawOval ( int x, int y, int width, int height ) {
      this.x = x;
      this.y = y;
      this.height = height;
      this.width = width;
      this.c = currentColor;
    }

    /**
     * Draw the object on the window
     * 
     * @param g
     *          graphics object
     */
    public void draw ( Graphics g ) {
      g.setColor(c);
      g.drawOval(x,y,width,height);
    }
  }

  /**
   * Draw an oval outline to the window
   * 
   * @param x
   *          x coordinate of upper-lefthand corner of box that bounds oval
   * @param y
   *          y coordinate of upper-lefthand corner of box that bounds oval
   * @param width
   *          width of box that bounds oval
   * @param height
   *          height of box that bounds oval
   */
  public static void drawOval ( int x, int y, int width, int height ) {
    addObject((DrawObject) new DrawOval(x,y,width,height));
  }

  /**
   * Class for a filled-in oval object
   * 
   * @author Marc Corliss
   */
  private static class FillOval extends DrawOval {
    /**
     * FillOval constructor
     * 
     * @param x
     *          x coordinate of upper-lefthand corner of box that bounds oval
     * @param y
     *          y coordinate of upper-lefthand corner of box that bounds oval
     * @param width
     *          width of box that bounds oval
     * @param height
     *          height of box that bounds oval
     */
    public FillOval ( int x, int y, int width, int height ) {
      super(x,y,width,height);
    }

    /**
     * Draw the object on the window
     * 
     * @param g
     *          graphics object
     */
    public void draw ( Graphics g ) {
      g.setColor(c);
      g.fillOval(x,y,width,height);
    }
  }

  /**
   * Draw a filled-in oval to the window
   * 
   * @param x
   *          x coordinate of upper-lefthand corner of box that bounds oval
   * @param y
   *          y coordinate of upper-lefthand corner of box that bounds oval
   * @param width
   *          width of box that bounds oval
   * @param height
   *          height of box that bounds oval
   */
  public static void fillOval ( int x, int y, int width, int height ) {
    addObject((DrawObject) new FillOval(x,y,width,height));
  }

  /**
   * Class for a rounded rectangle object
   * 
   * @author Marc Corliss
   */
  private static class DrawRoundRect extends DrawObject {
    /**
     * x coordinate of upper-lefthand corner of box that bounds rounded
     * rectangle
     */
    protected int x;

    /**
     * y coordinate of upper-lefthand corner of box that bounds rounded
     * rectangle
     */
    protected int y;

    /** Width of rounded rectangle */
    protected int width;

    /** Height of rounded rectangle */
    protected int height;

    /** Horizontal diameter of corner arc of elipse */
    protected int xdiam;

    /** Vertical diameter of corner arc of elipse */
    protected int ydiam;

    /**
     * Constructor DrawRoundRect
     * 
     * @param x
     *          x coordinate of upper-lefthand corner of box that bounds rounded
     *          rectangle
     * @param y
     *          y coordinate of upper-lefthand corner of box that bounds rounded
     *          rectangle
     * @param width
     *          width of rounded rectangle
     * @param height
     *          height of rounded rectangle
     * @param xdiam
     *          horizontal diameter of corner arc of elipse
     * @param ydiam
     *          vertical diameter of corner arc of elipse
     */
    public DrawRoundRect ( int x, int y, int width, int height, int xdiam,
			   int ydiam ) {
      this.x = x;
      this.y = y;
      this.height = height;
      this.width = width;
      this.xdiam = xdiam;
      this.ydiam = ydiam;
      this.c = currentColor;
    }

    /**
     * Draw the object on the window
     * 
     * @param g
     *          graphics object
     */
    public void draw ( Graphics g ) {
      g.setColor(c);
      g.drawRoundRect(x,y,width,height,xdiam,ydiam);
    }
  }

  /**
   * Draw a rounded rectangle outline to the window
   * 
   * @param x
   *          x coordinate of upper-lefthand corner of box that bounds rounded
   *          rectangle
   * @param y
   *          y coordinate of upper-lefthand corner of box that bounds rounded
   *          rectangle
   * @param width
   *          width of rounded rectangle
   * @param height
   *          height of rounded rectangle
   * @param xdiam
   *          horizontal diameter of corner arc of elipse
   * @param ydiam
   *          vertical diameter of corner arc of elipse
   */
  public static void drawRoundRect ( int x, int y, int width, int height,
				     int xdiam, int ydiam ) {
    addObject((DrawObject) new DrawRoundRect(x,y,width,height,xdiam,ydiam));
  }

  /**
   * Class for a filled-in rounded rectangle object
   * 
   * @author Marc Corliss
   */
  private static class FillRoundRect extends DrawRoundRect {
    /**
     * Constructor FillRoundRect
     * 
     * @param x
     *          x coordinate of upper-lefthand corner of box that bounds rounded
     *          rectangle
     * @param y
     *          y coordinate of upper-lefthand corner of box that bounds rounded
     *          rectangle
     * @param width
     *          width of rounded rectangle
     * @param height
     *          height of rounded rectangle
     * @param xdiam
     *          horizontal diameter of corner arc of elipse
     * @param ydiam
     *          vertical diameter of corner arc of elipse
     */
    public FillRoundRect ( int x, int y, int width, int height, int xdiam,
			   int ydiam ) {
      super(x,y,width,height,xdiam,ydiam);
    }

    /**
     * Draw the object on the window
     * 
     * @param g
     *          graphics object
     */
    public void draw ( Graphics g ) {
      g.setColor(c);
      g.fillRoundRect(x,y,width,height,xdiam,ydiam);
    }
  }

  /**
   * Draw a filled-in rounded rectangle to the window
   * 
   * @param x
   *          x coordinate of upper-lefthand corner of box that bounds rounded
   *          rectangle
   * @param y
   *          y coordinate of upper-lefthand corner of box that bounds rounded
   *          rectangle
   * @param width
   *          width of rounded rectangle
   * @param height
   *          height of rounded rectangle
   * @param xdiam
   *          horizontal diameter of corner arc of elipse
   * @param ydiam
   *          vertical diameter of corner arc of elipse
   */
  public static void fillRoundRect ( int x, int y, int width, int height,
				     int xdiam, int ydiam ) {
    addObject((DrawObject) new FillRoundRect(x,y,width,height,xdiam,ydiam));
  }

  /**
   * Class for an arc object
   * 
   * @author Marc Corliss
   */
  private static class DrawArc extends DrawObject {
    /**
     * x coordinate of upper-lefthand corner of the box containing the oval,
     * which the arc is contained within
     */
    protected int x;

    /**
     * y coordinate of upper-lefthand corner of the box containing the oval,
     * which the arc is contained within
     */
    protected int y;

    /** Width of the box containing the oval, which the arc is contained within */
    protected int width;

    /** Height of the box containing the oval, which the arc is contained within */
    protected int height;

    /** The starting angle of the arc (0 is at 3 o'clock position) */
    protected int startAngle;

    /**
     * The degrees to extend the arc from the starting angle (0 is at 3 o'clock
     * position)
     */
    protected int arcAngle;

    /**
     * DrawArc constructor
     * 
     * @param x
     *          x coordinate of upper-lefthand corner of the box containing the
     *          oval, which the arc is contained within
     * @param y
     *          y coordinate of upper-lefthand corner of the box containing the
     *          oval, which the arc is contained within
     * @param width
     *          width of the box containing the oval, which the arc is contained
     *          within
     * @param height
     *          height of the box containing the oval, which the arc is
     *          contained within
     * @param startAngle
     *          the starting angle of the arc (0 is at 3 o'clock position)
     * @param arcAngle
     *          the degrees to extend the arc from the starting angle (0 is at 3
     *          o'clock position)
     */
    public DrawArc ( int x, int y, int width, int height, int startAngle,
		     int arcAngle ) {
      this.x = x;
      this.y = y;
      this.height = height;
      this.width = width;
      this.startAngle = startAngle;
      this.arcAngle = arcAngle;
      this.c = currentColor;
    }

    /**
     * Draw the object on the window
     * 
     * @param g
     *          graphics object
     */
    public void draw ( Graphics g ) {
      g.setColor(c);
      g.drawArc(x,y,width,height,startAngle,arcAngle);
    }
  }

  /**
   * Draw an arc outline to the window
   * 
   * @param x
   *          x coordinate of upper-lefthand corner of the box containing the
   *          oval, which the arc is contained within
   * @param y
   *          y coordinate of upper-lefthand corner of the box containing the
   *          oval, which the arc is contained within
   * @param width
   *          width of the box containing the oval, which the arc is contained
   *          within
   * @param height
   *          height of the box containing the oval, which the arc is contained
   *          within
   * @param startAngle
   *          the starting angle of the arc (0 is at 3 o'clock position)
   * @param arcAngle
   *          the degrees to extend the arc from the starting angle (0 is at 3
   *          o'clock position)
   */
  public static void drawArc ( int x, int y, int width, int height,
			       int startAngle, int arcAngle ) {
    addObject((DrawObject) new DrawArc(x,y,width,height,startAngle,arcAngle));
  }

  /**
   * Class for a filled-in arc object
   * 
   * @author Marc Corliss
   */
  private static class FillArc extends DrawArc {
    /**
     * FillArc constructor
     * 
     * @param x
     *          x coordinate of upper-lefthand corner of the box containing the
     *          oval, which the arc is contained within
     * @param y
     *          y coordinate of upper-lefthand corner of the box containing the
     *          oval, which the arc is contained within
     * @param width
     *          width of the box containing the oval, which the arc is contained
     *          within
     * @param height
     *          height of the box containing the oval, which the arc is
     *          contained within
     * @param startAngle
     *          the starting angle of the arc (0 is at 3 o'clock position)
     * @param arcAngle
     *          the degrees to extend the arc from the starting angle (0 is at 3
     *          o'clock position)
     */
    public FillArc ( int x, int y, int width, int height, int startAngle,
		     int arcAngle ) {
      super(x,y,width,height,startAngle,arcAngle);
    }

    /**
     * Draw the object on the window
     * 
     * @param g
     *          graphics object
     */
    public void draw ( Graphics g ) {
      g.setColor(c);
      g.fillArc(x,y,width,height,startAngle,arcAngle);
    }
  }

  /**
   * Draw a filled-in arc to the window
   * 
   * @param x
   *          x coordinate of upper-lefthand corner of the box containing the
   *          oval, which the arc is contained within
   * @param y
   *          y coordinate of upper-lefthand corner of the box containing the
   *          oval, which the arc is contained within
   * @param width
   *          width of the box containing the oval, which the arc is contained
   *          within
   * @param height
   *          height of the box containing the oval, which the arc is contained
   *          within
   * @param startAngle
   *          the starting angle of the arc (0 is at 3 o'clock position)
   * @param arcAngle
   *          the degrees to extend the arc from the starting angle (0 is at 3
   *          o'clock position)
   */
  public static void fillArc ( int x, int y, int width, int height,
			       int startAngle, int arcAngle ) {
    addObject((DrawObject) new FillArc(x,y,width,height,startAngle,arcAngle));
  }

  /**
   * Class for a polygon object
   * 
   * @author Marc Corliss
   */
  private static class DrawPolygon extends DrawObject {
    /** Array of x coordinates (at least 1 element for each point) */
    protected int[] xPoints;

    /** Array of y coordinates (at least 1 element for each point) */
    protected int[] yPoints;

    /** Number of points */
    protected int nPoints;

    /**
     * DrawPolygon consructor
     * 
     * @param xPoints
     *          array of x coordinates (at least 1 element for each point)
     * @param yPoints
     *          array of y coordinates (at least 1 element for each point)
     * @param nPoints
     *          number of points
     */
    public DrawPolygon ( int[] xPoints, int[] yPoints, int nPoints ) {
      this.xPoints = xPoints;
      this.yPoints = yPoints;
      this.nPoints = nPoints;
      this.c = currentColor;
    }

    /**
     * Draw the object on the window
     * 
     * @param g
     *          graphics object
     */
    public void draw ( Graphics g ) {
      g.setColor(c);
      g.drawPolygon(xPoints,yPoints,nPoints);
    }
  }

  /**
   * Draw a polygon outline onto the window
   * 
   * @param xPoints
   *          array of x coordinates (at least 1 element for each point)
   * @param yPoints
   *          array of y coordinates (at least 1 element for each point)
   * @param nPoints
   *          number of points
   */
  public static void drawPolygon ( int[] xPoints, int[] yPoints, int nPoints ) {
    addObject((DrawObject) new DrawPolygon(xPoints,yPoints,nPoints));
  }

  /**
   * Class for a filled-in polygon object
   * 
   * @author Marc Corliss
   */
  private static class FillPolygon extends DrawPolygon {
    /**
     * FillPolygon consructor
     * 
     * @param xPoints
     *          array of x coordinates (at least 1 element for each point)
     * @param yPoints
     *          array of y coordinates (at least 1 element for each point)
     * @param nPoints
     *          number of points
     */
    public FillPolygon ( int[] xPoints, int[] yPoints, int nPoints ) {
      super(xPoints,yPoints,nPoints);
    }

    /**
     * Draw the object on the window
     * 
     * @param g
     *          graphics object
     */
    public void draw ( Graphics g ) {
      g.setColor(c);
      g.fillPolygon(xPoints,yPoints,nPoints);
    }
  }

  /**
   * Draw a filled-in polygon onto the window
   * 
   * @param xPoints
   *          array of x coordinates (at least 1 element for each point)
   * @param yPoints
   *          array of y coordinates (at least 1 element for each point)
   * @param nPoints
   *          number of points
   */
  public static void fillPolygon ( int[] xPoints, int[] yPoints, int nPoints ) {
    addObject((DrawObject) new FillPolygon(xPoints,yPoints,nPoints));
  }

  /**
   * Class for a triangle object
   * 
   * @author Marc Corliss
   */
  private static class DrawTriangle extends DrawObject {
    /** x coordinate of first point */
    protected int x1;

    /** y coordinate of first point */
    protected int y1;

    /** x coordinate of second point */
    protected int x2;

    /** y coordinate of second point */
    protected int y2;

    /** x coordinate of third point */
    protected int x3;

    /** y coordinate of third point */
    protected int y3;

    /**
     * DrawTriangle constructor
     * 
     * @param x1
     *          x coordinate of first point
     * @param y1
     *          y coordinate of first point
     * @param x2
     *          x coordinate of second point
     * @param y2
     *          y coordinate of second point
     * @param x3
     *          x coordinate of third point
     * @param y3
     *          y coordinate of third point
     */
    public DrawTriangle ( int x1, int y1, int x2, int y2, int x3, int y3 ) {
      this.x1 = x1;
      this.y1 = y1;
      this.x2 = x2;
      this.y2 = y2;
      this.x3 = x3;
      this.y3 = y3;
      this.c = currentColor;
    }

    /**
     * Draw the object on the window
     * 
     * @param g
     *          graphics object
     */
    public void draw ( Graphics g ) {
      g.setColor(c);
      g.drawPolygon(new int[] { x1, x2, x3 },new int[] { y1, y2, y3 },3);
    }
  }

  /**
   * Draw a triangle outline onto the window
   * 
   * @param x1
   *          x coordinate of first point
   * @param y1
   *          y coordinate of first point
   * @param x2
   *          x coordinate of second point
   * @param y2
   *          y coordinate of second point
   * @param x3
   *          x coordinate of third point
   * @param y3
   *          y coordinate of third point
   */
  public static void drawTriangle ( int x1, int y1, int x2, int y2, int x3,
				    int y3 ) {
    addObject((DrawObject) new DrawTriangle(x1,y1,x2,y2,x3,y3));
  }

  /**
   * Class for a filled-in triangle object
   * 
   * @author Marc Corliss
   */
  private static class FillTriangle extends DrawTriangle {
    /**
     * FillTriangle constructor
     * 
     * @param x1
     *          x coordinate of first point
     * @param y1
     *          y coordinate of first point
     * @param x2
     *          x coordinate of second point
     * @param y2
     *          y coordinate of second point
     * @param x3
     *          x coordinate of third point
     * @param y3
     *          y coordinate of third point
     */
    public FillTriangle ( int x1, int y1, int x2, int y2, int x3, int y3 ) {
      super(x1,y1,x2,y2,x3,y3);
    }

    /**
     * Draw the object on the window
     * 
     * @param g
     *          graphics object
     */
    public void draw ( Graphics g ) {
      g.setColor(c);
      g.fillPolygon(new int[] { x1, x2, x3 },new int[] { y1, y2, y3 },3);
    }
  }

  /**
   * Draw a filled-in triangle onto the window
   * 
   * @param x1
   *          x coordinate of first point
   * @param y1
   *          y coordinate of first point
   * @param x2
   *          x coordinate of second point
   * @param y2
   *          y coordinate of second point
   * @param x3
   *          x coordinate of third point
   * @param y3
   *          y coordinate of third point
   */
  public static void fillTriangle ( int x1, int y1, int x2, int y2, int x3,
				    int y3 ) {
    addObject((DrawObject) new FillTriangle(x1,y1,x2,y2,x3,y3));
  }

  /**
   * Clear the drawing window.
   */
  public static void clear () {
    drawObjectsNum = 0;
  }

  /**
   * Set whether or not repaint occurs automatically after a drawRect or similar
   * drawing command. Triggers a repaint if auto is true. 
   * 
   * @return the previous auto-repaint setting
   */

  public static boolean setAutoRepaint ( boolean auto ) {
    boolean old = repaint_;
    repaint_ = auto;
    if ( repaint_ ) {
      content.repaint();
    }
    return old;
  }

  /**
   * Paint the drawing window.
   */
  public static void paint () {
    content.repaint();
  }
}
