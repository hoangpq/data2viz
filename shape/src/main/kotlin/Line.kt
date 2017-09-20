package io.data2viz.shape


import curve.Linear
import io.data2viz.path.PathAdapter


fun <T> line(init: LineGenerator<T>.() -> Unit) = LineGenerator<T>().apply(init)

fun <T, D> const(constantValue: T): (D) -> T = { constantValue }


class LineGenerator<T> {

    var output:Linear? = null
    var x: (T) -> Double = const(.0)
    var y: (T) -> Double = const(.0)


    /**
     * Use the datas to generate a line on the context
     */
    fun <C: PathAdapter> line(datas: Array<T>, context:C):C{
        val n = datas.size

        var defined0 = false
        output = Linear(context)

        for (i in 0..n){

            //todo add defined
            fun startOrEnd() = (!(i<n) == defined0)

            if (startOrEnd()){
                defined0 = !defined0
                if (defined0)
                    output!!.lineStart()
                else
                    output!!.lineEnd()
            }

            if (defined0) {
                val data = datas[i]
                output!!.point(x(data), y(data))
            }
        }
        return context
    }

}
