package day16

import java.io.File

val input = File("inputs/day16.txt").readLines()[0].map { hexChar ->
    val hexx = hexChar.digitToInt(16)
    hexx.toString(2).padStart(4, '0')
}.joinToString("")

// PID 4: Literal value (siingle number)
// 4=bit aligned, groups of four bits
// groups prefixed by a 1 except the last group, prefixed by a 0
//  = groups of 5 bits
// PID non-4: operator packet
// length type ID
// 0 -> 15 bit number represents a number
// total length of bits ofo the sub-packets in the packet
// 1 -> 11 bits sub-packets
data class Packet(val header: PacketHeader, val body: PacketBody) {
    fun versionSum(): Int {
        val thisSum = header.packetVersion
        return thisSum + body.versionSum()
    }

    override fun toString(): String {
        return "[$header($body)]"
    }
}

sealed class PacketBody {
    abstract fun eval(): Long
    abstract fun versionSum(): Int
}

data class LiteralPacketBody(val number: Long) : PacketBody() {
    override fun eval(): Long {
        return number
    }

    override fun versionSum(): Int {
        return 0
    }

    override fun toString(): String {
        return "num=$number"
    }
}

sealed class OperatorPacketBody(val packets: List<Packet>) : PacketBody() {
    companion object {
        fun from(typeId: Int, packets: List<Packet>): PacketBody {
            return when (typeId) {
                0 -> SumPacketBody(packets)
                1 -> ProductPacketBody(packets)
                2 -> MinimumPacketBody(packets)
                3 -> MaximumPacketBody(packets)
                5 -> GTPacketBody(packets)
                6 -> LTPacketBody(packets)
                7 -> EQPacketBody(packets)
                else -> error(typeId)
            }
        }
    }

    override fun versionSum(): Int {
        return packets.sumOf { it.versionSum() }
    }

    override fun toString(): String {
        return "OP($packets)"
    }
}

class SumPacketBody(packets: List<Packet>) : OperatorPacketBody(packets) {
    override fun eval(): Long {
        return packets.sumOf { it.body.eval() }
    }
}

class ProductPacketBody(packets: List<Packet>) : OperatorPacketBody(packets) {
    override fun eval(): Long {
        return packets.fold(1L) { acc, packet ->
            acc * packet.body.eval()
        }
    }
}

class MinimumPacketBody(packets: List<Packet>) : OperatorPacketBody(packets) {
    override fun eval(): Long {
        return packets.minOf { it.body.eval() }
    }
}

class MaximumPacketBody(packets: List<Packet>) : OperatorPacketBody(packets) {
    override fun eval(): Long {
        return packets.maxOf { it.body.eval() }
    }
}

class GTPacketBody(packets: List<Packet>) : OperatorPacketBody(packets) {
    override fun eval(): Long {
        val (first, second) = packets
        return if (first.body.eval() > second.body.eval()) 1 else 0
    }
}

class LTPacketBody(packets: List<Packet>) : OperatorPacketBody(packets) {
    override fun eval(): Long {
        val (first, second) = packets
        return if (first.body.eval() < second.body.eval()) 1 else 0
    }
}

class EQPacketBody(packets: List<Packet>) : OperatorPacketBody(packets) {
    override fun eval(): Long {
        val (first, second) = packets
        return if (first.body.eval() == second.body.eval()) 1 else 0
    }
}

data class PacketHeader(val packetVersion: Int, val typeId: Int) {
    override fun toString(): String {
        return "v=$packetVersion type=$typeId"
    }
}

fun main() {
    println(input)
    val packets = parsePacket(StringBuilder(input))
    println(packets.versionSum())
    println(packets.body.eval())
}

fun parsePacket(sb: StringBuilder): Packet {
    // peek at header to determine type
    val type = sb.drop(3).take(3).toString().toInt(2)
    val parsedPacket = when (type) {
        4 -> parseLiteralPacket(sb)
        else -> parseOperatorPacket(sb)
    }
    println(parsedPacket)
    println(sb.toString())
    return parsedPacket
}

fun parseLiteralPacket(sb: StringBuilder): Packet {
    val version = sb.takeDeleting(3)
    val type = sb.takeDeleting(3)
    val fiveBitGroups = mutableListOf<String>()
    println(sb)
    while (true) {
        if (!sb.startsWith("1")) break
        val oneGroup = sb.takeDeleting(5)
        fiveBitGroups.add(oneGroup)
    }
    check(sb.startsWith("0"))
    val zeroGroup = sb.takeDeleting(5)
    fiveBitGroups.add(zeroGroup)
    val number = fiveBitGroups.map { it.drop(1) }.joinToString("").toLong(2)
    //sb.deletePrefix(3) // remove trailing zeroes
    return Packet(PacketHeader(version.toInt(2), type.toInt(2)), LiteralPacketBody(number))
}

fun parseOperatorPacket(sb: StringBuilder): Packet {
    val version = sb.takeDeleting(3)

    val type = sb.takeDeleting(3)
    val lengthType = sb.takeDeleting(1).toInt(2)
    val subpackets = mutableListOf<Packet>()
    if (lengthType == 0) {
        val bitsInSubpackets = sb.takeDeleting(15).toInt(2)
        val remainingLen = sb.toString().length
        while (remainingLen - sb.length < bitsInSubpackets) {
            subpackets.add(parsePacket(sb))
        }
        // then the next 15 bits = the total length in bits of the sub-packets contained by this packet
    } else if (lengthType == 1) {
        // then the next 11 bits are a number that represents the number of sub-packets immediately contained by this packet.
        val subpacketNum = sb.takeDeleting(11).toInt(2)
        println("getting $subpacketNum subpackets")

        repeat(subpacketNum) {
            println("subpacket $it")
            subpackets.add(parsePacket(sb))
            // subpackets take care of deleting their own prefixes
        }
    }

    return Packet(PacketHeader(version.toInt(2), type.toInt(2)), OperatorPacketBody.from(type.toInt(2), subpackets))
}

fun StringBuilder.deletePrefix(len: Int) {
    this.delete(0, len)
}

fun StringBuilder.takeDeleting(len: Int): String {
    val res = this.take(len)
    this.deletePrefix(len)
    return res.toString()
}