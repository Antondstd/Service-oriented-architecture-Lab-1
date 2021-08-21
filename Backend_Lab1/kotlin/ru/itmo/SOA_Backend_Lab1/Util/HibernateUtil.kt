package ru.itmo.SOA_Backend_Lab1.Util

import ru.itmo.SOA_Backend_Lab1.Model.Coordinates
import ru.itmo.SOA_Backend_Lab1.Model.Event
import ru.itmo.SOA_Backend_Lab1.Model.Ticket
import org.hibernate.SessionFactory
import org.hibernate.boot.registry.StandardServiceRegistryBuilder
import org.hibernate.cfg.Configuration
import org.hibernate.cfg.Environment
import org.hibernate.service.ServiceRegistry
import java.util.*


class HibernateUtil {

    companion object {
        var sessionFactory: SessionFactory? = null
        get() {
            if (field == null) {
                try {
                    val settings = Properties()
                    val configuration = Configuration()
                    settings.apply {
                        put(Environment.DRIVER, "org.postgresql.Driver")
                        put(Environment.URL, "jdbc:postgresql://localhost:5432/SOA_Lab1")
                        put(Environment.USER, "postgres")
                        put(Environment.PASS, "123456")
                        put(Environment.DIALECT, "org.hibernate.dialect.PostgreSQLDialect")
                        put(Environment.SHOW_SQL, "true")
                        put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread")
//                        put(Environment.HBM2DDL_AUTO, "create-drop");
                    }
                    configuration.apply {
                        setProperties(settings)
                        addAnnotatedClass(Ticket::class.java)
                        addAnnotatedClass(Coordinates::class.java)
                        addAnnotatedClass(Event::class.java)
                    }
                    val serviceRegistry: ServiceRegistry = StandardServiceRegistryBuilder()
                        .applySettings(configuration.getProperties()).build()

                    println("Hibernate serviceRegistry created")

                    field = configuration.buildSessionFactory(serviceRegistry)
                    return field
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            return field
        }
    }
}