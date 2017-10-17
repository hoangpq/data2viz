package io.data2viz.shape.curve

import io.data2viz.path.PathAdapter
import io.data2viz.shape.Curve

class CardinalClosed(override val context: PathAdapter, val tension: Double = 0.0) : Curve {

    private var x0 = -1.0
    private var y0 = -1.0
    private var x1 = -1.0
    private var y1 = -1.0
    private var x2 = -1.0
    private var y2 = -1.0
    private var x3 = -1.0
    private var y3 = -1.0
    private var x4 = -1.0
    private var y4 = -1.0
    private var x5 = -1.0
    private var y5 = -1.0

    private var lineStatus = -1
    private var pointStatus = -1

    private val k = (1.0 - tension) / 6.0

    override fun areaStart() {}

    override fun areaEnd() {}

    override fun lineStart() {
        x0 = -1.0
        y0 = -1.0
        x1 = -1.0
        y1 = -1.0
        x2 = -1.0
        y2 = -1.0
        x3 = -1.0
        y3 = -1.0
        x4 = -1.0
        y4 = -1.0
        x5 = -1.0
        y5 = -1.0
        pointStatus = 0
    }

    override fun lineEnd() {
        when (pointStatus) {
            1 -> {
                context.moveTo(x3, y3)
                context.closePath()
            }
            2 -> {
                context.lineTo(x3, y3)
                context.closePath()
            }
            3 -> {
                point(x3, y3)
                point(x4, y4)
                point(x5, y5)
            }
        }
        if (lineStatus > 0) context.closePath()
        lineStatus = 1 - lineStatus
    }

    // TODO : non specific, inherit from Cardinal
    private fun curve(x: Number, y: Number) {
        context.bezierCurveTo(
                x1 + k * (x2 - x0),
                y1 + k * (y2 - y0),
                x2 + k * (x1 - x.toDouble()),
                y2 + k * (y1 - y.toDouble()),
                x2,
                y2
        )
    }

    override fun point(x: Number, y: Number) {
        when (pointStatus) {
            0 -> {
                pointStatus = 1
                x3 = x.toDouble()
                y3 = y.toDouble()
            }
            1 -> {
                pointStatus = 2
                x4 = x.toDouble()
                y4 = y.toDouble()
                context.moveTo(x4, y4)
            }
            2 -> {
                pointStatus = 3
                x5 = x.toDouble()
                y5 = y.toDouble()
            }
            else -> curve(x, y)
        }
        x0 = x1
        x1 = x2
        x2 = x.toDouble()
        y0 = y1
        y1 = y2
        y2 = y.toDouble()
    }
}