package ru.itmo.SOA_Backend_Lab1.Service

import ru.itmo.SOA_Backend_Lab1.DAO.TicketDAO
import ru.itmo.SOA_Backend_Lab1.Model.QueryAdditions
import ru.itmo.SOA_Backend_Lab1.Model.ResponsePagesTickets
import ru.itmo.SOA_Backend_Lab1.Model.TablesQueryAdditions
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
        val sortQueryAdditions: MutableList<QueryAdditions> = mutableListOf()
        val filerQueryAdditions:MutableList<QueryAdditions> = mutableListOf()


        sortStateList?.forEach {
            val values: List<String>? = regExParam.find(it)?.groupValues //value(1) -> sortType, (2) -> table.col or col
            if (values != null) {
                if (!values[2].contains("."))
                    sortQueryAdditions.add(QueryAdditions(TablesQueryAdditions.TICKET,values[2],sortType = values[1] == "asc"))
//                    sortMap[values[2]] = Pair("ticket", values[1])
                else {
                    val classAndValue = regExName.find(values[2])
                    val table = when(classAndValue?.groupValues?.get(1)){
                        "coordinates" -> TablesQueryAdditions.COORDINATES
                        "event" -> TablesQueryAdditions.EVENT
                        else -> null
                    }
                    if (table != null && classAndValue != null) {
                            sortQueryAdditions.add(QueryAdditions(table, classAndValue.groupValues[2], sortType = values[1] == "asc" ))
//                        sortMap.put(
//                            classAndValue.groupValues[2],
//                            Pair(classAndValue.groupValues[1], values[1])
//                        )
                    }
                }
            }
        }

        filterMap.forEach{
            key,value ->
            var name = mutableListOf<String>()
            if (key.contains("."))
                name = key.split(".").toMutableList()
            else {
                name.add(0, "ticket")
                name.add(1, key)
            }
            val table = when(name[0]){
            "coordinates" -> TablesQueryAdditions.COORDINATES
            "event" -> TablesQueryAdditions.EVENT
            else -> TablesQueryAdditions.TICKET
            }
            filerQueryAdditions.add(QueryAdditions(table,name[1],value[0], if (value.size > 1) value[1] else null))
        }

        val count = getCountTicket(filerQueryAdditions)
        var pageNumber:Int
        if (count != null)
            pageNumber = Math.ceil(count.toDouble() / perPage ).toInt()
        else
            return null
        if (page > pageNumber)
            return null
//            tickets = getSortedFilteredTickets(page,perPage,sortMap,filterMap)
        tickets = getSortedFilteredTickets(page,perPage,sortQueryAdditions, filerQueryAdditions)
        return ResponsePagesTickets(currentPage = page,perPage = perPage,tickets = tickets, countAll = count)
    }

    fun test() {
        println("TEST")
    }
}