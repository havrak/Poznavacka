package poznavacka;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import static javafx.scene.input.KeyCode.DOWN;
import static javafx.scene.input.KeyCode.ENTER;
import static javafx.scene.input.KeyCode.LEFT;
import static javafx.scene.input.KeyCode.RIGHT;
import static javafx.scene.input.KeyCode.UP;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

/**
 *
 * @author havra, litos
 */
public class FXMLDocumentController {

	class ResizableCanvas extends Canvas {

		public ResizableCanvas() {
			widthProperty().addListener(e -> update());
			heightProperty().addListener(e -> update());
		}

		private void update() {
			if (Canvas != null && guessTF != null) {
				guessTF.setMinWidth(Canvas.getWidth() - 438);
			}
			if (inGame) {
				drawPic();
			}
		}

		public void bindWIthParent(Pane parent) {
			parent.getChildren().add(this);
			this.widthProperty().bind(parent.widthProperty());
			this.heightProperty().bind(parent.heightProperty());
		}
	}

	// private final String[] taxonomyLevel = {"phylum","subphylum","division","class","subclass","order"};  // taxonomy in English (just translated form czech)
	// private final String[] taxonomyLevelAbbr = {"PL","SP","DV","CL","SL","OD"};
	private final String[] taxonomyLevel = {"kmen","podkmen","nadtřída","třída","podtřída","řád"}; 
	private final String[] taxonomyLevelAbbr = {"KM","PK","NT","TR","PT","RA"};
	
	@FXML
	private Pane pane;

	@FXML
	private ScrollPane scrl;
	private ResizableCanvas Canvas;

	@FXML
	private VBox list;
	private TextField[] fields = new TextField[taxonomyLevel.length];

	@FXML
	private TextField guessTF;

	private File dir = new File("/home/havra/Documents/School/Second year/Poznávačka"); // choose your own directory
	private GraphicsContext gc;
	private Stage stage;
	private final ArrayList<File> picList = new ArrayList<>();
	private final ArrayList<ArrayList<File>> toShow = new ArrayList<>();

	private boolean inGame = false;
	private String rightAnswer;
	

	private final String[] rightList = new String[taxonomyLevel.length];
	private int indexOfShown;
	private boolean displayingAnswer = false;
	private int guessedRight, listRight;
	private int amount;

	public void setUp(Stage stage) {
		ResizableCanvas resizableCanvas = new ResizableCanvas();
		resizableCanvas.bindWIthParent(pane);
		this.Canvas = resizableCanvas;
		this.stage = stage;
		gc = Canvas.getGraphicsContext2D();
		gc.setFont(new Font(15));
		if(taxonomyLevel.length != taxonomyLevelAbbr.length){
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Error");
			alert.setContentText("Number of abbreviation of taxonomy leves and number of levels is different \n application will be closed");
			alert.showAndWait();
			System.exit(1);
		}
		try {
			addFiles(dir);
		} catch (NullPointerException e) {
		}
		for (int i = 0; i < fields.length; i++) {
			fields[i] = new TextField();
			fields[i].setPromptText(prompt(i));
			fields[i].setMaxSize(200, 30);
			fields[i].setMinSize(200, 30);
			fields[i].setOnKeyPressed((ke) -> {
				switcher(ke, false);
			});
		}
	}

	@FXML
	private void confirmGuessAction(KeyEvent ke) {
		if (inGame) {
			switcher(ke, true);
		}
	}

	@FXML
	private void helpAction(ActionEvent event) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Help");
		alert.setHeaderText("Help");
		alert.setWidth(200);
		alert.setHeight(200);
		alert.setResizable(true);
		alert.setContentText("Commiting answer: ENTER\n"
				+ "Picture:\n"
				+ "  -Next: SHIFT + ENTER/DOWN or PG_DOWN\n"
				+ "  -Previous: PAGE_UP or SHIFT + UP\n"
				+ "Text field:\n"
				+ "  -Next: CTRL + ENTER or DOWN\n"
				+ "  -Previous: UP\n"
				+ "Tgl between 'NR' or list: SHIFT + LEFT/RIGHT");
		alert.showAndWait();
	}

	private void addFiles(File src) {
		for (File f : src.listFiles()) {
			if (f.isDirectory()) {
				addFiles(f);
			} else {
				picList.add(f);
			}
		}
	}

	private String prompt(int i) {
		return taxonomyLevel[i];
	}

	private int prompt(String str) {
		for (int i = 0; i < taxonomyLevelAbbr.length; i++) {
			if(taxonomyLevelAbbr[i].equals(str)){
				return i;
			}
		}
		return -1;
//		for (String string : taxonomyLevelAbbr) {
//			if(string.equals(str)){
//			
//			}
//		}
//		switch (str) {
//			case "KM":
//				return 0;
//			case "PK":
//				return 1;
//			case "NT":
//				return 2;
//			case "TR":
//				return 3;
//			case "PT":
//				return 4;
//			case "RA":
//				return 5;
//			default:
//				return -1;
//		}
	}

	@FXML
	private void btnDir(ActionEvent event) {
		DirectoryChooser dirChooser = new DirectoryChooser();
		dirChooser.setTitle("Select File");
		dirChooser.setInitialDirectory(new File(dir.getAbsolutePath()));
		File dir1 = dirChooser.showDialog(this.stage);
		if (dir1 instanceof File) {
			dir = dir1;
		}
		picList.clear();
		addFiles(dir);
	}

	@FXML
	private void btnStartPressed(ActionEvent event) {
		if (picList.isEmpty()) {
			guessTF.setText("Choose dir with pictures");
			return;
		}
		reset(true);
		Random rd = new Random(System.nanoTime());
		for (int j = 0; j < picList.size(); j++) {
			boolean found = false;
			String name = makeName(picList.get(j));
			ArrayList<File> af;
			for (int i = toShow.size() - 1; i >= 0; i--) {
				af = toShow.get(i);
				if (makeName(af.get(0)).equals(name)) {
					found = true;
					af.add(rd.nextInt(af.size() + 1), picList.get(j));
					break;
				}
			}
			if (!found) {
				toShow.add(rd.nextInt(toShow.size() + 1), new ArrayList<>(Arrays.asList(picList.get(j))));
			}
		}
		inGame = true;
		amount = (toShow).size();
		setRight();
	}

	private void reset(boolean b) {
		if (b) {
			gc.clearRect(0, 0, Canvas.getWidth(), Canvas.getHeight());
			guessTF.setText("");
		}
		toShow.clear();
		inGame = false;
		indexOfShown = 0;
		displayingAnswer = false;
		guessedRight = listRight = 0;
	}

	private String makeName(File f) {
		String name = f.getName().substring(0, f.getName().lastIndexOf("."));
		if (Character.isDigit(name.charAt(name.length() - 1))) {
			name = name.substring(0, name.lastIndexOf(" "));
		}
		return name;
	}

	private void switcher(KeyEvent ke, boolean tf) {
		switch (ke.getCode()) {
			case ENTER:
				if (ke.isControlDown()) {
					select(ke.getSource(), true);
				} else if (ke.isShiftDown()) {
					move(true);
				} else if (displayingAnswer = !displayingAnswer) {
					if (guessTF.getText().toLowerCase().trim().equals(rightAnswer)) {
						gc.setFill(Color.GREEN);
						gc.fillRect(Canvas.getWidth() / 2 - 130, 20, 260, 40);
						gc.strokeText("Correct Name", Canvas.getWidth() / 2 - 90, 35);
						guessTF.setText("");
						guessedRight++;
					} else {
						gc.setFill(Color.RED);
						gc.fillRect(Canvas.getWidth() / 2 - 130, 20, 260, 40);
						gc.strokeText("Wrong Name, Right Name:\n" + rightAnswer, Canvas.getWidth() / 2 - 90, 35);
						guessTF.setText("");
					}
					if (testList()) {
						gc.setFill(Color.GREEN);
						gc.fillRect(Canvas.getWidth() / 2 - 130, 60, 260, 100);
						gc.strokeText("Correct taxonomy", Canvas.getWidth() / 2 - 90, 75);
						listRight++;
					} else {
						gc.setFill(Color.RED);
						gc.fillRect(Canvas.getWidth() / 2 - 130, 60, 260, 100);
						gc.strokeText("Wrong taxonomy, Right taxonomy:\n" + rightList(), Canvas.getWidth() / 2 - 90, 75);
						guessTF.setText("");
					}
				} else {
					toShow.remove(0);
					if (toShow.isEmpty()) {
						reset(true);
					} else {
						setRight();
					}
				}
				break;
			case DOWN:
				if (!ke.isShiftDown()) {
					select(ke.getSource(), true);
					break;
				}
			case PAGE_DOWN:
				move(true);
				break;
			case UP:
				if (!ke.isShiftDown()) {
					select(ke.getSource(), false);
					break;
				}
			case PAGE_UP:
				move(false);
				break;
			case LEFT:
			case RIGHT:
				if (tf && ke.isShiftDown() && !list.getChildren().isEmpty()) {
					list.getChildren().get((int) scrl.getVvalue()).requestFocus();
				} else if (!tf && ke.isShiftDown()) {
					guessTF.requestFocus();
				}
		}
	}

	@FXML
	private void btnPrev(ActionEvent event) {
		move(false);
	}

	@FXML
	private void btnNext(ActionEvent event) {
		move(true);
	}

	private void select(Object o, boolean down) {
		if (list.getChildren().isEmpty()) {
			return;
		}
		int index = list.getChildren().indexOf(o);
		if (index == -1) {
			list.getChildren().get(down ? 0 : list.getChildren().size() - 1).requestFocus();
			scrl.setVvalue(down ? 0 : list.getChildren().size() - 1);
		} else if (down ? index == list.getChildren().size() - 1 : index == 0) {
			guessTF.requestFocus();
		} else {
			list.getChildren().get(index + (down ? 1 : -1)).requestFocus();
			scrl.setVvalue(index + (down ? 1 : -1));
		}
	}

	private void move(boolean next) {
		if (!displayingAnswer && inGame) {
			if (next) {
				if (++indexOfShown == toShow.get(0).size()) {
					indexOfShown = 0;
				}
				drawPic();
			} else {
				if (indexOfShown-- == 0) {
					indexOfShown = toShow.get(0).size() - 1;
				}
				drawPic();
			}
		}
	}

	private void setRight() {
		list.getChildren().clear();
		indexOfShown = 0;
		String[] fullname = makeName(toShow.get(0).get(indexOfShown)).split("\\(");
		rightAnswer = fullname[0].toLowerCase().trim();
		int i = 0;
		if (fullname.length == 2) {
			String name = fullname[1].substring(0, fullname[1].indexOf(')'));
			String[] parts = name.split(";");
			for (; i < parts.length; i++) {
				int index = prompt(parts[i].split("\\.")[0]);
				rightList[i] = parts[i].split("\\.")[1];
				fields[i].setText("");
				list.getChildren().add(fields[index]);
			}
		}
		scrl.setVmax(i - 1);
		for (; i < 6; i++) {
			rightList[i] = "";
			fields[i].setText("");
		}
		drawPic();
	}

	private String rightList() {
		String s = "";
		for (int i = 0; i < list.getChildren().size(); i++) {
			s += rightList[i] + '\n';
		}
		return s;
	}

	private boolean testList() {
		for (int i = 0; i < list.getChildren().size(); i++) {
			boolean match = false;
			for (String s : rightList[i].toLowerCase().split(",")) {
				if (match = ((TextField) list.getChildren().get(i)).getText().toLowerCase().equals(s)) {
					break;
				}
			}
			if (!match) {
				return false;
			}
		}
		return true;
	}

	private void drawPic() {
		File file = toShow.get(0).get(indexOfShown);
		if (file.getName().toLowerCase().endsWith(".png") || file.getName().toLowerCase().endsWith(".jpg") || file.getName().toLowerCase().endsWith(".jpeg") || file.getName().toLowerCase().endsWith(".gif")) { // not totaly sure about gif
			Image image = new Image(file.toURI().toString());
			double hRatio = Canvas.getWidth() / image.getWidth();
			double vRatio = Canvas.getHeight() / image.getHeight();
			double ratio = Math.min(hRatio, vRatio);
			double centerShift_x = (Canvas.getWidth() - image.getWidth() * ratio) / 2;
			double centerShift_y = (Canvas.getHeight() - image.getHeight() * ratio) / 2;
			gc.clearRect(0, 0, Canvas.getWidth(), Canvas.getHeight());
			gc.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), centerShift_x, centerShift_y, image.getWidth() * ratio, image.getHeight() * ratio);
			gc.setFill(Color.GREEN);
			gc.fillRect(0, 0, 60, 20);
			gc.strokeText(guessedRight + "/" + listRight + '/' + amount, 5, 15);
		}
	}
}
