package com.anod.appwatcher.utils

import com.anod.appwatcher.utils.date.CustomParserFactory
import com.google.android.finsky.api.model.Document
import info.anodsplace.android.log.AppLog
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author alex
 * *
 * @date 2015-02-23
 */
object AppDetailsUploadDate {

    fun extract(doc: Document): Long {
        val defaultLocale = Locale.getDefault()
        val uploadDate = doc.appDetails.uploadDate
        val date = extract(uploadDate, defaultLocale) ?: return 0
        return date.time
    }

    internal fun extract(uploadDate: String, locale: Locale): Date? {
        if ("" == uploadDate) {
            return null
        }
        var df = CustomParserFactory.create(locale)
        if (df != null) {
            val date = extract(uploadDate, df, locale, true)
            if (date != null) {
                return date
            }
        }

        df = DateFormat.getDateInstance(DateFormat.MEDIUM, locale)
        return extract(uploadDate, df, locale, false)
    }

    private fun extract(uploadDate: String, df: DateFormat, locale: Locale, isCustom: Boolean): Date? {
        AppLog.d("Parsing: '%s' - '%s'", uploadDate, locale.toString())
        try {
            return df.parse(uploadDate)
        } catch (e: ParseException) {
            var format = "<UNKNOWN>"
            if (df is SimpleDateFormat) {
                format = df.toPattern()
            }
            val expected = df.format(Date())

            AppLog.e(ExtractDateError(
                    locale.toString(),
                    Locale.getDefault().toString(),
                    uploadDate,
                    expected,
                    format,
                    isCustom,
                    e
            ))
        }

        return null
    }

    class ExtractDateError internal constructor(val locale: String, val defaultlocale: String, val actual: String, val expected: String, val expectedFormat: String, val isCustomParser: Boolean, cause: ParseException) : Exception(cause)
}
