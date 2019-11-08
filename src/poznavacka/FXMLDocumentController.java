package poznavacka;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

/**
 *
 * @author havra
 */
public class FXMLDocumentController {

	class ResizableCanvas extends Canvas {

		public ResizableCanvas() {
			widthProperty().addListener(e -> update());
			heightProperty().addListener(e -> update());
		}

		private void update() {
			if (Canvas != null && guessTF != null) {
				guessTF.setMinWidth(Canvas.getWidth() - 220);
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

	@FXML
	private Pane pane;

	@FXML
	private ScrollPane scrl;
	private ResizableCanvas Canvas;

	@FXML
	private VBox list;
	private TextField[] fields = new TextField[6];

	@FXML
	private TextField guessTF;

	private File dir = new File("/home/havra/Documents/School/Second year/Poznávačka");
	private GraphicsContext gc;
	private final ArrayList<File> picList = new ArrayList<>();
	private final ArrayList<ArrayList<File>> toShow = new ArrayList<>();

	private boolean inGame = false;
	private Stage stage;
	private String rightAnswer;
	private final String[] rightList = new String[6];
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
		addFiles(dir);
		for (int i = 0; i < fields.length; i++) {
			fields[i] = new TextField();
			fields[i].setPromptText(prompt(i));
			fields[i].setMaxSize(200, 30);
			fields[i].setMinSize(200, 30);
			fields[i].setOnKeyPressed((ke) -> {
				switch (ke.getCode()) {
					case ENTER:
						if (ke.isShiftDown() || displayingAnswer) {
							confirmGuessAction(ke);
							break;
						}
					case DOWN:
						if (ke.isShiftDown()) {
							move(true);
						} else {
							select(ke.getSource(), true);
						}
						break;
					case UP:
						if (ke.isShiftDown()) {
							move(false);
						} else {
							select(ke.getSource(), false);
						}
						break;
					case LEFT:
					case RIGHT:
						if (ke.isShiftDown()) {
							guessTF.requestFocus();
						}
				}
			});
		}
	}

	private void select(Object o, boolean down) {
		if (list.getChildren().isEmpty()) {
			return;
		}
		int index = list.getChildren().indexOf(o);
		if (down && index == list.getChildren().size() - 1) {
			list.getChildren().get(0).requestFocus();
			scrl.setVvalue(0);
		} else if (index == 0 && !down) {
			list.getChildren().get(list.getChildren().size() - 1).requestFocus();
			scrl.setVvalue(scrl.getVmax());
		} else {
			list.getChildren().get(index + (down ? 1 : -1)).requestFocus();
			scrl.setVvalue(index + (down ? 1 : -1));
		}
	}

	private String prompt(int i) {
		switch (i) {
			case 0:
				return "kmen";
			case 1:
				return "podkmen";
			case 2:
				return "nadtřída";
			case 3:
				return "třída";
			case 4:
				return "podtřída";
			case 5:
				return "řád";
			default:
				return "none";
		}
	}

	private int prompt(String str) {
		switch (str) {
			case "KM":
				return 0;
			case "PK":
				return 1;
			case "NT":
				return 2;
			case "TR":
				return 3;
			case "PT":
				return 4;
			case "RA":
				return 5;
			default:
				return -1;
		}
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

	private void addFiles(File src) {
		for (File f : src.listFiles()) {
			if (f.isDirectory()) {
				addFiles(f);
			} else {
				picList.add(f);
			}
		}
	}

	@FXML
	private void btnResetPressed(ActionEvent event) {
		if (picList.isEmpty()) {
			guessTF.setText("Zvolte složku s obrázky.");
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

	private String makeName(File f) {
		String name = f.getName().substring(0, f.getName().lastIndexOf("."));
		if (Character.isDigit(name.charAt(name.length() - 1))) {
			name = name.substring(0, name.lastIndexOf(" "));
		}
		return name;
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

	@FXML
	private void confirmGuessAction(KeyEvent event) {
		if (inGame) {
			KeyCode kc = event.getCode();
			switch (kc) {
				case ENTER:
					if (!displayingAnswer) {
						displayingAnswer = true;
						System.out.println(guessTF.getText().toLowerCase().trim()+"+"+rightAnswer);	
						if (guessTF.getText().toLowerCase().trim().equals(rightAnswer)) {
							gc.setFill(Color.GREEN);
							gc.fillRect(Canvas.getWidth() / 2 - 110, 20, 220, 40);
							gc.strokeText("Correct", Canvas.getWidth() / 2 - 90, 35);
							guessTF.setText("");
							guessedRight++;
						} else {
							gc.setFill(Color.RED);
							gc.fillRect(Canvas.getWidth() / 2 - 110, 20, 220, 40);
							gc.strokeText("Wrong, Right Answer:\n" + rightAnswer, Canvas.getWidth() / 2 - 90, 35);
							guessTF.setText("");
						}
						if (testList()) {
							gc.setFill(Color.GREEN);
							gc.fillRect(Canvas.getWidth() / 2 - 110, 60, 220, 100);
							gc.strokeText("Correct", Canvas.getWidth() / 2 - 90, 75);
							listRight++;
						} else {
							gc.setFill(Color.RED);
							gc.fillRect(Canvas.getWidth() / 2 - 110, 60, 220, 100);
							gc.strokeText("Wrong, Right Answer:\n" + rightList(), Canvas.getWidth() / 2 - 90, 75);
							guessTF.setText("");
						}
					} else {
						toShow.remove(0);
						displayingAnswer = false;
						if (toShow.isEmpty()) {
							reset(true);
						} else {
							setRight();
						}
					}
					break;
				case PAGE_DOWN:
				case DOWN:
					move(true);
					break;
				case PAGE_UP:
				case UP:
					move(false);
					break;
				case RIGHT:
				case LEFT:
					if (event.isShiftDown() && !list.getChildren().isEmpty()) {
						list.getChildren().get(0).requestFocus();
						scrl.setVvalue(0);
					}
			}
		}
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
				System.out.println(s);
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

	@FXML
	private void btnPrev(ActionEvent event) {
		move(false);
	}

	@FXML
	private void btnNext(ActionEvent event) {
		move(true);
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

	private void drawPic() {
		File file = toShow.get(0).get(indexOfShown);
		if (file.getName().toLowerCase().endsWith(".png") || file.getName().toLowerCase().endsWith(".jpg")
				|| file.getName().toLowerCase().endsWith(".jpeg")) {
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
