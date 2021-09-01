package ru.itmo.SOA_Backend_Lab1.DAO

import ru.itmo.SOA_Backend_Lab1.Util.HibernateUtil
import org.hibernate.Transaction
import ru.itmo.SOA_Backend_Lab1.Model.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.ejb.Stateless
import javax.persistence.criteria.*


@Stateless
abstract class TicketDAO {
    fun getAllTickets(): List<Ticket>? {
        var transaction: Transaction? = null
        try {
            val session = HibernateUtil.sessionFactory?.openSession()
            transaction = session?.beginTransaction()
            if (transaction == null || session == null)
                return null
            val tickets = session.createQuery("from Ticket").resultList
            transaction.commit()
            return tickets as List<Ticket>
        } catch (e: Exception) {
            if (transaction != null)
                transaction.rollback()
            e.printStackTrace()
            return null
        }
    }

    fun getSortedFilteredTickets(
        page: Int,
        perPage: Int,
        sortState: Map<String, Pair<String, String>>,
        filterMap: Map<String, Array<String>>
    ): List<Ticket>? {
        println("GOING TO SORT")
        var transaction: Transaction? = null
        try {
            val session = HibernateUtil.sessionFactory?.openSession()
            val criteriaBuilder = session?.criteriaBuilder!!
            val q = criteriaBuilder.createQuery(Ticket::class.java)
            val root = q.from(Ticket::class.java)
            val coordinatesJoin: Join<Ticket, Coordinates> = root.join("coordinates")
            val eventJoin: Join<Ticket, Event> = root.join("event")
            val conditions: MutableList<Predicate?> = mutableListOf()
            val sortOrders = mutableListOf<Order>()
            var res = q.select(root)
            if (sortState.size > 0) {
                sortState.forEach { key, value ->
                    val cur: Path<String>
                    println("TABLE ${value.first} and sortType ${value.second}")
                    cur = when (value.first) {
                        "ticket" -> root.get<String>(key)
                        "coordinates" -> coordinatesJoin.get<String>(key)
                        "event" -> eventJoin.get<String>(key)
                        else -> return@forEach
                    }
                    if (value.second == "asc")
                        sortOrders.add(criteriaBuilder.asc(cur))
                    if (value.second == "desc")
                        sortOrders.add(criteriaBuilder.desc(cur))
                }
                res.orderBy(sortOrders)
            }
            if (filterMap.size > 0) {
                filterMap.forEach { (key, value) ->
                    var name = mutableListOf<String>()
                    if (key.contains("."))
                        name = key.split(".").toMutableList()
                    else {
                        name.add(0, "ticket")
                        name.add(1, key)
                    }
                    if (value.size == 1) {
                        val cur: Path<String>
                        cur = when (name[0]) {
                            "ticket" -> root.get<String>(name[1])
                            "coordinates" -> coordinatesJoin.get<String>(name[1])
                            "event" -> eventJoin.get<String>(name[1])
                            else -> {
                                println("OMG :(")
                                return@forEach
                            }
                        }
                        println("CHECK THIS : $name[1] and ${value[0]}")
                        if (name[1].equals("date") || name[1].equals("creationDate")) {
                            val formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yy")
                            val date = LocalDateTime.parse(value[0], formatter)
                            conditions.add(criteriaBuilder.equal(cur, value[0]))
                        } else
                            conditions.add(criteriaBuilder.equal(cur, value[0]))
                    }
                }
                res.where(criteriaBuilder.or(*conditions.toTypedArray()))
//                return session.createQuery(
//                    q.select(root).orderBy(sortOrders).where(criteriaBuilder.or(*conditions.toTypedArray()))
//                ).resultList
            }

            return session.createQuery(res).setFirstResult((page-1)*perPage).setMaxResults(perPage).resultList
        } catch (e: Exception) {
            if (transaction != null)
                transaction.rollback()
            e.printStackTrace()
            return null
        }
    }

    fun getSortedFilteredTickets(
        page: Int,
        perPage: Int,
        sortAdditions: List<QueryAdditions>,
        filterAdditions: List<QueryAdditions>
    ): List<Ticket>? {
        var transaction: Transaction? = null
        try {
            val session = HibernateUtil.sessionFactory?.openSession()
            val criteriaBuilder = session?.criteriaBuilder!!
            val q = criteriaBuilder.createQuery(Ticket::class.java)
            val root = q.from(Ticket::class.java)
            var res = q.select(root)
            if (sortAdditions.size > 0) {
                addClause(sortAdditions,root,true,q,criteriaBuilder)
            }
            if (filterAdditions.size > 0) {
                addClause(filterAdditions,root,false,q,criteriaBuilder)
                }

            return session.createQuery(res).setFirstResult((page-1)*perPage).setMaxResults(perPage).resultList
        } catch (e: Exception) {
            if (transaction != null)
                transaction.rollback()
            e.printStackTrace()
            return null
        }
    }

    fun addClause(clause:List<QueryAdditions>, root:Root<Ticket>, type:Boolean, resultQuery: CriteriaQuery<*>, criteriaBuilder: CriteriaBuilder){
        val coordinatesJoin: Join<Ticket, Coordinates> = root.join("coordinates")
        val eventJoin: Join<Ticket, Event> = root.join("event")
        val orders = mutableListOf<Order>()
        val conditions: MutableList<Predicate?> = mutableListOf()

        clause.forEach { addition ->
            val cur: Path<String>
            cur = when (addition.table) {
                TablesQueryAdditions.TICKET -> root.get<String>(addition.column)
                TablesQueryAdditions.COORDINATES -> coordinatesJoin.get<String>(addition.column)
                TablesQueryAdditions.EVENT -> eventJoin.get<String>(addition.column)
                else -> return@forEach
            }
            if (type) {
                if (addition.sortType)
                    orders.add(criteriaBuilder.asc(cur))
                else (addition.sortType)
                    orders.add(criteriaBuilder.desc(cur))

            } else{
                if (addition.column.equals("date") || addition.column.equals("creationDate")) {
                    val formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yy")
                    val date = LocalDateTime.parse(addition.firstValue, formatter)
                    conditions.add(criteriaBuilder.equal(cur, addition.firstValue))
                } else
                    conditions.add(criteriaBuilder.equal(cur, addition.firstValue))
            }
        }
        if (type)
            resultQuery.orderBy(orders)
        else
            resultQuery.where(criteriaBuilder.or(*conditions.toTypedArray()))
    }

    fun getCountTicket(filterAdditions: List<QueryAdditions>?):Long?{
        var transaction: Transaction? = null
        try {
            val session = HibernateUtil.sessionFactory?.openSession()
            transaction = session?.beginTransaction()
            if (transaction == null || session == null)
                return null
            val criteriaBuilder = session.criteriaBuilder!!
            val createQueryCount = criteriaBuilder.createQuery(Long::class.java)
//            val createQueryTicket = criteriaBuilder.createQuery(Ticket::class.java)
            val root = createQueryCount.from(Ticket::class.java)
            println("FILTER COUNT")
            if (filterAdditions != null && filterAdditions.size > 0) {
                addClause(filterAdditions, root, false, createQueryCount, criteriaBuilder)
            }
            createQueryCount.select(criteriaBuilder.count(root))
            val count = session.createQuery(createQueryCount).singleResult
            transaction.commit()
            return count as Long
        } catch (e: Exception) {
            if (transaction != null)
                transaction.rollback()
            e.printStackTrace()
            return null
        }
    }

    fun getTicket(id: Long): Ticket? {
        var transaction: Transaction? = null
        try {
            val session = HibernateUtil.sessionFactory?.openSession()
            transaction = session?.beginTransaction()
            if (transaction == null || session == null)
                return null
            val ticket = session.createQuery("from Ticket Where id = $id").resultList[0] as Ticket
            transaction.commit()
            return ticket
        } catch (e: Exception) {
            if (transaction != null)
                transaction.rollback()
            e.printStackTrace()
            return null
        }
    }

    fun updateTicket(ticket: Ticket): Boolean {
        var transaction: Transaction? = null
        try {
            val session = HibernateUtil.sessionFactory?.openSession()
            transaction = session?.beginTransaction()
            if (transaction == null || session == null)
                return false
            session.update(ticket)
            transaction.commit()
            return true
        } catch (e: Exception) {
            if (transaction != null)
                transaction.rollback()
            e.printStackTrace()
            return false
        }
    }

    fun addTicket(ticket: Ticket): Boolean {
        var transaction: Transaction? = null
        try {
            val session = HibernateUtil.sessionFactory?.openSession()
            transaction = session?.beginTransaction()
            if (transaction == null || session == null)
                return false
            val tickets = session.save(ticket)
            transaction.commit()
            return true
        } catch (e: Exception) {
            if (transaction != null)
                transaction.rollback()
            e.printStackTrace()
            return false
        }
    }

    fun deleteTicket(ticketId: Long): Boolean {
        var transaction: Transaction? = null
        try {
            val session = HibernateUtil.sessionFactory?.openSession()
            transaction = session?.beginTransaction()
            if (transaction == null || session == null)
                return false
            val ticket = session.find(Ticket::class.java, ticketId)
            if (ticket != null) {
                session.delete(ticket)
                session.flush()
            }
            transaction.commit()
            return true
        } catch (e: Exception) {
            if (transaction != null)
                transaction.rollback()
            e.printStackTrace()
            return false
        }
    }
}