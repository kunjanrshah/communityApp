package com.krs.community.utils


data class PdfDetails(
    val studentName:String,
    val totalMarks:Int,
    val subjectDetailsList: List<SubjectDetails>
)