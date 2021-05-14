package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import static util.Utilities.*;

public class DalliKlick extends JFrame {
	
	private static final long serialVersionUID = -6343140590992485359L;
	
	private static int WIDTH = 1400;
	private static int HEIGHT = 850;
	
	private static final int windowDecorationHeight = 22;
	private static final int heightOfButtons = 60;
	private static final int margin = 20;
	private static int maxWidthOfImage = WIDTH - 2 * margin;
	private static int maxHeightOfImage = HEIGHT - windowDecorationHeight - 2 * margin - heightOfButtons;
	
	private static final int widthOfImageLabel = 150;
	private static final int heightOfImageLabel = 30;
	private static final int gapBetweenImageLabels = 10;
	private static final int widthOfScrollBar = 20;
	
	private static final Rectangle REC_EXIT = new Rectangle(20, 20, 150, 40);
	private static final Rectangle REC_CHOOSE_IMAGES = new Rectangle(190, 20, 150, 40);
	private static final Rectangle REC_START_STOP_AUTOMATIC = new Rectangle(190, 20, 150, 40);
	private static final Rectangle REC_NEXT_POLYGON_TRANSPARENT = new Rectangle(360, 20, 150, 40);
	private static final Rectangle REC_ALL_POLYGONS_TRANSPARENT = new Rectangle(530, 20, 150, 40);
	private static final Rectangle REC_BACK_TO_LIST = new Rectangle(190, 20, 150, 40);
	private static final Rectangle REC_POINTS = new Rectangle(900, 10, 300, 60);
	
	private static final Point locationImagesList = new Point(margin, heightOfButtons + margin);
	
	private static final Color colorFillPolygons = new Color(64, 64, 64); 
	private static final Color colorDrawPolygons = Color.white; 
	private static final Color colorUnplayedImages = new Color(100, 255, 100);
	private static final Color colorPlayedImages = Color.gray;
	
	private JButton jBtnExit;
	private JButton jBtnChooseImages;
	
	private JScrollPane jSPImages;
	private JPanel jPnlImages;
	private ArrayList<JLabel> jLblsImagesList;
	
	private JButton jBtnStartStopAutomatic;
	private JButton jBtnMakeNextPolygonTransparent;
	private JButton jBtnMakeAllPolygonsTransparent;
	private JButton jBtnBackToList;
	
	private JLabel jLblPoints;
	
	private JLabel jLblImage;
	
	private BufferedImage image;
	private MyArrayList<MyPolygon> polygons;
	private ArrayList<Vertex> vertices;
	private ArrayList<Edge> edges;
	
	private File imageFolder;
	private ArrayList<String> imagesAbsolutePaths;
	private boolean[] played;
	
	private boolean playing;
	private boolean runningAutomatic;
	private String pathToImage;
	private int countTransparentPolygons;
	private double revealedRelativeArea;
	
	private int timeBetweenReveals = 2500;
	private int thresholdForBorderProximity = 25;
	private int minimumVertexDistance = 50;
	private int thresholdForVertexProximity = 300;
	private double minRelativeArea = 0.04;
	private double maxRelativeArea = 0.10;
	private double thresholdForFullPoints = 0.15;
	
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
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
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
			jBtnChooseImages = new JButton();
			getContentPane().add(jBtnChooseImages);
			jBtnChooseImages.setBounds(REC_CHOOSE_IMAGES);
			jBtnChooseImages.setText("Bilder wählen");
			jBtnChooseImages.setFocusable(false);
			jBtnChooseImages.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					chooseImages();
				}
			});
		}
		{
			jBtnStartStopAutomatic = new JButton();
			getContentPane().add(jBtnStartStopAutomatic);
			jBtnStartStopAutomatic.setBounds(REC_START_STOP_AUTOMATIC);
			jBtnStartStopAutomatic.setVisible(false);
			jBtnStartStopAutomatic.setFocusable(false);
			jBtnStartStopAutomatic.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (!runningAutomatic)	restartAutomatic();
					else					pauseAutomatic();
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
			jBtnMakeAllPolygonsTransparent = new JButton();
			getContentPane().add(jBtnMakeAllPolygonsTransparent);
			jBtnMakeAllPolygonsTransparent.setBounds(REC_ALL_POLYGONS_TRANSPARENT);
			jBtnMakeAllPolygonsTransparent.setText("Alle aufdecken");
			jBtnMakeAllPolygonsTransparent.setFocusable(false);
			jBtnMakeAllPolygonsTransparent.setVisible(false);
			jBtnMakeAllPolygonsTransparent.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					makeAllPolygonsTransparent();
				}
			});
		}
		{
			jBtnBackToList = new JButton();
			getContentPane().add(jBtnBackToList);
			jBtnBackToList.setBounds(REC_BACK_TO_LIST);
			jBtnBackToList.setText("zurück zur Liste");
			jBtnBackToList.setFocusable(false);
			jBtnBackToList.setVisible(false);
			jBtnBackToList.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					backToList();
				}
			});
		}
		{
			jLblPoints = new JLabel();
			getContentPane().add(jLblPoints);
			jLblPoints.setBounds(REC_POINTS);
			jLblPoints.setFont(jLblPoints.getFont().deriveFont((float) 30.0));
			jLblPoints.setVisible(false);
		}
		{
			jSPImages = new JScrollPane();
			getContentPane().add(jSPImages);
			jSPImages.getVerticalScrollBar().setUnitIncrement(20);
			jSPImages.setBorder(null);
			jSPImages.setLocation(locationImagesList);
		}
		{
			jPnlImages = new JPanel();
			jPnlImages.setLayout(null);
		}
		{
			jLblImage = new JLabel();
			getContentPane().add(jLblImage);
		}
		
		pack();
		setTitle("Dalli Klick");
		setSize(WIDTH, HEIGHT);
	}
	
	private void chooseImages() {
		boolean unplayedImage = false;
		if (played != null) {
			for (int i = 0; i < played.length; i++) {
				unplayedImage = unplayedImage || !played[i];
			}
		}
		if (unplayedImage && JOptionPane.showConfirmDialog(null, "Du hast noch ungespielte Bilder. Willst du wirklich fortfahren?", "", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
			return;
		}
		if (jLblsImagesList != null) {
			hideAll(jLblsImagesList);
		}
		
		JFileChooser jfc = new JFileChooser();
		jfc.setCurrentDirectory(new File("."));
		jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int state = jfc.showOpenDialog(null);
		if (state == JFileChooser.APPROVE_OPTION) {
			imageFolder = jfc.getSelectedFile();
			loadImagesFromFolder();
		}
	}
	
	private void loadImagesFromFolder() {
		if (!imageFolder.isDirectory())	return;
		File[] listFiles = imageFolder.listFiles(new ImageFilenameFilter());
		if (listFiles.length == 0)	return;
		
		imagesAbsolutePaths = new ArrayList<>();
		for (File f : listFiles) {
			imagesAbsolutePaths.add(f.getAbsolutePath());
		}
		played = new boolean[listFiles.length];
		
		createListOfImages();
	}
	
	private void createListOfImages() {
		jLblsImagesList = new ArrayList<>();
		for (int i = 0; i < imagesAbsolutePaths.size(); i++) {
			final int x = i;
			JLabel label = new JLabel();
			jPnlImages.add(label);
			alignCenter(label);
			label.setBounds(0, i * (heightOfImageLabel + gapBetweenImageLabels), widthOfImageLabel, heightOfImageLabel);
			label.setText("Bild " + (i + 1));
			label.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					played[x] = true;
					jBtnChooseImages.setVisible(false);
					jLblsImagesList.get(x).setBackground(colorPlayedImages);
					jSPImages.setVisible(false);
					setImage(imagesAbsolutePaths.get(x));
				}
			});
			label.setCursor(handCursor);
			label.setBackground(colorUnplayedImages);
			label.setOpaque(true);
			jLblsImagesList.add(label);
		}
		
		jPnlImages.setPreferredSize(new Dimension(widthOfImageLabel, imagesAbsolutePaths.size() * (heightOfImageLabel + gapBetweenImageLabels)));
		jSPImages.setViewportView(jPnlImages);
		jSPImages.setSize(new Dimension(widthOfImageLabel + widthOfScrollBar, Math.min(imagesAbsolutePaths.size() * (heightOfImageLabel + gapBetweenImageLabels), maxHeightOfImage)));
	}
	
	private void setImage(String pathToImage) {
		this.pathToImage = pathToImage;
		image = resizeImage(loadImage(pathToImage), maxWidthOfImage, maxHeightOfImage);
		vertices = getPolygonVertices(image.getWidth(), image.getHeight());
		edges = getPolygonEdges(vertices, image.getWidth(), image.getHeight());
		polygons = createTrianglesFromEdges(edges, image.getWidth(), image.getHeight());
		combinePolygons(polygons, image.getWidth(), image.getHeight());
		
		revealedRelativeArea = 0.0;
		countTransparentPolygons = 0;
		showImageWithPolygons();
		jBtnMakeNextPolygonTransparent.setVisible(true);
		jBtnMakeAllPolygonsTransparent.setVisible(true);
		jBtnStartStopAutomatic.setText("Start");
		jBtnStartStopAutomatic.setVisible(true);
		jLblPoints.setText("200,0 Punkte");
		jLblPoints.setVisible(true);
		playing = true;
		startAutomatic();
	}
	
	private void startAutomatic() {
		Runnable r = new Runnable() {
			public void run() {
				while (playing) {
					if (runningAutomatic) {
						makeNextPolygonTransparent();
					}
					try {
						Thread.sleep(timeBetweenReveals);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		
		Thread t = new Thread(r);
		t.start();
	}
	
	private void restartAutomatic() {
		runningAutomatic = true;
		jBtnStartStopAutomatic.setText("Pause");
	}
	
	private void pauseAutomatic() {
		runningAutomatic = false;
		jBtnStartStopAutomatic.setText("Weiter");
	}
	
	private ArrayList<Vertex> getPolygonVertices(int width, int height) {
		ArrayList<Vertex> vertices = getPolygonBorderVertices(width, height);
		
		int x = 0, y = 0, terminate = 0;
		while (terminate < 50) {
			boolean rejected = false;
			x = (int) (Math.random() * width);
			y = (int) (Math.random() * height);
			if (x + thresholdForBorderProximity > width)	x = width - 1 - thresholdForBorderProximity;
			else if (x < thresholdForBorderProximity)		x = thresholdForBorderProximity;
			if (y + thresholdForBorderProximity > height)	y = height - 1 - thresholdForBorderProximity;
			else if (y < thresholdForBorderProximity)		y = thresholdForBorderProximity;
			Vertex vertex = new Vertex(x, y);
			for (Vertex v : vertices) {
				if (distance(vertex, v) < minimumVertexDistance) {
					rejected = true;
					break;
				}
			}
			if (rejected)	terminate++;
			else {
				addVertex(vertices, vertex);
				terminate = 0;
			}
		}
		
		return vertices;
	}
	
	private ArrayList<Vertex> getPolygonBorderVertices(int width, int height) {
		ArrayList<Vertex> vertices = new ArrayList<>();
		
		addVertex(vertices, new Vertex(0, 0));
		addVertex(vertices, new Vertex(0, height - 1));
		addVertex(vertices, new Vertex(width - 1, 0));
		addVertex(vertices, new Vertex(width - 1, height - 1));
		
		// top
		int x = 0, y = 0;
		while (x < width) {
			if (width - x < 3 * minimumVertexDistance) {
				x = (x + width) / 2;
				addVertex(vertices, new Vertex(x, y));
				break;
			}
			x += minimumVertexDistance + minimumVertexDistance * Math.random();
			addVertex(vertices, new Vertex(x, y));
		}
		
		// left
		x = y = 0;
		while (y < height) {
			if (height - y < 3 * minimumVertexDistance) {
				y = (y + height) / 2;
				addVertex(vertices, new Vertex(x, y));
				break;
			}
			y += minimumVertexDistance + minimumVertexDistance * Math.random();
			addVertex(vertices, new Vertex(x, y));
		}
		
		// bottom
		x = 0;
		y = height - 1;
		while (x < width) {
			if (width - x < 3 * minimumVertexDistance) {
				x = (x + width) / 2;
				addVertex(vertices, new Vertex(x, y));
				break;
			}
			x += minimumVertexDistance + minimumVertexDistance * Math.random();
			addVertex(vertices, new Vertex(x, y));
		}
		
		// right
		x = width - 1;
		y = 0;
		while (y < height) {
			if (height - y < 3 * minimumVertexDistance) {
				y = (y + height) / 2;
				addVertex(vertices, new Vertex(x, y));
				break;
			}
			y += minimumVertexDistance + minimumVertexDistance * Math.random();
			addVertex(vertices, new Vertex(x, y));
		}
		
		return vertices;
	}
	
	private ArrayList<Edge> getPolygonEdges(ArrayList<Vertex> vertices, int width, int height) {
		ArrayList<Edge> edges = new ArrayList<>();
		int lastX = width - 1, lastY =  height - 1;
		
		for (int i = 0; i < vertices.size(); i++) {
			for (int j = i + 1; j < vertices.size(); j++) {
				if (distance(vertices.get(i), vertices.get(j)) < thresholdForVertexProximity)	edges.add(new Edge(vertices.get(i), vertices.get(j), lastX, lastY));
			}
		}
		
		
		for (int i = 0; i < edges.size(); i++) {
			boolean removeI = false;
			for (int j = i + 1; j < edges.size() && !removeI; j++) {
				if (intersect(edges.get(i), edges.get(j))) {
					if (edges.get(i).length() > edges.get(j).length()) {
						removeI = true;
					} else {
						edges.remove(j);
						j--;
					}
				}
			}
			if (removeI) {
				edges.remove(i);
				i--;
			}	
		}
		
		return edges;
	}
	
	private MyArrayList<MyPolygon> createTrianglesFromEdges(ArrayList<Edge> edges, int width, int height) {
		MyArrayList<MyPolygon> triangles = new MyArrayList<>();
		int lastX = width - 1, lastY =  height - 1;
		
		for (Edge edge : edges) {
			edge.resetPolygons();
		}
		
		while(true) {
			boolean didSth = false;
			for (Edge edge : edges) {
				if (edge.numberOfMissingPolygons() == 0)	continue;
				Vertex start = edge.getStart(), end = edge.getEnd();
				ArrayList<Edge> edgesFromStart = getEdgesFrom(edges, start, end);
				ArrayList<Edge> edgesFromEnd = getEdgesFrom(edges, end, start);
				
				for (Edge fromStart : edgesFromStart) {
					for (Edge fromEnd : edgesFromEnd) {
						if (fromStart.getOther(start).equals(fromEnd.getOther(end))) {
							MyPolygon triangle = MyPolygon.newTriangle(start, end, fromStart.getOther(start));
							
							if (edge.checkNewPolygon(triangle) && fromStart.checkNewPolygon(triangle) && fromEnd.checkNewPolygon(triangle)) {
								addPolygonAscending(triangles, triangle);
								edge.addPolygon(triangle);
								fromStart.addPolygon(triangle);
								fromEnd.addPolygon(triangle);
								didSth = true;
							}
						}
						if (edge.numberOfMissingPolygons() == 0)	break;
					}
					if (edge.numberOfMissingPolygons() == 0)	break;
				}
			}
			
			if (!didSth)	break;
		}
		while(true) {
			boolean didSth = false;
			for (int i = 0; i < edges.size(); i++) {
				Edge edge = edges.get(i);
				if (edge.numberOfMissingPolygons() == 0)	continue;
				Vertex start = edge.getStart(), end = edge.getEnd();
				ArrayList<Edge> edgesFromEnd = getEdgesFrom(edges, end, start);
				
				for (Edge fromEnd : edgesFromEnd) {
					Vertex other = fromEnd.getOther(end);
					Edge newEdge = new Edge(start, other, lastX, lastY);
					boolean duplicate = false;
					for (Edge otherEdge : edges) {
						if (duplicate = newEdge.equals(otherEdge)) {
							newEdge = otherEdge;
							break;
						}
					}
					
					boolean intersect = false;
					for (Edge otherEdge : edges) {
						if (intersect = intersect(newEdge, otherEdge))	break;
					}
					if (!intersect) {
						if (!duplicate)	edges.add(newEdge);
						MyPolygon triangle = MyPolygon.newTriangle(start, end, other);
						
						if (edge.checkNewPolygon(triangle) && newEdge.checkNewPolygon(triangle) && fromEnd.checkNewPolygon(triangle)) {
							addPolygonAscending(triangles, triangle);
							edge.addPolygon(triangle);
							newEdge.addPolygon(triangle);
							fromEnd.addPolygon(triangle);
							didSth = true;
						}
					}
					
					if (edge.numberOfMissingPolygons() == 0)	break;
				}
			}
			
			if (!didSth)	break;
		}
		
		return triangles;
	}
	
	private ArrayList<Edge> getEdgesFrom(ArrayList<Edge> edges, Vertex from, Vertex excludeTo) {
		ArrayList<Edge> edgesFrom = new ArrayList<>();
		
		for (Edge edge : edges) {
			if (!edge.missesPolygon())	continue;
			if ((edge.getStart().equals(from) && !edge.getEnd().equals(excludeTo))
					|| (edge.getEnd().equals(from) && !edge.getStart().equals(excludeTo)))	edgesFrom.add(edge);
		}
		
		return edgesFrom;
	}
	
	private ArrayList<MyPolygon> combinePolygons(ArrayList<MyPolygon> polygons, int width, int height) {
		double minArea = minRelativeArea * width * height;
		double maxArea = maxRelativeArea * width * height;
		
		while(polygons.get(0).getArea() < minArea) {
			boolean didSth = false;
			
			MyPolygon polygon = polygons.get(0);
			ArrayList<MyPolygon> neighbouringPolygons = polygon.getNeighbours();
			ArrayList<MyPolygon> combinedPolygons = new ArrayList<>();
			for (int i = 0; i < neighbouringPolygons.size(); i++) {
				MyPolygon combined = MyPolygon.combine(polygon, neighbouringPolygons.get(i));
				combinedPolygons.add(combined.getArea() < maxArea ? combined : null);
			}
			
			int indexOfSelectedCombinedPolygon = -1;
			boolean foundConvexCombinedPolygon = false;
			double sizeOfSelectedCombinedPolygon = maxArea;
			for (int i = 0; i < combinedPolygons.size(); i++) {
				if (combinedPolygons.get(i) == null)	continue;
				MyPolygon combined = combinedPolygons.get(i);
				if (foundConvexCombinedPolygon) {
					if (combined.isConvex() && combined.getArea() < sizeOfSelectedCombinedPolygon) {
						indexOfSelectedCombinedPolygon = i;
						foundConvexCombinedPolygon = combinedPolygons.get(indexOfSelectedCombinedPolygon).isConvex();
						sizeOfSelectedCombinedPolygon = combinedPolygons.get(indexOfSelectedCombinedPolygon).getArea();
					}
				} else {
					if (combined.isConvex() || combined.getArea() < sizeOfSelectedCombinedPolygon) {
						indexOfSelectedCombinedPolygon = i;
						foundConvexCombinedPolygon = combinedPolygons.get(indexOfSelectedCombinedPolygon).isConvex();
						sizeOfSelectedCombinedPolygon = combinedPolygons.get(indexOfSelectedCombinedPolygon).getArea();
					}
				}
			}
			
			if (indexOfSelectedCombinedPolygon != -1) {
				MyPolygon selectedNeighbour = neighbouringPolygons.get(indexOfSelectedCombinedPolygon);
				MyPolygon newPolygon = combinedPolygons.get(indexOfSelectedCombinedPolygon);
				
				for (MyPolygon neighbour : newPolygon.getNeighbours()) {
					neighbour.replaceEitherNeighbour(polygon, selectedNeighbour, newPolygon);
				}
				
				polygons.remove(polygon);
				polygon.setActive(false);
				polygons.remove(selectedNeighbour);
				selectedNeighbour.setActive(false);
				addPolygonAscending(polygons, newPolygon);
				
				didSth = true;
			}
			
			if (!didSth)	break;
		}
		
		return polygons;
	}
	
	private void showImageWithPolygons() {
		image = resizeImage(loadImage(pathToImage), maxWidthOfImage, maxHeightOfImage);
		drawPolygons(image);
		displayImage();
	}
	
	private void drawPolygons(BufferedImage image) {
		Graphics2D g2d = image.createGraphics();
		 
		g2d.setColor(colorFillPolygons);
		for (MyPolygon p : polygons) {
			if (p.isTransparent())	continue;
			g2d.fillPolygon(p);
		}
		
		if (countTransparentPolygons != polygons.size()) {
			g2d.setColor(colorDrawPolygons);
			for (Polygon p : polygons) {
				g2d.drawPolygon(p);
			}
		}
		
		g2d.dispose();
	}
	
	private void displayImage() {
		if (jLblImage != null)	jLblImage.setVisible(false);
	
		jLblImage = new JLabel(new ImageIcon(image));
		getContentPane().add(jLblImage);
		int width = image.getWidth(), height = image.getHeight();
		jLblImage.setBounds((WIDTH - width) / 2, heightOfButtons + (HEIGHT - windowDecorationHeight - height - heightOfButtons) / 2, width, height);
		jLblImage.setVisible(true);
	}
	
	private void makeNextPolygonTransparent() {
		if (countTransparentPolygons < polygons.size()) {
			int nextPolygonToMakeTransparent = (int) (Math.random() * polygons.size());
			while (polygons.get(nextPolygonToMakeTransparent).isTransparent()) {
				nextPolygonToMakeTransparent++;
			}
			polygons.get(nextPolygonToMakeTransparent).setTransparent();
			
			revealedRelativeArea += polygons.get(nextPolygonToMakeTransparent).getArea() / ((image.getWidth() - 1) * (image.getHeight() - 1));
			
			countTransparentPolygons++;
			
			jLblPoints.setText(getPoints() + " Punkte");
			showImageWithPolygons();
			finishRound();
		}
	}
	
	private String getPoints() {
		int points = (int) Math.round(200 * Math.exp(-Math.log(20)*(revealedRelativeArea - thresholdForFullPoints) / (1 - thresholdForFullPoints)));
		
		if (revealedRelativeArea <= thresholdForFullPoints)	points = 200;
		points = (points + 2) / 5 * 5;
		
		return String.format("%d,%d", points / 10, points % 10);
	}
	
	private void makeAllPolygonsTransparent() {
		for (int i = 0; i < polygons.size(); i++) {
			polygons.get(i).setTransparent();
		}
		countTransparentPolygons = polygons.size();
		
		showImageWithPolygons();
		finishRound();
	}
	
	private void finishRound() {
		if (countTransparentPolygons != polygons.size())	return;
		playing = runningAutomatic = false;
		jBtnStartStopAutomatic.setVisible(false);
		jBtnMakeNextPolygonTransparent.setVisible(false);
		jBtnMakeAllPolygonsTransparent.setVisible(false);
		jBtnBackToList.setVisible(true);
	}
	
	private void backToList() {
		jBtnBackToList.setVisible(false);
		jLblPoints.setVisible(false);
		jLblImage.setVisible(false);
		jBtnChooseImages.setVisible(true);
		jSPImages.setVisible(true);
	}
	
	private void exit() {
		System.exit(0);
	}
}
