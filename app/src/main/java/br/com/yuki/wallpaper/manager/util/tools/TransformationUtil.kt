package br.com.yuki.wallpaper.manager.util.tools

import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Size
import android.view.View
import br.com.yuki.wallpaper.manager.database.model.image.Image
import kotlin.math.max

object TransformationUtil {

    data class MatrixData(
        val matrix: Matrix,
        val baseScale: Float,
    )

    fun scaleToLowIfNecessary(currentImage: Bitmap, target: Size): Bitmap {
        if (currentImage.width > target.width || currentImage.height > target.height) {
            val scale = max(
                target.width / currentImage.width.toFloat(),
                target.height / currentImage.height.toFloat()
            )
            return Bitmap.createScaledBitmap(currentImage, currentImage.width.times(scale).toInt(), currentImage.height.times(scale).toInt(), false).apply {
                currentImage.recycle()
            }
        }

        return currentImage
    }

    fun makeMatrix(bitmap: Size, target: Size, info: Image.Info): MatrixData {
        return Matrix().let { matrix ->
            val scale = max(
                target.width / bitmap.width.toFloat(),
                target.height / bitmap.height.toFloat()
            )
            matrix.setTranslate(info.point.x * target.width, info.point.y * target.height)
            matrix.preScale(scale * info.zoom, scale * info.zoom)

            MatrixData(matrix, scale)
        }
    }

    fun centerCrop(bitmap: Size, target: View): MatrixData = centerCrop(bitmap, target.run { Size(width, height) })

    fun centerCrop(bitmap: Size, target: Size): MatrixData {
        val matrix = Matrix()

        val scale = max(
            target.width / bitmap.width.toFloat(),
            target.height / bitmap.height.toFloat()
        )

        val centerX = target.width.minus(bitmap.width * scale) / 2f
        val centerY = target.height.minus(bitmap.height * scale) / 2f

        matrix.setTranslate(centerX, centerY)
        matrix.preScale(scale, scale)

        return MatrixData(matrix, scale)
    }

}