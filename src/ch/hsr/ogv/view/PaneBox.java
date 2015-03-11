package ch.hsr.ogv.view;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.fxml.FXMLLoader;
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import ch.hsr.ogv.util.ColorUtils;
import ch.hsr.ogv.util.ResourceLocator;
import ch.hsr.ogv.util.TextUtils;
import ch.hsr.ogv.util.ResourceLocator.Resource;

/**
 * 
 * @author Simon Gwerder
 *
 */
public class PaneBox {
	
	private final static Logger logger = LoggerFactory.getLogger(PaneBox.class);
	
	private final static int INIT_BOX_HEIGHT = 10;
	
	private Group paneBox = new Group();
	private Selection selection = null;
	private BorderPane borderPane = null;
	private Color color;
	private Cuboid box;
	
	public Group get() {
		return this.paneBox;
	}

	public Cuboid getBox() {
		return this.box;
	}

	public Selection getSelection() {
		return this.selection;
	}
	
	public Color getColor() {
		return this.color;
	}
	
	public void setColor(Color color) {
		this.color = color;
		this.borderPane.setStyle(getPaneStyle());
		this.box.setColor(color);
	}
	
	public PaneBox() {
		this(Color.WHITE);
	}
	
	public PaneBox(Color color) {
		initLayout();
        this.borderPane.setCache(true);
        this.borderPane.setCacheHint(CacheHint.SCALE_AND_ROTATE);
        
        // rotate the pane correctly
        this.borderPane.getTransforms().add(new Rotate(180, Rotate.Y_AXIS));
        this.borderPane.getTransforms().add(new Rotate(90, Rotate.X_AXIS));
        
        // position the pane so, that the center is at the scene's origin (0, 0, 0)
        this.borderPane.translateXProperty().bind(this.borderPane.widthProperty().divide(2));
        this.borderPane.translateZProperty().bind(this.borderPane.heightProperty().divide(2));
        
        // build the box that stays beneath the borderPane
        buildBox();
        
        // create the selection objects that stays with this box
        this.selection = new Selection(this);
        
        //this.paneBoxSelection.getChildren().addAll(this.borderPane, this.box.getNode(), this.selection.getNode());
        this.paneBox.getChildren().addAll(this.borderPane, this.box.get());
        this.selection.get().setVisible(false);
        
        // position the whole group so, that the center is at scene's origin (0, 0, 0)
        setTranslateY(INIT_BOX_HEIGHT / 2);
        
        setColor(color);
	}
	
	private String getPaneStyle() {
		return "-fx-background-color: " + ColorUtils.toRGBCode(this.color) + ";\n"
		+ "-fx-border-color: black;\n"
		+ "-fx-border-width: 2;";
	}
	
	private void initLayout() {
		FXMLLoader loader = new FXMLLoader(); // load class preset from fxml file
        loader.setLocation(ResourceLocator.getResourcePath(Resource.PANEPRESET_FXML));
        try {
			this.borderPane = (BorderPane) loader.load();
		} catch (IOException e) {
			logger.debug(e.getMessage());
            e.printStackTrace();
		}
	}
	
	private void buildBox() {
		this.box = new Cuboid(INIT_BOX_HEIGHT);
		this.box.setDrawTopFace(false);
		this.box.widthProperty().bind(this.borderPane.widthProperty());
		this.box.heightProperty().bind(this.borderPane.heightProperty());
		this.box.getTransforms().add(new Rotate(90, Rotate.X_AXIS));
		this.box.translateXProperty().bind(this.borderPane.translateXProperty().subtract(this.borderPane.widthProperty().divide(2)));
		this.box.translateZProperty().bind(this.borderPane.translateZProperty().subtract(this.borderPane.heightProperty().divide(2)));
		this.box.translateYProperty().bind(this.borderPane.translateYProperty().subtract(INIT_BOX_HEIGHT / 2));
	}
	
	public TextField getTop() {
		Node topNode = this.borderPane.getTop();
		if((topNode instanceof VBox)) {
			VBox topVBox = (VBox) topNode;
			if(!topVBox.getChildren().isEmpty() && topVBox.getChildren().get(0) instanceof TextField) {
				return (TextField) topVBox.getChildren().get(0);
			}
		}
		return null;
	}
	
	public void adaptWidthByText(Font font, String text) {
		// + 50px for some additional space to compensate insets, borders etc.
		double newWidth = TextUtils.computeTextWidth(font, text, 0.0D) + 50;
		this.borderPane.setPrefWidth(newWidth);
	}
	
	public void setTopText(String text) {
		TextField topTextField = getTop();
		if(topTextField == null) return;
		adaptWidthByText(topTextField.getFont(), text);
		topTextField.setText(text);
	}
	
	public void setTopFont(Font font) {
		TextField topTextField = getTop();
		if(topTextField != null) {
			topTextField.setFont(font);
			adaptWidthByText(topTextField.getFont(), topTextField.getText());
		}
	}
	
	public void setSelected(boolean value) {
		this.selection.get().setVisible(value);
		getTop().setEditable(value);
		getTop().setDisable(!value);
	}
	
	public void setWidth(double witdh) {
		this.borderPane.setMinWidth(witdh);
	}
	
	public double getWidth() {
		return this.borderPane.getWidth();
	}
	
	public void setHeight(double height) {
		this.borderPane.setMinHeight(height);
	}
	
	public double getHeight() {
		return this.borderPane.getHeight();
	}

	public void setDepth(double depth) {
		this.box.setDepth(depth);
		this.box.translateYProperty().bind(this.borderPane.translateYProperty().subtract(depth / 2));
		setTranslateY( (depth / 2) - (getTranslateY() / 2) );
	}
	
	public double getDepth() {
		return this.box.getDepth();
	}
	
	public void setTranslateY(double y) {
		this.paneBox.setTranslateY(y);
		this.selection.get().setTranslateY(y);
	}
	
	public void setTranslateXYZ(double x, double y, double z) {
		this.paneBox.getTransforms().add(new Translate(x, y, z));
		this.selection.get().getTransforms().add(new Translate(x, y, z));
	}
	
	public void setTranslateX(double x) {
		this.paneBox.setTranslateX(x);
		this.selection.get().setTranslateX(x);
	}
	
	public void setTranslateZ(double z) {
		this.paneBox.setTranslateZ(z);
		this.selection.get().setTranslateZ(z);
	}
	
	public double getTranslateY() {
		return this.paneBox.getTranslateY();
	}
	
	public double getTranslateX() {
		return this.paneBox.getTranslateX();
	}
	
	public double getTranslateZ() {
		return this.paneBox.getTranslateZ();
	}
	
	public void setVisible(boolean visible) {
		this.paneBox.setVisible(visible);
	}
	
	public void setPaneVisible(boolean visible) {
		this.borderPane.setVisible(visible);
	}
	
	public void setBoxVisible(boolean visible) {
		this.box.setVisible(visible);
	}
	
}