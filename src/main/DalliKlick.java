package main;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import static util.Utilities.*;

public class DalliKlick extends JFrame {
	
	private static final long serialVersionUID = -6343140590992485359L;
	
	private static final int WIDTH = 1000;
	private static final int HEIGHT = 800;
	
	private static final Rectangle REC_EXIT = new Rectangle(20, 20, 150, 40);
	private static final Rectangle REC_SET_IMAGE = new Rectangle(200, 20, 200, 40);
	private static final Rectangle REC_NEXT_POLYGON_TRANSPARENT = new Rectangle(430, 20, 200, 40);
	
	private JButton jBtnExit;
	
	private JButton jBtnSetImage;
	private JButton jBtnMakeNextPolygonTransparent;
	
	private JLabel jLblImage;
	
	private BufferedImage image;
	private ArrayList<MyPolygon> polygons;
	
	private String pathToImage;
	private int countTransparentPolygons;
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				DalliKlick inst = new DalliKlick();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}
	
	public DalliKlick() {
		super();
		
		initGUI();
	}
	
	private void initGUI() {
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		getContentPane().setLayout(null);
		
		{
			jBtnExit = new JButton();
			getContentPane().add(jBtnExit);
			jBtnExit.setBounds(REC_EXIT);
			jBtnExit.setText("Beenden");
			jBtnExit.setFocusable(false);
			jBtnExit.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					exit();
				}
			});
		}
		{
			jBtnSetImage = new JButton();
			getContentPane().add(jBtnSetImage);
			jBtnSetImage.setBounds(REC_SET_IMAGE);
			jBtnSetImage.setText("Bild anzeigen");
			jBtnSetImage.setFocusable(false);
			jBtnSetImage.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setImage();
				}
			});
		}
		{
			jBtnMakeNextPolygonTransparent = new JButton();
			getContentPane().add(jBtnMakeNextPolygonTransparent);
			jBtnMakeNextPolygonTransparent.setBounds(REC_NEXT_POLYGON_TRANSPARENT);
			jBtnMakeNextPolygonTransparent.setText("Polygon aufdecken");
			jBtnMakeNextPolygonTransparent.setFocusable(false);
			jBtnMakeNextPolygonTransparent.setVisible(false);
			jBtnMakeNextPolygonTransparent.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					makeNextPolygonTransparent();
				}
			});
		}
		{
			jLblImage = new JLabel();
			getContentPane().add(jLblImage);
		}
		
		pack();
		setTitle("Dalli Klick");
		setSize(WIDTH, HEIGHT);
		setResizable(false);
	}
	
	private void setImage() {
		pathToImage = "test-image.png";
		image = loadImage(pathToImage);
		polygons = createPolygons(image.getWidth(), image.getHeight());
		countTransparentPolygons = 0;
		showImageWithPolygons();
		jBtnMakeNextPolygonTransparent.setVisible(true);
	}
	
	private ArrayList<MyPolygon> createPolygons(int width, int height) {
		ArrayList<MyPolygon> polygons = new ArrayList<>();
		
		MyPolygon p;
		p = new MyPolygon();
		p.addPoint(0, 0);
		p.addPoint(0, 300);
		p.addPoint(50, 250);
		p.addPoint(100, 100);
		p.addPoint(50, 0);
		
		polygons.add(p);
		
		p = new MyPolygon();
		p.addPoint(100, 100);
		p.addPoint(50, 250);
		p.addPoint(250, 200);
		p.addPoint(300, 125);
		
		polygons.add(p);
		
		p = new MyPolygon();
		p.addPoint(50, 0);
		p.addPoint(100, 100);
		p.addPoint(300, 125);
		p.addPoint(width - 1, 0);
		
		polygons.add(p);
		
		p = new MyPolygon();
		p.addPoint(0, 300);
		p.addPoint(0, height - 1);
		p.addPoint(350, height - 1);
		p.addPoint(250, 200);
		p.addPoint(50, 250);
		
		polygons.add(p);
		
		p = new MyPolygon();
		p.addPoint(width - 1, 0);
		p.addPoint(300, 125);
		p.addPoint(250, 200);
		p.addPoint(350, height - 1);
		p.addPoint(width - 1, height - 1);
		
		polygons.add(p);
		
		return polygons;
	}
	
	private void showImageWithPolygons() {
		image = loadImage(pathToImage);
		drawPolygons(image);
		displayImage();
	}
	
	private void makeNextPolygonTransparent() {
		if (countTransparentPolygons < polygons.size()) {
			int nextPolygonToMakeTransparent = (int) (Math.random() * polygons.size());
			while (polygons.get(nextPolygonToMakeTransparent).isTransparent()) {
				nextPolygonToMakeTransparent = (nextPolygonToMakeTransparent + 1) % polygons.size();
			}
			polygons.get(nextPolygonToMakeTransparent).setTransparent();
			
			countTransparentPolygons++;
			
			showImageWithPolygons();
			if (countTransparentPolygons == polygons.size()) {
				jBtnMakeNextPolygonTransparent.setVisible(false);
			}
		}
	}
	
	private void drawPolygons(BufferedImage image) {
		Graphics2D g2d = image.createGraphics();
		 
		g2d.setColor(Color.cyan);
		for (MyPolygon p : polygons) {
			if (p.isTransparent())	continue;
			g2d.fillPolygon(p);
		}
		
		g2d.setColor(Color.black);
		for (Polygon p : polygons) {
			g2d.drawPolygon(p);
		}
		
		g2d.dispose();
	}
	
	private void displayImage() {
		if (jLblImage != null)	jLblImage.setVisible(false);
	
		jLblImage = new JLabel(new ImageIcon(image));
		getContentPane().add(jLblImage);
		jLblImage.setBounds(20, 70, image.getWidth(), image.getHeight());
		jLblImage.setVisible(true);
	}
	
	private void exit() {
		System.exit(0);
	}
}
