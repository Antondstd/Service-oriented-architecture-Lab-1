package ru.itmo.SOA_Backend_Lab1.Model

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull

@Entity
data class Coordinates(
    @NotNull
    private var x:Int = 0,
    @Min(-571)
    private var y:Long = 0
) {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private var id:Long = 0
}