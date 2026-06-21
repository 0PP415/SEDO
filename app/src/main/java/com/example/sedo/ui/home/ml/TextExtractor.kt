package com.example.sedo.ui.home.ml

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions
import kotlinx.coroutines.tasks.await

class TextExtractor {

    private val recognizer = TextRecognition.getClient(KoreanTextRecognizerOptions.Builder().build())

    // 코루틴 안에서 순서대로 작동하도록 suspend 함수로 제작
    suspend fun extractText(bitmap: Bitmap): String {
        return try {
            val image = InputImage.fromBitmap(bitmap, 0)

            val visionText = recognizer.process(image).await()

            visionText.text
        } catch (e: Exception) {
            e.printStackTrace()
            "텍스트 인식 실패"
        }
    }
}