package com.krs.community.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Environment
import com.krs.community.model.Member
import com.krs.community.viewmodel.ProfileDetailViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal object MemberListPdfExporter {

    private const val PAGE_WIDTH = 595
    private const val PAGE_HEIGHT = 842
    private const val PAGE_MARGIN = 36f
    private const val CONTENT_BOTTOM = 790f
    private const val CARD_GAP = 14f
    private const val CARD_HEADER_HEIGHT = 34f
    private const val VALUE_LINE_HEIGHT = 12f

    private data class Field(val label: String, val value: String)

    private data class Record(
        val title: String,
        val initials: String,
        val includePhoto: Boolean,
        val fields: List<Field>
    )

    suspend fun create(
        context: Context,
        members: List<Member>,
        selectedFields: List<String>,
        profileDetailViewModel: ProfileDetailViewModel
    ): File = withContext(Dispatchers.IO) {
        val stateNames = mutableMapOf<Int, String>()
        val cityNames = mutableMapOf<Int, String>()
        val lastNames = mutableMapOf<Int, String>()
        val localNames = mutableMapOf<String, String>()
        val subCommunityNames = mutableMapOf<String, String>()
        val records = mutableListOf<Record>()
        members.forEachIndexed { index, member ->
            records += mapRecord(
                index,
                member,
                selectedFields,
                profileDetailViewModel,
                stateNames,
                cityNames,
                lastNames,
                localNames,
                subCommunityNames
            )
        }
        val outputDirectory = File(
            context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
                ?: File(context.filesDir, Environment.DIRECTORY_DOCUMENTS),
            "community"
        )
        if (!outputDirectory.exists() && !outputDirectory.mkdirs()) {
            throw IllegalStateException("Unable to create the PDF output directory")
        }

        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss_SSS", Locale.US).format(Date())
        val outputFile = File(outputDirectory, "members_$timestamp.pdf")
        val temporaryFile = File(outputDirectory, "${outputFile.name}.tmp")
        val document = PdfDocument()
        try {
            ReportRenderer(context.applicationContext, document, records).render()
            FileOutputStream(temporaryFile).use(document::writeTo)
            if (!temporaryFile.renameTo(outputFile)) {
                throw IllegalStateException("Unable to save the generated PDF")
            }
            outputFile
        } finally {
            document.close()
            temporaryFile.delete()
        }
    }

    private suspend fun mapRecord(
        index: Int,
        member: Member,
        selectedFields: List<String>,
        viewModel: ProfileDetailViewModel,
        stateNames: MutableMap<Int, String>,
        cityNames: MutableMap<Int, String>,
        lastNames: MutableMap<Int, String>,
        localNames: MutableMap<String, String>,
        subCommunityNames: MutableMap<String, String>
    ): Record {
        fun raw(value: String?): String = value.orEmpty().takeUnless {
            it.isBlank() || it.equals("null", ignoreCase = true)
        }.orEmpty()

        val state = if ("state" in selectedFields) {
            val rawState = raw(member.stateId)
            rawState.toIntOrNull()?.let { id ->
                stateNames.getOrPut(id) { viewModel.getstateNameById(id) }
            } ?: rawState
        } else ""
        val city = if ("city" in selectedFields) {
            val rawCity = raw(member.cityId)
            rawCity.toIntOrNull()?.let { id ->
                cityNames.getOrPut(id) { viewModel.getcityName(id) }
            } ?: rawCity
        } else ""
        val lastName = if ("name" in selectedFields) {
            raw(member.subCastId).toIntOrNull()?.let { id ->
                lastNames.getOrPut(id) { viewModel.getLastNameById(id) }
            }.orEmpty()
        } else ""
        val local = if ("local_community" in selectedFields) {
            val id = raw(member.localCommunityId)
            id.takeIf(String::isNotEmpty)?.let { key ->
                localNames.getOrPut(key) { viewModel.getLocalCommunityName(key) }
            } ?: id
        } else ""
        val subCommunity = if ("sub_community" in selectedFields) {
            val id = raw(member.subCommunityId)
            id.takeIf(String::isNotEmpty)?.let { key ->
                subCommunityNames.getOrPut(key) { viewModel.getSubCommName(key) }
            } ?: id
        } else ""

        val fullName = listOf(raw(member.firstName), raw(member.fatherName), lastName)
            .filter(String::isNotEmpty)
            .joinToString(" ")
        val values = mapOf(
            "gender" to raw(member.gender),
            "marital" to raw(member.maritalStatus),
            "bdate" to formatDate(member.birthDate),
            "blood" to raw(member.bloodGroup),
            "mother" to raw(member.motherName),
            "mobile" to raw(member.mobile),
            "email" to raw(member.emailAddress),
            "address" to raw(member.address),
            "state" to state,
            "city" to city,
            "area" to raw(member.area),
            "pincode" to raw(member.pincode),
            "local_community" to local,
            "sub_community" to subCommunity
        )
        val labels = mapOf(
            "gender" to "Gender",
            "marital" to "Marital status",
            "bdate" to "Birth date",
            "blood" to "Blood group",
            "mother" to "Mother",
            "mobile" to "Mobile",
            "email" to "Email",
            "address" to "Address",
            "state" to "State",
            "city" to "City",
            "area" to "Area",
            "pincode" to "Pincode",
            "local_community" to "Local community",
            "sub_community" to "Sub community"
        )
        val fields = selectedFields.mapNotNull { key ->
            val value = values[key].orEmpty()
            if (value.isBlank()) null else Field(labels[key] ?: key, value)
        }
        return Record(
            title = if ("name" in selectedFields) fullName.ifBlank { "Member ${index + 1}" }
            else "Record ${index + 1}",
            initials = raw(member.firstName).split(Regex("\\s+"))
                .mapNotNull { it.firstOrNull()?.uppercaseChar() }
                .take(2)
                .joinToString("")
                .ifBlank { "#" },
            includePhoto = "photo" in selectedFields,
            fields = fields
        )
    }

    private fun formatDate(value: String?): String {
        val date = value?.takeIf(String::isNotBlank) ?: return ""
        return Utility.changeDateFormat(date, Utility.yyyy_MM_dd, Utility.dd_MM_yyyy)
    }

    private class ReportRenderer(
        private val context: Context,
        private val document: PdfDocument,
        private val records: List<Record>
    ) {
        private val titlePaint = paint(18f, Color.rgb(48, 43, 43), bold = true)
        private val subtitlePaint = paint(9f, Color.rgb(105, 99, 99))
        private val cardTitlePaint = paint(11f, Color.WHITE, bold = true)
        private val labelPaint = paint(7.5f, Color.rgb(132, 63, 82), bold = true)
        private val valuePaint = paint(9f, Color.rgb(42, 42, 42))
        private val footerPaint = paint(8f, Color.rgb(110, 110, 110))
        private val linePaint = paint(0.7f, Color.rgb(225, 221, 220))
        private val cardPaint = paint(1f, Color.rgb(225, 221, 220))
        private val headerFillPaint = paint(1f, Color.rgb(132, 63, 82))
        private var page: PdfDocument.Page? = null
        private var canvas: Canvas? = null
        private var pageNumber = 0
        private var y = 0f
        private val contentWidth = PAGE_WIDTH - PAGE_MARGIN * 2
        private val columnGap = 18f
        private val columnWidth = (contentWidth - columnGap) / 2f

        fun render() {
            if (records.isEmpty()) {
                startPage()
                drawText("No records to export", PAGE_MARGIN, y, titlePaint)
            } else {
                records.forEach { record -> drawRecord(record) }
            }
            finishPage()
            if (pageNumber == 0) {
                startPage()
                finishPage()
            }
        }

        private fun drawRecord(record: Record) {
            val rows = record.fields.chunked(2)
            val wrappedRows = rows.map { row ->
                row.mapIndexed { column, field ->
                    val x = PAGE_MARGIN + column * (columnWidth + columnGap)
                    val lines = wrap(field.value, valuePaint, columnWidth)
                    Triple(field, x, lines)
                }
            }
            val estimatedHeight = CARD_HEADER_HEIGHT + 16f +
                    if (wrappedRows.isEmpty()) 20f else wrappedRows.fold(0f) { total, row ->
                        total + maxOf(
                            30f,
                            row.maxOfOrNull { 14f + it.third.size * VALUE_LINE_HEIGHT } ?: 30f
                        )
                    }
            if (page == null || y + estimatedHeight > CONTENT_BOTTOM) startPage()

            val currentCanvas = requireNotNull(canvas)
            val top = y
            val cardRect = RectF(PAGE_MARGIN, top, PAGE_WIDTH - PAGE_MARGIN, top + estimatedHeight)
            currentCanvas.drawRoundRect(cardRect, 6f, 6f, cardPaint)
            currentCanvas.drawRoundRect(
                RectF(PAGE_MARGIN, top, PAGE_WIDTH - PAGE_MARGIN, top + CARD_HEADER_HEIGHT),
                6f,
                6f,
                headerFillPaint
            )
            currentCanvas.drawRect(
                PAGE_MARGIN,
                top + CARD_HEADER_HEIGHT - 6f,
                PAGE_WIDTH - PAGE_MARGIN,
                top + CARD_HEADER_HEIGHT,
                headerFillPaint
            )
            val hasPhoto = record.includePhoto
            val titleX = if (hasPhoto) PAGE_MARGIN + 42f else PAGE_MARGIN + 12f
            drawText(record.title, titleX, top + 22f, cardTitlePaint)
            if (hasPhoto) drawAvatar(currentCanvas, record.initials, PAGE_MARGIN + 23f, top + 17f)

            var rowY = top + CARD_HEADER_HEIGHT + 10f
            wrappedRows.forEach { row ->
                val rowHeight = maxOf(
                    30f,
                    row.maxOfOrNull { 14f + it.third.size * VALUE_LINE_HEIGHT } ?: 30f
                )
                val dividerX = PAGE_MARGIN + columnWidth + columnGap / 2f
                currentCanvas.drawLine(
                    dividerX,
                    rowY - 3f,
                    dividerX,
                    rowY + rowHeight - 5f,
                    linePaint
                )
                row.forEach { (field, x, lines) ->
                    drawText(
                        field.label.uppercase(Locale.getDefault()),
                        x + 8f,
                        rowY + 6f,
                        labelPaint
                    )
                    lines.forEachIndexed { lineIndex, line ->
                        drawText(
                            line,
                            x + 8f,
                            rowY + 18f + lineIndex * VALUE_LINE_HEIGHT,
                            valuePaint
                        )
                    }
                }
                currentCanvas.drawLine(
                    PAGE_MARGIN + 8f,
                    rowY + rowHeight - 4f,
                    PAGE_WIDTH - PAGE_MARGIN - 8f,
                    rowY + rowHeight - 4f,
                    linePaint
                )
                rowY += rowHeight
            }
            if (record.fields.isEmpty()) {
                drawText(
                    "No selected details available",
                    PAGE_MARGIN + 12f,
                    rowY + 7f,
                    subtitlePaint
                )
            }
            canvas?.drawRoundRect(cardRect, 6f, 6f, cardPaint)
            y = top + estimatedHeight + CARD_GAP
        }

        private fun drawAvatar(target: Canvas, initials: String, centerX: Float, centerY: Float) {
            val avatarPaint = paint(1f, Color.rgb(255, 255, 255))
            target.drawCircle(centerX, centerY, 11f, avatarPaint)
            val initialsPaint = paint(8f, Color.rgb(132, 63, 82), bold = true).apply {
                textAlign = Paint.Align.CENTER
            }
            target.drawText(initials, centerX, centerY + 3f, initialsPaint)
        }

        private fun startPage() {
            finishPage()
            pageNumber += 1
            val info = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
            val newPage = document.startPage(info)
            page = newPage
            canvas = newPage.canvas.apply { drawColor(Color.WHITE) }
            drawText(
                context.getString(com.krs.community.R.string.app_name),
                PAGE_MARGIN,
                38f,
                titlePaint
            )
            drawText(
                "Member directory  |  ${records.size} records",
                PAGE_MARGIN,
                56f,
                subtitlePaint
            )
            val generated = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
            footerPaint.textAlign = Paint.Align.RIGHT
            drawText(
                "Generated $generated   |   Page $pageNumber",
                PAGE_WIDTH - PAGE_MARGIN,
                817f,
                footerPaint
            )
            footerPaint.textAlign = Paint.Align.LEFT
            canvas?.drawLine(PAGE_MARGIN, 68f, PAGE_WIDTH - PAGE_MARGIN, 68f, linePaint)
            y = 82f
        }

        private fun finishPage() {
            page?.let(document::finishPage)
            page = null
            canvas = null
        }

        private fun drawText(text: String, x: Float, baseline: Float, paint: Paint) {
            canvas?.drawText(text, x, baseline, paint)
        }

        private fun wrap(text: String, paint: Paint, maxWidth: Float): List<String> {
            val result = mutableListOf<String>()
            var line = StringBuilder()
            text.split(Regex("\\s+")).forEach { word ->
                val candidate = if (line.isEmpty()) word else "$line $word"
                if (paint.measureText(candidate) <= maxWidth - 16f) {
                    line = StringBuilder(candidate)
                } else {
                    if (line.isNotEmpty()) result.add(line.toString())
                    line = StringBuilder()
                    var part = StringBuilder()
                    word.forEach { character ->
                        val nextPart = "$part$character"
                        if (paint.measureText(nextPart) > maxWidth - 16f && part.isNotEmpty()) {
                            result.add(part.toString())
                            part = StringBuilder(character.toString())
                        } else {
                            part.append(character)
                        }
                    }
                    line.append(part)
                }
            }
            if (line.isNotEmpty()) result.add(line.toString())
            return result.ifEmpty { listOf("") }
        }

        private fun paint(size: Float, color: Int, bold: Boolean = false): Paint =
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                textSize = size
                this.color = color
                typeface =
                    if (bold) Typeface.create(Typeface.DEFAULT, Typeface.BOLD) else Typeface.DEFAULT
                style = Paint.Style.STROKE.takeIf { color == Color.rgb(225, 221, 220) }
                    ?: Paint.Style.FILL
            }
    }
}
