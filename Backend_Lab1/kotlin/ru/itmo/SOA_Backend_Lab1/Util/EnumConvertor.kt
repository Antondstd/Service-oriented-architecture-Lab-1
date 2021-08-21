package ru.itmo.SOA_Backend_Lab1.Util

import ru.itmo.SOA_Backend_Lab1.Model.CustomEnum
import com.thoughtworks.xstream.converters.Converter
import com.thoughtworks.xstream.converters.MarshallingContext
import com.thoughtworks.xstream.converters.UnmarshallingContext
import com.thoughtworks.xstream.io.HierarchicalStreamReader
import com.thoughtworks.xstream.io.HierarchicalStreamWriter
import kotlin.reflect.KClass

class EnumConvertor: Converter {
    override fun canConvert(type: Class<*>): Boolean {
        return (type as KClass<out Any>).qualifiedName == "kotlin.Enum"
    }

    override fun marshal(source: Any, writer: HierarchicalStreamWriter, context: MarshallingContext) {
        writer.setValue((source as CustomEnum).type)
    }

    override fun unmarshal(reader: HierarchicalStreamReader, context: UnmarshallingContext): Any {
        var enum = ((reader.nodeName::class) as Enum<*>)
        return enum
    }
}