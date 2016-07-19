import java.awt.Graphics;  
 import java.awt.image.BufferedImage;  
 import java.util.ArrayList;  
 import java.util.List;  
 import javax.swing.JFrame;  
 import javax.swing.JPanel;  
 import org.opencv.core.Core;  
 import org.opencv.core.Mat;   
 import org.opencv.core.Point;  
 import org.opencv.core.Scalar;  
 import org.opencv.core.Size;  
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
		 
 
public class StopSign {
	
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
	     frame2.setBounds(900,300, frame1.getWidth()+900, 300+frame1.getHeight());  
	     Panel panel4 = new Panel();  
	     frame2.setContentPane(panel4);      
	     frame2.setVisible(true);  
	     
	     VideoCapture capture =new VideoCapture(1);  
	     Mat webcam_image=new Mat();  
	     Mat hsv_image=new Mat();  
	     Mat thresholded=new Mat();  
	     Mat thresholded2=new Mat();  
	     
	     capture.read(webcam_image);  
	      
	      frame1.setSize(webcam_image.width()+40,webcam_image.height()+60);   
	      frame2.setSize(webcam_image.width()+40,webcam_image.height()+60);  
	      
	      
	      if( capture.isOpened())  
	      {  
	       while( true )  
	       {  
	         capture.read(webcam_image);  
	         if( !webcam_image.empty() )  
	          {  	           	         
	        	 double recH = webcam_image.height() *.3;
				   double recW= webcam_image.width() * .4;
				   int recHeight= (int) recH;
				   int recWidth= (int) recW;
				   int y_centerA= webcam_image.height()/2 - recHeight/2;
				   int x_centerA= webcam_image.width()/2 - recWidth/2;
				   int y_centerB= webcam_image.height()/2 + recHeight/2;
				   int x_centerB= webcam_image.width()/2 + recWidth/2;
				   
				   
	           Core.rectangle(webcam_image, new Point (x_centerA,y_centerA),new Point (x_centerB,y_centerB), new Scalar(100,10,10), 4, 1, 0);
//This is how u put text	           
	           //Core.putText(webcam_image,String.format("HSV: (" + String.valueOf(a value)),new Point(30, 30) , 3 //FONT_HERSHEY_SCRIPT_SIMPLEX  
//	                ,1.0,new Scalar(100,10,10,255),3);

	           Core.putText(webcam_image,String.format("HSV: "),new Point(30, 30) , 3   
		                ,1.0,new Scalar(100,10,10,255),3); 
	      
	           
	           
	    
	           //-- 5. Display the image  
	           panel1.setimagewithMat(webcam_image);    
	             frame1.repaint();      
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