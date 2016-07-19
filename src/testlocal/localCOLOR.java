package testlocal;
import java.awt.Graphics;  
 import java.awt.image.BufferedImage;  
 import java.util.ArrayList;  
 import java.util.List;  
 import javax.swing.JFrame;  
 import javax.swing.JPanel;  
 import org.opencv.core.Core;  
 import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;  
 import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;  
 import org.opencv.imgproc.Imgproc;  
 import org.opencv.core.CvType;  

 class Panel extends JPanel{  
	   private static final long serialVersionUID = 1L;  
	   private BufferedImage image;    
	   // Create a constructor method  
	   public Panel(){  
	     super();  
	   }  
	   private BufferedImage getimage(){  
	     return image;  
	   }  
	   public void setimage(BufferedImage newimage){  
	     image=newimage;  
	     return;  
	   }  
	   public void setimagewithMat(Mat newimage){  
	     image=this.matToBufferedImage(newimage);  
	     return;  
	   }  
	   public BufferedImage matToBufferedImage(Mat matrix) {  
		     int cols = matrix.cols();  
		     int rows = matrix.rows();  
		     int elemSize = (int)matrix.elemSize();  
		     byte[] data = new byte[cols * rows * elemSize];  
		     int type;  
		     matrix.get(0, 0, data);  
		     switch (matrix.channels()) {  
		       case 1:  
		         type = BufferedImage.TYPE_BYTE_GRAY;  
		         break;  
		       case 3:  
		         type = BufferedImage.TYPE_3BYTE_BGR;  
		         // bgr to rgb  
		         byte b;  
		         for(int i=0; i<data.length; i=i+3) {  
		           b = data[i];  
		           data[i] = data[i+2];  
		           data[i+2] = b;  
		         }  
		         break;  
		       default:  
		         return null;  
		     }  
		     BufferedImage image2 = new BufferedImage(cols, rows, type);  
		     image2.getRaster().setDataElements(0, 0, cols, rows, data);  
		     return image2;  
		   }  
		   @Override  
		   
		   protected void paintComponent(Graphics g){ 
			   
		      super.paintComponent(g);  
		      //BufferedImage temp=new BufferedImage(640, 480, BufferedImage.TYPE_3BYTE_BGR);  
		      BufferedImage temp=getimage();  
		      //Graphics2D g2 = (Graphics2D)g;
		      if( temp != null)
		        g.drawImage(temp,10,10,temp.getWidth(),temp.getHeight(), this);
		      
		   }  
		 }  
		 
 
public class localCOLOR {
	
	 public static void main(String arg[]){  
	     // Load the native library.  
		   System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	    
	     JFrame frame1 = new JFrame("Camera Input");  
	     frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);    
	     frame1.setBounds(0, 0, frame1.getWidth(), frame1.getHeight());  
	     Panel panel1 = new Panel();  
	     frame1.setContentPane(panel1);  
	     frame1.setVisible(true);   
	     JFrame frame2 = new JFrame("Thresholded Output");  
	     frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
	     frame2.setBounds(500,200, frame1.getWidth()+900, 300+frame1.getHeight());  
	     Panel panel2 = new Panel();  
	     frame2.setContentPane(panel2);      
	     frame2.setVisible(true);  
	     JFrame frame3 = new JFrame("Contour");  
	     frame3.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
	     frame3.setBounds(900,400, frame1.getWidth()+900, 300+frame1.getHeight());  
	     Panel panel3 = new Panel();  
	     frame3.setContentPane(panel3);      
	     frame3.setVisible(false);
	     
	     VideoCapture capture =new VideoCapture(1);  
	     Mat webcam_image=new Mat(); 
	     capture.read(webcam_image); 
	     frame1.setSize(webcam_image.width()+40,webcam_image.height()+60);   
	     frame2.setSize(webcam_image.width()+40,webcam_image.height()+60);  
	     frame3.setSize(webcam_image.width()+40,webcam_image.height()+60);  
	      
	     Mat hsv_image=new Mat();  
	     Mat thresholded=new Mat();  
	     Mat thresholded2=new Mat();
	
	     
	     
	      
	     Scalar hsv_min = new Scalar(64, 70, 70, 0);  
	     Scalar hsv_max = new Scalar(85, 255, 255, 0); 
	     
	    Size s = new Size(3,3);
	    


	    List<MatOfPoint> contours = new ArrayList<MatOfPoint>();    
	    
	      if( capture.isOpened())  
	      {  
	       while( true )	      {  
	         capture.read(webcam_image);  
	         if( !webcam_image.empty() )  
	          {  	           	         
	        	
				   
				   Imgproc.medianBlur(webcam_image, webcam_image, 3); //cancel noise right from camera
				   Imgproc.cvtColor(webcam_image, hsv_image, Imgproc.COLOR_BGR2HSV);
				   Core.inRange(hsv_image , hsv_min, hsv_max, thresholded);
	           
	           
	           
	           Imgproc.GaussianBlur(thresholded, thresholded2,s ,1.5); //make smooth output
	           
	
	           
	           
	           Imgproc.findContours(thresholded2, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
	           Imgproc.drawContours(thresholded2, contours, 0, new Scalar(255,255,0));
	           
	           
	           MatOfPoint2f         approxCurve = new MatOfPoint2f();

	           //For each contour found
	           for (int i=0; i<contours.size(); i++)
	           {
	               //Convert contours(i) from MatOfPoint to MatOfPoint2f
	               MatOfPoint2f contour2f = new MatOfPoint2f( contours.get(i).toArray() );
	               //Processing on mMOP2f1 which is in type MatOfPoint2f
	               double approxDistance = Imgproc.arcLength(contour2f, true)*0.02;
	               Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true);

	               //Convert back to MatOfPoint
	               MatOfPoint points = new MatOfPoint( approxCurve.toArray() );

	               // Get bounding rect of contour
	               Rect rect = Imgproc.boundingRect(points);

	                // draw enclosing rectangle (all same color, but you could use variable i to make them unique)
	               Core.rectangle(thresholded2, new Point(rect.x,rect.y), new Point(rect.x+rect.width,rect.y+rect.height), new Scalar(255, 255, 0), 3); 

	           }
	           
	           
	           
	           
	           
	          //Imgproc.findContours(thresholded2, contours, thresholded2, Imgproc.RETR_LIST,Imgproc.CHAIN_APPROX_SIMPLE);  
	          // Imgproc.drawContours(thresholded2, contours, -1, colorCont);
	           
	           //-- 5. Display the image  
	           panel1.setimagewithMat(webcam_image);    
	             frame1.repaint();     
	             panel2.setimagewithMat(thresholded2);    
	             frame2.repaint(); 
	             Highgui.imwrite("FInal.JPG", thresholded2);
	             //panel3.setimagewithMat(cont);    
	             //frame3.repaint();  
	             //System.out.println(contoursCounter); 
	             
	          }  
	          else  
	          {  
	            System.out.println(" --(!) No captured frame -- Break!");  
	            break;  
	          }  
	         }  
	        }  
	      return;  
	    }  
	  }   