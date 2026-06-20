package com.example.sedo.ui.home.ml

import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

data class Detection(val label: String, val confidence: Float)

class SymbolDetector(private val context: Context) {

    private var interpreter: Interpreter? = null

    // ⭐️ yaml 파일에서 추출한 70개의 라벨을 코틀린 리스트로 하드코딩! (파일 IO 단축)
    private val labels = listOf(
        "ER_bleach", "ER_chlorine_bleach", "ER_dry_clean", "ER_dry_clean_chemical",
        "ER_dry_clean_chemical_mild", "ER_dry_clean_hydrocarbon", "ER_dry_clean_hydrocarbon_mild",
        "ER_dry_in_shade", "ER_flat_dry", "ER_flat_dry_in_shade", "ER_hand_wash", "ER_iron",
        "ER_iron_high_temp", "ER_iron_low_temp", "ER_iron_medium_temp", "ER_natural_dry",
        "ER_no_bleach", "ER_no_dry_clean", "ER_no_iron", "ER_no_tumble_dry", "ER_no_wash",
        "ER_no_wet_clean", "ER_non_chlorine_bleach", "ER_tumble_dry", "ER_tumble_dry_low_heat",
        "ER_tumble_dry_normal_heat", "ER_wash", "ER_wash_30", "ER_wash_30_mild", "ER_wash_40",
        "ER_wash_40_mild", "ER_wash_50", "ER_wash_50_mild", "ER_wash_60", "ER_wash_70",
        "ER_wash_95", "ER_wash_95_mild", "KR_chlorine_bleach", "KR_chlorine_or_oxygen_bleach",
        "KR_dry_clean", "KR_dry_clean_petroleum", "KR_flat_dry", "KR_flat_dry_in_shade",
        "KR_gentle_wring", "KR_hand_wash_30_neutral", "KR_hang_dry", "KR_hang_dry_in_shade",
        "KR_iron_high_temp", "KR_iron_high_temp_with_cloth", "KR_iron_low_temp",
        "KR_iron_low_temp_with_cloth", "KR_iron_medium_temp", "KR_iron_medium_temp_with_cloth",
        "KR_no_bleach", "KR_no_chlorine_bleach", "KR_no_dry_clean", "KR_no_iron",
        "KR_no_oxygen_bleach", "KR_no_tumble_dry", "KR_no_wash", "KR_no_wring",
        "KR_oxygen_bleach", "KR_professional_dry_clean_only", "KR_tumble_dry", "KR_wash_40",
        "KR_wash_60", "KR_wash_95", "KR_wash_warm_30_neutral", "KR_wash_warm_40",
        "wash-care-symbols"
    )

    init {
        setupModel()
    }

    private fun setupModel() {
        val fileDescriptor = context.assets.openFd("best_float32.tflite")
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        val modelBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)

        val options = Interpreter.Options().apply { numThreads = 4 }
        interpreter = Interpreter(modelBuffer, options)

        fileChannel.close()
        fileDescriptor.close()
    }

    fun detectSymbols(bitmap: Bitmap): List<Detection> {
        val tflite = interpreter ?: return emptyList()

        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 640, 640, true)
        val inputBuffer = ByteBuffer.allocateDirect(640 * 640 * 3 * 4)
        inputBuffer.order(ByteOrder.nativeOrder())
        val pixels = IntArray(640 * 640)
        resizedBitmap.getPixels(pixels, 0, 640, 0, 0, 640, 640)
        for (pixel in pixels) {
            inputBuffer.putFloat(((pixel shr 16) and 0xFF) / 255.0f)
            inputBuffer.putFloat(((pixel shr 8) and 0xFF) / 255.0f)
            inputBuffer.putFloat((pixel and 0xFF) / 255.0f)
        }

        // ⭐️ 핵심 수정 부분: 모델이 알려준 진짜 크기 [1, 300, 6] 에 맞게 출력 그릇을 준비합니다!
        val outputArray = Array(1) { Array(300) { FloatArray(6) } }

        // 추론 실행
        tflite.run(inputBuffer, outputArray)

        // 5. 결과 파싱 (함수 인자 타입도 변경됨)
        return parseNmsOutput(outputArray[0])
    }

    // ⭐️ nms=True 형태에 맞춰 파싱하는 새로운 로직
    private fun parseNmsOutput(output: Array<FloatArray>): List<Detection> {
        val detections = mutableListOf<Detection>()
        val confidenceThreshold = 0.25f // 임계값 25%

        // output은 300개의 박스를 담고 있습니다.
        for (i in 0 until 300) {
            val boxData = output[i]

            // 데이터 배열 규격: [x_center, y_center, width, height, confidence, class_id]
            // YOLO 버전에 따라 [4]번이 정확도, [5]번이 라벨 인덱스인 경우가 일반적입니다.
            val confidence = boxData[4]
            val classIdFloat = boxData[5]

            val classId = classIdFloat.toInt()

            // 박스의 정확도가 임계값을 넘고, 클래스 ID가 유효한지 검사
            if (confidence > confidenceThreshold && classId >= 0 && classId < labels.size) {
                detections.add(Detection(labels[classId], confidence))
            }
        }

        // 중복되는 세탁 기호를 제거 (예: 똑같은 아이콘이 두 개 찍혔을 경우 정확도 높은 것 하나만)
        return detections
            .groupBy { it.label }
            .map { it.value.maxByOrNull { d -> d.confidence }!! }
            .sortedByDescending { it.confidence }
    }

    private fun parseYoloOutput(output: Array<FloatArray>): List<Detection> {
        val detections = mutableListOf<Detection>()
        val numElements = output.size // 74
        val numBoxes = output[0].size // 8400

        val confidenceThreshold = 0.25f // 임계값 25%

        for (i in 0 until numBoxes) {
            var maxClassConf = 0f
            var maxClassId = -1

            // 인덱스 4번부터 73번까지가 클래스별 정확도입니다.
            for (c in 0 until (numElements - 4)) {
                val conf = output[c + 4][i]
                if (conf > maxClassConf) {
                    maxClassConf = conf
                    maxClassId = c
                }
            }

            // 가장 높은 정확도가 기준치를 넘으면 저장
            if (maxClassConf > confidenceThreshold && maxClassId != -1) {
                detections.add(Detection(labels[maxClassId], maxClassConf))
            }
        }

        // 중복 라벨 제거 (가장 정확도 높은 1개만 남김) 및 정렬
        return detections
            .groupBy { it.label }
            .map { it.value.maxByOrNull { d -> d.confidence }!! }
            .sortedByDescending { it.confidence }
    }

    fun close() {
        interpreter?.close()
        interpreter = null
    }
}