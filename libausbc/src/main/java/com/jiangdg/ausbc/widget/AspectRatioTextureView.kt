/*
 * Copyright 2017-2023 Jiangdg
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jiangdg.ausbc.widget

import android.content.Context
import android.content.res.Configuration
import android.util.AttributeSet
import android.view.Surface
import android.view.TextureView
import com.jiangdg.ausbc.utils.Logger
import kotlin.math.abs

/** Adaptive TextureView
 * Aspect ratio (width:height, such as 4:3, 16:9).
 *
 * @author Created by jiangdg on 2021/12/23
 */
class AspectRatioTextureView: TextureView, IAspectRatio {

    private var mAspectRatio = -1.0

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)
    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(context, attributeSet, defStyleAttr)

    override fun setAspectRatio(width: Int, height: Int) {
        post {
//            val orientation = context.resources.configuration.orientation
//        // 处理竖屏和横屏情况
//        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            setAspectRatio(height.toDouble() / width)
//            return
//        }
            setAspectRatio(width.toDouble() / height)
        }

    }

    override fun getSurfaceWidth(): Int  = measuredWidth

    override fun getSurfaceHeight(): Int  = measuredHeight

    override fun getSurface(): Surface? {
        return try {
            Surface(surfaceTexture)
        } catch (e: Exception) {
            null
        }
    }

    override fun postUITask(task: () -> Unit) {
        post {
            task()
        }
    }

    private fun setAspectRatio(aspectRatio: Double) {
        if (aspectRatio < 0 || mAspectRatio == aspectRatio) {
            return
        }
        mAspectRatio = aspectRatio
        Logger.i(TAG, "AspectRatio = $mAspectRatio")
        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)

        // 如果设置了宽高比，按比例调整尺寸
        if (mAspectRatio > 0) {
            // 以宽度为基准，计算正确的高度
            val targetHeight = (width / mAspectRatio).toInt()

            // 如果计算出的高度超过父容器限制，则反过来以高度为基准
            if (targetHeight > height) {
                val targetWidth = (height * mAspectRatio).toInt()
                Logger.i(TAG, "AspectRatio1 = $targetWidth x $height")
                setMeasuredDimension(targetWidth, height)
            } else {
                setMeasuredDimension(width, targetHeight)
                Logger.i(TAG, "AspectRatio2 = $height x $targetHeight")
            }
        } else {
            // 没有设置比例时使用原始尺寸
            setMeasuredDimension(width, height)
            Logger.i(TAG, "AspectRatio3 = $width x $height")
        }
    }

    companion object {
        private const val TAG = "AspectRatioTextureView"
    }
}