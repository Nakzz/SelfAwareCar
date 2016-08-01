package test;

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
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.CvType;
import javax.swing.JSlider;

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

	/**
	 * Converts/writes a Mat into a BufferedImage.
	 * 
	 * @param matrix
	 *            Mat of type CV_8UC3 or CV_8UC1
	 * @return BufferedImage of type TYPE_3BYTE_BGR or TYPE_BYTE_GRAY
	 */
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
		// BufferedImage temp=new BufferedImage(640, 480,
		// BufferedImage.TYPE_3BYTE_BGR);
		BufferedImage temp = getimage();
		// Graphics2D g2 = (Graphics2D)g;
		if (temp != null)
			g.drawImage(temp, 10, 10, temp.getWidth(), temp.getHeight(), this);
	}
}

public class Trackbar {
    static public Mat thresholded;
    static public Mat hsvImg;
    static public Mat processed;
    static public Mat webcam_image;
    
	public static void main(String arg[]) {

		// Load the native library.
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		// It is better to group all frames together so cut and paste to
		// create more frames is easier

		JFrame frame1 = new JFrame("Camera");
		frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame1.setSize(640, 480);
		frame1.setBounds(0, 0, frame1.getWidth(), frame1.getHeight());
		Panel panel1 = new Panel();
		frame1.setContentPane(panel1);
		frame1.setVisible(true);
		JFrame frame2 = new JFrame("HSV");
		frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame2.setSize(640, 480);
		frame2.setBounds(300, 100, frame2.getWidth() + 300, 100 + frame2.getHeight());
		Panel panel2 = new Panel();
		frame2.setContentPane(panel2);
		frame2.setVisible(true);

		JFrame frame4 = new JFrame("Threshold");
		frame4.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame4.setSize(640, 480);
		frame4.setBounds(900, 300, frame2.getWidth() + 900, 300 + frame2.getHeight());
		Panel panel4 = new Panel();
		frame4.setContentPane(panel4);
		frame4.setVisible(true);

		// -- 2. Read the video stream
		VideoCapture capture = new VideoCapture(1);
		if (!capture.isOpened()) {
			System.exit(-1);
		}

		webcam_image = new Mat();
		thresholded = new Mat();
		hsvImg = new Mat();
		processed = new Mat();
		
		capture.read(webcam_image);
		frame1.setSize(webcam_image.width() + 40, webcam_image.height() + 60);
		frame2.setSize(webcam_image.width() + 40, webcam_image.height() + 60);
		frame4.setSize(webcam_image.width() + 40, webcam_image.height() + 60);

		JSlider hsv_H = new JSlider(JSlider.HORIZONTAL, 0, 180, 153);
		JSlider hsv_UH = new JSlider(JSlider.HORIZONTAL, 0, 180, 180);
		JSlider hsv_S = new JSlider(JSlider.HORIZONTAL, 0, 255, 117);
		JSlider hsv_US = new JSlider(JSlider.HORIZONTAL, 0, 255, 255);
		JSlider hsv_V = new JSlider(JSlider.HORIZONTAL, 0, 255, 170);
		JSlider hsv_UV = new JSlider(JSlider.HORIZONTAL, 0, 255, 255);

		// Scalar hsv_minR1 = new Scalar(hsv_H.getValue(), hsv_S.getValue(),
		// hsv_V.getValue(), 0);
		// Scalar hsv_maxR1 = new Scalar(hsv_UH.getValue(), hsv_US.getValue(),
		// hsv_UV.getValue(), 0);


		panel1.add(hsv_H);
		panel1.add(hsv_UH);
		panel1.add(hsv_S);
		panel1.add(hsv_US);
		panel1.add(hsv_V);
		panel1.add(hsv_UV);

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Scalar hsv_minG = new Scalar(64, 70, 70, 0);
		Scalar hsv_maxG = new Scalar(85, 255, 255, 0);

		if (capture.isOpened()) {
			while (true) {
				capture.read(webcam_image);
				if (!webcam_image.empty()) {
					// One way to select a range of colors by Hue
					Imgproc.cvtColor(webcam_image, hsvImg, Imgproc.COLOR_BGR2HSV);
					Scalar hsv_minR1 = new Scalar(hsv_H.getValue(), hsv_S.getValue(), hsv_V.getValue(), 0);
					Scalar hsv_maxR1 = new Scalar(hsv_UH.getValue(), hsv_US.getValue(), hsv_UV.getValue(), 0);

					// Display the image
					
					panel2.setimagewithMat(hsvImg);

					boolean foundRed = findColor(hsv_minR1, hsv_maxR1);
					panel4.setimagewithMat(thresholded); //show image thresholded for red
					boolean foundGreen = findColor(hsv_minG, hsv_maxG);
					panel4.setimagewithMat(thresholded); //show image thresholded for green
					panel1.setimagewithMat(webcam_image);
					frame1.repaint();
					frame2.repaint();
					frame4.repaint();
					
					if (foundRed) {
						System.out.println("Red Found");
					} else if (foundGreen) {
						System.out.println("Green Found");
						break;
					} else {
						System.out.println("Idek");
					}
					
				} else {
					System.out.println(" --(!) No captured frame -- Break!");
					break;
				}
			}
		}

		return;
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
		
		if (bigContourCount > 0 ) {
			foundColor = true;
		} else {
			foundColor = false;
		}

		return foundColor;
	}
}