package com.adeshchandra.smartcalc;

public class CalcEngine {

    public static String evaluate(String expr) {
        try {
            expr = expr.trim()
                .replace("×", "*")
                .replace("÷", "/")
                .replace("−", "-")
                .replace("π", String.valueOf(Math.PI))
                .replace("ℯ", String.valueOf(Math.E));

            double result = parseExpr(expr.toCharArray(), new int[]{0});
            if (Double.isNaN(result))      return "Error";
            if (Double.isInfinite(result)) return result > 0 ? "∞" : "-∞";

            if (result == Math.floor(result) && Math.abs(result) < 1e15)
                return String.valueOf((long) result);

            return String.format("%.10f", result)
                .replaceAll("0+$", "")
                .replaceAll("\\.$", "");
        } catch (Exception e) {
            return "Error";
        }
    }

    private static double parseExpr(char[] c, int[] p) {
        double v = parseTerm(c, p);
        while (p[0] < c.length) {
            char op = c[p[0]];
            if      (op == '+') { p[0]++; v += parseTerm(c, p); }
            else if (op == '-') { p[0]++; v -= parseTerm(c, p); }
            else break;
        }
        return v;
    }

    private static double parseTerm(char[] c, int[] p) {
        double v = parsePow(c, p);
        while (p[0] < c.length) {
            char op = c[p[0]];
            if      (op == '*') { p[0]++; v *= parsePow(c, p); }
            else if (op == '/') { p[0]++; double d = parsePow(c, p); v = d == 0 ? Double.NaN : v / d; }
            else if (op == '%') { p[0]++; v = v % parsePow(c, p); }
            else break;
        }
        return v;
    }

    private static double parsePow(char[] c, int[] p) {
        double b = parseUnary(c, p);
        skip(c, p);
        if (p[0] < c.length && c[p[0]] == '^') { p[0]++; return Math.pow(b, parsePow(c, p)); }
        return b;
    }

    private static double parseUnary(char[] c, int[] p) {
        skip(c, p);
        if (p[0] < c.length && c[p[0]] == '-') { p[0]++; return -parsePrimary(c, p); }
        if (p[0] < c.length && c[p[0]] == '+') { p[0]++; }
        return parsePrimary(c, p);
    }

    private static double parsePrimary(char[] c, int[] p) {
        skip(c, p);
        if (p[0] >= c.length) return 0;

        String r = new String(c, p[0], c.length - p[0]);

        // Math functions
        if (r.startsWith("sinh("))  { p[0]+=5; double v=parseExpr(c,p); eat(c,p); return Math.sinh(v); }
        if (r.startsWith("cosh("))  { p[0]+=5; double v=parseExpr(c,p); eat(c,p); return Math.cosh(v); }
        if (r.startsWith("tanh("))  { p[0]+=5; double v=parseExpr(c,p); eat(c,p); return Math.tanh(v); }
        if (r.startsWith("asin("))  { p[0]+=5; double v=parseExpr(c,p); eat(c,p); return Math.toDegrees(Math.asin(v)); }
        if (r.startsWith("acos("))  { p[0]+=5; double v=parseExpr(c,p); eat(c,p); return Math.toDegrees(Math.acos(v)); }
        if (r.startsWith("atan("))  { p[0]+=5; double v=parseExpr(c,p); eat(c,p); return Math.toDegrees(Math.atan(v)); }
        if (r.startsWith("sin("))   { p[0]+=4; double v=parseExpr(c,p); eat(c,p); return Math.sin(Math.toRadians(v)); }
        if (r.startsWith("cos("))   { p[0]+=4; double v=parseExpr(c,p); eat(c,p); return Math.cos(Math.toRadians(v)); }
        if (r.startsWith("tan("))   { p[0]+=4; double v=parseExpr(c,p); eat(c,p); return Math.tan(Math.toRadians(v)); }
        if (r.startsWith("sqrt("))  { p[0]+=5; double v=parseExpr(c,p); eat(c,p); return Math.sqrt(v); }
        if (r.startsWith("cbrt("))  { p[0]+=5; double v=parseExpr(c,p); eat(c,p); return Math.cbrt(v); }
        if (r.startsWith("log2("))  { p[0]+=5; double v=parseExpr(c,p); eat(c,p); return Math.log(v)/Math.log(2); }
        if (r.startsWith("log("))   { p[0]+=4; double v=parseExpr(c,p); eat(c,p); return Math.log10(v); }
        if (r.startsWith("ln("))    { p[0]+=3; double v=parseExpr(c,p); eat(c,p); return Math.log(v); }
        if (r.startsWith("abs("))   { p[0]+=4; double v=parseExpr(c,p); eat(c,p); return Math.abs(v); }
        if (r.startsWith("ceil("))  { p[0]+=5; double v=parseExpr(c,p); eat(c,p); return Math.ceil(v); }
        if (r.startsWith("floor(")) { p[0]+=6; double v=parseExpr(c,p); eat(c,p); return Math.floor(v); }
        if (r.startsWith("fact("))  { p[0]+=5; double v=parseExpr(c,p); eat(c,p); return factorial((int)v); }
        if (r.startsWith("exp("))   { p[0]+=4; double v=parseExpr(c,p); eat(c,p); return Math.exp(v); }
        if (r.startsWith("deg("))   { p[0]+=4; double v=parseExpr(c,p); eat(c,p); return Math.toDegrees(v); }
        if (r.startsWith("rad("))   { p[0]+=4; double v=parseExpr(c,p); eat(c,p); return Math.toRadians(v); }

        if (c[p[0]] == '(') {
            p[0]++;
            double v = parseExpr(c, p);
            eat(c, p);
            return v;
        }

        int s = p[0];
        while (p[0] < c.length && (Character.isDigit(c[p[0]]) || c[p[0]] == '.')) p[0]++;
        if (p[0] == s) throw new RuntimeException("Unexpected char: " + c[p[0]]);
        return Double.parseDouble(new String(c, s, p[0]-s));
    }

    private static void eat(char[] c, int[] p) {
        skip(c, p);
        if (p[0] < c.length && c[p[0]] == ')') p[0]++;
    }

    private static void skip(char[] c, int[] p) {
        while (p[0] < c.length && c[p[0]] == ' ') p[0]++;
    }

    private static double factorial(int n) {
        if (n < 0) return Double.NaN;
        if (n > 20) return Double.POSITIVE_INFINITY;
        double r = 1;
        for (int i = 2; i <= n; i++) r *= i;
        return r;
    }
}
