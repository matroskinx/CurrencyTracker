package com.vladislav.currencytracker

import android.content.res.XmlResourceParser
import org.xmlpull.v1.XmlPullParser

class XmlParser(xmlFile: XmlResourceParser) {
    private val elements = mutableListOf<CurrencyItem>()
    var parser: XmlPullParser = xmlFile

    fun test() {
        //call twice because of xmlResources for testing purposes
        parser.next()
        parser.next()
        parser.require(XmlPullParser.START_TAG, null, ROOT_TAG)
        val date = parser.getAttributeValue(null, DATE)

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            if (parser.name == CURRENCY) {
                elements.add(readCurrency(parser))
            }
        }
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