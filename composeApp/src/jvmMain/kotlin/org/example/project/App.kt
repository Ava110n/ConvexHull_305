package org.example.project

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.resources.painterResource

import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.compose_multiplatform
import kotlin.math.acos
import kotlin.math.sqrt

@OptIn(ExperimentalComposeUiApi::class)
@Composable
@Preview
fun App() {
    val points = remember { mutableStateListOf<Offset>() }
    //val convex_hull = remember { mutableStateListOf<Offset>() }
    Row {
        Canvas(modifier = Modifier.fillMaxSize().
            onPointerEvent(eventType = PointerEventType.Press){
                val pos = it.changes.first().position
                points.add(pos)
            }.
            onPointerEvent(eventType = PointerEventType.Release){

            }
        ) {
            drawPoints(points)
            if(points.size >= 2){
                val convex_hull = convex_hull_of_points(points)
            }
        }
    }
}
fun left_bot_point(points: SnapshotStateList<Offset>): Offset{
    var result_point = points[0]
    for(point in points){
        if(point.x < result_point.x) {
            result_point = point
            break
        }
        if(point.x == result_point.x && point.y < result_point.y)
            result_point = point
    }
    return result_point
}

fun convex_hull_of_points(points: SnapshotStateList<Offset>): List<Offset>{
    val convex_hull = mutableStateListOf<Offset>(left_bot_point(points))
    do{
        val cur_point = convex_hull.last()
        val prev_point : Offset

        if(convex_hull.size < 2) prev_point = Offset(cur_point.x, cur_point.y + 1)
        else prev_point = convex_hull[convex_hull.size -2]

        var next_point = prev_point
        for(point in points){
            //ищем ту точку, у которой угол отклонения от предыдущей прямой наибольшей
            if(angle(prev_point, cur_point, next_point) <
                angle(prev_point, cur_point, point)) next_point = point
            //если таких точек несколько - выбираем наиболее отдаленную
            if(angle(prev_point, cur_point, next_point) ==
                angle(prev_point, cur_point, point) &&
                dist(cur_point, next_point) < dist(cur_point, point))
                    next_point = point
        }
        convex_hull.add(next_point)
    }while(convex_hull.first() != convex_hull.last())
    return convex_hull
}

fun DrawScope.drawConvexHull(convex_hull: List<Offset>){
    for(i in 0 until convex_hull.size - 1){
        drawLine(Color.Red, convex_hull[i], convex_hull[i+1])
    }
    drawLine(Color.Red, convex_hull.last(), convex_hull.first())
}
fun DrawScope.drawPoints(points: SnapshotStateList<Offset>){
    for(point in points){
        drawCircle(Color.Black, radius = 10f, center = point)
    }
}
fun dist(a: Offset, b: Offset):Float {
    return sqrt((b.x-a.x)*(b.x-a.x)+(b.y-a.y)*(b.y-a.y))
}
fun angle(a:Offset, b: Offset, c: Offset):Float{
    return acos((
            pow(dist(a,b))+pow(dist(b,c))-
                pow(dist(a,c)))/
                    2*dist(a,b)*dist(b,c)
    )
}
fun pow(x: Float, n: Int = 2) = x*x

