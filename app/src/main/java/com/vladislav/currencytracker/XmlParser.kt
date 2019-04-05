package com.vladislav.currencytracker

import android.util.Log
import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.InputStream

class XmlParser(val inputStream: InputStream) {
    private val elements = mutableListOf<CurrencyItem>()
    var parser: XmlPullParser = Xml.newPullParser()

    fun parse(): DayExchangeRates {
        parser.setInput(inputStream, null)
        parser.nextTag()
        parser.require(XmlPullParser.START_TAG, null, ROOT_TAG)

        if(parser.isEmptyElementTag)
        {
            Log.d("Parser", "empty tag")
            throw XmlPullParserException("Document is empty. Rates are not available")
        }

        val date = parser.getAttributeValue(null, DATE)

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            if (parser.name == CURRENCY) {
                elements.add(readCurrency(parser))
            }
        }

        return DayExchangeRates(elements, date)
    }

    private fun readCurrency(parser: XmlPullParser): CurrencyItem {
        parser.require(XmlPullParser.START_TAG, null, CURRENCY)
        val id = parser.getAttributeValue(null, ID)
        var numCode = ""
        var charCode = ""
        var scale = ""
        var name = ""
        var rate = ""

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            when (parser.name) {
                NUM_CODE -> numCode = readTag(parser, NUM_CODE)
                CHAR_CODE -> charCode = readTag(parser, CHAR_CODE)
                SCALE -> scale = readTag(parser, SCALE)
                NAME -> name = readTag(parser, NAME)
                RATE -> rate = readTag(parser, RATE)
            }
        }

        return CurrencyItem(id, numCode, charCode, scale, name, rate)
    }

    private fun readTag(parser: XmlPullParser, tagName: String): String {
        parser.require(XmlPullParser.START_TAG, null, tagName)
        var text = ""
        if (parser.next() == XmlPullParser.TEXT) {
            text = parser.text
            parser.nextTag()
        }
        parser.require(XmlPullParser.END_TAG, null, tagName)
        return text
    }

    companion object {
        const val NUM_CODE = "NumCode"
        const val CHAR_CODE = "CharCode"
        const val SCALE = "Scale"
        const val NAME = "Name"
        const val RATE = "Rate"
        const val CURRENCY = "Currency"
        const val DATE = "Date"
        const val ROOT_TAG = "DailyExRates"
        const val ID = "Id"
    }
}