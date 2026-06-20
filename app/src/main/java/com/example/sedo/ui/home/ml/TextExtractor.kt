package com.example.sedo.ui.home.ml

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions
import kotlinx.coroutines.tasks.await

class TextExtractor {

    // ⭐️ 한국어(및 기본 영어 숫자) 전용 OCR 엔진 초기화
    private val recognizer = TextRecognition.getClient(KoreanTextRecognizerOptions.Builder().build())

    // 코루틴 안에서 동기적으로(순서대로) 작동하도록 suspend 함수로 만듭니다.
    suspend fun extractText(bitmap: Bitmap): String {
        return try {
            // Bitmap을 ML Kit이 좋아하는 InputImage 포맷으로 변환
            val image = InputImage.fromBitmap(bitmap, 0)

            // ⭐️ 핵심: await()를 써서 텍스트를 다 읽을 때까지 기다린 다음 결과를 반환!
            val visionText = recognizer.process(image).await()

            // 추출된 전체 텍스트 덩어리(String)를 반환
            visionText.text
        } catch (e: Exception) {
            e.printStackTrace()
            "텍스트 인식 실패"
        }
    }
}