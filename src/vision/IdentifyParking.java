package vision;


import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

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


class Panel extends JPanel {
	private static final long serialVersionUID = 1L;
	private BufferedImage image;

	// Create a constructor method
	public Panel() {
		super();
	}

	private BufferedImage getimage() {
		return image;
	}

	public void setimage(BufferedImage newimage) {
		image = newimage;
		return;
	}

	public void setimagewithMat(Mat newimage) {
		image = this.matToBufferedImage(newimage);
		return;
	}

	public BufferedImage matToBufferedImage(Mat matrix) {
		int cols = matrix.cols();
		int rows = matrix.rows();
		int elemSize = (int) matrix.elemSize();
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
			for (int i = 0; i < data.length; i = i + 3) {
				b = data[i];
				data[i] = data[i + 2];
				data[i + 2] = b;
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

	protected void paintComponent(Graphics g) {

		super.paintComponent(g);
		BufferedImage temp = getimage();
		if (temp != null)
			g.drawImage(temp, 10, 10, temp.getWidth(), temp.getHeight(), this);

	}
}

public class IdentifyParking {

	public int[] center () throws InterruptedException{
		int[] CenterOfObject;

        // allocates memory for 10 integers
		CenterOfObject = new int[2];
           
		// Load the native library.
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		Mat webcam_snap = new Mat();
		VideoCapture capture = new VideoCapture(1); //
		if (capture.isOpened()) {
			Thread.sleep(1000); // delay for webcam to load
			capture.read(webcam_snap);
			Highgui.imwrite("camera.jpeg", webcam_snap);
		}

		Mat webcam_image = Highgui.imread("green3.jpeg", Highgui.CV_LOAD_IMAGE_COLOR);

		Mat hsv_image = new Mat();
		Mat thresholded = new Mat();


		int y_center = webcam_image.height() / 2;
		int x_center = webcam_image.width() / 2;
int deltaX = 0, deltaY=0;
		Scalar hsv_min = new Scalar(64, 70, 70, 0);
		Scalar hsv_max = new Scalar(85, 255, 255, 0);

		Size s = new Size(3, 3);

		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

		if (!webcam_image.empty()) {

			Imgproc.medianBlur(webcam_image, webcam_image, 3); // cancel noise
																// right from
																// camera
			Imgproc.cvtColor(webcam_image, hsv_image, Imgproc.COLOR_BGR2HSV);
			Core.inRange(hsv_image, hsv_min, hsv_max, thresholded);

			Imgproc.GaussianBlur(thresholded, thresholded, s, 1.5); 
			Imgproc.dilate(thresholded, thresholded, new Mat());
			Imgproc.erode(thresholded, thresholded, new Mat());

			Imgproc.findContours(thresholded, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

			for (int i = 0; i < contours.size(); i++) {
				// System.out.println(Imgproc.contourArea(contours.get(i)));

				MatOfPoint2f approxCurve = new MatOfPoint2f();
				// Convert contours(i) from MatOfPoint to MatOfPoint2f

				if (Imgproc.contourArea(contours.get(i)) > 50) {
					Rect recta = Imgproc.boundingRect(contours.get(i));
					// System.out.println(recta.height);
					if (recta.height > 48) {
						MatOfPoint2f contour2f = new MatOfPoint2f(contours.get(i).toArray());
						// Processing on mMOP2f1 which is in type MatOfPoint2f
						double approxDistance = Imgproc.arcLength(contour2f, true) * 0.02;
						Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true);

						// Convert back to MatOfPoint
						MatOfPoint points = new MatOfPoint(approxCurve.toArray());

						// Get bounding rect of contour
						Rect rect = Imgproc.boundingRect(points);

						// draw enclosing rectangle (all same color, but you
						// could use variable i to make them unique)
						Core.rectangle(webcam_image, new Point(rect.x, rect.y),
								new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(255, 255, 0), 1);
						Core.rectangle(thresholded, new Point(rect.x, rect.y),
								new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(255, 255, 0), 1);
						
					int	xCenter = rect.x + (rect.width/2); 
						int yCenter = rect.y + (rect.height/2);
						
						
//debug						System.out.println("REc.x center:" + xCenter + "   " + "Rec.y center:" + yCenter);
//debug						System.out.println(" " +rect.x +" " + rect.y +" " + rect.height +" " + rect.width );
						Core.circle(webcam_image, new Point(xCenter, yCenter), 2, new Scalar(255,255, 0, 255));
						Core.line(thresholded, new Point(xCenter, yCenter), new Point(x_center, y_center),
								new Scalar(255, 49, 0, 255));
						deltaX = rect.x - x_center;
						deltaY = rect.y - y_center;
/*debug						if (deltaX > 0) {
							System.out.println("X is " + deltaX + " px right.");
						} else {
							System.out.println("X is " + deltaX + " px left.");
						}
						if (deltaY < 0) {
							System.out.println("Y is " + deltaY + " px up.");
						} else {
							System.out.println("X is " + deltaY + " px down.");
												}*/
						
					}

				}
			}


			// -- 5. Display the image
/*debug			panel1.setimagewithMat(webcam_image);
			frame1.repaint();
			panel2.setimagewithMat(thresholded);
			frame2.repaint();
 */

			Highgui.imwrite("FInal.JPG", thresholded);


		} else {
			System.out.println(" --(!) No captured frame -- Break!");

		}
		CenterOfObject[0]= deltaX;
		CenterOfObject[1]=deltaY;
		return CenterOfObject;
	}
}