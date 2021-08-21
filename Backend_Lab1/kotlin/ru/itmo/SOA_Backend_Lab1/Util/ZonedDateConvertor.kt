package ru.itmo.SOA_Backend_Lab1.Util

import com.thoughtworks.xstream.converters.Converter
import com.thoughtworks.xstream.converters.MarshallingContext
import com.thoughtworks.xstream.converters.UnmarshallingContext
import com.thoughtworks.xstream.io.HierarchicalStreamReader
import com.thoughtworks.xstream.io.HierarchicalStreamWriter
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class ZonedDateConvertor:Converter {
    override fun canConvert(type: Class<*>): Boolean {
        return type == ZonedDateTime::class.java
//        return (type as KClass<out Any>).qualifiedName == "kotlin.Enum"
    }

    override fun marshal(source: Any, writer: HierarchicalStreamWriter, context: MarshallingContext) {
        writer.setValue((source as ZonedDateTime).format(DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yy")))
    }

    override fun unmarshal(reader: HierarchicalStreamReader, context: UnmarshallingContext): Any {
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yy")
//        LocalDateTime.parse(reader.value,formatter)
        return ZonedDateTime.parse(reader.value, formatter.withZone(ZoneId.systemDefault()))
    }
}