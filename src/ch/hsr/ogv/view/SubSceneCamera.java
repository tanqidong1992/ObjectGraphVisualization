package ch.hsr.ogv.view;

import ch.hsr.ogv.controller.CameraController;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SubScene;

public class SubSceneCamera {

	private final PerspectiveCamera camera = new PerspectiveCamera(true);
	private final Xform cameraXform = new Xform();
	private final Xform cameraXform2 = new Xform();
	private final Xform cameraXform3 = new Xform();
	private final double cameraDistance = 1000;
	
	public PerspectiveCamera getPerspectiveCamera() {
		return camera;
	}
	
	public Xform getCameraXform() {
		return cameraXform;
	}

	public Xform getCameraXform2() {
		return cameraXform2;
	}

	public Xform getCameraXform3() {
		return cameraXform3;
	}
	
	public SubSceneCamera(Group root, SubScene subScene) {
        root.getChildren().add(cameraXform);
        cameraXform.getChildren().add(cameraXform2);
        cameraXform2.getChildren().add(cameraXform3);
        cameraXform3.getChildren().add(camera);
        cameraXform3.setRotateZ(180.0);
        
        camera.setNearClip(0.1);
        camera.setFarClip(10000.0);
        camera.setTranslateZ(-cameraDistance);
        cameraXform.ry.setAngle(320.0);
        cameraXform.rx.setAngle(40);
        
        subScene.setCamera(camera);
        
        new CameraController(root, subScene, this);
	}
	
}
