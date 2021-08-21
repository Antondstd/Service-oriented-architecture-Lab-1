package ru.itmo.SOA_Backend_Lab1.Service

import ru.itmo.SOA_Backend_Lab1.DAO.TicketDAO
import ru.itmo.SOA_Backend_Lab1.Model.ResponsePagesTickets
import ru.itmo.SOA_Backend_Lab1.Model.Ticket
import javax.ejb.Stateless


@Stateless
class TicketService() : TicketDAO() {


    fun `get Ticket with sorting,paging and filtering`(
        page: Int = 1,
        perPage: Int = 25,
        sortStateList: List<String>?,
        filterMap: Map<String, Array<String>>
    ): ResponsePagesTickets? {

        val tickets: List<Ticket>?
        val regExParam = "(\\w+)\\(([^\\)]+).+".toRegex()
        val regExName = "(\\w+).(\\w+)".toRegex()
        val sortMap: MutableMap<String, Pair<String, String>> = mutableMapOf() // Map<col,<table,sortType>>

        val count = getCountAndLastPageTicket()
        var pageNumber:Int
        if (count != null)
            pageNumber = Math.ceil(count.toDouble() / perPage ).toInt()
        else
            return null
        if (page > pageNumber)
            return null


        sortStateList?.forEach {
            val values: List<String>? = regExParam.find(it)?.groupValues //value(1) -> sortType, (2) -> table.col or col
            if (values != null) {
                if (!values[2].contains("."))
                    sortMap[values[2]] = Pair("ticket", values[1])
                else {
                    val classAndValue = regExName.find(values[2])
                    if (classAndValue != null) {
                        sortMap.put(
                            classAndValue.groupValues[2],
                            Pair(classAndValue.groupValues[1], values[1])
                        )
                    }
                }
            }
        }
            tickets = getSortedFilteredTickets(page,perPage,sortMap,filterMap)
        return ResponsePagesTickets(currentPage = page,perPage = perPage,tickets = tickets, countAll = count)
    }

    fun test() {
        println("TEST")
    }
}