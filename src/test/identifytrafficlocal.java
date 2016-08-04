package test;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.EmptyStackException;
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
import javax.swing.JSlider;


public class identifytrafficlocal {
	static public Mat thresholded;
	static public Mat hsvImg;
	static public Mat processed;
	static public Mat webcam_image;
	public int c=0;

	public int traffic() {

		// Load the native library.
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		// It is better to group all frames together so cut and paste to
		// create more frames is easier

		int color = 0;
		/*
		 * JFrame frame1 = new JFrame("Camera");
		 * frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		 * frame1.setSize(640, 480); frame1.setBounds(0, 0, frame1.getWidth(),
		 * frame1.getHeight()); Panel panel1 = new Panel();
		 * frame1.setContentPane(panel1); frame1.setVisible(true); JFrame frame2
		 * = new JFrame("Threshold");
		 * frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		 * frame2.setSize(640, 480); frame2.setBounds(300, 100,
		 * frame2.getWidth() + 300, 100 + frame2.getHeight()); Panel panel2 =
		 * new Panel(); frame2.setContentPane(panel2); frame2.setVisible(true);
		 */

		// -- 2. Read the video stream
		webcam_image = new Mat();
		VideoCapture capture = new VideoCapture(0); //
		if (capture.isOpened()) {

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} // delay for webcam to load

			capture.read(webcam_image);
			
			if (capture.read(webcam_image) == false) {
				try {
					throw new Exception();
				} catch (Exception e) { // TODO Auto-generated catch block
					e.printStackTrace();
					System.exit(0);
				}

			}
			
			
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

			}
			c++;
			System.out.println("Camera opening after tries: "+ c);
			
			Highgui.imwrite("camerasnap.jpeg", webcam_image);
		}

		Mat webcam_image = Highgui.imread("camerasnap.jpeg", Highgui.CV_LOAD_IMAGE_COLOR);

		
		thresholded = new Mat();
		hsvImg = new Mat();
		processed = new Mat();

//		capture.read(webcam_image);
//


		// frame1.setSize(webcam_image.width() + 40, webcam_image.height() +
		// 60);
		// frame2.setSize(webcam_image.width() + 40, webcam_image.height() +
		// 60);

		Scalar hsv_minR1 = new Scalar(155, 90, 229, 0);
		Scalar hsv_maxR1 = new Scalar(180, 255, 255, 0);

		Scalar hsv_minG = new Scalar(80, 36, 255, 0);
		Scalar hsv_maxG = new Scalar(88, 255, 255, 0);

		Scalar hsv_minB = new Scalar(85, 136, 171, 0);
		Scalar hsv_maxB = new Scalar(93, 196, 255, 0);

		Size s = new Size(3, 3);

		if (capture.isOpened()) {

			capture.read(webcam_image);
			if (!webcam_image.empty()) {
				// One way to select a range of colors by Hue
				Imgproc.cvtColor(webcam_image, hsvImg, Imgproc.COLOR_BGR2HSV);

				// panel2.setimagewithMat(hsvImg);

				boolean foundRed = findColor(hsv_minR1, hsv_maxR1);
				// panel4.setimagewithMat(thresholded); //show image thresholded
				// for red
				boolean foundGreen = findColor(hsv_minG, hsv_maxG);
				// panel4.setimagewithMat(thresholded); //show image thresholded
				// for green
				// panel1.setimagewithMat(webcam_image);
				// frame1.repaint();
				// frame2.repaint();
				// frame4.repaint();

				if (foundRed) {
					System.out.println("Red Found");
					color = 1;
				} else if (foundGreen) {
					System.out.println("Green Found");
					color = 2;
				} else {
					System.out.println("Idek");
					color = 0;
				}

			} else {
				System.out.println(" --(!) No captured frame -- Break!");

			}

		}
		capture.release();
		
		return color;
	}

	public static boolean findColor(Scalar hsv_min, Scalar hsv_max) {
		boolean foundColor = false;
		int bigContourCount = 0;
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Core.inRange(hsvImg, hsv_min, hsv_max, thresholded);

		Size s = new Size(3, 3);
		Imgproc.GaussianBlur(thresholded, processed, s, 1.5);
		Imgproc.dilate(processed, processed, new Mat());
		Imgproc.erode(processed, processed, new Mat());
		Imgproc.findContours(processed, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

		for (int i = 0; i < contours.size(); i++) {
			if (Imgproc.contourArea(contours.get(i)) > 30) {
				Rect rectr = Imgproc.boundingRect(contours.get(i));
				if (rectr.height > 20) {
					bigContourCount++;
					MatOfPoint2f approxCurve = new MatOfPoint2f();
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
							new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(255, 255, 0), 3);

				}
			}
		}
		System.out.println(bigContourCount);

		if (bigContourCount > 0) {
			foundColor = true;
		} else {
			foundColor = false;
		}

		return foundColor;
	}
}