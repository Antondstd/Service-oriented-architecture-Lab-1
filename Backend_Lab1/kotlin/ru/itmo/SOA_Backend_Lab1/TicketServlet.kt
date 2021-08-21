package ru.itmo.SOA_Backend_Lab1

import ru.itmo.SOA_Backend_Lab1.Model.ResponsePagesTickets
import ru.itmo.SOA_Backend_Lab1.Model.Ticket
import ru.itmo.SOA_Backend_Lab1.Service.TicketService
import ru.itmo.SOA_Backend_Lab1.Util.TicketXstream
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.stream.Collectors
import javax.inject.Inject
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@WebServlet(name = "ticketServlet", value = ["/api/ticket"])
class TicketServlet : HttpServlet() {

    @Inject
    private lateinit var ticketService: TicketService

    override fun doPut(req: HttpServletRequest, resp: HttpServletResponse) {
        req.setCharacterEncoding("utf8");

        val inputStream: InputStream = req.inputStream
        val reader = BufferedReader(InputStreamReader(inputStream, StandardCharsets.UTF_8))
        val body = reader.lines().collect(Collectors.joining(System.lineSeparator()))
        val xstream = TicketXstream.getParser()
        val newTicket = xstream.fromXML(body) as Ticket

        ticketService.addTicket(newTicket)

        resp.writer.println("NICE")
        cors(resp)
    }

    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        req.parameterMap.forEach { key, value ->
            println("Key : $key")
            value.forEach { println(it) }
        }
        val pageNumberS = req.getParameter("page")
        val pageNumber: Int
        val perPageS = req.getParameter("perPage")
        val perPage: Int
        if (pageNumberS == null || pageNumberS == "")
            pageNumber = 1
        else
            pageNumber = pageNumberS.toInt()
        if (perPageS == null || perPageS == "")
            perPage = 25
        else
            perPage = perPageS.toInt()

        val sortStateArray = req.getParameter("sortState")?.split(',')
        val listOfTicketNames = Ticket.getNamesForFilter()
        val filterMap: Map<String, Array<String>> = req.parameterMap.toMutableMap().filter { (key, value) ->
            println("CHECKS $key in ${listOfTicketNames.toString()}")
            listOfTicketNames.contains(key)
        }


        val responsePagesTickets = ticketService.`get Ticket with sorting,paging and filtering`(pageNumber, perPage, sortStateArray, filterMap)
        val xstream = TicketXstream.getParser()

        resp.setHeader("Content-Type", "text/xml; charset=UTF-16LE");
        xstream.alias("ResponsePagesTickets", ResponsePagesTickets::class.java)
        resp.writer.println(xstream.toXML(responsePagesTickets))
        cors(resp)
    }

    override fun doDelete(req: HttpServletRequest, resp: HttpServletResponse) {
        println(req.getParameter("id"))
        val id = req.getParameter("id").toLong()
        println("DELETING with id $id")
        ticketService.deleteTicket(id)
        cors(resp)
    }

    override fun doPost(req: HttpServletRequest, resp: HttpServletResponse) {
        req.setCharacterEncoding("utf8");

        val inputStream: InputStream = req.inputStream
        val reader = BufferedReader(InputStreamReader(inputStream, StandardCharsets.UTF_8))
        val body = reader.lines().collect(Collectors.joining(System.lineSeparator()))
        val xstream = TicketXstream.getParser()
        val newTicket = xstream.fromXML(body) as Ticket
        val oldTicket = ticketService.getTicket(req.getParameter("id").toLong())


        if (oldTicket != null) {
            newTicket.creationDate = oldTicket.creationDate
            ticketService.updateTicket(newTicket)
            return
        }


        resp.sendError(503)
        cors(resp)
    }

    override fun doOptions(req: HttpServletRequest, resp: HttpServletResponse) {
        cors(resp)
    }


    fun cors(resp: HttpServletResponse) {
        resp.addHeader("Access-Control-Allow-Origin", "*")
        resp.addHeader("Access-Control-Allow-Methods", "GET, PUT, POST, DELETE, HEAD, OPTIONS");
        resp.addHeader("Access-Control-Allow-Headers", "Content-Type");
        resp.addHeader("Access-Control-Allow-Credentials", "true");
    }
}