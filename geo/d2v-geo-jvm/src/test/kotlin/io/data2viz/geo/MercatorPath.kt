package io.data2viz.geo

import com.fasterxml.jackson.databind.ObjectMapper
import io.data2viz.geo.clip.clipCircle
import io.data2viz.geo.clip.clipRectangle
import io.data2viz.geo.path.geoPath
import io.data2viz.geo.projection.Extent
import io.data2viz.geo.projection.equirectangularProjection
import io.data2viz.geo.projection.mercatorProjection
import io.data2viz.geojson.JacksonGeoJsonObject
import io.data2viz.geojson.MultiPolygon
import io.data2viz.geojson.toGeoJsonObject
import io.data2viz.path.SvgPath
import io.data2viz.path.svgPath
import io.data2viz.path.toJfxPath
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.stage.Stage

class Timer {
    var last = System.currentTimeMillis()

    fun log(msg: String) {
        val newTime = System.currentTimeMillis()
        println("$msg in ${newTime - last} ms")
        last = newTime
    }
}

class MercatorPath : Application() {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(MercatorPath::class.java)
        }
    }

    private val timer = Timer()

    override fun start(primaryStage: Stage?) {

        val input = this.javaClass.getResourceAsStream("/world-110m.geojson")
        val geojson = ObjectMapper().readValue(input, JacksonGeoJsonObject::class.java)
        val geoJsonObject = geojson.toGeoJsonObject()

//        val geoJsonObject = MultiPolygon(
//            arrayOf(
//                arrayOf(
//                    arrayOf(
//                        arrayOf(50.0, 50.0),
//                        arrayOf(25.5, 75.5),
//                        arrayOf(50.0, 100.0),
//                        arrayOf(100.0, 100.0),
//                        arrayOf(125.5, 75.5),
//                        arrayOf(100.0, 50.0),
//                        arrayOf(50.0, 50.0)
//                    )
//                )
//            )
//        )

        val projection = equirectangularProjection {
            center = doubleArrayOf(10.0, 5.0)
            translate = doubleArrayOf(480.0, 350.0)
            scale = 200.0
            precision = .0
//          postClip = clipRectangle(Extent(48.0, 50.0, 498.0, 500.0))
            preClip = clipCircle(45.0)
        }

        var geoPath = geoPath(projection, svgPath())
        var path: SvgPath = geoPath.path(geoJsonObject) as SvgPath

        val root = Pane()
        root.children.add(path.toJfxPath().apply {
            fill = null
            stroke = Color.BLACK
            strokeWidth = 1.0
        })
        primaryStage!!.scene = (Scene(root, 960.0, 700.0))
        primaryStage.show()
    }

}