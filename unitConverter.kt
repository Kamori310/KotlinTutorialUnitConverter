package converter


fun main() {
    while (true) {
        println("Enter what you want to convert (or exit): ")
        val inputString = readln()

        if (inputString == "exit") {
            break
        }

        val delimiter = if ("in" in inputString.split(' ')) " in" else " to"

        val startingValue: Double
        val startingUnit: String
        val targetUnit: String

        try {
            startingValue = getStartingValueFromInputString(inputString, delimiter)
            startingUnit = getStartingUnitFromInputString(inputString, delimiter)
            targetUnit = getTartgetUnitFromInputString(inputString, delimiter)
        } catch (e: NumberFormatException) {
            println("Parse error")
            continue
        }

        val startingUnitUnit = Unit.findUnit(startingUnit)
        val targetUnitUnit = Unit.findUnit(targetUnit)

        if (isNotSameUnitType(startingUnitUnit, targetUnitUnit) || isNoUnit(startingUnitUnit)) {
            println("Conversion from ${startingUnitUnit.plural} to ${targetUnitUnit.plural} is impossible")
            continue
        }

        val targetValue: Double = if (isMassOrLength(startingUnitUnit)) {
            if (startingValue < 0) {
                println("${startingUnitUnit.type} shouldn't be negative.")
                continue
            }
            convertUnit(startingValue, startingUnitUnit, targetUnitUnit)
        } else {
            convertTemperature(startingUnitUnit, targetUnitUnit, startingValue)
        }

        print("$startingValue ")
        if (startingValue == 1.0) print(startingUnitUnit.singular) else print(startingUnitUnit.plural)
        print(" is $targetValue ")
        if (targetValue == 1.0) println(targetUnitUnit.singular) else println(targetUnitUnit.plural)
    }
}

private fun getTartgetUnitFromInputString(inputString: String, delimiter: String) =
    inputString.substringAfter(delimiter).lowercase().trim()

private fun getStartingUnitFromInputString(inputString: String, delimiter: String) =
    inputString.substringBefore(delimiter).substringAfter(' ').lowercase().trim()

private fun getStartingValueFromInputString(inputString: String, delimiter: String) =
    inputString.substringBefore(delimiter).substringBefore(' ').toDouble()

private fun isNoUnit(startingUnitUnit: Unit) = startingUnitUnit.type == "Other"

private fun isNotSameUnitType(startingUnitUnit: Unit, targetUnitUnit: Unit) =
    startingUnitUnit.type != targetUnitUnit.type

private fun isMassOrLength(startingUnitUnit: Unit) =
    (startingUnitUnit.type == "Weight" || startingUnitUnit.type == "Length")

private fun convertUnit(
    startingValue: Double,
    startingUnitUnit: Unit,
    targetUnitUnit: Unit
) = startingValue * startingUnitUnit.conversion / targetUnitUnit.conversion

private fun convertTemperature(startingUnitUnit: Unit, targetUnitUnit: Unit, startingValue: Double): Double {
    val valueInKelvin: Double = when (startingUnitUnit.name) {
        "CELSIUS" -> convertCelsiusToKelvin(startingValue)
        "FAHRENHEIT" -> convertFahrenheitToKelvin(startingValue)
        else -> startingValue
    }

    return when (targetUnitUnit.name) {
        "CELSIUS" -> convertKelvinToCelsius(valueInKelvin)
        "FAHRENHEIT" -> convertKelvinToFahrenheit(valueInKelvin)
        else -> valueInKelvin
    }
}

private fun convertKelvinToFahrenheit(value: Double) = value * 9.0 / 5.0 - 459.67

private fun convertKelvinToCelsius(value: Double) = value - 273.15

private fun convertFahrenheitToKelvin(startingValue: Double) = (startingValue + 459.67) * 5.0 / 9.0

private fun convertCelsiusToKelvin(startingValue: Double) = startingValue + 273.15

enum class Unit(
    val type: String, val abrev: String, val singular: String, val plural: String,
    val conversion: Double, val extra1: String = "???", val extra2: String = "???"
) {
    METER("Length", "m", "meter", "meters", 1.0),
    KILOMETER("Length", "km", "kilometer", "kilometers", 1000.0),
    CENTIMETER("Length", "cm", "centimeter", "centimeters", 0.01),
    MILLIMETER("Length", "mm", "millimeter", "millimeters", 0.001),
    MILES("Length", "mi", "mile", "miles", 1609.35),
    YARDS("Length", "yd", "yard", "yards", 0.9144),
    FEET("Length", "ft", "foot", "feet", 0.3048),
    INCHES("Length", "in", "inch", "inches", 0.0254),
    GRAM("Weight", "g", "gram", "grams", 1.0),
    KILOGRAM("Weight", "kg", "kilogram", "kilograms", 1000.0),
    MILLIGRAM("Weight", "mg", "milligram", "milligrams", 0.001),
    POUND("Weight", "lb", "pound", "pounds", 453.592),
    OUNCE("Weight", "oz", "ounce", "ounces", 28.3495),
    CELSIUS(
        "Temperature", "c", "degree celsius", "degrees celsius", -1.0,
        "dc", "celsius"
    ),
    FAHRENHEIT(
        "Temperature", "f", "degree fahrenheit", "degrees fahrenheit", -1.0,
        "df", "fahrenheit"
    ),
    KELVIN("Temperature", "k", "kelvin", "kelvins", -1.0),
    NULL("Other", "???", "???", "???", -1.0);

    companion object {
        fun findUnit(unit: String): Unit {
            for (enum in Unit.values()) {
                if (unit == enum.abrev || unit == enum.singular || unit == enum.plural ||
                    unit == enum.extra1 || unit == enum.extra2
                ) {
                    return enum
                }
            }
            return NULL
        }
    }
}
