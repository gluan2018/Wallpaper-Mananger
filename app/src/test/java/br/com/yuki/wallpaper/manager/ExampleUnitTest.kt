package br.com.yuki.wallpaper.manager

import android.util.Size
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val (bitmapWidth:Int, bitmapHeight:Int) = 50 to 50
        val (targetWidth:Int, targetHeight:Int) = 100 to 100

        if (targetWidth > bitmapWidth || targetHeight > bitmapHeight) {
            println("Precisa escalonar e centralizar")
        } else {
            print("Precisa centralizar")
        }
    }
}