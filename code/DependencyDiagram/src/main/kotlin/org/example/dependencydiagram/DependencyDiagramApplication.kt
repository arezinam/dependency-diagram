package org.example.dependencydiagram

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.stage.Stage

class DependencyDiagramApplication : Application() {
    private var controller: MainController? = null


    override fun start(stage: Stage) {
        val fxmlLoader = FXMLLoader(DependencyDiagramApplication::class.java.getResource("main-view.fxml"))
        val scene = Scene(fxmlLoader.load(), 1200.0, 800.0)
        // I wanted to move the css file in a different place, but the below function refuses to work with different path
        // And it's used in the default JavaFX application, so I am afraid to use something different
        scene.stylesheets.add("/main.css")
        controller = fxmlLoader.getController()
        stage.title = "Hello!"
        stage.scene = scene
        stage.show()
    }

    override fun stop() {
        controller?.onClose()
        super.stop()
    }
}