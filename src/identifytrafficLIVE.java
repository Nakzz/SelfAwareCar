import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;

public class identifytrafficLIVE {
	public int traffic() {

		// Load the native library.
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		// It is better to group all frames together so cut and paste to
		// create more frames is easier

		int color = 0;
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

		Mat webcam_image = new Mat();
		Mat hsv_image = new Mat();
		Mat tGreen = new Mat();
		Mat tRed = new Mat();
		Mat tRed2 = new Mat();

		capture.read(webcam_image);
		frame1.setSize(webcam_image.width() + 40, webcam_image.height() + 60);
		frame2.setSize(webcam_image.width() + 40, webcam_image.height() + 60);
		frame4.setSize(webcam_image.width() + 40, webcam_image.height() + 60);

		Scalar hsv_minR1 = new Scalar(153, 117, 170, 0);
		Scalar hsv_maxR1 = new Scalar(180, 255, 255, 0);

		Scalar hsv_minG = new Scalar(64, 70, 70, 0);
		Scalar hsv_maxG = new Scalar(85, 255, 255, 0);

		Size s = new Size(3, 3);

		if (capture.isOpened()) {
			while (true) {
				capture.read(webcam_image);
				if (!webcam_image.empty()) {
					// One way to select a range of colors by Hue
					Imgproc.cvtColor(webcam_image, hsv_image, Imgproc.COLOR_BGR2HSV);

					List<MatOfPoint> Rcontours = new ArrayList<MatOfPoint>();
					List<MatOfPoint> Gcontours = new ArrayList<MatOfPoint>();

					// red
					Core.inRange(hsv_image, hsv_minR1, hsv_maxR1, tRed);
					// Core.inRange(hsv_image, hsv_minR2, hsv_maxR2, tRed2);
					// Core.bitwise_or(tRed, tRed2, tRed);
					Imgproc.GaussianBlur(tRed, tRed, s, 1.5);
					Imgproc.dilate(tRed, tRed, new Mat());
					Imgproc.erode(tRed, tRed, new Mat());
					Imgproc.findContours(tRed, Rcontours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

					// Core.bitwise_or(thresholded, thresholded2, thresholded);
					// Core.bitwise_and(thresholded, thresholded2, thresholded);

					for (int i = 0; i < Rcontours.size(); i++) {
						// System.out.println(Imgproc.contourArea(contours.get(i)));

						MatOfPoint2f approxCurve = new MatOfPoint2f();
						// Convert contours(i) from MatOfPoint to MatOfPoint2f

						if (Imgproc.contourArea(Rcontours.get(i)) > 30) {
							Rect rectr = Imgproc.boundingRect(Rcontours.get(i));
							// System.out.println(recta.height);
							if (rectr.height > 20) {
								MatOfPoint2f contour2f = new MatOfPoint2f(Rcontours.get(i).toArray());
								// Processing on mMOP2f1 which is in type
								// MatOfPoint2f
								double approxDistance = Imgproc.arcLength(contour2f, true) * 0.02;
								Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true);

								color = 1;
							} else {
								color = 0;
							}

						}
					}

					Core.inRange(hsv_image, hsv_minG, hsv_maxG, tGreen);
					Imgproc.GaussianBlur(tGreen, tGreen, s, 1.5);
					Imgproc.dilate(tGreen, tGreen, new Mat());
					Imgproc.erode(tGreen, tGreen, new Mat());
					Imgproc.findContours(tGreen, Gcontours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

					for (int i = 0; i < Gcontours.size(); i++) {
						// System.out.println(Imgproc.contourArea(contours.get(i)));

						MatOfPoint2f approxCurve = new MatOfPoint2f();
						// Convert contours(i) from MatOfPoint to MatOfPoint2f

						if (Imgproc.contourArea(Gcontours.get(i)) > 50) {
							Rect recta = Imgproc.boundingRect(Gcontours.get(i));
							// System.out.println(recta.height);
							if (recta.height > 48) {
								MatOfPoint2f contour2f = new MatOfPoint2f(Gcontours.get(i).toArray());
								// Processing on mMOP2f1 which is in type
								// MatOfPoint2f
								double approxDistance = Imgproc.arcLength(contour2f, true) * 0.02;
								Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true);

								color = 2;
							} else {
								color = 0;
							}

						}
					}

					// -- 5. Display the image
					panel1.setimagewithMat(webcam_image);
					panel2.setimagewithMat(hsv_image);

					panel4.setimagewithMat(tRed);
					frame1.repaint();
					frame2.repaint();
					frame4.repaint();
					if (color == 1) {
						System.out.println("Red Found");
					} else if (color == 2) {
						System.out.println("Green Found");
					} else {
						System.out.println("Idek");
						color = 0;
					}

				} else {
					System.out.println(" --(!) No captured frame -- Break!");
					break;
				}
			}
		}

		return color;

	}
}