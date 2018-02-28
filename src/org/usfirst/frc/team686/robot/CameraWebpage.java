package org.usfirst.frc.team686.robot;

import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.opencv.core.Mat;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;

public class CameraWebpage extends JFrame {
	
	public static UsbCamera camera;
	public static CvSink cvSink;
	
	public CameraWebpage() { 
		
		camera = CameraServer.getInstance().startAutomaticCapture();
		
		cvSink = CameraServer.getInstance().getVideo();
		
		initUI();
		setVisible(true);
	}

	private void initUI() {
		
		getContentPane().setLayout(new FlowLayout());
		setTitle("Camera");
		setSize(320, 640);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
	}
	
	public void putImageOnWebpage(){
		
		Mat frame = new Mat();
		
		cvSink.grabFrame(frame);
		
		int type = 0;
		
		BufferedImage image = new BufferedImage(frame.width(), frame.height(), type);
	    WritableRaster raster = image.getRaster();
	    DataBufferByte dataBuffer = (DataBufferByte) raster.getDataBuffer();
	    byte[] data = dataBuffer.getData();
	    frame.get(0, 0, data);
		
	    getContentPane().add(new JLabel(new ImageIcon(image)));
	    pack();
	}
	
	
}
