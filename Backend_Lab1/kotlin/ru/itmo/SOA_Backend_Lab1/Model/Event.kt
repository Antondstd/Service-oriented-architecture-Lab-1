package ru.itmo.SOA_Backend_Lab1.Model

import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.validation.constraints.NotNull

@Entity
data class Event(
    @NotNull
    private var name:String = "",
    @NotNull
    private var date:LocalDateTime = LocalDateTime.now(),
    @NotNull
    private var minAge:Int = 0
//    private var ticketsCount:Long = 0,
//    private var eventType:EventType = EventType.BASEBALL
){
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private var id:Long =0
}