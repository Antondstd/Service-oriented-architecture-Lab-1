package ru.itmo.SOA_Backend_Lab1.Filters

import javax.servlet.Filter

import javax.servlet.FilterChain

import javax.servlet.ServletResponse

import javax.servlet.ServletRequest


class RusFilter:Filter {

    override fun doFilter(request: ServletRequest, response: ServletResponse?, filterChain: FilterChain) {
        try {
            request.setCharacterEncoding("Cp1251")
            filterChain.doFilter(request, response)
        }
        catch (e: Exception){
            println("Error in filter: $e")
        }
    }
}