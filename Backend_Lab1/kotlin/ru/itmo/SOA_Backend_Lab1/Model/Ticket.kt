package ru.itmo.SOA_Backend_Lab1.Model

import org.hibernate.annotations.CreationTimestamp
import java.time.ZonedDateTime
import javax.persistence.*
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size
import kotlin.reflect.full.declaredMemberProperties

@Entity
data class Ticket(
    @NotNull
    var name: String = "000",
    @NotNull
    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn
//    @JacksonXmlElementWrapper(localName = "coordinates")
    var coordinates: Coordinates = Coordinates(),
    @NotNull
    @CreationTimestamp
    @Column(columnDefinition= "TIMESTAMP WITH TIME ZONE")
    var creationDate: ZonedDateTime = ZonedDateTime.now(),
    @NotNull
    @Min(0)
    var price: Long = 0,
    @NotNull
    @Min(0)
    @Max(100)
    var discount: Int = 0,
    @NotNull
    @Size(max = 891)
    var comment: String = "",
    @NotNull
    var type: TicketType = TicketType.BUDGETARY,
    @NotNull
    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn
    var event: Event = Event()
) {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long = 0

    companion object{
        fun getNamesForFilter():List<String> {
            val listOfName: MutableList<String> = mutableListOf()
            Ticket::class.declaredMemberProperties.toMutableList().forEach {
                listOfName.add(it.name)
            }
            listOfName.remove("coordinates")
            listOfName.remove("event")
            Coordinates::class.declaredMemberProperties.toMutableList().forEach {
                listOfName.add("coordinates." + it.name)
            }
            Event::class.declaredMemberProperties.toMutableList().forEach {
                listOfName.add("event." + it.name)
            }
            return listOfName
        }
    }


}