package ru.itmo.SOA_Backend_Lab1.Util

import ru.itmo.SOA_Backend_Lab1.Model.Ticket
import com.thoughtworks.xstream.XStream

class TicketXstream {
    companion object{
        private var xstream:XStream? = null
        fun getParser():XStream{
            if (xstream == null){
                xstream = XStream()
                xstream!!.alias("Ticket", Ticket::class.java)
                xstream!!.registerConverter(LocalDateConvertor())
                xstream!!.registerConverter(ZonedDateConvertor())
            }
            return xstream as XStream
        }
    }
}