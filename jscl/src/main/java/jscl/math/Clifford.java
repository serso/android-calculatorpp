package jscl.math;

/**
 * User: serso
 * Date: 12/26/11
 * Time: 9:45 AM
 */
class Clifford {
    int p, n;
    int operator[][];

    Clifford(int algebra[]) {
        this(algebra[0], algebra[1]);
    }

    Clifford(int p, int q) {
        this.p = p;
        n = p + q;
        int m = 1 << n;
        operator = new int[m][m];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < m; j++) {
                int a = combination(i, n);
                int b = combination(j, n);
                int c = a ^ b;
                int l = location(c, n);
                boolean s = sign(a, b);
                int k = l + 1;
                operator[i][j] = s ? -k : k;
            }
        }
    }

    static int combination(int l, int n) {
        if (n <= 2) return l;
        int b[] = new int[1];
        int l1 = decimation(l, n, b);
        int c = combination(l1, n - 1);
        return (c << 1) + b[0];
    }

    static int location(int c, int n) {
        if (n <= 2) return c;
        int c1 = c >> 1;
        int b = c & 1;
        int l1 = location(c1, n - 1);
        return dilatation(l1, n, new int[]{b});
    }

    static int decimation(int l, int n, int b[]) {
        int p = grade(l, n - 1, 1);
        int p1 = (p + 1) >> 1;
        b[0] = p & 1;
        return l - sum(p1, n - 1);
    }

    static int dilatation(int l, int n, int b[]) {
        int p1 = grade(l, n - 1);
        return l + sum(p1 + b[0], n - 1);
    }

    static int grade(int l, int n) {
        return grade(l, n, 0);
    }

    static int grade(int l, int n, int d) {
        int s = 0, p = 0;
        while (true) {
            s += binomial(n, p >> d);
            if (s <= l) p++;
            else break;
        }
        return p;
    }

    static int sum(int p, int n) {
        int q = 0, s = 0;
        while (q < p) s += binomial(n, q++);
        return s;
    }

    static int binomial(int n, int p) {
        int a = 1, b = 1;
        for (int i = n - p + 1; i <= n; i++) a *= i;
        for (int i = 2; i <= p; i++) b *= i;
        return a / b;
    }

    static int log2e(int n) {
        int i;
        for (i = 0; n > 1; n >>= 1) i++;
        return i;
    }

    boolean sign(int a, int b) {
        boolean s = false;
        for (int i = 0; i < n; i++) {
            if ((b & (1 << i)) > 0) {
                for (int j = i; j < n; j++) {
                    if ((a & (1 << j)) > 0 && (j > i || i >= p)) s = !s;
                }
            }
        }
        return s;
    }

    int[][] operator() {
        return operator;
    }
}
