package com.adeshchandra.smartcalc;

import java.util.*;

public class UnitConverter {

    public static class Category {
        public final String name;
        public final String emoji;
        public final String[] units;
        public final double[] toBase; // multiply to get base unit value
        Category(String name, String emoji, String[] units, double[] toBase) {
            this.name = name; this.emoji = emoji; this.units = units; this.toBase = toBase;
        }
    }

    public static final Category[] CATEGORIES = {
        new Category("Length", "📏", new String[]{
            "Millimeter","Centimeter","Meter","Kilometer","Inch","Foot","Yard","Mile","Nautical Mile","Light Year"
        }, new double[]{
            0.001, 0.01, 1, 1000, 0.0254, 0.3048, 0.9144, 1609.344, 1852, 9.461e15
        }),
        new Category("Weight / Mass", "⚖️", new String[]{
            "Milligram","Gram","Kilogram","Metric Ton","Pound","Ounce","Stone","US Ton","Carat"
        }, new double[]{
            0.000001, 0.001, 1, 1000, 0.453592, 0.0283495, 6.35029, 907.185, 0.0002
        }),
        new Category("Temperature", "🌡️", new String[]{
            "Celsius","Fahrenheit","Kelvin","Rankine"
        }, new double[]{1,1,1,1}), // special handling
        new Category("Area", "📐", new String[]{
            "sq mm","sq cm","sq meter","sq km","sq inch","sq foot","sq yard","sq mile","Hectare","Acre"
        }, new double[]{
            0.000001, 0.0001, 1, 1e6, 0.00064516, 0.092903, 0.836127, 2.59e6, 10000, 4046.86
        }),
        new Category("Volume", "🧪", new String[]{
            "Milliliter","Liter","Cubic meter","Cubic inch","Cubic foot","US Gallon","UK Gallon","US Pint","US Cup","Tablespoon","Teaspoon","Fluid Oz"
        }, new double[]{
            0.001, 1, 1000, 0.0163871, 28.3168, 3.78541, 4.54609, 0.473176, 0.236588, 0.0147868, 0.00492892, 0.0295735
        }),
        new Category("Speed", "🚀", new String[]{
            "m/s","km/h","mph","ft/s","Knot","Mach"
        }, new double[]{
            1, 0.277778, 0.44704, 0.3048, 0.514444, 340.29
        }),
        new Category("Time", "⏱️", new String[]{
            "Millisecond","Second","Minute","Hour","Day","Week","Month","Year","Decade","Century"
        }, new double[]{
            0.001, 1, 60, 3600, 86400, 604800, 2.628e6, 3.156e7, 3.156e8, 3.156e9
        }),
        new Category("Digital Storage", "💾", new String[]{
            "Bit","Byte","Kilobyte","Megabyte","Gigabyte","Terabyte","Petabyte"
        }, new double[]{
            0.125, 1, 1024, 1048576, 1073741824, 1.0995e12, 1.1259e15
        }),
        new Category("Pressure", "🌬️", new String[]{
            "Pascal","KiloPascal","MegaPascal","Bar","Atmosphere","PSI","Torr","mmHg"
        }, new double[]{
            1, 1000, 1e6, 100000, 101325, 6894.76, 133.322, 133.322
        }),
        new Category("Energy", "⚡", new String[]{
            "Joule","KiloJoule","MegaJoule","Calorie","Kilocalorie","Wh","kWh","BTU","eV","Erg"
        }, new double[]{
            1, 1000, 1e6, 4.184, 4184, 3600, 3.6e6, 1055.06, 1.602e-19, 1e-7
        }),
        new Category("Power", "🔋", new String[]{
            "Watt","Kilowatt","Megawatt","Horsepower","BTU/hr","Cal/sec","ft·lb/min"
        }, new double[]{
            1, 1000, 1e6, 745.7, 0.29307, 4.184, 0.02260
        }),
        new Category("Angle", "📐", new String[]{
            "Degree","Radian","Gradian","Arcminute","Arcsecond","Revolution"
        }, new double[]{
            Math.PI/180, 1, Math.PI/200, Math.PI/10800, Math.PI/648000, 2*Math.PI
        }),
        new Category("Fuel Economy", "⛽", new String[]{
            "km/L","L/100km","MPG (US)","MPG (UK)","Miles/L"
        }, new double[]{1,1,1,1,1}), // special handling
        new Category("Currency (approx)", "💰", new String[]{
            "USD","EUR","GBP","INR","JPY","CNY","AUD","CAD","CHF","SGD","AED","SAR"
        }, new double[]{
            1, 1.08, 1.27, 0.012, 0.0067, 0.138, 0.65, 0.73, 1.13, 0.74, 0.272, 0.267
        }),
    };

    public static String convert(int catIdx, double value, int fromIdx, int toIdx) {
        Category cat = CATEGORIES[catIdx];

        if (cat.name.equals("Temperature")) {
            return convertTemp(value, fromIdx, toIdx);
        }
        if (cat.name.equals("Fuel Economy")) {
            return convertFuel(value, fromIdx, toIdx);
        }

        double inBase = value * cat.toBase[fromIdx];
        double result = inBase / cat.toBase[toIdx];

        if (result == Math.floor(result) && Math.abs(result) < 1e12)
            return String.valueOf((long) result);

        return String.format("%.8f", result).replaceAll("0+$","").replaceAll("\\.$","");
    }

    private static String convertTemp(double v, int from, int to) {
        // to Celsius first
        double c;
        switch (from) {
            case 0: c = v; break;
            case 1: c = (v - 32) * 5.0/9; break;
            case 2: c = v - 273.15; break;
            case 3: c = (v - 491.67) * 5.0/9; break;
            default: c = v;
        }
        double result;
        switch (to) {
            case 0: result = c; break;
            case 1: result = c * 9.0/5 + 32; break;
            case 2: result = c + 273.15; break;
            case 3: result = (c + 273.15) * 9.0/5; break;
            default: result = c;
        }
        return String.format("%.4f", result).replaceAll("0+$","").replaceAll("\\.$","");
    }

    private static String convertFuel(double v, int from, int to) {
        // convert everything to km/L first
        double kmL;
        switch (from) {
            case 0: kmL = v; break;                     // km/L
            case 1: kmL = 100.0 / v; break;             // L/100km
            case 2: kmL = v * 0.425144; break;          // MPG US
            case 3: kmL = v * 0.354006; break;          // MPG UK
            case 4: kmL = v * 1.60934; break;           // Miles/L
            default: kmL = v;
        }
        double result;
        switch (to) {
            case 0: result = kmL; break;
            case 1: result = 100.0 / kmL; break;
            case 2: result = kmL / 0.425144; break;
            case 3: result = kmL / 0.354006; break;
            case 4: result = kmL / 1.60934; break;
            default: result = kmL;
        }
        return String.format("%.4f", result).replaceAll("0+$","").replaceAll("\\.$","");
    }
}
