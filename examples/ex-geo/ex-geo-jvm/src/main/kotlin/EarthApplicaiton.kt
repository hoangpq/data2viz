package io.data2viz.examples.geo

import io.data2viz.color.colors
import io.data2viz.geo.path.geoPath
import io.data2viz.geo.projection.Extent
import io.data2viz.geo.projection.orthographic
import io.data2viz.geojson.toGeoJsonObject
import io.data2viz.viz.PathVizJfx
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.layout.Pane
import javafx.stage.Stage

class EarthApplication : Application() {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(EarthApplication::class.java)
        }
    }

    override fun start(primaryStage: Stage?) {

        val extent = Extent(.0, .0, 800.0, 600.0)

        val world = this.javaClass.getResourceAsStream("/world-110m.geojson")
            .reader().readText().toGeoJsonObject()

        // OUTER GLOBE
        val projectionOuter = orthographic {
            translate = doubleArrayOf(400.0, 300.0)
            scale = 250.0
        }
        val pathVizJfxOuter = PathVizJfx().apply {
            stroke = colors.black
            fill = colors.white
        }
        val geoPathOuter = geoPath(projectionOuter, pathVizJfxOuter)
        geoPathOuter.path(world)

        // INNER GLOBE
        val projectionInner = orthographic {
            translate = doubleArrayOf(400.0, 300.0)
            scale = 250.0
            clipAngle = Double.NaN          // remove angle clipping in order to see-through
        }
        val pathVizJfxInner = PathVizJfx().apply {
            stroke = colors.black
            fill = colors.grey
        }
        val geoPathInner = geoPath(projectionInner, pathVizJfxInner)
        geoPathInner.path(world)


        val root = Pane()
        root.children.add(pathVizJfxInner.jfxElement)                   // first the "see-through" globe
        root.children.add(pathVizJfxOuter.jfxElement)                   // then the "outer" globe
        primaryStage!!.scene = (Scene(root, extent.width, extent.height))
        primaryStage.show()

        var initX = .0
        var initY = .0
        var initRotate: DoubleArray = geoPathInner.projection.rotate

        root.setOnMousePressed { event ->
            initX = event.x
            initY = event.y
            initRotate = geoPathInner.projection.rotate

        }


        root.setOnMouseDragged { event ->
            val rotate = doubleArrayOf(
                initRotate[0] + .25 * (event.x - initX),
                initRotate[1] - .25 * (event.y - initY)
            )

            pathVizJfxInner.jfxElement.elements.clear()
            pathVizJfxOuter.jfxElement.elements.clear()

            geoPathInner.projection.rotate = rotate
            geoPathOuter.projection.rotate = rotate

            geoPathOuter.path(world)
            geoPathInner.path(world)
        }

    }

}