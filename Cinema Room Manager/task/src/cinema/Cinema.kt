/*
 * JetBrains Academy exercise for Kotlin for "Kotlin Basics" track,
 * track URL: https://hyperskill.org/tracks/18
 *
 * Submission (c) 2023 by Jamie Cashin
 *   eMail:  jkcashin@jamiecashin.com
 *   My info:  https://about.me/jamiecashin
 */
package cinema

import java.lang.Exception

// Set PRODUCTION false to enable extra features intended for testing.
// Note that when PRODUCTION is false, the code will fail the hyperskill.org
// exercise checker.
const val PRODUCTION = false // Set false for testing, true for production

const val SEAT_PRICE_DISCOUNT = 8 // cheap seat in a "large" theatre
const val SEAT_PRICE_REGULAR = 10
const val LARGE_THEATRE = 60 // min # seats in a large theatre
const val SEAT_AVAILABLE = 'S'
const val SEAT_BOOKED = 'B'
const val MAXROWS = 9
const val MAXSEATS = 9

/**
 * Given a prompt string, presents the prompt (formatted) and gets the
 * user's response, returning it as an Int.
 *
 * @param prompt the prompt to reformat and present to the operator
 *
 * @return the value provided by the operator
 */
fun getInt(prompt: String): Int {
    print("$prompt:\n> ")
    return readln().toInt()
}

/**
 * This is the Cinema class
 *
 * @param rows the number of rows in our Cinema
 * @param seats the number of seats per row in our Cinema
 *
 * @constructor Creates a Cinema object
 *
 * @throws Exception for out-of-range rows, seats
 */
class Cinema(rows: Int, seats: Int) {
    // Constants ---------------------------------------------------------------
    private val myNumRows: Int
    private val myNumSeats: Int

    init {
        when {
            rows !in 1..MAXROWS -> throw Exception("Cinema() rows value $rows invalid, must be between 1 and $MAXROWS")
            seats !in 1..MAXSEATS -> throw Exception("Cinema() seats value $seats invalid, must be between 1 and $MAXSEATS")
        }
        myNumRows = rows
        myNumSeats = seats
    }

    private val myCapacity = rows * seats
    private val myTotalIncome = (1..rows).sumOf { seats * getPrice(it) }

    // Variables ---------------------------------------------------------------
    private val myBookingTable = MutableList(rows) {
        MutableList(seats) { SEAT_AVAILABLE }
    }
    private var myTicketsPurchased = 0
    private var myCurrentIncome = 0

    /**
     * Print existing availability.
     * - First line is header "Cinema:".
     * - Second line is seat header row showing seat number, space separated
     *   and aligned with columns below.
     * - Subsequent lines repeat once per row, with first column showing row
     *   number, followed by a set of space separated indicators of availability
     *   for each seat in the row. Indicator SEAT_AVAILABLE ('S' in our example)
     *   for available seat, SEAT_BOOKED ('B' in our example) for a booked seat.
     * Here's sample output for 3 rows of 4 seats, with seat 3, row 1 reserved:
     * Cinema:
     *   1 2 3 4
     * 1 S S S S
     * 2 S S S S
     * 3 S S S S
     *
     * @return Unit
     */
    fun printTable() {
        println(  "\nCinema:"
                + "\n  ${(1..myNumSeats).joinToString(" ") { "$it" }}")
        for (i in 0 until myBookingTable.size) {
        	println("${i + 1} ${myBookingTable[i].joinToString(" ")}")
        }
    }

    /**
     * Gets the ticket price for the given row.
     *
     * Tickets are SEAT_PRICE_REGULAR, with a discounted price of
     * SEAT_PRICE_DISCOUNT for seats in the back half (rounded up)
     * of a cinema with seat count of at least LARGE_THEATRE
     *
     * @return the price for the requested seat
     */
    private fun getPrice(row: Int): Int = when {
        myCapacity < LARGE_THEATRE || row <= myNumRows / 2 -> SEAT_PRICE_REGULAR
        else -> SEAT_PRICE_DISCOUNT
    }

    /**
     * Repeatedly prompts the user for a row and seat to book, and once they
     * have selected a free seat, books it, and prints the price.
     *
     * @return Unit
     */
    fun purchaseSeat() {
        // Repeat until a free seat is booked
        while (myTicketsPurchased < myCapacity) {
            println()
            val row = getInt("Enter a row number")
            val seat = getInt("Enter a seat number in that row")

            when {
                row !in 1..myNumRows || seat !in 1..myNumSeats -> println("Wrong input!")
                myBookingTable[row - 1][seat - 1] == SEAT_BOOKED -> println("That ticket has already been purchased!")
                else -> {
                    myBookingTable[row - 1][seat - 1] = SEAT_BOOKED
                    val price = getPrice(row)
                    myCurrentIncome += price
                    myTicketsPurchased++

                    println("\nTicket price: \$$price")
                    break
                }
            }
        }
    }

    /**
     * Prints statistics for Cinema.
     *
     * @return Unit
     */
    fun printStatistics() = println(
          "\n"
        + "Number of purchased tickets: $myTicketsPurchased\n"
        + "Percentage: ${ "%.2f".format(myTicketsPurchased.toFloat() / myCapacity.toFloat() * 100) }%\n"
        + "Current income: \$$myCurrentIncome\n"
        + "Total income: \$$myTotalIncome")

    /**
     * Run code tests. May not be used in PRODUCTION mode.
     *
     * @return Unit
     * @throws Exception when PRODUCTION is false
     */
    fun test() {
        if (PRODUCTION) {
            throw Exception("PRODUCTION is true, fun test() should not be called for production code")
        }
        for (row in 1..myNumRows) {
            println("Row $row seats are \$${getPrice(row)} each")
        }
    }
}

fun main() {
    // Get number of rows and seats
    val rows  = getInt("Enter the number of rows")
    val seats = getInt("Enter the number of seats in each row")

    // Generate Cinema instance
    val myCinema = Cinema(rows, seats)

    while (true) {
        print( "\n"
             + "1. Show the seats\n"
             + "2. Buy a ticket\n"
             + "3. Statistics\n"
             + if (!PRODUCTION) { "9. Test\n" } else { "" }
             + "0. Exit\n"
             + "> ")

        try {
            when (readln().toInt()) {
                1 -> myCinema.printTable()
                2 -> myCinema.purchaseSeat()
                3 -> myCinema.printStatistics()
                9 -> if (!PRODUCTION) { myCinema.test() }
                0 -> break
            }
        } catch (e: NumberFormatException) {
            println("\nInvalid number format - NumberFormatException: ${e.message}")
        }
    }
}
