


package midpcalc;



/**
 * <b>Java integer implementation of 63-bit precision floating point.</b>
 * <br><i>Version 1.13</i>
 *
 * <p>Copyright 2003-2009 Roar Lauritzsen <roarl@pvv.org>
 *
 * <blockquote>
 *
 * <p>This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option)
 * any later version.
 *
 * <p>This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * for more details.
 *
 * <p>The following link provides a copy of the GNU General Public License:
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;<a
 * href="http://www.gnu.org/licenses/gpl.txt">http://www.gnu.org/licenses/gpl.txt</a>
 * <br>If you are unable to obtain the copy from this address, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 *
 * </blockquote>
 *
 * <p><b>General notes</b>
 * <ul>
 *
 *   <li><code>Real</code> objects are not immutable, like Java
 *   <code>Double</code> or <code>BigDecimal</code>. This means that you
 *   should not think of a <code>Real</code> object as a "number", but rather
 *   as a "register holding a number". This design choice is done to encourage
 *   object reuse and limit garbage production for more efficient execution on
 *   e.g. a limited MIDP device. The design choice is reflected in the API,
 *   where an operation like {@link #add(Real) add} does not return a new
 *   object containing the result (as with {@link
 *   java.math.BigDecimal#add(java.math.BigDecimal) BigDecimal}), but rather
 *   adds the argument to the object itself, and returns nothing.
 *
 *   <li>This library implements infinities and NaN (Not-a-Number) following
 *   the IEEE 754 logic. If an operation produces a result larger (in
 *   magnitude) than the largest representable number, a value representing
 *   positive or negative infinity is generated. If an operation produces a
 *   result smaller than the smallest representable number, a positive or
 *   negative zero is generated. If an operation is undefined, a NaN value is
 *   produced. Abnormal numbers are often fine to use in further
 *   calculations. In most cases where the final result would be meaningful,
 *   abnormal numbers accomplish this, e.g. atan(1/0)=&pi;/2. In most cases
 *   where the final result is not meaningful, a NaN will be produced.
 *   <i>No exception is ever (deliberately) thrown.</i>
 *
 *   <li>Error bounds listed under <a href="#method_detail">Method Detail</a>
 *   are calculated using William Rossi's <a
 *   href="http://dfp.sourceforge.net/">rossi.dfp.dfp</a> at 40 decimal digits
 *   accuracy. Error bounds are for "typical arguments" and may increase when
 *   results approach zero or 
 *   infinity. The abbreviation {@link Math#ulp(double) ULP} means Unit in the
 *   Last Place. An error bound of ½ ULP means that the result is correctly
 *   rounded. The relative execution time listed under each method is the
 *   average from running on SonyEricsson T610 (R3C), K700i, and Nokia 6230i.
 *
 *   <li>The library is not thread-safe. Static <code>Real</code> objects are
 *   used extensively as temporary values to avoid garbage production and the
 *   overhead of <code>new</code>. To make the library thread-safe, references
 *   to all these static objects must be replaced with code that instead
 *   allocates new <code>Real</code> objects in their place.
 *
 *   <li>There is one bug that occurs again and again and is really difficult
 *   to debug. Although the pre-calculated constants are declared <code>static
 *   final</code>, Java cannot really protect the contents of the objects in
 *   the same way as <code>const</code>s are protected in C/C++. Consequently,
 *   you can accidentally change these values if you send them into a function
 *   that modifies its arguments. If you were to modify {@link #ONE Real.ONE}
 *   for instance, many of the succeeding calculations would be wrong because
 *   the same variable is used extensively in the internal calculations of
 *   Real.java.
 *
 * </ul>
 */
public final class Real
{
    /**
     * The mantissa of a <code>Real</code>. <i>To maintain numbers in a
     * normalized state and to preserve the integrity of abnormal numbers, it
     * is discouraged to modify the inner representation of a
     * <code>Real</code> directly.</i>
     *
     * <p>The number represented by a <code>Real</code> equals:<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;-1<sup>sign</sup>&nbsp;·&nbsp;mantissa&nbsp;·&nbsp;2<sup>-62</sup>&nbsp;·&nbsp;2<sup>exponent-0x40000000</sup>
     *
     * <p>The normalized mantissa of a finite <code>Real</code> must be
     * between <code>0x4000000000000000L</code> and
     * <code>0x7fffffffffffffffL</code>. Using a denormalized
     * <code>Real</code> in <u>any</u> operation other than {@link
     * #normalize()} may produce undefined results. The mantissa of zero and
     * of an infinite value is <code>0x0000000000000000L</code>.
     *
     * <p>The mantissa of a NaN is any nonzero value. However, it is
     * recommended to use the value <code>0x4000000000000000L</code>. Any
     * other values are reserved for future extensions.
     */
    public long mantissa;
    /**
     * The exponent of a <code>Real</code>. <i>To maintain numbers in a
     * normalized state and to preserve the integrity of abnormal numbers, it
     * is discouraged to modify the inner representation of a
     * <code>Real</code> directly.</i>
     *
     * <p>The exponent of a finite <code>Real</code> must be between
     * <code>0x00000000</code> and <code>0x7fffffff</code>. The exponent of
     * zero <code>0x00000000</code>.
     *
     * <p>The exponent of an infinite value and of a NaN is any negative
     * value. However, it is recommended to use the value
     * <code>0x80000000</code>. Any other values are reserved for future
     * extensions.
     */
    public int exponent;
    /**
     * The sign of a <code>Real</code>. <i>To maintain numbers in a normalized
     * state and to preserve the integrity of abnormal numbers, it is
     * discouraged to modify the inner representation of a <code>Real</code>
     * directly.</i>
     *
     * <p>The sign of a finite, zero or infinite <code>Real</code> is 0 for
     * positive values and 1 for negative values. Any other values may produce
     * undefined results.
     *
     * <p>The sign of a NaN is ignored. However, it is recommended to use the
     * value <code>0</code>. Any other values are reserved for future
     * extensions.
     */
    public byte sign;
    /**
     * Set to <code>false</code> during numerical algorithms to favor accuracy
     * over prettyness. This flag is initially set to <code>true</code>.
     *
     * <p>The flag controls the operation of a subtraction of two
     * almost-identical numbers that differ only in the last three bits of the
     * mantissa. With this flag enabled, the result of such a subtraction is
     * rounded down to zero. Probabilistically, this is the correct course of
     * action in an overwhelmingly large percentage of calculations.
     * However, certain numerical algorithms such as differentiation depend
     * on keeping maximum accuracy during subtraction.
     *
     * <p>Note, that because of <code>magicRounding</code>,
     * <code>a.sub(b)</code> may produce zero even though
     * <code>a.equalTo(b)</code> returns <code>false</code>. This must be
     * considered e.g. when trying to avoid division by zero.
     */
    public static boolean magicRounding = true;
    /**
     * A <code>Real</code> constant holding the exact value of 0.  Among other
     * uses, this value is used as a result when a positive underflow occurs.
     */
    public static final Real ZERO = new Real(0,0x00000000,0x0000000000000000L);
    /**
     * A <code>Real</code> constant holding the exact value of 1.
     */
    public static final Real ONE = new Real(0,0x40000000,0x4000000000000000L);
    /**
     * A <code>Real</code> constant holding the exact value of 2.
     */
    public static final Real TWO = new Real(0,0x40000001,0x4000000000000000L);
    /**
     * A <code>Real</code> constant holding the exact value of 3.
     */
    public static final Real THREE= new Real(0,0x40000001,0x6000000000000000L);
    /**
     * A <code>Real</code> constant holding the exact value of 5.
     */
    public static final Real FIVE = new Real(0,0x40000002,0x5000000000000000L);
    /**
     * A <code>Real</code> constant holding the exact value of 10.
     */
    public static final Real TEN = new Real(0,0x40000003,0x5000000000000000L);
    /**
     * A <code>Real</code> constant holding the exact value of 100.
     */
    public static final Real HUNDRED=new Real(0,0x40000006,0x6400000000000000L);
    /**
     * A <code>Real</code> constant holding the exact value of 1/2.
     */
    public static final Real HALF = new Real(0,0x3fffffff,0x4000000000000000L);
    /**
     * A <code>Real</code> constant that is closer than any other to 1/3.
     */
    public static final Real THIRD= new Real(0,0x3ffffffe,0x5555555555555555L);
    /**
     * A <code>Real</code> constant that is closer than any other to 1/10.
     */
    public static final Real TENTH= new Real(0,0x3ffffffc,0x6666666666666666L);
    /**
     * A <code>Real</code> constant that is closer than any other to 1/100.
     */
    public static final Real PERCENT=new Real(0,0x3ffffff9,0x51eb851eb851eb85L);
    /**
     * A <code>Real</code> constant that is closer than any other to the
     * square root of 2.
     */
    public static final Real SQRT2= new Real(0,0x40000000,0x5a827999fcef3242L);
    /**
     * A <code>Real</code> constant that is closer than any other to the
     * square root of 1/2.
     */
    public static final Real SQRT1_2=new Real(0,0x3fffffff,0x5a827999fcef3242L);
    /**
     * A <code>Real</code> constant that is closer than any other to 2&pi;.
     */
    public static final Real PI2 = new Real(0,0x40000002,0x6487ed5110b4611aL);
    /**
     * A <code>Real</code> constant that is closer than any other to &pi;, the
     * ratio of the circumference of a circle to its diameter.
     */
    public static final Real PI = new Real(0,0x40000001,0x6487ed5110b4611aL);
    /**
     * A <code>Real</code> constant that is closer than any other to &pi;/2.
     */
    public static final Real PI_2 = new Real(0,0x40000000,0x6487ed5110b4611aL);
    /**
     * A <code>Real</code> constant that is closer than any other to &pi;/4.
     */
    public static final Real PI_4 = new Real(0,0x3fffffff,0x6487ed5110b4611aL);
    /**
     * A <code>Real</code> constant that is closer than any other to &pi;/8.
     */
    public static final Real PI_8 = new Real(0,0x3ffffffe,0x6487ed5110b4611aL);
    /**
     * A <code>Real</code> constant that is closer than any other to <i>e</i>,
     * the base of the natural logarithms.
     */
    public static final Real E = new Real(0,0x40000001,0x56fc2a2c515da54dL);
    /**
     * A <code>Real</code> constant that is closer than any other to the
     * natural logarithm of 2.
     */
    public static final Real LN2 = new Real(0,0x3fffffff,0x58b90bfbe8e7bcd6L);
    /**
     * A <code>Real</code> constant that is closer than any other to the
     * natural logarithm of 10.
     */
    public static final Real LN10 = new Real(0,0x40000001,0x49aec6eed554560bL);
    /**
     * A <code>Real</code> constant that is closer than any other to the
     * base-2 logarithm of <i>e</i>.
     */
    public static final Real LOG2E= new Real(0,0x40000000,0x5c551d94ae0bf85eL);
    /**
     * A <code>Real</code> constant that is closer than any other to the
     * base-10 logarithm of <i>e</i>.
     */
    public static final Real LOG10E=new Real(0,0x3ffffffe,0x6f2dec549b9438cbL);
    /**
     * A <code>Real</code> constant holding the maximum non-infinite positive
     * number = 4.197e323228496.
     */
    public static final Real MAX = new Real(0,0x7fffffff,0x7fffffffffffffffL);
    /**
     * A <code>Real</code> constant holding the minimum non-zero positive
     * number = 2.383e-323228497.
     */
    public static final Real MIN = new Real(0,0x00000000,0x4000000000000000L);
    /**
     * A <code>Real</code> constant holding the value of NaN (not-a-number).
     * This value is always used as a result to signal an invalid operation.
     */
    public static final Real NAN = new Real(0,0x80000000,0x4000000000000000L);
    /**
     * A <code>Real</code> constant holding the value of positive infinity.
     * This value is always used as a result to signal a positive overflow.
     */
    public static final Real INF = new Real(0,0x80000000,0x0000000000000000L);
    /**
     * A <code>Real</code> constant holding the value of negative infinity.
     * This value is always used as a result to signal a negative overflow.
     */
    public static final Real INF_N= new Real(1,0x80000000,0x0000000000000000L);
    /**
     * A <code>Real</code> constant holding the value of negative zero. This
     * value is used as a result e.g. when a negative underflow occurs.
     */
    public static final Real ZERO_N=new Real(1,0x00000000,0x0000000000000000L);
    /**
     * A <code>Real</code> constant holding the exact value of -1.
     */
    public static final Real ONE_N= new Real(1,0x40000000,0x4000000000000000L);
    private static final int clz_magic = 0x7c4acdd;
    private static final byte[] clz_tab =
    { 31,22,30,21,18,10,29, 2,20,17,15,13, 9, 6,28, 1,
      23,19,11, 3,16,14, 7,24,12, 4, 8,25, 5,26,27, 0 };
    /**
     * Creates a new <code>Real</code> with a value of zero.
     */
    public Real() {
    }
    /**
     * Creates a new <code>Real</code>, assigning the value of another
     * <code>Real</code>. See {@link #assign(Real)}.
     *
     * @param a the <code>Real</code> to assign.
     */
    public Real(Real a) {
        { this.mantissa = a.mantissa; this.exponent = a.exponent; this.sign = a.sign; };
    }
    /**
     * Creates a new <code>Real</code>, assigning the value of an integer. See
     * {@link #assign(int)}.
     *
     * @param a the <code>int</code> to assign.
     */
    public Real(int a) {
        assign(a);
    }
    /**
     * Creates a new <code>Real</code>, assigning the value of a long
     * integer. See {@link #assign(long)}.
     *
     * @param a the <code>long</code> to assign.
     */
    public Real(long a) {
        assign(a);
    }
    /**
     * Creates a new <code>Real</code>, assigning the value encoded in a
     * <code>String</code> using base-10. See {@link #assign(String)}.
     *
     * @param a the <code>String</code> to assign.
     */
    public Real(String a) {
        assign(a,10);
    }
    /**
     * Creates a new <code>Real</code>, assigning the value encoded in a
     * <code>String</code> using the specified number base. See {@link
     * #assign(String,int)}.
     *
     * @param a the <code>String</code> to assign.
     * @param base the number base of <code>a</code>. Valid base values are 2,
     *     8, 10 and 16.
     */
    public Real(String a, int base) {
        assign(a,base);
    }
    /**
     * Creates a new <code>Real</code>, assigning a value by directly setting
     * the fields of the internal representation. The arguments must represent
     * a valid, normalized <code>Real</code>. This is the fastest way of
     * creating a constant value.  See {@link #assign(int,int,long)}.
     *
     * @param s {@link #sign} bit, 0 for positive sign, 1 for negative sign
     * @param e {@link #exponent}
     * @param m {@link #mantissa}
     */
    public Real(int s, int e, long m) {
        { this.sign=(byte)s; this.exponent=e; this.mantissa=m; };
    }
    /**
     * Creates a new <code>Real</code>, assigning the value previously encoded
     * into twelve consecutive bytes in a byte array using {@link
     * #toBytes(byte[],int) toBytes}. See {@link #assign(byte[],int)}.
     *
     * @param data byte array to decode into this <code>Real</code>.
     * @param offset offset to start encoding from. The bytes
     *     <code>data[offset]...data[offset+11]</code> will be
     *     read.
     */
    public Real(byte [] data, int offset) {
        assign(data,offset);
    }
    /**
     * Assigns this <code>Real</code> the value of another <code>Real</code>.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <code>this = a;</code>
     * </td></tr><tr><td><i>Error&nbsp;bound:</i></td><td>
     *     0 ULPs
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     0.3
     * </td></tr></table>
     *
     * @param a the <code>Real</code> to assign.
     */
    public void assign(Real a) {
        if (a == null) {
            makeZero();
            return;
        }
        sign = a.sign;
        exponent = a.exponent;
        mantissa = a.mantissa;
    }
    /**
     * Assigns this <code>Real</code> the value of an integer.
     * All integer values can be represented without loss of accuracy.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <code>this = (double)a;</code>
     * </td></tr><tr><td><i>Error&nbsp;bound:</i></td><td>
     *     0 ULPs
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     0.6
     * </td></tr></table>
     *
     * @param a the <code>int</code> to assign.
     */
    public void assign(int a) {
        if (a==0) {
            makeZero();
            return;
        }
        sign = 0;
        if (a<0) {
            sign = 1;
            a = -a; // Also works for 0x80000000
        }
        // Normalize int
        int t=a; t|=t>>1; t|=t>>2; t|=t>>4; t|=t>>8; t|=t>>16;
        t = clz_tab[(t*clz_magic)>>>27]-1;
        exponent = 0x4000001E-t;
        mantissa = ((long)a)<<(32+t);
    }
    /**
     * Assigns this <code>Real</code> the value of a signed long integer.
     * All long values can be represented without loss of accuracy.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <code>this = (double)a;</code>
     * </td></tr><tr><td><i>Error&nbsp;bound:</i></td><td>
     *     0 ULPs
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     1.0
     * </td></tr></table>
     *
     * @param a the <code>long</code> to assign.
     */
    public void assign(long a) {
        sign = 0;
        if (a<0) {
            sign = 1;
            a = -a; // Also works for 0x8000000000000000
        }
        exponent = 0x4000003E;
        mantissa = a;
        normalize();
    }
    /**
     * Assigns this <code>Real</code> a value encoded in a <code>String</code>
     * using base-10, as specified in {@link #assign(String,int)}.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *    <code>this = Double.{@link Double#valueOf(String) valueOf}(a);</code>
     * </td></tr><tr><td><i>Approximate&nbsp;error&nbsp;bound:</i></td><td>
     *     ½-1 ULPs
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     80
     * </td></tr></table>
     *
     * @param a the <code>String</code> to assign.
     */
    public void assign(String a) {
        assign(a,10);
    }
    /**
     * Assigns this <code>Real</code> a value encoded in a <code>String</code>
     * using the specified number base. The string is parsed as follows:
     *
     * <ul>
     *   <li>If the string is <code>null</code> or an empty string, zero is
     *       assigned.
     *   <li>Leading spaces are ignored.
     *   <li>An optional sign, '+', '-' or '/', where '/' precedes a negative
     *       two's-complement number, reading: "an infinite number of 1-bits
     *       preceding the number".
     *   <li>Optional digits preceding the radix, in the specified base.
     *       <ul>
     *           <li>In base-2, allowed digits are '01'.
     *           <li>In base-8, allowed digits are '01234567'.
     *           <li>In base-10, allowed digits are '0123456789'.
     *           <li>In base-16, allowed digits are '0123456789ABCDEF'.
     </ul>
     *   <li>An optional radix character, '.' or ','.
     *   <li>Optional digits following the radix.
     *   <li>The following spaces are ignored.
     *   <li>An optional exponent indicator, 'e'. If not base-16, or after a
     *       space, 'E' is also accepted.
     *   <li>An optional sign, '+' or '-'.
     *   <li>Optional exponent digits <i><b>in base-10</b></i>.
     * </ul>
     *
     * <p><i>Valid examples:</i><br>
     * &nbsp;&nbsp;&nbsp;&nbsp;base-2:  <code>"-.110010101e+5"</code><br>
     * &nbsp;&nbsp;&nbsp;&nbsp;base-8:  <code>"+5462E-99"</code><br>
     * &nbsp;&nbsp;&nbsp;&nbsp;base-10: <code>"&nbsp;&nbsp;3,1415927"</code><br>
     * &nbsp;&nbsp;&nbsp;&nbsp;base-16: <code>"/FFF800C.CCCE e64"</code>
     *
     * <p>The number is parsed until the end of the string or an unknown
     * character is encountered, then silently returns even if the whole
     * string has not been parsed. Please note that specifying an
     * excessive number of digits in base-10 may in fact decrease the
     * accuracy of the result because of the extra multiplications performed.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td>
     * <td colspan="2">
     *     <code>this = Double.{@link Double#valueOf(String) valueOf}(a);
     *           // Works only for base-10</code>
     * </td></tr><tr><td valign="top" rowspan="2"><i>
     * Approximate&nbsp;error&nbsp;bound:</i>
     * </td><td width="1%">base-10</td><td>
     *     ½-1 ULPs
     * </td></tr><tr><td>2/8/16</td><td>
     *     ½ ULPs
     * </td></tr><tr><td valign="top" rowspan="4"><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;</i>
     * </td><td width="1%">base-2</td><td>
     *     54
     * </td></tr><tr><td>base-8</td><td>
     *     60
     * </td></tr><tr><td>base-10</td><td>
     *     80
     * </td></tr><tr><td>base-16&nbsp;&nbsp;</td><td>
     *     60
     * </td></tr></table>
     *
     * @param a the <code>String</code> to assign.
     * @param base the number base of <code>a</code>. Valid base values are
     *     2, 8, 10 and 16.
     */
    public void assign(String a, int base) {
        if (a==null || a.length()==0) {
            assign(ZERO);
            return;
        }
        atof(a,base);
    }
    /**
     * Assigns this <code>Real</code> a value by directly setting the fields
     * of the internal representation. The arguments must represent a valid,
     * normalized <code>Real</code>. This is the fastest way of assigning a
     * constant value.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <code>this = (1-2*s) * m *
     *           Math.{@link Math#pow(double,double)
     *           pow}(2.0,e-0x400000e3);</code>
     * </td></tr><tr><td><i>Error&nbsp;bound:</i></td><td>
     *     0 ULPs
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     0.3
     * </td></tr></table>
     *
     * @param s {@link #sign} bit, 0 for positive sign, 1 for negative sign
     * @param e {@link #exponent}
     * @param m {@link #mantissa}
     */
    public void assign(int s, int e, long m) {
        sign = (byte)s;
        exponent = e;
        mantissa = m;
    }
    /**
     * Assigns this <code>Real</code> a value previously encoded into into
     * twelve consecutive bytes in a byte array using {@link
     * #toBytes(byte[],int) toBytes}.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Error&nbsp;bound:</i></td><td>
     *     0 ULPs
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     1.2
     * </td></tr></table>
     *
     * @param data byte array to decode into this <code>Real</code>.
     * @param offset offset to start encoding from. The bytes
     *     <code>data[offset]...data[offset+11]</code> will be
     *     read.
     */
    public void assign(byte [] data, int offset) {
        sign = (byte)((data[offset+4]>>7)&1);
        exponent = (((data[offset ]&0xff)<<24)+
                    ((data[offset +1]&0xff)<<16)+
                    ((data[offset +2]&0xff)<<8)+
                    ((data[offset +3]&0xff)));
        mantissa = (((long)(data[offset+ 4]&0x7f)<<56)+
                    ((long)(data[offset+ 5]&0xff)<<48)+
                    ((long)(data[offset+ 6]&0xff)<<40)+
                    ((long)(data[offset+ 7]&0xff)<<32)+
                    ((long)(data[offset+ 8]&0xff)<<24)+
                    ((long)(data[offset+ 9]&0xff)<<16)+
                    ((long)(data[offset+10]&0xff)<< 8)+
                    ( (data[offset+11]&0xff)));
    }
    /**
     * Encodes an accurate representation of this <code>Real</code> value into
     * twelve consecutive bytes in a byte array. Can be decoded using {@link
     * #assign(byte[],int)}.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     1.2
     * </td></tr></table>
     *
     * @param data byte array to save this <code>Real</code> in.
     * @param offset offset to start encoding to. The bytes
     *     <code>data[offset]...data[offset+11]</code> will be
     *     written.
     */
    public void toBytes(byte [] data, int offset) {
        data[offset ] = (byte)(exponent>>24);
        data[offset+ 1] = (byte)(exponent>>16);
        data[offset+ 2] = (byte)(exponent>>8);
        data[offset+ 3] = (byte)(exponent);
        data[offset+ 4] = (byte)((sign<<7)+(mantissa>>56));
        data[offset+ 5] = (byte)(mantissa>>48);
        data[offset+ 6] = (byte)(mantissa>>40);
        data[offset+ 7] = (byte)(mantissa>>32);
        data[offset+ 8] = (byte)(mantissa>>24);
        data[offset+ 9] = (byte)(mantissa>>16);
        data[offset+10] = (byte)(mantissa>>8);
        data[offset+11] = (byte)(mantissa);
    }
    /**
     * Assigns this <code>Real</code> the value corresponding to a given bit
     * representation. The argument is considered to be a representation of a
     * floating-point value according to the IEEE 754 floating-point "single
     * format" bit layout.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>float</code><i>&nbsp;code:</i></td><td>
     *     <code>this = Float.{@link Float#intBitsToFloat(int)
     *           intBitsToFloat}(bits);</code>
     * </td></tr><tr><td><i>Error&nbsp;bound:</i></td><td>
     *     0 ULPs
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     0.6
     * </td></tr></table>
     *
     * @param bits a <code>float</code> value encoded in an <code>int</code>.
     */
    public void assignFloatBits(int bits) {
        sign = (byte)(bits>>>31);
        exponent = (bits>>23)&0xff;
        mantissa = (long)(bits&0x007fffff)<<39;
        if (exponent == 0 && mantissa == 0)
            return; // Valid zero
        if (exponent == 0 && mantissa != 0) {
            // Degenerate small float
            exponent = 0x40000000-126;
            normalize();
            return;
        }
        if (exponent <= 254) {
            // Normal IEEE 754 float
            exponent += 0x40000000-127;
            mantissa |= 1L<<62;
            return;
        }
        if (mantissa == 0)
            makeInfinity(sign);
        else
            makeNan();
    }
    /**
     * Assigns this <code>Real</code> the value corresponding to a given bit
     * representation. The argument is considered to be a representation of a
     * floating-point value according to the IEEE 754 floating-point "double
     * format" bit layout.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <code>this = Double.{@link Double#longBitsToDouble(long)
     *           longBitsToDouble}(bits);</code>
     * </td></tr><tr><td><i>Error&nbsp;bound:</i></td><td>
     *     0 ULPs
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     0.6
     * </td></tr></table>
     *
     * @param bits a <code>double</code> value encoded in a <code>long</code>.
     */
    public void assignDoubleBits(long bits) {
        sign = (byte)((bits>>63)&1);
        exponent = (int)((bits>>52)&0x7ff);
        mantissa = (bits&0x000fffffffffffffL)<<10;
        if (exponent == 0 && mantissa == 0)
            return; // Valid zero
        if (exponent == 0 && mantissa != 0) {
            // Degenerate small float
            exponent = 0x40000000-1022;
            normalize();
            return;
        }
        if (exponent <= 2046) {
            // Normal IEEE 754 float
            exponent += 0x40000000-1023;
            mantissa |= 1L<<62;
            return;
        }
        if (mantissa == 0)
            makeInfinity(sign);
        else
            makeNan();
    }
    /**
     * Returns a representation of this <code>Real</code> according to the
     * IEEE 754 floating-point "single format" bit layout.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>float</code><i>&nbsp;code:</i></td><td>
     *     <code>Float.{@link Float#floatToIntBits(float)
     *           floatToIntBits}(this)</code>
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     0.7
     * </td></tr></table>
     *
     * @return the bits that represent the floating-point number.
     */
    public int toFloatBits() {
        if ((this.exponent < 0 && this.mantissa != 0))
            return 0x7fffffff; // nan
        int e = exponent-0x40000000+127;
        long m = mantissa;
        // Round properly!
        m += 1L<<38;
        if (m<0) {
            m >>>= 1;
            e++;
            if (exponent < 0) // Overflow
                return (sign<<31)|0x7f800000; // inf
        }
        if ((this.exponent < 0 && this.mantissa == 0) || e > 254)
            return (sign<<31)|0x7f800000; // inf
        if ((this.exponent == 0 && this.mantissa == 0) || e < -22)
            return (sign<<31); // zero
        if (e <= 0) // Degenerate small float
            return (sign<<31)|((int)(m>>>(40-e))&0x007fffff);
        // Normal IEEE 754 float
        return (sign<<31)|(e<<23)|((int)(m>>>39)&0x007fffff);
    }
    /**
     * Returns a representation of this <code>Real</code> according to the
     * IEEE 754 floating-point "double format" bit layout.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <code>Double.{@link Double#doubleToLongBits(double)
     *           doubleToLongBits}(this)</code>
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     0.7
     * </td></tr></table>
     *
     * @return the bits that represent the floating-point number.
     */
    public long toDoubleBits() {
        if ((this.exponent < 0 && this.mantissa != 0))
            return 0x7fffffffffffffffL; // nan
        int e = exponent-0x40000000+1023;
        long m = mantissa;
        // Round properly!
        m += 1L<<9;
        if (m<0) {
            m >>>= 1;
            e++;
            if (exponent < 0)
                return ((long)sign<<63)|0x7ff0000000000000L; // inf
        }
        if ((this.exponent < 0 && this.mantissa == 0) || e > 2046)
            return ((long)sign<<63)|0x7ff0000000000000L; // inf
        if ((this.exponent == 0 && this.mantissa == 0) || e < -51)
            return ((long)sign<<63); // zero
        if (e <= 0) // Degenerate small double
            return ((long)sign<<63)|((m>>>(11-e))&0x000fffffffffffffL);
        // Normal IEEE 754 double
        return ((long)sign<<63)|((long)e<<52)|((m>>>10)&0x000fffffffffffffL);
    }
    /**
     * Makes this <code>Real</code> the value of positive zero.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <code>this = 0;</code>
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     0.2
     * </td></tr></table>
     */
    public void makeZero() {
        sign = 0;
        mantissa = 0;
        exponent = 0;
    }
    /**
     * Makes this <code>Real</code> the value of zero with the specified sign.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <code>this = 0.0 * (1-2*s);</code>
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     0.2
     * </td></tr></table>
     *
     * @param s sign bit, 0 to make a positive zero, 1 to make a negative zero
     */
    public void makeZero(int s) {
        sign = (byte)s;
        mantissa = 0;
        exponent = 0;
    }
    /**
     * Makes this <code>Real</code> the value of infinity with the specified
     * sign.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *   <code>this = Double.{@link Double#POSITIVE_INFINITY POSITIVE_INFINITY}
     *           * (1-2*s);</code>
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     0.3
     * </td></tr></table>
     *
     * @param s sign bit, 0 to make positive infinity, 1 to make negative
     * infinity
     */
    public void makeInfinity(int s) {
        sign = (byte)s;
        mantissa = 0;
        exponent = 0x80000000;
    }
    /**
     * Makes this <code>Real</code> the value of Not-a-Number (NaN).
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <code>this = Double.{@link Double#NaN NaN};</code>
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     0.3
     * </td></tr></table>
     */
    public void makeNan() {
        sign = 0;
        mantissa = 0x4000000000000000L;
        exponent = 0x80000000;
    }
    /**
     * Returns <code>true</code> if the value of this <code>Real</code> is
     * zero, <code>false</code> otherwise.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <code>(this == 0)</code>
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     0.3
     * </td></tr></table>
     *
     * @return <code>true</code> if the value represented by this object is
     *     zero, <code>false</code> otherwise.
     */
    public boolean isZero() {
        return (exponent == 0 && mantissa == 0);
    }
    /**
     * Returns <code>true</code> if the value of this <code>Real</code> is
     * infinite, <code>false</code> otherwise.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *   <code>Double.{@link Double#isInfinite(double) isInfinite}(this)</code>
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     0.3
     * </td></tr></table>
     *
     * @return <code>true</code> if the value represented by this object is
     *     infinite, <code>false</code> if it is finite or NaN.
     */
    public boolean isInfinity() {
        return (exponent < 0 && mantissa == 0);
    }
    /**
     * Returns <code>true</code> if the value of this <code>Real</code> is
     * Not-a-Number (NaN), <code>false</code> otherwise.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <code>Double.{@link Double#isNaN(double) isNaN}(this)</code>
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     0.3
     * </td></tr></table>
     *
     * @return <code>true</code> if the value represented by this object is
     *     NaN, <code>false</code> otherwise.
     */
    public boolean isNan() {
        return (exponent < 0 && mantissa != 0);
    }
    /**
     * Returns <code>true</code> if the value of this <code>Real</code> is
     * finite, <code>false</code> otherwise.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <code>(!Double.{@link Double#isNaN(double) isNaN}(this) &&
     *            !Double.{@link Double#isInfinite(double)
     *            isInfinite}(this))</code>
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     0.3
     * </td></tr></table>
     *
     * @return <code>true</code> if the value represented by this object is
     *     finite, <code>false</code> if it is infinite or NaN.
     */
    public boolean isFinite() {
        // That is, non-infinite and non-nan
        return (exponent >= 0);
    }
    /**
     * Returns <code>true</code> if the value of this <code>Real</code> is
     * finite and nonzero, <code>false</code> otherwise.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *  <code>(!Double.{@link Double#isNaN(double) isNaN}(this) &&
     *         !Double.{@link Double#isInfinite(double) isInfinite}(this) &&
     *         (this!=0))</code>
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     0.3
     * </td></tr></table>
     *
     * @return <code>true</code> if the value represented by this object is
     *     finite and nonzero, <code>false</code> if it is infinite, NaN or
     *     zero.
     */
    public boolean isFiniteNonZero() {
        // That is, non-infinite and non-nan and non-zero
        return (exponent >= 0 && mantissa != 0);
    }
    /**
     * Returns <code>true</code> if the value of this <code>Real</code> is
     * negative, <code>false</code> otherwise.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <code>(this < 0)</code>
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     0.3
     * </td></tr></table>
     *
     * @return <code>true</code> if the value represented by this object
     *     is negative, <code>false</code> if it is positive or NaN.
     */
    public boolean isNegative() {
        return sign!=0;
    }
    /**
     * Calculates the absolute value.
     * Replaces the contents of this <code>Real</code> with the result.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <code>this = Math.{@link Math#abs(double) abs}(this);</code>
     * </td></tr><tr><td><i>Error&nbsp;bound:</i></td><td>
     *     0 ULPs
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     0.2
     * </td></tr></table>
     */
    public void abs() {
        sign = 0;
    }
    /**
     * Negates this <code>Real</code>.
     * Replaces the contents of this <code>Real</code> with the result.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <code>this = -this;</code>
     * </td></tr><tr><td><i>Error&nbsp;bound:</i></td><td>
     *     0 ULPs
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     0.2
     * </td></tr></table>
     */
    public void neg() {
        if (!(this.exponent < 0 && this.mantissa != 0))
            sign ^= 1;
    }
    /**
     * Copies the sign from <code>a</code>.
     * Replaces the contents of this <code>Real</code> with the result.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <code>this = Math.{@link Math#abs(double)
     *           abs}(this)*Math.{@link Math#signum(double) signum}(a);</code>
     * </td></tr><tr><td><i>Error&nbsp;bound:</i></td><td>
     *     0 ULPs
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     0.2
     * </td></tr></table>
     *
     * @param a the <code>Real</code> to copy the sign from.
     */
    public void copysign(Real a) {
        if ((this.exponent < 0 && this.mantissa != 0) || (a.exponent < 0 && a.mantissa != 0)) {
            makeNan();
            return;
        }
        sign = a.sign;
    }
    /**
     * Readjusts the mantissa of this <code>Real</code>. The exponent is
     * adjusted accordingly. This is necessary when the mantissa has been
     * {@link #mantissa modified directly} for some purpose and may be
     * denormalized. The normalized mantissa of a finite <code>Real</code>
     * must have bit 63 cleared and bit 62 set. Using a denormalized
     * <code>Real</code> in <u>any</u> other operation may produce undefined
     * results.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Error&nbsp;bound:</i></td><td>
     *     ½ ULPs
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     0.7
     * </td></tr></table>
     */
    public void normalize() {
        if ((this.exponent >= 0)) {
            if (mantissa > 0)
            {
                int clz = 0;
                int t = (int)(mantissa>>>32);
                if (t == 0) { clz = 32; t = (int)mantissa; }
                t|=t>>1; t|=t>>2; t|=t>>4; t|=t>>8; t|=t>>16;
                clz += clz_tab[(t*clz_magic)>>>27]-1;
                mantissa <<= clz;
                exponent -= clz;
                if (exponent < 0) // Underflow
                    makeZero(sign);
            }
            else if (mantissa < 0)
            {
                mantissa = (mantissa+1)>>>1;
                exponent ++;
                if (mantissa == 0) { // Ooops, it was 0xffffffffffffffffL
                    mantissa = 0x4000000000000000L;
                    exponent ++;
                }
                if (exponent < 0) // Overflow
                    makeInfinity(sign);
            }
            else // mantissa == 0
            {
                exponent = 0;
            }
        }
    }
    /**
     * Readjusts the mantissa of a <code>Real</code> with extended
     * precision. The exponent is adjusted accordingly. This is necessary when
     * the mantissa has been {@link #mantissa modified directly} for some
     * purpose and may be denormalized. The normalized mantissa of a finite
     * <code>Real</code> must have bit 63 cleared and bit 62 set. Using a
     * denormalized <code>Real</code> in <u>any</u> other operation may
     * produce undefined results.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Approximate&nbsp;error&nbsp;bound:</i></td><td>
     *     2<sup>-64</sup> ULPs (i.e. of a normal precision <code>Real</code>)
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     0.7
     * </td></tr></table>
     *
     * @param extra the extra 64 bits of mantissa of this extended precision
     *     <code>Real</code>.
     * @return the extra 64 bits of mantissa of the resulting extended
     *     precision <code>Real</code>.
     */
    public long normalize128(long extra) {
        if (!(this.exponent >= 0))
            return 0;
        if (mantissa == 0) {
            if (extra == 0) {
                exponent = 0;
                return 0;
            }
            mantissa = extra;
            extra = 0;
            exponent -= 64;
            if (exponent < 0) { // Underflow
                makeZero(sign);
                return 0;
            }
        }
        if (mantissa < 0) {
            extra = (mantissa<<63)+(extra>>>1);
            mantissa >>>= 1;
            exponent ++;
            if (exponent < 0) { // Overflow
                makeInfinity(sign);
                return 0;
            }
            return extra;
        }
        int clz = 0;
        int t = (int)(mantissa>>>32);
        if (t == 0) { clz = 32; t = (int)mantissa; }
        t|=t>>1; t|=t>>2; t|=t>>4; t|=t>>8; t|=t>>16;
        clz += clz_tab[(t*clz_magic)>>>27]-1;
        if (clz == 0)
            return extra;
        mantissa = (mantissa<<clz)+(extra>>>(64-clz));
        extra <<= clz;
        exponent -= clz;
        if (exponent < 0) { // Underflow
            makeZero(sign);
            return 0;
        }
        return extra;
    }
    /**
     * Rounds an extended precision <code>Real</code> to the nearest
     * <code>Real</code> of normal precision. Replaces the contents of this
     * <code>Real</code> with the result.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Error&nbsp;bound:</i></td><td>
     *     ½ ULPs
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     1.0
     * </td></tr></table>
     *
     * @param extra the extra 64 bits of mantissa of this extended precision
     *     <code>Real</code>.
     */
    public void roundFrom128(long extra) {
        mantissa += (extra>>63)&1;
        normalize();
    }
    /**
     * Returns <code>true</code> if this Java object is the same
     * object as <code>a</code>. Since a <code>Real</code> should be
     * thought of as a "register holding a number", this method compares the
     * object references, not the contents of the two objects.
     * This is very different from {@link #equalTo(Real)}.
     *
     * @param a the object to compare to this.
     * @return <code>true</code> if this object is the same as <code>a</code>.
     */
    public boolean equals(Object a) {
        return this==a;
    }
    private int compare(Real a) {
        // Compare of normal floats, zeros, but not nan or equal-signed inf
        if ((this.exponent == 0 && this.mantissa == 0) && (a.exponent == 0 && a.mantissa == 0))
            return 0;
        if (sign != a.sign)
            return a.sign-sign;
        int s = (this.sign==0) ? 1 : -1;
        if ((this.exponent < 0 && this.mantissa == 0))
            return s;
        if ((a.exponent < 0 && a.mantissa == 0))
            return -s;
        if (exponent != a.exponent)
            return exponent<a.exponent ? -s : s;
        if (mantissa != a.mantissa)
            return mantissa<a.mantissa ? -s : s;
        return 0;
    }
    private boolean invalidCompare(Real a) {
        return ((this.exponent < 0 && this.mantissa != 0) || (a.exponent < 0 && a.mantissa != 0) ||
                ((this.exponent < 0 && this.mantissa == 0) && (a.exponent < 0 && a.mantissa == 0) && sign == a.sign));
    }
    /**
     * Returns <code>true</code> if this <code>Real</code> is equal to
     * <code>a</code>.
     * If the numbers are incomparable, i.e. the values are infinities of
     * the same sign or any of them is NaN, <code>false</code> is always
     * returned. This method must not be confused with {@link #equals(Object)}.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <code>(this == a)</code>
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     1.0
     * </td></tr></table>
     *
     * @param a the <code>Real</code> to compare to this.
     * @return <code>true</code> if the value represented by this object is
     *     equal to the value represented by <code>a</code>. <code>false</code>
     *     otherwise, or if the numbers are incomparable.
     */
    public boolean equalTo(Real a) {
        if (invalidCompare(a))
            return false;
        return compare(a) == 0;
    }
    /**
     * Returns <code>true</code> if this <code>Real</code> is equal to
     * the integer <code>a</code>.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <code>(this == a)</code>
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     1.7
     * </td></tr></table>
     *
     * @param a the <code>int</code> to compare to this.
     * @return <code>true</code> if the value represented by this object is
     *     equal to the integer <code>a</code>. <code>false</code>
     *     otherwise.
     */
    public boolean equalTo(int a) {
        tmp0.assign(a);
        return equalTo(tmp0);
    }
    /**
     * Returns <code>true</code> if this <code>Real</code> is not equal to
     * <code>a</code>.
     * If the numbers are incomparable, i.e. the values are infinities of
     * the same sign or any of them is NaN, <code>false</code> is always
     * returned.
     * This distinguishes <code>notEqualTo(a)</code> from the expression
     * <code>!equalTo(a)</code>.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <code>(this != a)</code>
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     1.0
     * </td></tr></table>
     *
     * @param a the <code>Real</code> to compare to this.
     * @return <code>true</code> if the value represented by this object is not
     *     equal to the value represented by <code>a</code>. <code>false</code>
     *     otherwise, or if the numbers are incomparable.
     */
    public boolean notEqualTo(Real a) {
        if (invalidCompare(a))
            return false;
        return compare(a) != 0;
    }
    /**
     * Returns <code>true</code> if this <code>Real</code> is not equal to
     * the integer <code>a</code>.
     * If this <code>Real</code> is NaN, <code>false</code> is always
     * returned.
     * This distinguishes <code>notEqualTo(a)</code> from the expression
     * <code>!equalTo(a)</code>.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <code>(this != a)</code>
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     1.7
     * </td></tr></table>
     *
     * @param a the <code>int</code> to compare to this.
     * @return <code>true</code> if the value represented by this object is not
     *     equal to the integer <code>a</code>. <code>false</code>
     *     otherwise, or if this <code>Real</code> is NaN.
     */
    public boolean notEqualTo(int a) {
        tmp0.assign(a);
        return notEqualTo(tmp0);
    }
    /**
     * Returns <code>true</code> if this <code>Real</code> is less than
     * <code>a</code>.
     * If the numbers are incomparable, i.e. the values are infinities of
     * the same sign or any of them is NaN, <code>false</code> is always
     * returned.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <code>(this &lt; a)</code>
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     1.0
     * </td></tr></table>
     *
     * @param a the <code>Real</code> to compare to this.
     * @return <code>true</code> if the value represented by this object is
     *     less than the value represented by <code>a</code>.
     *     <code>false</code> otherwise, or if the numbers are incomparable.
     */
    public boolean lessThan(Real a) {
        if (invalidCompare(a))
            return false;
        return compare(a) < 0;
    }
    /**
     * Returns <code>true</code> if this <code>Real</code> is less than
     * the integer <code>a</code>.
     * If this <code>Real</code> is NaN, <code>false</code> is always
     * returned.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <code>(this &lt; a)</code>
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     1.7
     * </td></tr></table>
     *
     * @param a the <code>int</code> to compare to this.
     * @return <code>true</code> if the value represented by this object is
     *     less than the integer <code>a</code>. <code>false</code> otherwise,
     *     or if this <code>Real</code> is NaN.
     */
    public boolean lessThan(int a) {
        tmp0.assign(a);
        return lessThan(tmp0);
    }
    /**
     * Returns <code>true</code> if this <code>Real</code> is less than or
     * equal to <code>a</code>.
     * If the numbers are incomparable, i.e. the values are infinities of
     * the same sign or any of them is NaN, <code>false</code> is always
     * returned.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <code>(this &lt;= a)</code>
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     1.0
     * </td></tr></table>
     *
     * @param a the <code>Real</code> to compare to this.
     * @return <code>true</code> if the value represented by this object is
     *     less than or equal to the value represented by <code>a</code>.
     *     <code>false</code> otherwise, or if the numbers are incomparable.
     */
    public boolean lessEqual(Real a) {
        if (invalidCompare(a))
            return false;
        return compare(a) <= 0;
    }
    /**
     * Returns <code>true</code> if this <code>Real</code> is less than or
     * equal to the integer <code>a</code>.
     * If this <code>Real</code> is NaN, <code>false</code> is always
     * returned.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <code>(this &lt;= a)</code>
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     1.7
     * </td></tr></table>
     *
     * @param a the <code>int</code> to compare to this.
     * @return <code>true</code> if the value represented by this object is
     *     less than or equal to the integer <code>a</code>. <code>false</code>
     *     otherwise, or if this <code>Real</code> is NaN.
     */
    public boolean lessEqual(int a) {
        tmp0.assign(a);
        return lessEqual(tmp0);
    }
    /**
     * Returns <code>true</code> if this <code>Real</code> is greater than
     * <code>a</code>.
     * If the numbers are incomparable, i.e. the values are infinities of
     * the same sign or any of them is NaN, <code>false</code> is always
     * returned.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <code>(this &gt; a)</code>
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     1.0
     * </td></tr></table>
     *
     * @param a the <code>Real</code> to compare to this.
     * @return <code>true</code> if the value represented by this object is
     *     greater than the value represented by <code>a</code>.
     *     <code>false</code> otherwise, or if the numbers are incomparable.
     */
    public boolean greaterThan(Real a) {
        if (invalidCompare(a))
            return false;
        return compare(a) > 0;
    }
    /**
     * Returns <code>true</code> if this <code>Real</code> is greater than
     * the integer <code>a</code>.
     * If this <code>Real</code> is NaN, <code>false</code> is always
     * returned.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <code>(this &gt; a)</code>
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     1.7
     * </td></tr></table>
     *
     * @param a the <code>int</code> to compare to this.
     * @return <code>true</code> if the value represented by this object is
     *     greater than the integer <code>a</code>.
     *     <code>false</code> otherwise, or if this <code>Real</code> is NaN.
     */
    public boolean greaterThan(int a) {
        tmp0.assign(a);
        return greaterThan(tmp0);
    }
    /**
     * Returns <code>true</code> if this <code>Real</code> is greater than
     * or equal to <code>a</code>.
     * If the numbers are incomparable, i.e. the values are infinities of
     * the same sign or any of them is NaN, <code>false</code> is always
     * returned.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <code>(this &gt;= a)</code>
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     1.0
     * </td></tr></table>
     *
     * @param a the <code>Real</code> to compare to this.
     * @return <code>true</code> if the value represented by this object is
     *     greater than or equal to the value represented by <code>a</code>.
     *     <code>false</code> otherwise, or if the numbers are incomparable.
     */
    public boolean greaterEqual(Real a) {
        if (invalidCompare(a))
            return false;
        return compare(a) >= 0;
    }
    /**
     * Returns <code>true</code> if this <code>Real</code> is greater than
     * or equal to the integer <code>a</code>.
     * If this <code>Real</code> is NaN, <code>false</code> is always
     * returned.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <code>(this &gt;= a)</code>
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     1.7
     * </td></tr></table>
     *
     * @param a the <code>int</code> to compare to this.
     * @return <code>true</code> if the value represented by this object is
     *     greater than or equal to the integer <code>a</code>.
     *     <code>false</code> otherwise, or if this <code>Real</code> is NaN.
     */
    public boolean greaterEqual(int a) {
        tmp0.assign(a);
        return greaterEqual(tmp0);
    }
    /**
     * Returns <code>true</code> if the absolute value of this
     * <code>Real</code> is less than the absolute value of
     * <code>a</code>.
     * If the numbers are incomparable, i.e. the values are both infinite
     * or any of them is NaN, <code>false</code> is always returned.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <code>(Math.{@link Math#abs(double) abs}(this) &lt;
     *           Math.{@link Math#abs(double) abs}(a))</code>
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     0.5
     * </td></tr></table>
     *
     * @param a the <code>Real</code> to compare to this.
     * @return <code>true</code> if the absolute of the value represented by
     *     this object is less  than the absolute of the value represented by
     *     <code>a</code>.
     *     <code>false</code> otherwise, or if the numbers are incomparable.
     */
    public boolean absLessThan(Real a) {
        if ((this.exponent < 0 && this.mantissa != 0) || (a.exponent < 0 && a.mantissa != 0) || (this.exponent < 0 && this.mantissa == 0))
            return false;
        if ((a.exponent < 0 && a.mantissa == 0))
            return true;
        if (exponent != a.exponent)
            return exponent<a.exponent;
        return mantissa<a.mantissa;
    }
    /**
     * Multiplies this <code>Real</code> by 2 to the power of <code>n</code>.
     * Replaces the contents of this <code>Real</code> with the result.
     * This operation is faster than normal multiplication since it only
     * involves adding to the exponent.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *    <code>this *= Math.{@link Math#pow(double,double) pow}(2.0,n);</code>
     * </td></tr><tr><td><i>Error&nbsp;bound:</i></td><td>
     *     0 ULPs
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     0.3
     * </td></tr></table>
     *
     * @param n the integer argument.
     */
    public void scalbn(int n) {
        if (!(this.exponent >= 0 && this.mantissa != 0))
            return;
        exponent += n;
        if (exponent < 0) {
            if (n<0)
                makeZero(sign); // Underflow
            else
                makeInfinity(sign); // Overflow
        }
    }
    /**
     * Calculates the next representable neighbour of this <code>Real</code>
     * in the direction towards <code>a</code>.
     * Replaces the contents of this <code>Real</code> with the result.
     * If the two values are equal, nothing happens.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <code>this += Math.{@link Math#ulp(double) ulp}(this)*Math.{@link
     *           Math#signum(double) signum}(a-this);</code>
     * </td></tr><tr><td><i>Error&nbsp;bound:</i></td><td>
     *     0 ULPs
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     0.8
     * </td></tr></table>
     *
     * @param a the <code>Real</code> argument.
     */
    public void nextafter(Real a) {
        if ((this.exponent < 0 && this.mantissa != 0) || (a.exponent < 0 && a.mantissa != 0)) {
            makeNan();
            return;
        }
        if ((this.exponent < 0 && this.mantissa == 0) && (a.exponent < 0 && a.mantissa == 0) && sign == a.sign)
            return;
        int dir = -compare(a);
        if (dir == 0)
            return;
        if ((this.exponent == 0 && this.mantissa == 0)) {
            { this.mantissa = MIN.mantissa; this.exponent = MIN.exponent; this.sign = MIN.sign; };
            sign = (byte)(dir<0 ? 1 : 0);
            return;
        }
        if ((this.exponent < 0 && this.mantissa == 0)) {
            { this.mantissa = MAX.mantissa; this.exponent = MAX.exponent; this.sign = MAX.sign; };
            sign = (byte)(dir<0 ? 0 : 1);
            return;
        }
        if ((this.sign==0) ^ dir<0) {
            mantissa ++;
        } else {
            if (mantissa == 0x4000000000000000L) {
                mantissa <<= 1;
                exponent--;
            }
            mantissa --;
        }
        normalize();
    }
    /**
     * Calculates the largest (closest to positive infinity)
     * <code>Real</code> value that is less than or equal to this
     * <code>Real</code> and is equal to a mathematical integer.
     * Replaces the contents of this <code>Real</code> with the result.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <code>this = Math.{@link Math#floor(double) floor}(this);</code>
     * </td></tr><tr><td><i>Approximate&nbsp;error&nbsp;bound:</i></td><td>
     *     0 ULPs
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     0.5
     * </td></tr></table>
     */
    public void floor() {
        if (!(this.exponent >= 0 && this.mantissa != 0))
            return;
        if (exponent < 0x40000000) {
            if ((this.sign==0))
                makeZero(sign);
            else {
                exponent = ONE.exponent;
                mantissa = ONE.mantissa;
                // sign unchanged!
            }
            return;
        }
        int shift = 0x4000003e-exponent;
        if (shift <= 0)
            return;
        if ((this.sign!=0))
            mantissa += ((1L<<shift)-1);
        mantissa &= ~((1L<<shift)-1);
        if ((this.sign!=0))
            normalize();
    }
    /**
     * Calculates the smallest (closest to negative infinity)
     * <code>Real</code> value that is greater than or equal to this
     * <code>Real</code> and is equal to a mathematical integer.
     * Replaces the contents of this <code>Real</code> with the result.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <code>this = Math.{@link Math#ceil(double) ceil}(this);</code>
     * </td></tr><tr><td><i>Approximate&nbsp;error&nbsp;bound:</i></td><td>
     *     0 ULPs
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     1.8
     * </td></tr></table>
     */
    public void ceil() {
        neg();
        floor();
        neg();
    }
    /**
     * Rounds this <code>Real</code> value to the closest value that is equal
     * to a mathematical integer. If two <code>Real</code> values that are
     * mathematical integers are equally close, the result is the integer
     * value with the largest magnitude (positive or negative).  Replaces the
     * contents of this <code>Real</code> with the result.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <code>this = Math.{@link Math#rint(double) rint}(this);</code>
     * </td></tr><tr><td><i>Approximate&nbsp;error&nbsp;bound:</i></td><td>
     *     0 ULPs
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     1.3
     * </td></tr></table>
     */
    public void round() {
        if (!(this.exponent >= 0 && this.mantissa != 0))
            return;
        if (exponent < 0x3fffffff) {
            makeZero(sign);
            return;
        }
        int shift = 0x4000003e-exponent;
        if (shift <= 0)
            return;
        mantissa += 1L<<(shift-1); // Bla-bla, this works almost
        mantissa &= ~((1L<<shift)-1);
        normalize();
    }
    /**
     * Truncates this <code>Real</code> value to the closest value towards
     * zero that is equal to a mathematical integer.
     * Replaces the contents of this <code>Real</code> with the result.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <code>this = (double)((long)this);</code>
     * </td></tr><tr><td><i>Approximate&nbsp;error&nbsp;bound:</i></td><td>
     *     0 ULPs
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     1.2
     * </td></tr></table>
     */
    public void trunc() {
        if (!(this.exponent >= 0 && this.mantissa != 0))
            return;
        if (exponent < 0x40000000) {
            makeZero(sign);
            return;
        }
        int shift = 0x4000003e-exponent;
        if (shift <= 0)
            return;
        mantissa &= ~((1L<<shift)-1);
        normalize();
    }
    /**
     * Calculates the fractional part of this <code>Real</code> by subtracting
     * the closest value towards zero that is equal to a mathematical integer.
     * Replaces the contents of this <code>Real</code> with the result.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <code>this -= (double)((long)this);</code>
     * </td></tr><tr><td><i>Approximate&nbsp;error&nbsp;bound:</i></td><td>
     *     0 ULPs
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     1.2
     * </td></tr></table>
     */
    public void frac() {
        if (!(this.exponent >= 0 && this.mantissa != 0) || exponent < 0x40000000)
            return;
        int shift = 0x4000003e-exponent;
        if (shift <= 0) {
            makeZero(sign);
            return;
        }
        mantissa &= ((1L<<shift)-1);
        normalize();
    }
    /**
     * Converts this <code>Real</code> value to the closest <code>int</code>
     * value towards zero.
     *
     * <p>If the value of this <code>Real</code> is too large, {@link
     * Integer#MAX_VALUE} is returned. However, if the value of this
     * <code>Real</code> is too small, <code>-Integer.MAX_VALUE</code> is
     * returned, not {@link Integer#MIN_VALUE}. This is done to ensure that
     * the sign will be correct if you calculate
     * <code>-this.toInteger()</code>. A NaN is converted to 0.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <code>(int)this</code>
     * </td></tr><tr><td><i>Approximate&nbsp;error&nbsp;bound:</i></td><td>
     *     0 ULPs
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     0.6
     * </td></tr></table>
     *
     * @return an <code>int</code> representation of this <code>Real</code>.
     */
    public int toInteger() {
        if ((this.exponent == 0 && this.mantissa == 0) || (this.exponent < 0 && this.mantissa != 0))
            return 0;
        if ((this.exponent < 0 && this.mantissa == 0)) {
            return ((this.sign==0)) ? 0x7fffffff : 0x80000001;
            // 0x80000001, so that you can take -x.toInteger()
        }
        if (exponent < 0x40000000)
            return 0;
        int shift = 0x4000003e-exponent;
        if (shift < 32) {
            return ((this.sign==0)) ? 0x7fffffff : 0x80000001;
            // 0x80000001, so that you can take -x.toInteger()
        }
        return (this.sign==0) ?
            (int)(mantissa>>>shift) : -(int)(mantissa>>>shift);
    }
    /**
     * Converts this <code>Real</code> value to the closest <code>long</code>
     * value towards zero.
     *
     * <p>If the value of this <code>Real</code> is too large, {@link
     * Long#MAX_VALUE} is returned. However, if the value of this
     * <code>Real</code> is too small, <code>-Long.MAX_VALUE</code> is
     * returned, not {@link Long#MIN_VALUE}. This is done to ensure that the
     * sign will be correct if you calculate <code>-this.toLong()</code>.
     * A NaN is converted to 0.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <code>(long)this</code>
     * </td></tr><tr><td><i>Approximate&nbsp;error&nbsp;bound:</i></td><td>
     *     0 ULPs
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     0.5
     * </td></tr></table>
     *
     * @return a <code>long</code> representation of this <code>Real</code>.
     */
    public long toLong() {
        if ((this.exponent == 0 && this.mantissa == 0) || (this.exponent < 0 && this.mantissa != 0))
            return 0;
        if ((this.exponent < 0 && this.mantissa == 0)) {
            return ((this.sign==0))? 0x7fffffffffffffffL:0x8000000000000001L;
            // 0x8000000000000001L, so that you can take -x.toLong()
        }
        if (exponent < 0x40000000)
            return 0;
        int shift = 0x4000003e-exponent;
        if (shift < 0) {
            return ((this.sign==0))? 0x7fffffffffffffffL:0x8000000000000001L;
            // 0x8000000000000001L, so that you can take -x.toLong()
        }
        return (this.sign==0) ? (mantissa>>>shift) : -(mantissa>>>shift);
    }
    /**
     * Returns <code>true</code> if the value of this <code>Real</code>
     * represents a mathematical integer. If the value is too large to
     * determine if it is an integer, <code>true</code> is returned.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <code>(this == (long)this)</code>
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     0.6
     * </td></tr></table>
     *
     * @return <code>true</code> if the value represented by this object
     *     represents a mathematical integer, <code>false</code> otherwise.
     */
    public boolean isIntegral() {
        if ((this.exponent < 0 && this.mantissa != 0))
            return false;
        if ((this.exponent == 0 && this.mantissa == 0) || (this.exponent < 0 && this.mantissa == 0))
            return true;
        if (exponent < 0x40000000)
            return false;
        int shift = 0x4000003e-exponent;
        if (shift <= 0)
            return true;
        return (mantissa&((1L<<shift)-1)) == 0;
    }
    /**
     * Returns <code>true</code> if the mathematical integer represented
     * by this <code>Real</code> is odd. You <u>must</u> first determine
     * that the value is actually an integer using {@link
     * #isIntegral()}. If the value is too large to determine if the
     * integer is odd, <code>false</code> is returned.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <code>((((long)this)&1) == 1)</code>
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     0.6
     * </td></tr></table>
     *
     * @return <code>true</code> if the mathematical integer represented by
     *     this <code>Real</code> is odd, <code>false</code> otherwise.
     */
    public boolean isOdd() {
        if (!(this.exponent >= 0 && this.mantissa != 0) ||
            exponent < 0x40000000 || exponent > 0x4000003e)
            return false;
        int shift = 0x4000003e-exponent;
        return ((mantissa>>>shift)&1) != 0;
    }
    /**
     * Exchanges the contents of this <code>Real</code> and <code>a</code>.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <code>tmp=this; this=a; a=tmp;</code>
     * </td></tr><tr><td><i>Error&nbsp;bound:</i></td><td>
     *     0 ULPs
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     0.5
     * </td></tr></table>
     *
     * @param a the <code>Real</code> to exchange with this.
     */
    public void swap(Real a) {
        long tmpMantissa=mantissa; mantissa=a.mantissa; a.mantissa=tmpMantissa;
        int tmpExponent=exponent; exponent=a.exponent; a.exponent=tmpExponent;
        byte tmpSign =sign; sign =a.sign; a.sign =tmpSign;
    }
    // Temporary values used by functions (to avoid "new" inside functions)
    private static Real tmp0 = new Real(); // tmp for basic functions
    private static Real recipTmp = new Real();
    private static Real recipTmp2 = new Real();
    private static Real sqrtTmp = new Real();
    private static Real expTmp = new Real();
    private static Real expTmp2 = new Real();
    private static Real expTmp3 = new Real();
    private static Real tmp1 = new Real();
    private static Real tmp2 = new Real();
    private static Real tmp3 = new Real();
    private static Real tmp4 = new Real();
    private static Real tmp5 = new Real();
    /**
     * Calculates the sum of this <code>Real</code> and <code>a</code>.
     * Replaces the contents of this <code>Real</code> with the result.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <code>this += a;</code>
     * </td></tr><tr><td><i>Error&nbsp;bound:</i></td><td>
     *     ½ ULPs
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     «« 1.0 »»
     * </td></tr></table>
     *
     * @param a the <code>Real</code> to add to this.
     */
    public void add(Real a) {
        if ((this.exponent < 0 && this.mantissa != 0) || (a.exponent < 0 && a.mantissa != 0)) {
            makeNan();
            return;
        }
        if ((this.exponent < 0 && this.mantissa == 0) || (a.exponent < 0 && a.mantissa == 0)) {
            if ((this.exponent < 0 && this.mantissa == 0) && (a.exponent < 0 && a.mantissa == 0) && sign != a.sign)
                makeNan();
            else
                makeInfinity((this.exponent < 0 && this.mantissa == 0) ? sign : a.sign);
            return;
        }
        if ((this.exponent == 0 && this.mantissa == 0) || (a.exponent == 0 && a.mantissa == 0)) {
            if ((this.exponent == 0 && this.mantissa == 0))
                { this.mantissa = a.mantissa; this.exponent = a.exponent; this.sign = a.sign; };
            if ((this.exponent == 0 && this.mantissa == 0))
                sign=0;
            return;
        }
        byte s;
        int e;
        long m;
        if (exponent > a.exponent ||
            (exponent == a.exponent && mantissa>=a.mantissa))
        {
            s = a.sign;
            e = a.exponent;
            m = a.mantissa;
        } else {
            s = sign;
            e = exponent;
            m = mantissa;
            sign = a.sign;
            exponent = a.exponent;
            mantissa = a.mantissa;
        }
        int shift = exponent-e;
        if (shift>=64)
            return;
        if (sign == s) {
            mantissa += m>>>shift;
            if (mantissa >= 0 && shift>0 && ((m>>>(shift-1))&1) != 0)
                mantissa ++; // We don't need normalization, so round now
            if (mantissa < 0) {
                // Simplified normalize()
                mantissa = (mantissa+1)>>>1;
                exponent ++;
                if (exponent < 0) { // Overflow
                    makeInfinity(sign);
                    return;
                }
            }
        } else {
            if (shift>0) {
                // Shift mantissa up to increase accuracy
                mantissa <<= 1;
                exponent --;
                shift --;
            }
            m = -m;
            mantissa += m>>shift;
            if (mantissa >= 0 && shift>0 && ((m>>>(shift-1))&1) != 0)
                mantissa ++; // We don't need to shift down, so round now
            if (mantissa < 0) {
                // Simplified normalize()
                mantissa = (mantissa+1)>>>1;
                exponent ++; // Can't overflow
            } else if (shift==0) {
                // Operands have equal exponents => many bits may be cancelled
                // Magic rounding: if result of subtract leaves only a few bits
                // standing, the result should most likely be 0...
                if (magicRounding && mantissa > 0 && mantissa <= 7) {
                    // If arguments were integers <= 2^63-1, then don't
                    // do the magic rounding anyway.
                    // This is a bit "post mortem" investigation but it happens
                    // so seldom that it's no problem to spend the extra time.
                    m = -m;
                    if (exponent == 0x4000003c || exponent == 0x4000003d ||
                        (exponent == 0x4000003e && mantissa+m > 0)) {
                        long mask = (1<<(0x4000003e-exponent))-1;
                        if ((mantissa & mask) != 0 || (m & mask) != 0)
                            mantissa = 0;
                    } else
                        mantissa = 0;
                }
                normalize();
            } // else... if (shift>=1 && mantissa>=0) it should be a-ok
        }
        if ((this.exponent == 0 && this.mantissa == 0))
            sign=0;
    }
    /**
     * Calculates the sum of this <code>Real</code> and the integer
     * <code>a</code>.
     * Replaces the contents of this <code>Real</code> with the result.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <code>this += a;</code>
     * </td></tr><tr><td><i>Error&nbsp;bound:</i></td><td>
     *     ½ ULPs
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     1.8
     * </td></tr></table>
     *
     * @param a the <code>int</code> to add to this.
     */
    public void add(int a) {
        tmp0.assign(a);
        add(tmp0);
    }
    /**
     * Calculates the sum of this <code>Real</code> and <code>a</code> with
     * extended precision.  Replaces the contents of this <code>Real</code>
     * with the result.  Returns the extra mantissa of the extended precision
     * result.
     *
     * <p>An extra 64 bits of mantissa is added to both arguments for extended
     * precision. If any of the arguments are not of extended precision, use
     * <code>0</code> for the extra mantissa.
     *
     * <p>Extended prevision can be useful in many situations. For instance,
     * when accumulating a lot of very small values it is advantageous for the
     * accumulator to have extended precision. To convert the extended
     * precision value back to a normal <code>Real</code> for further
     * processing, use {@link #roundFrom128(long)}.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <code>this += a;</code>
     * </td></tr><tr><td><i>Approximate&nbsp;error&nbsp;bound:</i></td><td>
     *     2<sup>-62</sup> ULPs (i.e. of a normal precision <code>Real</code>)
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     2.0
     * </td></tr></table>
     *
     * @param extra the extra 64 bits of mantissa of this extended precision
     *     <code>Real</code>.
     * @param a the <code>Real</code> to add to this.
     * @param aExtra the extra 64 bits of mantissa of the extended precision
     *     value <code>a</code>.
     * @return the extra 64 bits of mantissa of the resulting extended
     *     precision <code>Real</code>.
     */
    public long add128(long extra, Real a, long aExtra) {
        if ((this.exponent < 0 && this.mantissa != 0) || (a.exponent < 0 && a.mantissa != 0)) {
            makeNan();
            return 0;
        }
        if ((this.exponent < 0 && this.mantissa == 0) || (a.exponent < 0 && a.mantissa == 0)) {
            if ((this.exponent < 0 && this.mantissa == 0) && (a.exponent < 0 && a.mantissa == 0) && sign != a.sign)
                makeNan();
            else
                makeInfinity((this.exponent < 0 && this.mantissa == 0) ? sign : a.sign);
            return 0;
        }
        if ((this.exponent == 0 && this.mantissa == 0) || (a.exponent == 0 && a.mantissa == 0)) {
            if ((this.exponent == 0 && this.mantissa == 0)) {
                { this.mantissa = a.mantissa; this.exponent = a.exponent; this.sign = a.sign; };
                extra = aExtra;
            }
            if ((this.exponent == 0 && this.mantissa == 0))
                sign=0;
            return extra;
        }
        byte s;
        int e;
        long m;
        long x;
        if (exponent > a.exponent ||
            (exponent == a.exponent && mantissa>a.mantissa) ||
            (exponent == a.exponent && mantissa==a.mantissa &&
             (extra>>>1)>=(aExtra>>>1)))
        {
            s = a.sign;
            e = a.exponent;
            m = a.mantissa;
            x = aExtra;
        } else {
            s = sign;
            e = exponent;
            m = mantissa;
            x = extra;
            sign = a.sign;
            exponent = a.exponent;
            mantissa = a.mantissa;
            extra = aExtra;
        }
        int shift = exponent-e;
        if (shift>=127)
            return extra;
        if (shift>=64) {
            x = m>>>(shift-64);
            m = 0;
        } else if (shift>0) {
            x = (x>>>shift)+(m<<(64-shift));
            m >>>= shift;
        }
        extra >>>= 1;
        x >>>= 1;
        if (sign == s) {
            extra += x;
            mantissa += (extra>>63)&1;
            mantissa += m;
        } else {
            extra -= x;
            mantissa -= (extra>>63)&1;
            mantissa -= m;
            // Magic rounding: if result of subtract leaves only a few bits
            // standing, the result should most likely be 0...
            if (mantissa == 0 && extra > 0 && extra <= 0x1f)
                extra = 0;
        }
        extra <<= 1;
        extra = normalize128(extra);
        if ((this.exponent == 0 && this.mantissa == 0))
            sign=0;
        return extra;
    }
    /**
     * Calculates the difference between this <code>Real</code> and
     * <code>a</code>.
     * Replaces the contents of this <code>Real</code> with the result.
     *
     * <p>(To achieve extended precision subtraction, it is enough to call
     * <code>a.{@link #neg() neg}()</code> before calling <code>{@link
     * #add128(long,Real,long) add128}(extra,a,aExtra)</code>, since only
     * the sign bit of <code>a</code> need to be changed.)
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <code>this -= a;</code>
     * </td></tr><tr><td><i>Error&nbsp;bound:</i></td><td>
     *     ½ ULPs
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     2.0
     * </td></tr></table>
     *
     * @param a the <code>Real</code> to subtract from this.
     */
    public void sub(Real a) {
        tmp0.mantissa = a.mantissa;
        tmp0.exponent = a.exponent;
        tmp0.sign = (byte)(a.sign^1);
        add(tmp0);
    }
    /**
     * Calculates the difference between this <code>Real</code> and the
     * integer <code>a</code>.
     * Replaces the contents of this <code>Real</code> with the result.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <code>this -= a;</code>
     * </td></tr><tr><td><i>Error&nbsp;bound:</i></td><td>
     *     ½ ULPs
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     2.4
     * </td></tr></table>
     *
     * @param a the <code>int</code> to subtract from this.
     */
    public void sub(int a) {
        tmp0.assign(a);
        sub(tmp0);
    }
    /**
     * Calculates the product of this <code>Real</code> and <code>a</code>.
     * Replaces the contents of this <code>Real</code> with the result.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <code>this *= a;</code>
     * </td></tr><tr><td><i>Error&nbsp;bound:</i></td><td>
     *     ½ ULPs
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     1.3
     * </td></tr></table>
     *
     * @param a the <code>Real</code> to multiply to this.
     */
    public void mul(Real a) {
        if ((this.exponent < 0 && this.mantissa != 0) || (a.exponent < 0 && a.mantissa != 0)) {
            makeNan();
            return;
        }
        sign ^= a.sign;
        if ((this.exponent == 0 && this.mantissa == 0) || (a.exponent == 0 && a.mantissa == 0)) {
            if ((this.exponent < 0 && this.mantissa == 0) || (a.exponent < 0 && a.mantissa == 0))
                makeNan();
            else
                makeZero(sign);
            return;
        }
        if ((this.exponent < 0 && this.mantissa == 0) || (a.exponent < 0 && a.mantissa == 0)) {
            makeInfinity(sign);
            return;
        }
        long a0 = mantissa & 0x7fffffff;
        long a1 = mantissa >>> 31;
        long b0 = a.mantissa & 0x7fffffff;
        long b1 = a.mantissa >>> 31;
        mantissa = a1*b1;
        // If we're going to need normalization, we don't want to round twice
        int round = (mantissa<0) ? 0 : 0x40000000;
        mantissa += ((a0*b1 + a1*b0 + ((a0*b0)>>>31) + round)>>>31);
        int aExp = a.exponent;
        exponent += aExp-0x40000000;
        if (exponent < 0) {
            if (exponent == -1 && aExp < 0x40000000 && mantissa < 0) {
                // Not underflow after all, it will be corrected in the
                // normalization below
            } else {
                if (aExp < 0x40000000)
                    makeZero(sign); // Underflow
                else
                    makeInfinity(sign); // Overflow
                return;
            }
        }
        // Simplified normalize()
        if (mantissa < 0) {
            mantissa = (mantissa+1)>>>1;
            exponent ++;
            if (exponent < 0) // Overflow
                makeInfinity(sign);
        }
    }
    /**
     * Calculates the product of this <code>Real</code> and the integer
     * <code>a</code>.
     * Replaces the contents of this <code>Real</code> with the result.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <code>this *= a;</code>
     * </td></tr><tr><td><i>Error&nbsp;bound:</i></td><td>
     *     ½ ULPs
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     1.3
     * </td></tr></table>
     *
     * @param a the <code>int</code> to multiply to this.
     */
    public void mul(int a) {
        if ((this.exponent < 0 && this.mantissa != 0))
            return;
        if (a<0) {
            sign ^= 1;
            a = -a;
        }
        if ((this.exponent == 0 && this.mantissa == 0) || a==0) {
            if ((this.exponent < 0 && this.mantissa == 0))
                makeNan();
            else
                makeZero(sign);
            return;
        }
        if ((this.exponent < 0 && this.mantissa == 0))
            return;
        // Normalize int
        int t=a; t|=t>>1; t|=t>>2; t|=t>>4; t|=t>>8; t|=t>>16;
        t = clz_tab[(t*clz_magic)>>>27];
        exponent += 0x1F-t;
        a <<= t;
        if (exponent < 0) {
            makeInfinity(sign); // Overflow
            return;
        }
        long a0 = mantissa & 0x7fffffff;
        long a1 = mantissa >>> 31;
        long b0 = a & 0xffffffffL;
        mantissa = a1*b0;
        // If we're going to need normalization, we don't want to round twice
        int round = (mantissa<0) ? 0 : 0x40000000;
        mantissa += ((a0*b0 + round)>>>31);
        // Simplified normalize()
        if (mantissa < 0) {
            mantissa = (mantissa+1)>>>1;
            exponent ++;
            if (exponent < 0) // Overflow
                makeInfinity(sign);
        }
    }
    /**
     * Calculates the product of this <code>Real</code> and <code>a</code> with
     * extended precision.
     * Replaces the contents of this <code>Real</code> with the result.
     * Returns the extra mantissa of the extended precision result.
     *
     * <p>An extra 64 bits of mantissa is added to both arguments for
     * extended precision. If any of the arguments are not of extended
     * precision, use <code>0</code> for the extra mantissa. See also {@link
     * #add128(long,Real,long)}.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <code>this *= a;</code>
     * </td></tr><tr><td><i>Approximate&nbsp;error&nbsp;bound:</i></td><td>
     *     2<sup>-60</sup> ULPs
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     3.1
     * </td></tr></table>
     *
     * @param extra the extra 64 bits of mantissa of this extended precision
     *     <code>Real</code>.
     * @param a the <code>Real</code> to multiply to this.
     * @param aExtra the extra 64 bits of mantissa of the extended precision
     *     value <code>a</code>.
     * @return the extra 64 bits of mantissa of the resulting extended
     *     precision <code>Real</code>.
     */
    public long mul128(long extra, Real a, long aExtra) {
        if ((this.exponent < 0 && this.mantissa != 0) || (a.exponent < 0 && a.mantissa != 0)) {
            makeNan();
            return 0;
        }
        sign ^= a.sign;
        if ((this.exponent == 0 && this.mantissa == 0) || (a.exponent == 0 && a.mantissa == 0)) {
            if ((this.exponent < 0 && this.mantissa == 0) || (a.exponent < 0 && a.mantissa == 0))
                makeNan();
            else
                makeZero(sign);
            return 0;
        }
        if ((this.exponent < 0 && this.mantissa == 0) || (a.exponent < 0 && a.mantissa == 0)) {
            makeInfinity(sign);
            return 0;
        }
        int aExp = a.exponent;
        exponent += aExp-0x40000000;
        if (exponent < 0) {
            if (aExp < 0x40000000)
                makeZero(sign); // Underflow
            else
                makeInfinity(sign); // Overflow
            return 0;
        }
        long ffffffffL = 0xffffffffL;
        long a0 = extra & ffffffffL;
        long a1 = extra >>> 32;
        long a2 = mantissa & ffffffffL;
        long a3 = mantissa >>> 32;
        long b0 = aExtra & ffffffffL;
        long b1 = aExtra >>> 32;
        long b2 = a.mantissa & ffffffffL;
        long b3 = a.mantissa >>> 32;
        a0 = ((a3*b0>>>2)+
              (a2*b1>>>2)+
              (a1*b2>>>2)+
              (a0*b3>>>2)+
              0x60000000)>>>28;
        //(a2*b0>>>34)+(a1*b1>>>34)+(a0*b2>>>34)+0x08000000)>>>28;
        a1 *= b3;
        b0 = a2*b2;
        b1 *= a3;
        a0 += ((a1<<2)&ffffffffL) + ((b0<<2)&ffffffffL) + ((b1<<2)&ffffffffL);
        a1 = (a0>>>32) + (a1>>>30) + (b0>>>30) + (b1>>>30);
        a0 &= ffffffffL;
        a2 *= b3;
        b2 *= a3;
        a1 += ((a2<<2)&ffffffffL) + ((b2<<2)&ffffffffL);
        extra = (a1<<32) + a0;
        mantissa = ((a3*b3)<<2) + (a1>>>32) + (a2>>>30) + (b2>>>30);
        extra = normalize128(extra);
        return extra;
    }
    private void mul10() {
        if (!(this.exponent >= 0 && this.mantissa != 0))
            return;
        mantissa += (mantissa+2)>>>2;
        exponent += 3;
        if (mantissa < 0) {
            mantissa = (mantissa+1)>>>1;
            exponent++;
        }
        if (exponent < 0)
            makeInfinity(sign); // Overflow
    }
    /**
     * Calculates the square of this <code>Real</code>.
     * Replaces the contents of this <code>Real</code> with the result.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <code>this = this*this;</code>
     * </td></tr><tr><td><i>Error&nbsp;bound:</i></td><td>
     *     ½ ULPs
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     1.1
     * </td></tr></table>
     */
    public void sqr() {
        sign = 0;
        if (!(this.exponent >= 0 && this.mantissa != 0))
            return;
        int e = exponent;
        exponent += exponent-0x40000000;
        if (exponent < 0) {
            if (e < 0x40000000)
                makeZero(sign); // Underflow
            else
                makeInfinity(sign); // Overflow
            return;
        }
        long a0 = mantissa&0x7fffffff;
        long a1 = mantissa>>>31;
        mantissa = a1*a1;
        // If we're going to need normalization, we don't want to round twice
        int round = (mantissa<0) ? 0 : 0x40000000;
        mantissa += ((((a0*a1)<<1) + ((a0*a0)>>>31) + round)>>>31);
        // Simplified normalize()
        if (mantissa < 0) {
            mantissa = (mantissa+1)>>>1;
            exponent ++;
            if (exponent < 0) // Overflow
                makeInfinity(sign);
        }
    }
    private static long ldiv(long a, long b) {
        // Calculate (a<<63)/b, where a<2**64, b<2**63, b<=a and a<2*b The
        // result will always be 63 bits, leading to a 3-stage radix-2**21
        // (very high radix) algorithm, as described here:
        // S.F. Oberman and M.J. Flynn, "Division Algorithms and
        // Implementations," IEEE Trans. Computers, vol. 46, no. 8,
        // pp. 833-854, Aug 1997 Section 4: "Very High Radix Algorithms"
        int bInv24; // Approximate 1/b, never more than 24 bits
        int aHi24; // High 24 bits of a (sometimes 25 bits)
        int next21; // The next 21 bits of result, possibly 1 less
        long q; // Resulting quotient: round((a<<63)/b)
        // Preparations
        bInv24 = (int)(0x400000000000L/((b>>>40)+1));
        aHi24 = (int)(a>>32)>>>8;
        a <<= 20; // aHi24 and a overlap by 4 bits
        // Now perform the division
        next21 = (int)(((long)aHi24*(long)bInv24)>>>26);
        a -= next21*b; // Bits above 2**64 will always be cancelled
        // No need to remove remainder, this will be cared for in next block
        q = next21;
        aHi24 = (int)(a>>32)>>>7;
        a <<= 21;
        // Two more almost identical blocks...
        next21 = (int)(((long)aHi24*(long)bInv24)>>>26);
        a -= next21*b;
        q = (q<<21)+next21;
        aHi24 = (int)(a>>32)>>>7;
        a <<= 21;
        next21 = (int)(((long)aHi24*(long)bInv24)>>>26);
        a -= next21*b;
        q = (q<<21)+next21;
        // Remove final remainder
        if (a<0 || a>=b) { q++; a -= b; }
        a <<= 1;
        // Round correctly
        if (a<0 || a>=b) q++;
        return q;
    }
    /**
     * Calculates the quotient of this <code>Real</code> and <code>a</code>.
     * Replaces the contents of this <code>Real</code> with the result.
     *
     * <p>(To achieve extended precision division, call
     * <code>aExtra=a.{@link #recip128(long) recip128}(aExtra)</code> before
     * calling <code>{@link #mul128(long,Real,long)
     * mul128}(extra,a,aExtra)</code>.)
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <code>this /= a;</code>
     * </td></tr><tr><td><i>Error&nbsp;bound:</i></td><td>
     *     ½ ULPs
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     2.6
     * </td></tr></table>
     *
     * @param a the <code>Real</code> to divide this with.
     */
    public void div(Real a) {
        if ((this.exponent < 0 && this.mantissa != 0) || (a.exponent < 0 && a.mantissa != 0)) {
            makeNan();
            return;
        }
        sign ^= a.sign;
        if ((this.exponent < 0 && this.mantissa == 0)) {
            if ((a.exponent < 0 && a.mantissa == 0))
                makeNan();
            return;
        }
        if ((a.exponent < 0 && a.mantissa == 0)) {
            makeZero(sign);
            return;
        }
        if ((this.exponent == 0 && this.mantissa == 0)) {
            if ((a.exponent == 0 && a.mantissa == 0))
                makeNan();
            return;
        }
        if ((a.exponent == 0 && a.mantissa == 0)) {
            makeInfinity(sign);
            return;
        }
        exponent += 0x40000000-a.exponent;
        if (mantissa < a.mantissa) {
            mantissa <<= 1;
            exponent--;
        }
        if (exponent < 0) {
            if (a.exponent >= 0x40000000)
                makeZero(sign); // Underflow
            else
                makeInfinity(sign); // Overflow
            return;
        }
        if (a.mantissa == 0x4000000000000000L)
            return;
        mantissa = ldiv(mantissa,a.mantissa);
    }
    /**
     * Calculates the quotient of this <code>Real</code> and the integer
     * <code>a</code>.
     * Replaces the contents of this <code>Real</code> with the result.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <code>this /= a;</code>
     * </td></tr><tr><td><i>Error&nbsp;bound:</i></td><td>
     *     ½ ULPs
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     2.6
     * </td></tr></table>
     *
     * @param a the <code>int</code> to divide this with.
     */
    public void div(int a) {
        if ((this.exponent < 0 && this.mantissa != 0))
            return;
        if (a<0) {
            sign ^= 1;
            a = -a;
        }
        if ((this.exponent < 0 && this.mantissa == 0))
            return;
        if ((this.exponent == 0 && this.mantissa == 0)) {
            if (a==0)
                makeNan();
            return;
        }
        if (a==0) {
            makeInfinity(sign);
            return;
        }
        long denom = a & 0xffffffffL;
        long remainder = mantissa%denom;
        mantissa /= denom;
        // Normalizing mantissa and scaling remainder accordingly
        int clz = 0;
        int t = (int)(mantissa>>>32);
        if (t == 0) { clz = 32; t = (int)mantissa; }
        t|=t>>1; t|=t>>2; t|=t>>4; t|=t>>8; t|=t>>16;
        clz += clz_tab[(t*clz_magic)>>>27]-1;
        mantissa <<= clz;
        remainder <<= clz;
        exponent -= clz;
        // Final division, correctly rounded
        remainder = (remainder+denom/2)/denom;
        mantissa += remainder;
        if (exponent < 0) // Underflow
            makeZero(sign);
    }
    /**
     * Calculates the quotient of <code>a</code> and this <code>Real</code>.
     * Replaces the contents of this <code>Real</code> with the result.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <code>this = a/this;</code>
     * </td></tr><tr><td><i>Error&nbsp;bound:</i></td><td>
     *     ½ ULPs
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     3.1
     * </td></tr></table>
     *
     * @param a the <code>Real</code> to be divided by this.
     */
    public void rdiv(Real a) {
        { recipTmp.mantissa = a.mantissa; recipTmp.exponent = a.exponent; recipTmp.sign = a.sign; };
        recipTmp.div(this);
        { this.mantissa = recipTmp.mantissa; this.exponent = recipTmp.exponent; this.sign = recipTmp.sign; };
    }
    /**
     * Calculates the quotient of the integer <code>a</code> and this
     * <code>Real</code>.
     * Replaces the contents of this <code>Real</code> with the result.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <code>this = a/this;</code>
     * </td></tr><tr><td><i>Error&nbsp;bound:</i></td><td>
     *     ½ ULPs
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     3.9
     * </td></tr></table>
     *
     * @param a the <code>int</code> to be divided by this.
     */
    public void rdiv(int a) {
        tmp0.assign(a);
        rdiv(tmp0);
    }
    /**
     * Calculates the reciprocal of this <code>Real</code>.
     * Replaces the contents of this <code>Real</code> with the result.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <code>this = 1/this;</code>
     * </td></tr><tr><td><i>Error&nbsp;bound:</i></td><td>
     *     ½ ULPs
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     2.3
     * </td></tr></table>
     */
    public void recip() {
        if ((this.exponent < 0 && this.mantissa != 0))
            return;
        if ((this.exponent < 0 && this.mantissa == 0)) {
            makeZero(sign);
            return;
        }
        if ((this.exponent == 0 && this.mantissa == 0)) {
            makeInfinity(sign);
            return;
        }
        exponent = 0x80000000-exponent;
        if (mantissa == 0x4000000000000000L) {
            if (exponent < 0)
                makeInfinity(sign); // Overflow
            return;
        }
        exponent--;
        mantissa = ldiv(0x8000000000000000L,mantissa);
    }
    /**
     * Calculates the reciprocal of this <code>Real</code> with
     * extended precision.
     * Replaces the contents of this <code>Real</code> with the result.
     * Returns the extra mantissa of the extended precision result.
     *
     * <p>An extra 64 bits of mantissa is added for extended precision.
     * If the argument is not of extended precision, use <code>0</code>
     * for the extra mantissa. See also {@link #add128(long,Real,long)}.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <code>this = 1/this;</code>
     * </td></tr><tr><td><i>Approximate&nbsp;error&nbsp;bound:</i></td><td>
     *     2<sup>-60</sup> ULPs
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     17
     * </td></tr></table>
     *
     * @param extra the extra 64 bits of mantissa of this extended precision
     *     <code>Real</code>.
     * @return the extra 64 bits of mantissa of the resulting extended
     *     precision <code>Real</code>.
     */
    public long recip128(long extra) {
        if ((this.exponent < 0 && this.mantissa != 0))
            return 0;
        if ((this.exponent < 0 && this.mantissa == 0)) {
            makeZero(sign);
            return 0;
        }
        if ((this.exponent == 0 && this.mantissa == 0)) {
            makeInfinity(sign);
            return 0;
        }
        byte s = sign;
        sign = 0;
        // Special case, simple power of 2
        if (mantissa == 0x4000000000000000L && extra == 0) {
            exponent = 0x80000000-exponent;
            if (exponent<0) // Overflow
                makeInfinity(s);
            return 0;
        }
        // Normalize exponent
        int exp = 0x40000000-exponent;
        exponent = 0x40000000;
        // Save -A
        { recipTmp.mantissa = this.mantissa; recipTmp.exponent = this.exponent; recipTmp.sign = this.sign; };
        long recipTmpExtra = extra;
        recipTmp.neg();
        // First establish approximate result (actually 63 bit accurate)
        recip();
        // Perform one Newton-Raphson iteration
        // Xn+1 = Xn + Xn*(1-A*Xn)
        { recipTmp2.mantissa = this.mantissa; recipTmp2.exponent = this.exponent; recipTmp2.sign = this.sign; };
        extra = mul128(0,recipTmp,recipTmpExtra);
        extra = add128(extra,ONE,0);
        extra = mul128(extra,recipTmp2,0);
        extra = add128(extra,recipTmp2,0);
        // Fix exponent
        scalbn(exp);
        // Fix sign
        if (!isNan())
            sign = s;
        return extra;
    }
    /**
     * Calculates the mathematical integer that is less than or equal to
     * this <code>Real</code> divided by <code>a</code>.
     * Replaces the contents of this <code>Real</code> with the result.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <code>this = Math.{@link Math#floor(double) floor}(this/a);</code>
     * </td></tr><tr><td><i>Error&nbsp;bound:</i></td><td>
     *     0 ULPs
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     22
     * </td></tr></table>
     *
     * @param a the <code>Real</code> argument.
     */
    public void divf(Real a) {
        if ((this.exponent < 0 && this.mantissa != 0) || (a.exponent < 0 && a.mantissa != 0)) {
            makeNan();
            return;
        }
        if ((this.exponent < 0 && this.mantissa == 0)) {
            if ((a.exponent < 0 && a.mantissa == 0))
                makeNan();
            return;
        }
        if ((a.exponent < 0 && a.mantissa == 0)) {
            makeZero(sign);
            return;
        }
        if ((this.exponent == 0 && this.mantissa == 0)) {
            if ((a.exponent == 0 && a.mantissa == 0))
                makeNan();
            return;
        }
        if ((a.exponent == 0 && a.mantissa == 0)) {
            makeInfinity(sign);
            return;
        }
        { tmp0.mantissa = a.mantissa; tmp0.exponent = a.exponent; tmp0.sign = a.sign; }; // tmp0 should be free
        // Perform same division as with mod, and don't round up
        long extra = tmp0.recip128(0);
        extra = mul128(0,tmp0,extra);
        if (((tmp0.sign!=0) && (extra < 0 || extra > 0x1f)) ||
            (!(tmp0.sign!=0) && extra < 0 && extra > 0xffffffe0))
        {
            // For accurate floor()
            mantissa++;
            normalize();
        }
        floor();
    }
    private void modInternal(/*long thisExtra,*/ Real a, long aExtra) {
        { tmp0.mantissa = a.mantissa; tmp0.exponent = a.exponent; tmp0.sign = a.sign; }; // tmp0 should be free
        long extra = tmp0.recip128(aExtra);
        extra = tmp0.mul128(extra,this,0/*thisExtra*/); // tmp0 == this/a
        if (tmp0.exponent > 0x4000003e) {
            // floor() will be inaccurate
            makeZero(a.sign); // What else can be done? makeNan?
            return;
        }
        if (((tmp0.sign!=0) && (extra < 0 || extra > 0x1f)) ||
            (!(tmp0.sign!=0) && extra < 0 && extra > 0xffffffe0))
        {
            // For accurate floor() with a bit of "magical rounding"
            tmp0.mantissa++;
            tmp0.normalize();
        }
        tmp0.floor();
        tmp0.neg(); // tmp0 == -floor(this/a)
        extra = tmp0.mul128(0,a,aExtra);
        extra = add128(0/*thisExtra*/,tmp0,extra);
        roundFrom128(extra);
    }
    /**
     * Calculates the value of this <code>Real</code> modulo <code>a</code>.
     * Replaces the contents of this <code>Real</code> with the result.
     * The modulo in this case is defined as the remainder after subtracting
     * <code>a</code> multiplied by the mathematical integer that is less than
     * or equal to this <code>Real</code> divided by <code>a</code>.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <code>this = this -
     *           a*Math.{@link Math#floor(double) floor}(this/a);</code>
     * </td></tr><tr><td><i>Error&nbsp;bound:</i></td><td>
     *     0 ULPs
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     27
     * </td></tr></table>
     *
     * @param a the <code>Real</code> argument.
     */
    public void mod(Real a) {
        if ((this.exponent < 0 && this.mantissa != 0) || (a.exponent < 0 && a.mantissa != 0)) {
            makeNan();
            return;
        }
        if ((this.exponent < 0 && this.mantissa == 0)) {
            makeNan();
            return;
        }
        if ((this.exponent == 0 && this.mantissa == 0)) {
            if ((a.exponent == 0 && a.mantissa == 0))
                makeNan();
            else
                sign = a.sign;
            return;
        }
        if ((a.exponent < 0 && a.mantissa == 0)) {
            if (sign != a.sign)
                makeInfinity(a.sign);
            return;
        }
        if ((a.exponent == 0 && a.mantissa == 0)) {
            makeZero(a.sign);
            return;
        }
        modInternal(a,0);
    }
    /**
     * Calculates the logical <i>AND</i> of this <code>Real</code> and
     * <code>a</code>.
     * Replaces the contents of this <code>Real</code> with the result.
     *
     * <p>Semantics of bitwise logical operations exactly mimic those of
     * Java's bitwise integer operators. In these operations, the
     * internal binary representation of the numbers are used. If the
     * values represented by the operands are not mathematical
     * integers, the fractional bits are also included in the operation.
     *
     * <p>Negative numbers are interpreted as two's-complement,
     * generalized to real numbers: Negating the number inverts all
     * bits, including an infinite number of 1-bits before the radix
     * point and an infinite number of 1-bits after the radix point. The
     * infinite number of 1-bits after the radix is rounded upwards
     * producing an infinite number of 0-bits, until the first 0-bit is
     * encountered which will be switched to a 1 (rounded or not, these
     * two forms are mathematically equivalent). For example, the number
     * "1" negated, becomes (in binary form)
     * <code>...1111110.111111....</code> Rounding of the infinite
     * number of 1's after the radix gives the number
     * <code>...1111111.000000...</code>, which is exactly the way we
     * usually see "-1" as two's-complement.
     *
     * <p>This method calculates a negative value if and only
     * if this and <code>a</code> are both negative.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>int</code><i>&nbsp;code:</i></td><td>
     *     <code>this &= a;</code>
     * </td></tr><tr><td><i>Error&nbsp;bound:</i></td><td>
     *     0 ULPs
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     1.5
     * </td></tr></table>
     *
     * @param a the <code>Real</code> argument
     */
    public void and(Real a) {
        if ((this.exponent < 0 && this.mantissa != 0) || (a.exponent < 0 && a.mantissa != 0)) {
            makeNan();
            return;
        }
        if ((this.exponent == 0 && this.mantissa == 0) || (a.exponent == 0 && a.mantissa == 0)) {
            makeZero();
            return;
        }
        if ((this.exponent < 0 && this.mantissa == 0) || (a.exponent < 0 && a.mantissa == 0)) {
            if (!(this.exponent < 0 && this.mantissa == 0) && (this.sign!=0)) {
                { this.mantissa = a.mantissa; this.exponent = a.exponent; this.sign = a.sign; };
            } else if (!(a.exponent < 0 && a.mantissa == 0) && (a.sign!=0))
                ; // ASSIGN(this,this)
            else if ((this.exponent < 0 && this.mantissa == 0) && (a.exponent < 0 && a.mantissa == 0) &&
                     (this.sign!=0) && (a.sign!=0))
                ; // makeInfinity(1)
            else
                makeZero();
            return;
        }
        byte s;
        int e;
        long m;
        if (exponent >= a.exponent) {
            s = a.sign;
            e = a.exponent;
            m = a.mantissa;
        } else {
            s = sign;
            e = exponent;
            m = mantissa;
            sign = a.sign;
            exponent = a.exponent;
            mantissa = a.mantissa;
        }
        int shift = exponent-e;
        if (shift>=64) {
            if (s == 0)
                makeZero(sign);
            return;
        }
        if (s != 0)
            m = -m;
        if ((this.sign!=0))
            mantissa = -mantissa;
        mantissa &= m>>shift;
        sign = 0;
        if (mantissa < 0) {
            mantissa = -mantissa;
            sign = 1;
        }
        normalize();
    }
    /**
     * Calculates the logical <i>OR</i> of this <code>Real</code> and
     * <code>a</code>.
     * Replaces the contents of this <code>Real</code> with the result.
     *
     * <p>See {@link #and(Real)} for an explanation of the
     * interpretation of a <code>Real</code> in bitwise operations.
     * This method calculates a negative value if and only
     * if either this or <code>a</code> is negative.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>int</code><i>&nbsp;code:</i></td><td>
     *     <code>this |= a;</code>
     * </td></tr><tr><td><i>Error&nbsp;bound:</i></td><td>
     *     0 ULPs
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     1.6
     * </td></tr></table>
     *
     * @param a the <code>Real</code> argument
     */
    public void or(Real a) {
        if ((this.exponent < 0 && this.mantissa != 0) || (a.exponent < 0 && a.mantissa != 0)) {
            makeNan();
            return;
        }
        if ((this.exponent == 0 && this.mantissa == 0) || (a.exponent == 0 && a.mantissa == 0)) {
            if ((this.exponent == 0 && this.mantissa == 0))
                { this.mantissa = a.mantissa; this.exponent = a.exponent; this.sign = a.sign; };
            return;
        }
        if ((this.exponent < 0 && this.mantissa == 0) || (a.exponent < 0 && a.mantissa == 0)) {
            if (!(this.exponent < 0 && this.mantissa == 0) && (this.sign!=0))
                ; // ASSIGN(this,this);
            else if (!(a.exponent < 0 && a.mantissa == 0) && (a.sign!=0)) {
                { this.mantissa = a.mantissa; this.exponent = a.exponent; this.sign = a.sign; };
            } else
                makeInfinity(sign | a.sign);
            return;
        }
        byte s;
        int e;
        long m;
        if (((this.sign!=0) && exponent <= a.exponent) ||
            ((a.sign==0) && exponent >= a.exponent))
        {
            s = a.sign;
            e = a.exponent;
            m = a.mantissa;
        } else {
            s = sign;
            e = exponent;
            m = mantissa;
            sign = a.sign;
            exponent = a.exponent;
            mantissa = a.mantissa;
        }
        int shift = exponent-e;
        if (shift>=64 || shift<=-64)
            return;
        if (s != 0)
            m = -m;
        if ((this.sign!=0))
            mantissa = -mantissa;
        if (shift>=0)
            mantissa |= m>>shift;
        else
            mantissa |= m<<(-shift);
        sign = 0;
        if (mantissa < 0) {
            mantissa = -mantissa;
            sign = 1;
        }
        normalize();
    }
    /**
     * Calculates the logical <i>XOR</i> of this <code>Real</code> and
     * <code>a</code>.
     * Replaces the contents of this <code>Real</code> with the result.
     *
     * <p>See {@link #and(Real)} for an explanation of the
     * interpretation of a <code>Real</code> in bitwise operations.
     * This method calculates a negative value if and only
     * if exactly one of this and <code>a</code> is negative.
     *
     * <p>The operation <i>NOT</i> has been omitted in this library
     * because it cannot be generalized to fractional numbers. If this
     * <code>Real</code> represents a mathematical integer, the
     * operation <i>NOT</i> can be calculated as "this <i>XOR</i> -1",
     * which is equivalent to "this <i>XOR</i>
     * <code>/FFFFFFFF.0000</code>".
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>int</code><i>&nbsp;code:</i></td><td>
     *     <code>this ^= a;</code>
     * </td></tr><tr><td><i>Error&nbsp;bound:</i></td><td>
     *     0 ULPs
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     1.5
     * </td></tr></table>
     *
     * @param a the <code>Real</code> argument
     */
    public void xor(Real a) {
        if ((this.exponent < 0 && this.mantissa != 0) || (a.exponent < 0 && a.mantissa != 0)) {
            makeNan();
            return;
        }
        if ((this.exponent == 0 && this.mantissa == 0) || (a.exponent == 0 && a.mantissa == 0)) {
            if ((this.exponent == 0 && this.mantissa == 0))
                { this.mantissa = a.mantissa; this.exponent = a.exponent; this.sign = a.sign; };
            return;
        }
        if ((this.exponent < 0 && this.mantissa == 0) || (a.exponent < 0 && a.mantissa == 0)) {
            makeInfinity(sign ^ a.sign);
            return;
        }
        byte s;
        int e;
        long m;
        if (exponent >= a.exponent) {
            s = a.sign;
            e = a.exponent;
            m = a.mantissa;
        } else {
            s = sign;
            e = exponent;
            m = mantissa;
            sign = a.sign;
            exponent = a.exponent;
            mantissa = a.mantissa;
        }
        int shift = exponent-e;
        if (shift>=64)
            return;
        if (s != 0)
            m = -m;
        if ((this.sign!=0))
            mantissa = -mantissa;
        mantissa ^= m>>shift;
        sign = 0;
        if (mantissa < 0) {
            mantissa = -mantissa;
            sign = 1;
        }
        normalize();
    }
    /**
     * Calculates the value of this <code>Real</code> <i>AND NOT</i>
     * <code>a</code>. The opeation is read as "bit clear".
     * Replaces the contents of this <code>Real</code> with the result.
     *
     * <p>See {@link #and(Real)} for an explanation of the
     * interpretation of a <code>Real</code> in bitwise operations.
     * This method calculates a negative value if and only
     * if this is negative and not <code>a</code> is negative.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>int</code><i>&nbsp;code:</i></td><td>
     *     <code>this &= ~a;</code>
     * </td></tr><tr><td><i>Error&nbsp;bound:</i></td><td>
     *     0 ULPs
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     1.5
     * </td></tr></table>
     *
     * @param a the <code>Real</code> argument
     */
    public void bic(Real a) {
        if ((this.exponent < 0 && this.mantissa != 0) || (a.exponent < 0 && a.mantissa != 0)) {
            makeNan();
            return;
        }
        if ((this.exponent == 0 && this.mantissa == 0) || (a.exponent == 0 && a.mantissa == 0))
            return;
        if ((this.exponent < 0 && this.mantissa == 0) || (a.exponent < 0 && a.mantissa == 0)) {
            if (!(this.exponent < 0 && this.mantissa == 0)) {
                if ((this.sign!=0))
                    if ((a.sign!=0))
                        makeInfinity(0);
                    else
                        makeInfinity(1);
            } else if ((a.sign!=0)) {
                if ((a.exponent < 0 && a.mantissa == 0))
                    makeInfinity(0);
                else
                    makeZero();
            }
            return;
        }
        int shift = exponent-a.exponent;
        if (shift>=64 || (shift<=-64 && (this.sign==0)))
            return;
        long m = a.mantissa;
        if ((a.sign!=0))
            m = -m;
        if ((this.sign!=0))
            mantissa = -mantissa;
        if (shift<0) {
            if ((this.sign!=0)) {
                if (shift<=-64)
                    mantissa = ~m;
                else
                    mantissa = (mantissa>>(-shift)) & ~m;
                exponent = a.exponent;
            } else
                mantissa &= ~(m<<(-shift));
        } else
            mantissa &= ~(m>>shift);
        sign = 0;
        if (mantissa < 0) {
            mantissa = -mantissa;
            sign = 1;
        }
        normalize();
    }
    private int compare(int a) {
        tmp0.assign(a);
        return compare(tmp0);
    }
    /**
     * Calculates the square root of this <code>Real</code>.
     * Replaces the contents of this <code>Real</code> with the result.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <code>this = Math.{@link Math#sqrt(double) sqrt}(this);</code>
     * </td></tr><tr><td><i>Approximate&nbsp;error&nbsp;bound:</i></td><td>
     *     1 ULPs
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     19
     * </td></tr></table>
     */
    public void sqrt() {
        /*
         * Adapted from:
         * Cephes Math Library Release 2.2:  December, 1990
         * Copyright 1984, 1990 by Stephen L. Moshier
         *
         * sqrtl.c
         *
         * long double sqrtl(long double x);
         */
        if ((this.exponent < 0 && this.mantissa != 0))
            return;
        if ((this.exponent == 0 && this.mantissa == 0)) {
            sign=0;
            return;
        }
        if ((this.sign!=0)) {
            makeNan();
            return;
        }
        if ((this.exponent < 0 && this.mantissa == 0))
            return;
        // Save X
        { recipTmp.mantissa = this.mantissa; recipTmp.exponent = this.exponent; recipTmp.sign = this.sign; };
        // normalize to range [0.5, 1)
        int e = exponent-0x3fffffff;
        exponent = 0x3fffffff;
        // quadratic approximation, relative error 6.45e-4
        { recipTmp2.mantissa = this.mantissa; recipTmp2.exponent = this.exponent; recipTmp2.sign = this.sign; };
        { sqrtTmp.sign=(byte)1; sqrtTmp.exponent=0x3ffffffd; sqrtTmp.mantissa=0x68a7e193370ff21bL; };//-0.2044058315473477195990
        mul(sqrtTmp);
        { sqrtTmp.sign=(byte)0; sqrtTmp.exponent=0x3fffffff; sqrtTmp.mantissa=0x71f1e120690deae8L; };//0.89019407351052789754347
        add(sqrtTmp);
        mul(recipTmp2);
        { sqrtTmp.sign=(byte)0; sqrtTmp.exponent=0x3ffffffe; sqrtTmp.mantissa=0x5045ee6baf28677aL; };//0.31356706742295303132394
        add(sqrtTmp);
        // adjust for odd powers of 2
        if ((e&1) != 0)
            mul(SQRT2);
        // calculate exponent
        exponent += e>>1;
        // Newton iteratios:
        //   Yn+1 = (Yn + X/Yn)/2
        for (int i=0; i<3; i++) {
            { recipTmp2.mantissa = recipTmp.mantissa; recipTmp2.exponent = recipTmp.exponent; recipTmp2.sign = recipTmp.sign; };
            recipTmp2.div(this);
            add(recipTmp2);
            scalbn(-1);
        }
    }
    /**
     * Calculates the reciprocal square root of this <code>Real</code>.
     * Replaces the contents of this <code>Real</code> with the result.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <code>this = 1/Math.{@link Math#sqrt(double) sqrt}(this);</code>
     * </td></tr><tr><td><i>Approximate&nbsp;error&nbsp;bound:</i></td><td>
     *     1 ULPs
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     21
     * </td></tr></table>
     */
    public void rsqrt() {
        sqrt();
        recip();
    }
    /**
     * Calculates the cube root of this <code>Real</code>.
     * Replaces the contents of this <code>Real</code> with the result.
     * The cube root of a negative value is the negative of the cube
     * root of that value's magnitude.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <code>this = Math.{@link Math#cbrt(double) cbrt}(this);</code>
     * </td></tr><tr><td><i>Approximate&nbsp;error&nbsp;bound:</i></td><td>
     *     2 ULPs
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     32
     * </td></tr></table>
     */
    public void cbrt() {
        if (!(this.exponent >= 0 && this.mantissa != 0))
            return;
        byte s = sign;
        sign = 0;
        // Calculates recipocal cube root of normalized Real,
        // not zero, nan or infinity
        final long start = 0x5120000000000000L;
        // Save -A
        { recipTmp.mantissa = this.mantissa; recipTmp.exponent = this.exponent; recipTmp.sign = this.sign; };
        recipTmp.neg();
        // First establish approximate result
        mantissa = start-(mantissa>>>2);
        int expRmd = exponent==0 ? 2 : (exponent-1)%3;
        exponent = 0x40000000-(exponent-0x40000000-expRmd)/3;
        normalize();
        if (expRmd>0) {
            { recipTmp2.sign=(byte)0; recipTmp2.exponent=0x3fffffff; recipTmp2.mantissa=0x6597fa94f5b8f20bL; }; // cbrt(1/2)
            mul(recipTmp2);
            if (expRmd>1)
                mul(recipTmp2);
        }
        // Now perform Newton-Raphson iteration
        // Xn+1 = (4*Xn - A*Xn**4)/3
        for (int i=0; i<4; i++) {
            { recipTmp2.mantissa = this.mantissa; recipTmp2.exponent = this.exponent; recipTmp2.sign = this.sign; };
            sqr();
            sqr();
            mul(recipTmp);
            recipTmp2.scalbn(2);
            add(recipTmp2);
            mul(THIRD);
        }
        recip();
        if (!(this.exponent < 0 && this.mantissa != 0))
            sign = s;
    }
    /**
     * Calculates the n'th root of this <code>Real</code>.
     * Replaces the contents of this <code>Real</code> with the result.
     * For odd integer n, the n'th root of a negative value is the
     * negative of the n'th root of that value's magnitude.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <code>this = Math.{@link Math#pow(double,double)
     *           pow}(this,1/a);</code>
     * </td></tr><tr><td><i>Approximate&nbsp;error&nbsp;bound:</i></td><td>
     *     2 ULPs
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     110
     * </td></tr></table>
     *
     * @param n the <code>Real</code> argument.
     */
    public void nroot(Real n) {
        if ((n.exponent < 0 && n.mantissa != 0)) {
            makeNan();
            return;
        }
        if (n.compare(THREE)==0) {
            cbrt(); // Most probable application of nroot...
            return;
        } else if (n.compare(TWO)==0) {
            sqrt(); // Also possible, should be optimized like this
            return;
        }
        boolean negative = false;
        if ((this.sign!=0) && n.isIntegral() && n.isOdd()) {
            negative = true;
            abs();
        }
        { tmp2.mantissa = n.mantissa; tmp2.exponent = n.exponent; tmp2.sign = n.sign; }; // Copy to temporary location in case of x.nroot(x)
        tmp2.recip();
        pow(tmp2);
        if (negative)
            neg();
    }
    /**
     * Calculates <code>sqrt(this*this+a*a)</code>.
     * Replaces the contents of this <code>Real</code> with the result.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <code>this = Math.{@link Math#hypot(double,double)
     *           hypot}(this,a);</code>
     * </td></tr><tr><td><i>Approximate&nbsp;error&nbsp;bound:</i></td><td>
     *     1 ULPs
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     24
     * </td></tr></table>
     *
     * @param a the <code>Real</code> argument.
     */
    public void hypot(Real a) {
        { tmp1.mantissa = a.mantissa; tmp1.exponent = a.exponent; tmp1.sign = a.sign; }; // Copy to temporary location in case of x.hypot(x)
        tmp1.sqr();
        sqr();
        add(tmp1);
        sqrt();
    }
    private void exp2Internal(long extra) {
        if ((this.exponent < 0 && this.mantissa != 0))
            return;
        if ((this.exponent < 0 && this.mantissa == 0)) {
            if ((this.sign!=0))
                makeZero(0);
            return;
        }
        if ((this.exponent == 0 && this.mantissa == 0)) {
            { this.mantissa = ONE.mantissa; this.exponent = ONE.exponent; this.sign = ONE.sign; };
            return;
        }
        // Extract integer part
        { expTmp.mantissa = this.mantissa; expTmp.exponent = this.exponent; expTmp.sign = this.sign; };
        expTmp.add(HALF);
        expTmp.floor();
        int exp = expTmp.toInteger();
        if (exp > 0x40000000) {
            makeInfinity(sign);
            return;
        }
        if (exp < -0x40000000) {
            makeZero(sign);
            return;
        }
        // Subtract integer part (this is where we need the extra accuracy)
        expTmp.neg();
        add128(extra,expTmp,0);
        /*
         * Adapted from:
         * Cephes Math Library Release 2.7:  May, 1998
         * Copyright 1984, 1991, 1998 by Stephen L. Moshier
         *
         * exp2l.c
         *
         * long double exp2l(long double x);
         */
        // Now -0.5<X<0.5
        // rational approximation
        // exp2(x) = 1 + 2x P(x²)/(Q(x²) - x P(x²))
        { expTmp2.mantissa = this.mantissa; expTmp2.exponent = this.exponent; expTmp2.sign = this.sign; };
        expTmp2.sqr();
        // P(x²)
        { expTmp.sign=(byte)0; expTmp.exponent=0x40000005; expTmp.mantissa=0x793ace15b56b7fecL; };//60.614853552242266094567
        expTmp.mul(expTmp2);
        { expTmp3.sign=(byte)0; expTmp3.exponent=0x4000000e; expTmp3.mantissa=0x764ef8cf96e29a13L; };//30286.971917562792508623
        expTmp.add(expTmp3);
        expTmp.mul(expTmp2);
        { expTmp3.sign=(byte)0; expTmp3.exponent=0x40000014; expTmp3.mantissa=0x7efa0173e820bf60L; };//2080384.3631901852422887
        expTmp.add(expTmp3);
        mul(expTmp);
        // Q(x²)
        expTmp.assign(expTmp2);
        { expTmp3.sign=(byte)0; expTmp3.exponent=0x4000000a; expTmp3.mantissa=0x6d549a6b4dc9abadL; };//1749.2876999891839021063
        expTmp.add(expTmp3);
        expTmp.mul(expTmp2);
        { expTmp3.sign=(byte)0; expTmp3.exponent=0x40000012; expTmp3.mantissa=0x5002d27836ba71c6L; };//327725.15434906797273099
        expTmp.add(expTmp3);
        expTmp.mul(expTmp2);
        { expTmp3.sign=(byte)0; expTmp3.exponent=0x40000016; expTmp3.mantissa=0x5b98206867dd59bfL; };//6002720.4078348487957118
        expTmp.add(expTmp3);
        expTmp.sub(this);
        div(expTmp);
        scalbn(1);
        add(ONE);
        // Scale by power of 2
        scalbn(exp);
    }
    /**
     * Calculates <i>e</i> raised to the power of this <code>Real</code>.
     * Replaces the contents of this <code>Real</code> with the result.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <code>this = Math.{@link Math#exp(double) exp}(this);</code>
     * </td></tr><tr><td><i>Approximate&nbsp;error&nbsp;bound:</i></td><td>
     *     1 ULPs
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     31
     * </td></tr></table>
     */
    public void exp() {
        { expTmp.sign=(byte)0; expTmp.exponent=0x40000000; expTmp.mantissa=0x5c551d94ae0bf85dL; }; // log2(e)
        long extra = mul128(0,expTmp,0xdf43ff68348e9f44L);
        exp2Internal(extra);
    }
    /**
     * Calculates 2 raised to the power of this <code>Real</code>.
     * Replaces the contents of this <code>Real</code> with the result.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <code>this = Math.{@link Math#exp(double) exp}(this *
     *           Math.{@link Math#log(double) log}(2));</code>
     * </td></tr><tr><td><i>Approximate&nbsp;error&nbsp;bound:</i></td><td>
     *     1 ULPs
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     27
     * </td></tr></table>
     */
    public void exp2() {
        exp2Internal(0);
    }
    /**
     * Calculates 10 raised to the power of this <code>Real</code>.
     * Replaces the contents of this <code>Real</code> with the result.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <code>this = Math.{@link Math#exp(double) exp}(this *
     *           Math.{@link Math#log(double) log}(10));</code>
     * </td></tr><tr><td><i>Approximate&nbsp;error&nbsp;bound:</i></td><td>
     *     1 ULPs
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     31
     * </td></tr></table>
     */
    public void exp10() {
        { expTmp.sign=(byte)0; expTmp.exponent=0x40000001; expTmp.mantissa=0x6a4d3c25e68dc57fL; }; // log2(10)
        long extra = mul128(0,expTmp,0x2495fb7fa6d7eda6L);
        exp2Internal(extra);
    }
    private int lnInternal()
    {
        if ((this.exponent < 0 && this.mantissa != 0))
            return 0;
        if ((this.sign!=0)) {
            makeNan();
            return 0;
        }
        if ((this.exponent == 0 && this.mantissa == 0)) {
            makeInfinity(1);
            return 0;
        }
        if ((this.exponent < 0 && this.mantissa == 0))
            return 0;
        /*
         * Adapted from:
         * Cephes Math Library Release 2.7:  May, 1998
         * Copyright 1984, 1990, 1998 by Stephen L. Moshier
         *
         * logl.c
         *
         * long double logl(long double x);
         */
        // normalize to range [0.5, 1)
        int e = exponent-0x3fffffff;
        exponent = 0x3fffffff;
        // rational appriximation
        // log(1+x) = x - x²/2 + x³ P(x)/Q(x)
        if (this.compare(SQRT1_2) < 0) {
            e--;
            exponent++;
        }
        sub(ONE);
        { expTmp2.mantissa = this.mantissa; expTmp2.exponent = this.exponent; expTmp2.sign = this.sign; };
        // P(x)
        { this.sign=(byte)0; this.exponent=0x3ffffff1; this.mantissa=0x5ef0258ace5728ddL; };//4.5270000862445199635215E-5
        mul(expTmp2);
        { expTmp3.sign=(byte)0; expTmp3.exponent=0x3ffffffe; expTmp3.mantissa=0x7fa06283f86a0ce8L; };//0.4985410282319337597221
        add(expTmp3);
        mul(expTmp2);
        { expTmp3.sign=(byte)0; expTmp3.exponent=0x40000002; expTmp3.mantissa=0x69427d1bd3e94ca1L; };//6.5787325942061044846969
        add(expTmp3);
        mul(expTmp2);
        { expTmp3.sign=(byte)0; expTmp3.exponent=0x40000004; expTmp3.mantissa=0x77a5ce2e32e7256eL; };//29.911919328553073277375
        add(expTmp3);
        mul(expTmp2);
        { expTmp3.sign=(byte)0; expTmp3.exponent=0x40000005; expTmp3.mantissa=0x79e63ae1b0cd4222L; };//60.949667980987787057556
        add(expTmp3);
        mul(expTmp2);
        { expTmp3.sign=(byte)0; expTmp3.exponent=0x40000005; expTmp3.mantissa=0x7239d65d1e6840d6L; };//57.112963590585538103336
        add(expTmp3);
        mul(expTmp2);
        { expTmp3.sign=(byte)0; expTmp3.exponent=0x40000004; expTmp3.mantissa=0x502880b6660c265fL; };//20.039553499201281259648
        add(expTmp3);
        // Q(x)
        { expTmp.mantissa = expTmp2.mantissa; expTmp.exponent = expTmp2.exponent; expTmp.sign = expTmp2.sign; };
        { expTmp3.sign=(byte)0; expTmp3.exponent=0x40000003; expTmp3.mantissa=0x7880d67a40f8dc5cL; };//15.062909083469192043167
        expTmp.add(expTmp3);
        expTmp.mul(expTmp2);
        { expTmp3.sign=(byte)0; expTmp3.exponent=0x40000006; expTmp3.mantissa=0x530c2d4884d25e18L; };//83.047565967967209469434
        expTmp.add(expTmp3);
        expTmp.mul(expTmp2);
        { expTmp3.sign=(byte)0; expTmp3.exponent=0x40000007; expTmp3.mantissa=0x6ee19643f3ed5776L; };//221.76239823732856465394
        expTmp.add(expTmp3);
        expTmp.mul(expTmp2);
        { expTmp3.sign=(byte)0; expTmp3.exponent=0x40000008; expTmp3.mantissa=0x4d465177242295efL; };//309.09872225312059774938
        expTmp.add(expTmp3);
        expTmp.mul(expTmp2);
        { expTmp3.sign=(byte)0; expTmp3.exponent=0x40000007; expTmp3.mantissa=0x6c36c4f923819890L; };//216.42788614495947685003
        expTmp.add(expTmp3);
        expTmp.mul(expTmp2);
        { expTmp3.sign=(byte)0; expTmp3.exponent=0x40000005; expTmp3.mantissa=0x783cc111991239a3L; };//60.118660497603843919306
        expTmp.add(expTmp3);
        div(expTmp);
        { expTmp3.mantissa = expTmp2.mantissa; expTmp3.exponent = expTmp2.exponent; expTmp3.sign = expTmp2.sign; };
        expTmp3.sqr();
        mul(expTmp3);
        mul(expTmp2);
        expTmp3.scalbn(-1);
        sub(expTmp3);
        add(expTmp2);
        return e;
    }
    /**
     * Calculates the natural logarithm (base-<i>e</i>) of this
     * <code>Real</code>.
     * Replaces the contents of this <code>Real</code> with the result.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <code>this = Math.{@link Math#log(double) log}(this);</code>
     * </td></tr><tr><td><i>Approximate&nbsp;error&nbsp;bound:</i></td><td>
     *     2 ULPs
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     51
     * </td></tr></table>
     */
    public void ln() {
        int exp = lnInternal();
        expTmp.assign(exp);
        expTmp.mul(LN2);
        add(expTmp);
    }
    /**
     * Calculates the base-2 logarithm of this <code>Real</code>.
     * Replaces the contents of this <code>Real</code> with the result.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <code>this = Math.{@link Math#log(double) log}(this)/Math.{@link
     *           Math#log(double) log}(2);</code>
     * </td></tr><tr><td><i>Approximate&nbsp;error&nbsp;bound:</i></td><td>
     *     1 ULPs
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     51
     * </td></tr></table>
     */
    public void log2() {
        int exp = lnInternal();
        mul(LOG2E);
        add(exp);
    }
    /**
     * Calculates the base-10 logarithm of this <code>Real</code>.
     * Replaces the contents of this <code>Real</code> with the result.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <code>this = Math.{@link Math#log10(double) log10}(this);</code>
     * </td></tr><tr><td><i>Approximate&nbsp;error&nbsp;bound:</i></td><td>
     *     2 ULPs
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     53
     * </td></tr></table>
     */
    public void log10() {
        int exp = lnInternal();
        expTmp.assign(exp);
        expTmp.mul(LN2);
        add(expTmp);
        mul(LOG10E);
    }
    /**
     * Calculates the closest power of 10 that is less than or equal to this
     * <code>Real</code>.
     * Replaces the contents of this <code>Real</code> with the result.
     * The base-10 exponent of the result is returned.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <code>int exp = (int)(Math.{@link Math#floor(double)
     *       floor}(Math.{@link Math#log10(double) log10}(this)));
     *       <br>this = Math.{@link Math#pow(double,double) pow}(10, exp);<br>
     *           return exp;</code>
     * </td></tr><tr><td><i>Error&nbsp;bound:</i></td><td>
     *     ½ ULPs
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     3.6
     * </td></tr></table>
     *
     * @return the base-10 exponent
     */
    public int lowPow10() {
        if (!(this.exponent >= 0 && this.mantissa != 0))
            return 0;
        { tmp2.mantissa = this.mantissa; tmp2.exponent = this.exponent; tmp2.sign = this.sign; };
        // Approximate log10 using exponent only
        int e = exponent - 0x40000000;
        if (e<0) // it's important to achieve floor(exponent*ln2/ln10)
            e = -(int)(((-e)*0x4d104d43L+((1L<<32)-1)) >> 32);
        else
            e = (int)(e*0x4d104d43L >> 32);
        // Now, e < log10(this) < e+1
        { this.mantissa = TEN.mantissa; this.exponent = TEN.exponent; this.sign = TEN.sign; };
        pow(e);
        if ((this.exponent == 0 && this.mantissa == 0)) { // A *really* small number, then
            { tmp3.mantissa = TEN.mantissa; tmp3.exponent = TEN.exponent; tmp3.sign = TEN.sign; };
            tmp3.pow(e+1);
        } else {
            { tmp3.mantissa = this.mantissa; tmp3.exponent = this.exponent; tmp3.sign = this.sign; };
            tmp3.mul10();
        }
        if (tmp3.compare(tmp2) <= 0) {
            // First estimate of log10 was too low
            e++;
            { this.mantissa = tmp3.mantissa; this.exponent = tmp3.exponent; this.sign = tmp3.sign; };
        }
        return e;
    }
    /**
     * Calculates the value of this <code>Real</code> raised to the power of
     * <code>a</code>.
     * Replaces the contents of this <code>Real</code> with the result.
     *
     * <p> Special cases:
     * <ul>
     * <li> if a is 0.0 or -0.0 then result is 1.0
     * <li> if a is NaN then result is NaN
     * <li> if this is NaN and a is not zero then result is NaN
     * <li> if a is 1.0 then result is this
     * <li> if |this| > 1.0 and a is +Infinity then result is +Infinity
     * <li> if |this| < 1.0 and a is -Infinity then result is +Infinity
     * <li> if |this| > 1.0 and a is -Infinity then result is +0
     * <li> if |this| < 1.0 and a is +Infinity then result is +0
     * <li> if |this| = 1.0 and a is ±Infinity then result is NaN
     * <li> if this = +0 and a > 0 then result is +0
     * <li> if this = +0 and a < 0 then result is +Inf
     * <li> if this = -0 and a > 0, and odd integer then result is -0
     * <li> if this = -0 and a < 0, and odd integer then result is -Inf
     * <li> if this = -0 and a > 0, not odd integer then result is +0
     * <li> if this = -0 and a < 0, not odd integer then result is +Inf
     * <li> if this = +Inf and a > 0 then result is +Inf
     * <li> if this = +Inf and a < 0 then result is +0
     * <li> if this = -Inf and a not integer then result is NaN
     * <li> if this = -Inf and a > 0, and odd integer then result is -Inf
     * <li> if this = -Inf and a > 0, not odd integer then result is +Inf
     * <li> if this = -Inf and a < 0, and odd integer then result is -0
     * <li> if this = -Inf and a < 0, not odd integer then result is +0
     * <li> if this < 0 and a not integer then result is NaN
     * <li> if this < 0 and a odd integer then result is -(|this|<sup>a</sup>)
     * <li> if this < 0 and a not odd integer then result is |this|<sup>a</sup>
     * <li> else result is exp(ln(this)*a)
     * </ul>
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *   <code>this = Math.{@link Math#pow(double,double) pow}(this, a);</code>
     * </td></tr><tr><td><i>Approximate&nbsp;error&nbsp;bound:</i></td><td>
     *     2 ULPs
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     110
     * </td></tr></table>
     *
     * @param a the <code>Real</code> argument.
     */
    public void pow(Real a) {
        if ((a.exponent == 0 && a.mantissa == 0)) {
            { this.mantissa = ONE.mantissa; this.exponent = ONE.exponent; this.sign = ONE.sign; };
            return;
        }
        if ((this.exponent < 0 && this.mantissa != 0) || (a.exponent < 0 && a.mantissa != 0)) {
            makeNan();
            return;
        }
        if (a.compare(ONE)==0)
            return;
        if ((a.exponent < 0 && a.mantissa == 0)) {
            { tmp1.mantissa = this.mantissa; tmp1.exponent = this.exponent; tmp1.sign = this.sign; };
            tmp1.abs();
            int test = tmp1.compare(ONE);
            if (test>0) {
                if ((a.sign==0))
                    makeInfinity(0);
                else
                    makeZero();
            } else if (test<0) {
                if ((a.sign!=0))
                    makeInfinity(0);
                else
                    makeZero();
            } else {
                makeNan();
            }
            return;
        }
        if ((this.exponent == 0 && this.mantissa == 0)) {
            if ((this.sign==0)) {
                if ((a.sign==0))
                    makeZero();
                else
                    makeInfinity(0);
            } else {
                if (a.isIntegral() && a.isOdd()) {
                    if ((a.sign==0))
                        makeZero(1);
                    else
                        makeInfinity(1);
                } else {
                    if ((a.sign==0))
                        makeZero();
                    else
                        makeInfinity(0);
                }
            }
            return;
        }
        if ((this.exponent < 0 && this.mantissa == 0)) {
            if ((this.sign==0)) {
                if ((a.sign==0))
                    makeInfinity(0);
                else
                    makeZero();
            } else {
                if (a.isIntegral()) {
                    if (a.isOdd()) {
                        if ((a.sign==0))
                            makeInfinity(1);
                        else
                            makeZero(1);
                    } else {
                        if ((a.sign==0))
                            makeInfinity(0);
                        else
                            makeZero();
                    }
                } else {
                    makeNan();
                }
            }
            return;
        }
        if (a.isIntegral() && a.exponent <= 0x4000001e) {
            pow(a.toInteger());
            return;
        }
        byte s=0;
        if ((this.sign!=0)) {
            if (a.isIntegral()) {
                if (a.isOdd())
                    s = 1;
            } else {
                makeNan();
                return;
            }
            sign = 0;
        }
        { tmp1.mantissa = a.mantissa; tmp1.exponent = a.exponent; tmp1.sign = a.sign; };
        if (tmp1.exponent <= 0x4000001e) {
            // For increased accuracy, exponentiate with integer part of
            // exponent by successive squaring
            // (I really don't know why this works)
            { tmp2.mantissa = tmp1.mantissa; tmp2.exponent = tmp1.exponent; tmp2.sign = tmp1.sign; };
            tmp2.floor();
            { tmp3.mantissa = this.mantissa; tmp3.exponent = this.exponent; tmp3.sign = this.sign; };
            tmp3.pow(tmp2.toInteger());
            tmp1.sub(tmp2);
        } else {
            { tmp3.mantissa = ONE.mantissa; tmp3.exponent = ONE.exponent; tmp3.sign = ONE.sign; };
        }
        // Do log2 and maintain accuracy
        int e = lnInternal();
        { tmp2.sign=(byte)0; tmp2.exponent=0x40000000; tmp2.mantissa=0x5c551d94ae0bf85dL; }; // log2(e)
        long extra = mul128(0,tmp2,0xdf43ff68348e9f44L);
        tmp2.assign(e);
        extra = add128(extra,tmp2,0);
        // Do exp2 of this multiplied by (fractional part of) exponent
        extra = tmp1.mul128(0,this,extra);
        tmp1.exp2Internal(extra);
        { this.mantissa = tmp1.mantissa; this.exponent = tmp1.exponent; this.sign = tmp1.sign; };
        mul(tmp3);
        if (!(this.exponent < 0 && this.mantissa != 0))
            sign = s;
    }
    /**
     * Calculates the value of this <code>Real</code> raised to the power of
     * the integer <code>a</code>.
     * Replaces the contents of this <code>Real</code> with the result.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *   <code>this = Math.{@link Math#pow(double,double) pow}(this, a);</code>
     * </td></tr><tr><td><i>Error&nbsp;bound:</i></td><td>
     *     ½ ULPs
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     84
     * </td></tr></table>
     *
     * @param a the integer argument.
     */
    public void pow(int a) {
        // Calculate power of integer by successive squaring
        boolean recp=false;
        if (a < 0) {
            a = -a; // Also works for 0x80000000
            recp = true;
        }
        long extra = 0, expTmpExtra = 0;
        { expTmp.mantissa = this.mantissa; expTmp.exponent = this.exponent; expTmp.sign = this.sign; };
        { this.mantissa = ONE.mantissa; this.exponent = ONE.exponent; this.sign = ONE.sign; };
        for (; a!=0; a>>>=1) {
            if ((a & 1) != 0)
                extra = mul128(extra,expTmp,expTmpExtra);
            expTmpExtra = expTmp.mul128(expTmpExtra,expTmp,expTmpExtra);
        }
        if (recp)
            extra = recip128(extra);
        roundFrom128(extra);
    }
    private void sinInternal() {
        /*
         * Adapted from:
         * Cephes Math Library Release 2.7:  May, 1998
         * Copyright 1985, 1990, 1998 by Stephen L. Moshier
         *
         * sinl.c
         *
         * long double sinl(long double x);
         */
        // X<PI/4
        // polynomial approximation
        // sin(x) = x + x³ P(x²)
        { tmp1.mantissa = this.mantissa; tmp1.exponent = this.exponent; tmp1.sign = this.sign; };
        { tmp2.mantissa = this.mantissa; tmp2.exponent = this.exponent; tmp2.sign = this.sign; };
        tmp2.sqr();
        { this.sign=(byte)1; this.exponent=0x3fffffd7; this.mantissa=0x6aa891c4f0eb2713L; };//-7.578540409484280575629E-13
        mul(tmp2);
        { tmp3.sign=(byte)0; tmp3.exponent=0x3fffffdf; tmp3.mantissa=0x58482311f383326cL; };//1.6058363167320443249231E-10
        add(tmp3);
        mul(tmp2);
        { tmp3.sign=(byte)1; tmp3.exponent=0x3fffffe6; tmp3.mantissa=0x6b9914a35f9a00d8L; };//-2.5052104881870868784055E-8
        add(tmp3);
        mul(tmp2);
        { tmp3.sign=(byte)0; tmp3.exponent=0x3fffffed; tmp3.mantissa=0x5c778e94cc22e47bL; };//2.7557319214064922217861E-6
        add(tmp3);
        mul(tmp2);
        { tmp3.sign=(byte)1; tmp3.exponent=0x3ffffff3; tmp3.mantissa=0x680680680629b28aL; };//-1.9841269841254799668344E-4
        add(tmp3);
        mul(tmp2);
        { tmp3.sign=(byte)0; tmp3.exponent=0x3ffffff9; tmp3.mantissa=0x4444444444442b4dL; };//8.3333333333333225058715E-3
        add(tmp3);
        mul(tmp2);
        { tmp3.sign=(byte)1; tmp3.exponent=0x3ffffffd; tmp3.mantissa=0x555555555555554cL; };//-1.6666666666666666640255E-1
        add(tmp3);
        mul(tmp2);
        mul(tmp1);
        add(tmp1);
    }
    private void cosInternal() {
        /*
         * Adapted from:
         * Cephes Math Library Release 2.7:  May, 1998
         * Copyright 1985, 1990, 1998 by Stephen L. Moshier
         *
         * sinl.c
         *
         * long double cosl(long double x);
         */
        // X<PI/4
        // polynomial approximation
        // cos(x) = 1 - x²/2 + x**4 Q(x²)
        { tmp1.mantissa = this.mantissa; tmp1.exponent = this.exponent; tmp1.sign = this.sign; };
        { tmp2.mantissa = this.mantissa; tmp2.exponent = this.exponent; tmp2.sign = this.sign; };
        tmp2.sqr();
        { this.sign=(byte)0; this.exponent=0x3fffffd3; this.mantissa=0x6aaf461d37ccba1bL; };//4.7377507964246204691685E-14
        mul(tmp2);
        { tmp3.sign=(byte)1; tmp3.exponent=0x3fffffdb; tmp3.mantissa=0x64e4c907ac7a179bL; };//-1.147028484342535976567E-11
        add(tmp3);
        mul(tmp2);
        { tmp3.sign=(byte)0; tmp3.exponent=0x3fffffe3; tmp3.mantissa=0x47bb632432cf29a8L; };//2.0876754287081521758361E-9
        add(tmp3);
        mul(tmp2);
        { tmp3.sign=(byte)1; tmp3.exponent=0x3fffffea; tmp3.mantissa=0x49f93edd7ae32696L; };//-2.7557319214999787979814E-7
        add(tmp3);
        mul(tmp2);
        { tmp3.sign=(byte)0; tmp3.exponent=0x3ffffff0; tmp3.mantissa=0x68068068063329f7L; };//2.4801587301570552304991E-5L
        add(tmp3);
        mul(tmp2);
        { tmp3.sign=(byte)1; tmp3.exponent=0x3ffffff6; tmp3.mantissa=0x5b05b05b05b03db3L; };//-1.3888888888888872993737E-3
        add(tmp3);
        mul(tmp2);
        { tmp3.sign=(byte)0; tmp3.exponent=0x3ffffffb; tmp3.mantissa=0x555555555555554dL; };//4.1666666666666666609054E-2
        add(tmp3);
        mul(tmp2);
        sub(HALF);
        mul(tmp2);
        add(ONE);
    }
    /**
     * Calculates the trigonometric sine of this <code>Real</code>.
     * Replaces the contents of this <code>Real</code> with the result.
     * The input value is treated as an angle measured in radians.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <code>this = Math.{@link Math#sin(double) sin}(this);</code>
     * </td></tr><tr><td><i>Approximate&nbsp;error&nbsp;bound:</i></td><td>
     *     1 ULPs
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     28
     * </td></tr></table>
     */
    public void sin() {
        if (!(this.exponent >= 0 && this.mantissa != 0)) {
            if (!(this.exponent == 0 && this.mantissa == 0))
                makeNan();
            return;
        }
        // Since sin(-x) = -sin(x) we can make sure that x > 0
        boolean negative = false;
        if ((this.sign!=0)) {
            abs();
            negative = true;
        }
        // Then reduce the argument to the range of 0 < x < pi*2
        if (this.compare(PI2) > 0)
            modInternal(PI2,0x62633145c06e0e69L);
        // Since sin(pi*2 - x) = -sin(x) we can reduce the range 0 < x < pi
        if (this.compare(PI) > 0) {
            sub(PI2);
            neg();
            negative = !negative;
        }
        // Since sin(x) = sin(pi - x) we can reduce the range to 0 < x < pi/2
        if (this.compare(PI_2) > 0) {
            sub(PI);
            neg();
        }
        // Since sin(x) = cos(pi/2 - x) we can reduce the range to 0 < x < pi/4
        if (this.compare(PI_4) > 0) {
            sub(PI_2);
            neg();
            cosInternal();
        } else {
            sinInternal();
        }
        if (negative)
            neg();
        if ((this.exponent == 0 && this.mantissa == 0))
            abs(); // Remove confusing "-"
    }
    /**
     * Calculates the trigonometric cosine of this <code>Real</code>.
     * Replaces the contents of this <code>Real</code> with the result.
     * The input value is treated as an angle measured in radians.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <code>this = Math.{@link Math#cos(double) cos}(this);</code>
     * </td></tr><tr><td><i>Approximate&nbsp;error&nbsp;bound:</i></td><td>
     *     1 ULPs
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     37
     * </td></tr></table>
     */
    public void cos() {
        if ((this.exponent == 0 && this.mantissa == 0)) {
            { this.mantissa = ONE.mantissa; this.exponent = ONE.exponent; this.sign = ONE.sign; };
            return;
        }
        if ((this.sign!=0))
            abs();
        if (this.compare(PI_4) < 0) {
            cosInternal();
        } else {
            add(PI_2);
            sin();
        }
    }
    /**
     * Calculates the trigonometric tangent of this <code>Real</code>.
     * Replaces the contents of this <code>Real</code> with the result.
     * The input value is treated as an angle measured in radians.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <code>this = Math.{@link Math#tan(double) tan}(this);</code>
     * </td></tr><tr><td><i>Approximate&nbsp;error&nbsp;bound:</i></td><td>
     *     2 ULPs
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     70
     * </td></tr></table>
     */
    public void tan() {
        { tmp4.mantissa = this.mantissa; tmp4.exponent = this.exponent; tmp4.sign = this.sign; };
        tmp4.cos();
        sin();
        div(tmp4);
    }
    /**
     * Calculates the trigonometric arc sine of this <code>Real</code>,
     * in the range -&pi;/2 to &pi;/2.
     * Replaces the contents of this <code>Real</code> with the result.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <code>this = Math.{@link Math#asin(double) asin}(this);</code>
     * </td></tr><tr><td><i>Approximate&nbsp;error&nbsp;bound:</i></td><td>
     *     3 ULPs
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     68
     * </td></tr></table>
     */
    public void asin() {
        { tmp1.mantissa = this.mantissa; tmp1.exponent = this.exponent; tmp1.sign = this.sign; };
        sqr();
        neg();
        add(ONE);
        rsqrt();
        mul(tmp1);
        atan();
    }
    /**
     * Calculates the trigonometric arc cosine of this <code>Real</code>,
     * in the range 0.0 to &pi;.
     * Replaces the contents of this <code>Real</code> with the result.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <code>this = Math.{@link Math#acos(double) acos}(this);</code>
     * </td></tr><tr><td><i>Approximate&nbsp;error&nbsp;bound:</i></td><td>
     *     2 ULPs
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     67
     * </td></tr></table>
     */
    public void acos() {
        boolean negative = (this.sign!=0);
        abs();
        { tmp1.mantissa = this.mantissa; tmp1.exponent = this.exponent; tmp1.sign = this.sign; };
        sqr();
        neg();
        add(ONE);
        sqrt();
        div(tmp1);
        atan();
        if (negative) {
            neg();
            add(PI);
        }
    }
    /**
     * Calculates the trigonometric arc tangent of this <code>Real</code>,
     * in the range -&pi;/2 to &pi;/2.
     * Replaces the contents of this <code>Real</code> with the result.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <code>this = Math.{@link Math#atan(double) atan}(this);</code>
     * </td></tr><tr><td><i>Approximate&nbsp;error&nbsp;bound:</i></td><td>
     *     2 ULPs
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     37
     * </td></tr></table>
     */
    public void atan() {
        /*
         * Adapted from:
         * Cephes Math Library Release 2.7:  May, 1998
         * Copyright 1984, 1990, 1998 by Stephen L. Moshier
         *
         * atanl.c
         *
         * long double atanl(long double x);
         */
        if ((this.exponent == 0 && this.mantissa == 0) || (this.exponent < 0 && this.mantissa != 0))
            return;
        if ((this.exponent < 0 && this.mantissa == 0)) {
            byte s = sign;
            { this.mantissa = PI_2.mantissa; this.exponent = PI_2.exponent; this.sign = PI_2.sign; };
            sign = s;
            return;
        }
        byte s = sign;
        sign = 0;
        // range reduction
        boolean addPI_2 = false;
        boolean addPI_4 = false;
        { tmp1.mantissa = SQRT2.mantissa; tmp1.exponent = SQRT2.exponent; tmp1.sign = SQRT2.sign; };
        tmp1.add(ONE);
        if (this.compare(tmp1) > 0) {
            addPI_2 = true;
            recip();
            neg();
        } else {
            tmp1.sub(TWO);
            if (this.compare(tmp1) > 0) {
                addPI_4 = true;
                { tmp1.mantissa = this.mantissa; tmp1.exponent = this.exponent; tmp1.sign = this.sign; };
                tmp1.add(ONE);
                sub(ONE);
                div(tmp1);
            }
        }
        // Now |X|<sqrt(2)-1
        // rational approximation
        // atan(x) = x + x³ P(x²)/Q(x²)
        { tmp1.mantissa = this.mantissa; tmp1.exponent = this.exponent; tmp1.sign = this.sign; };
        { tmp2.mantissa = this.mantissa; tmp2.exponent = this.exponent; tmp2.sign = this.sign; };
        tmp2.sqr();
        mul(tmp2);
        { tmp3.sign=(byte)1; tmp3.exponent=0x3fffffff; tmp3.mantissa=0x6f2f89336729c767L; };//-0.8686381817809218753544
        tmp3.mul(tmp2);
        { tmp4.sign=(byte)1; tmp4.exponent=0x40000003; tmp4.mantissa=0x7577d35fd03083f3L; };//-14.683508633175792446076
        tmp3.add(tmp4);
        tmp3.mul(tmp2);
        { tmp4.sign=(byte)1; tmp4.exponent=0x40000005; tmp4.mantissa=0x7ff42abff948a9f7L; };//-63.976888655834347413154
        tmp3.add(tmp4);
        tmp3.mul(tmp2);
        { tmp4.sign=(byte)1; tmp4.exponent=0x40000006; tmp4.mantissa=0x63fd1f9f76d37cebL; };//-99.988763777265819915721
        tmp3.add(tmp4);
        tmp3.mul(tmp2);
        { tmp4.sign=(byte)1; tmp4.exponent=0x40000005; tmp4.mantissa=0x65c9c9b0b55e5b62L; };//-50.894116899623603312185
        tmp3.add(tmp4);
        mul(tmp3);
        { tmp3.mantissa = tmp2.mantissa; tmp3.exponent = tmp2.exponent; tmp3.sign = tmp2.sign; };
        { tmp4.sign=(byte)0; tmp4.exponent=0x40000004; tmp4.mantissa=0x5bed73b744a72a6aL; };//22.981886733594175366172
        tmp3.add(tmp4);
        tmp3.mul(tmp2);
        { tmp4.sign=(byte)0; tmp4.exponent=0x40000007; tmp4.mantissa=0x47fed7d13d233b5cL; };//143.99096122250781605352
        tmp3.add(tmp4);
        tmp3.mul(tmp2);
        { tmp4.sign=(byte)0; tmp4.exponent=0x40000008; tmp4.mantissa=0x5a5c35f774e071d5L; };//361.44079386152023162701
        tmp3.add(tmp4);
        tmp3.mul(tmp2);
        { tmp4.sign=(byte)0; tmp4.exponent=0x40000008; tmp4.mantissa=0x61e4d84c2853d5e0L; };//391.57570175111990631099
        tmp3.add(tmp4);
        tmp3.mul(tmp2);
        { tmp4.sign=(byte)0; tmp4.exponent=0x40000007; tmp4.mantissa=0x4c5757448806c48eL; };//152.68235069887081006606
        tmp3.add(tmp4);
        div(tmp3);
        add(tmp1);
        if (addPI_2)
            add(PI_2);
        if (addPI_4)
            add(PI_4);
        if (s != 0)
            neg();
    }
    /**
     * Calculates the trigonometric arc tangent of this
     * <code>Real</code> divided by <code>x</code>, in the range -&pi;
     * to &pi;. The signs of both arguments are used to determine the
     * quadrant of the result. Replaces the contents of this
     * <code>Real</code> with the result.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <code>this = Math.{@link Math#atan2(double,double)
     *           atan2}(this,x);</code>
     * </td></tr><tr><td><i>Approximate&nbsp;error&nbsp;bound:</i></td><td>
     *     2 ULPs
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     48
     * </td></tr></table>
     *
     * @param x the <code>Real</code> argument.
     */
    public void atan2(Real x) {
        if ((this.exponent < 0 && this.mantissa != 0) || (x.exponent < 0 && x.mantissa != 0) || ((this.exponent < 0 && this.mantissa == 0) && (x.exponent < 0 && x.mantissa == 0))) {
            makeNan();
            return;
        }
        if ((this.exponent == 0 && this.mantissa == 0) && (x.exponent == 0 && x.mantissa == 0))
            return;
        byte s = sign;
        byte s2 = x.sign;
        sign = 0;
        x.sign = 0;
        div(x);
        atan();
        if (s2 != 0) {
            neg();
            add(PI);
        }
        sign = s;
    }
    /**
     * Calculates the hyperbolic sine of this <code>Real</code>.
     * Replaces the contents of this <code>Real</code> with the result.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <code>this = Math.{@link Math#sinh(double) sinh}(this);</code>
     * </td></tr><tr><td><i>Approximate&nbsp;error&nbsp;bound:</i></td><td>
     *     2 ULPs
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     67
     * </td></tr></table>
     */
    public void sinh() {
        { tmp1.mantissa = this.mantissa; tmp1.exponent = this.exponent; tmp1.sign = this.sign; };
        tmp1.neg();
        tmp1.exp();
        exp();
        sub(tmp1);
        scalbn(-1);
    }
    /**
     * Calculates the hyperbolic cosine of this <code>Real</code>.
     * Replaces the contents of this <code>Real</code> with the result.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <code>this = Math.{@link Math#cosh(double) cosh}(this);</code>
     * </td></tr><tr><td><i>Approximate&nbsp;error&nbsp;bound:</i></td><td>
     *     2 ULPs
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     66
     * </td></tr></table>
     */
    public void cosh() {
        { tmp1.mantissa = this.mantissa; tmp1.exponent = this.exponent; tmp1.sign = this.sign; };
        tmp1.neg();
        tmp1.exp();
        exp();
        add(tmp1);
        scalbn(-1);
    }
    /**
     * Calculates the hyperbolic tangent of this <code>Real</code>.
     * Replaces the contents of this <code>Real</code> with the result.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <code>this = Math.{@link Math#tanh(double) tanh}(this);</code>
     * </td></tr><tr><td><i>Approximate&nbsp;error&nbsp;bound:</i></td><td>
     *     2 ULPs
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     70
     * </td></tr></table>
     */
    public void tanh() {
        { tmp1.mantissa = this.mantissa; tmp1.exponent = this.exponent; tmp1.sign = this.sign; };
        tmp1.neg();
        tmp1.exp();
        exp();
        { tmp2.mantissa = this.mantissa; tmp2.exponent = this.exponent; tmp2.sign = this.sign; };
        tmp2.add(tmp1);
        sub(tmp1);
        div(tmp2);
    }
    /**
     * Calculates the hyperbolic arc sine of this <code>Real</code>.
     * Replaces the contents of this <code>Real</code> with the result.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <i>none</i>
     * </td></tr><tr><td><i>Approximate&nbsp;error&nbsp;bound:</i></td><td>
     *     2 ULPs
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     77
     * </td></tr></table>
     */
    public void asinh() {
        if (!(this.exponent >= 0 && this.mantissa != 0))
            return;
        // Use symmetry to prevent underflow error for very large negative
        // values
        byte s = sign;
        sign = 0;
        { tmp1.mantissa = this.mantissa; tmp1.exponent = this.exponent; tmp1.sign = this.sign; };
        tmp1.sqr();
        tmp1.add(ONE);
        tmp1.sqrt();
        add(tmp1);
        ln();
        if (!(this.exponent < 0 && this.mantissa != 0))
            sign = s;
    }
    /**
     * Calculates the hyperbolic arc cosine of this <code>Real</code>.
     * Replaces the contents of this <code>Real</code> with the result.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <i>none</i>
     * </td></tr><tr><td><i>Approximate&nbsp;error&nbsp;bound:</i></td><td>
     *     2 ULPs
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     75
     * </td></tr></table>
     */
    public void acosh() {
        { tmp1.mantissa = this.mantissa; tmp1.exponent = this.exponent; tmp1.sign = this.sign; };
        tmp1.sqr();
        tmp1.sub(ONE);
        tmp1.sqrt();
        add(tmp1);
        ln();
    }
    /**
     * Calculates the hyperbolic arc tangent of this <code>Real</code>.
     * Replaces the contents of this <code>Real</code> with the result.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <i>none</i>
     * </td></tr><tr><td><i>Approximate&nbsp;error&nbsp;bound:</i></td><td>
     *     2 ULPs
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     57
     * </td></tr></table>
     */
    public void atanh() {
        { tmp1.mantissa = this.mantissa; tmp1.exponent = this.exponent; tmp1.sign = this.sign; };
        tmp1.neg();
        tmp1.add(ONE);
        add(ONE);
        div(tmp1);
        ln();
        scalbn(-1);
    }
    /**
     * Calculates the factorial of this <code>Real</code>.
     * Replaces the contents of this <code>Real</code> with the result.
     * The definition is generalized to all real numbers (not only integers),
     * by using the fact that <code>(n!)={@link #gamma() gamma}(n+1)</code>.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <i>none</i>
     * </td></tr><tr><td><i>Approximate&nbsp;error&nbsp;bound:</i></td><td>
     *     15 ULPs
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     8-190
     * </td></tr></table>
     */
    public void fact() {
        if (!(this.exponent >= 0))
            return;
        if (!this.isIntegral() || this.compare(ZERO)<0 || this.compare(200)>0)
        {
            // x<0, x>200 or not integer: fact(x) = gamma(x+1)
            add(ONE);
            gamma();
            return;
        }
        { tmp1.mantissa = this.mantissa; tmp1.exponent = this.exponent; tmp1.sign = this.sign; };
        { this.mantissa = ONE.mantissa; this.exponent = ONE.exponent; this.sign = ONE.sign; };
        while (tmp1.compare(ONE) > 0) {
            mul(tmp1);
            tmp1.sub(ONE);
        }
    }
    /**
     * Calculates the gamma function for this <code>Real</code>.
     * Replaces the contents of this <code>Real</code> with the result.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <i>none</i>
     * </td></tr><tr><td><i>Approximate&nbsp;error&nbsp;bound:</i></td><td>
     *     100+ ULPs
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     190
     * </td></tr></table>
     */
    public void gamma() {
        if (!(this.exponent >= 0))
            return;
        // x<0: gamma(-x) = -pi/(x*gamma(x)*sin(pi*x))
        boolean negative = (this.sign!=0);
        abs();
        { tmp1.mantissa = this.mantissa; tmp1.exponent = this.exponent; tmp1.sign = this.sign; };
        // x<n: gamma(x) = gamma(x+m)/x*(x+1)*(x+2)*...*(x+m-1)
        // n=20
        { tmp2.mantissa = ONE.mantissa; tmp2.exponent = ONE.exponent; tmp2.sign = ONE.sign; };
        boolean divide = false;
        while (this.compare(20) < 0) {
            divide = true;
            tmp2.mul(this);
            add(ONE);
        }
        // x>n: gamma(x) = exp((x-1/2)*ln(x) - x + ln(2*pi)/2 + 1/12x - 1/360x³
        //                     + 1/1260x**5 - 1/1680x**7+1/1188x**9)
        { tmp3.mantissa = this.mantissa; tmp3.exponent = this.exponent; tmp3.sign = this.sign; }; // x
        { tmp4.mantissa = this.mantissa; tmp4.exponent = this.exponent; tmp4.sign = this.sign; };
        tmp4.sqr(); // x²
        // (x-1/2)*ln(x)-x
        ln(); { tmp5.mantissa = tmp3.mantissa; tmp5.exponent = tmp3.exponent; tmp5.sign = tmp3.sign; }; tmp5.sub(HALF); mul(tmp5); sub(tmp3);
        // + ln(2*pi)/2
        { tmp5.sign=(byte)0; tmp5.exponent=0x3fffffff; tmp5.mantissa=0x759fc72192fad29aL; }; add(tmp5);
        // + 1/12x
        tmp5.assign( 12); tmp5.mul(tmp3); tmp5.recip(); add(tmp5); tmp3.mul(tmp4);
        // - 1/360x³
        tmp5.assign( 360); tmp5.mul(tmp3); tmp5.recip(); sub(tmp5); tmp3.mul(tmp4);
        // + 1/1260x**5
        tmp5.assign(1260); tmp5.mul(tmp3); tmp5.recip(); add(tmp5); tmp3.mul(tmp4);
        // - 1/1680x**7
        tmp5.assign(1680); tmp5.mul(tmp3); tmp5.recip(); sub(tmp5); tmp3.mul(tmp4);
        // + 1/1188x**9
        tmp5.assign(1188); tmp5.mul(tmp3); tmp5.recip(); add(tmp5);
        exp();
        if (divide)
            div(tmp2);
        if (negative) {
            { tmp5.mantissa = tmp1.mantissa; tmp5.exponent = tmp1.exponent; tmp5.sign = tmp1.sign; }; // sin() uses tmp1
            // -pi/(x*gamma(x)*sin(pi*x))
            mul(tmp5);
            tmp5.scalbn(-1); tmp5.frac(); tmp5.mul(PI2); // Fixes integer inaccuracy
            tmp5.sin(); mul(tmp5); recip(); mul(PI); neg();
        }
    }
    private void erfc1Internal() {
        //                                3       5        7        9
        //                 2    /        x       x        x        x                  // erfc(x) = 1 - ------ | x  -  ---  +  ----  -  ----  +  ----  - ... |
        //              sqrt(pi)\        3      2!*5     3!*7     4!*9        /
        //
        long extra=0,tmp1Extra,tmp2Extra,tmp3Extra,tmp4Extra;
        { tmp1.mantissa = this.mantissa; tmp1.exponent = this.exponent; tmp1.sign = this.sign; }; tmp1Extra = 0;
        { tmp2.mantissa = this.mantissa; tmp2.exponent = this.exponent; tmp2.sign = this.sign; };
        tmp2Extra = tmp2.mul128(0,tmp2,0);
        tmp2.neg();
        { tmp3.mantissa = ONE.mantissa; tmp3.exponent = ONE.exponent; tmp3.sign = ONE.sign; }; tmp3Extra = 0;
        int i=1;
        do {
            tmp1Extra = tmp1.mul128(tmp1Extra,tmp2,tmp2Extra);
            tmp4.assign(i);
            tmp3Extra = tmp3.mul128(tmp3Extra,tmp4,0);
            tmp4.assign(2*i+1);
            tmp4Extra = tmp4.mul128(0,tmp3,tmp3Extra);
            tmp4Extra = tmp4.recip128(tmp4Extra);
            tmp4Extra = tmp4.mul128(tmp4Extra,tmp1,tmp1Extra);
            extra = add128(extra,tmp4,tmp4Extra);
            i++;
        } while (exponent - tmp4.exponent < 128);
        { tmp1.sign=(byte)1; tmp1.exponent=0x40000000; tmp1.mantissa=0x48375d410a6db446L; }; // -2/sqrt(pi)
        extra = mul128(extra,tmp1,0xb8ea453fb5ff61a2L);
        extra = add128(extra,ONE,0);
        roundFrom128(extra);
    }
    private void erfc2Internal() {
        //             -x² -1
        //            e   x   /      1      3       3*5     3*5*7                // erfc(x) = -------- | 1 - --- + ------ - ------ + ------ - ... |
        //           sqrt(pi) \     2x²        2        3        4       /
        //                                (2x²)    (2x²)    (2x²)
        // Calculate iteration stop criteria
        { tmp1.mantissa = this.mantissa; tmp1.exponent = this.exponent; tmp1.sign = this.sign; };
        tmp1.sqr();
        { tmp2.sign=(byte)0; tmp2.exponent=0x40000000; tmp2.mantissa=0x5c3811b4bfd0c8abL; }; // 1/0.694
        tmp2.mul(tmp1);
        tmp2.sub(HALF);
        int digits = tmp2.toInteger(); // number of accurate digits = x*x/0.694-0.5
        if (digits > 64)
            digits = 64;
        tmp1.scalbn(1);
        int dxq = tmp1.toInteger()+1;
        { tmp1.mantissa = this.mantissa; tmp1.exponent = this.exponent; tmp1.sign = this.sign; };
        recip();
        { tmp2.mantissa = this.mantissa; tmp2.exponent = this.exponent; tmp2.sign = this.sign; };
        { tmp3.mantissa = this.mantissa; tmp3.exponent = this.exponent; tmp3.sign = this.sign; };
        tmp3.sqr();
        tmp3.neg();
        tmp3.scalbn(-1);
        { this.mantissa = ONE.mantissa; this.exponent = ONE.exponent; this.sign = ONE.sign; };
        { tmp4.mantissa = ONE.mantissa; tmp4.exponent = ONE.exponent; tmp4.sign = ONE.sign; };
        int i=1;
        do {
            tmp4.mul(2*i-1);
            tmp4.mul(tmp3);
            add(tmp4);
            i++;
        } while (tmp4.exponent-0x40000000>-(digits+2) && 2*i-1<dxq);
        mul(tmp2);
        tmp1.sqr();
        tmp1.neg();
        tmp1.exp();
        mul(tmp1);
        { tmp1.sign=(byte)0; tmp1.exponent=0x3fffffff; tmp1.mantissa=0x48375d410a6db447L; }; // 1/sqrt(pi)
        mul(tmp1);
    }
    /**
     * Calculates the complementary error function for this <code>Real</code>.
     * Replaces the contents of this <code>Real</code> with the result.
     *
     * <p>The complementary error function is defined as the integral from
     * x to infinity of 2/&#8730;<span style="text-decoration:
     * overline;">&pi;</span>&nbsp;·<i>e</i><sup>-t²</sup>&nbsp;dt. It is
     * related to the error function, <i>erf</i>, by the formula
     * erfc(x)=1-erf(x).
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <i>none</i>
     * </td></tr><tr><td><i>Approximate&nbsp;error&nbsp;bound:</i></td><td>
     *     2<sup>19</sup> ULPs
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     80-4900
     * </td></tr></table>
     */
    public void erfc() {
        if ((this.exponent < 0 && this.mantissa != 0))
            return;
        if ((this.exponent == 0 && this.mantissa == 0)) {
            { this.mantissa = ONE.mantissa; this.exponent = ONE.exponent; this.sign = ONE.sign; };
            return;
        }
        if ((this.exponent < 0 && this.mantissa == 0) || toInteger()>27281) {
            if ((this.sign!=0)) {
                { this.mantissa = TWO.mantissa; this.exponent = TWO.exponent; this.sign = TWO.sign; };
            } else
                makeZero(0);
            return;
        }
        byte s = sign;
        sign = 0;
        { tmp1.sign=(byte)0; tmp1.exponent=0x40000002; tmp1.mantissa=0x570a3d70a3d70a3dL; }; // 5.44
        if (this.lessThan(tmp1))
            erfc1Internal();
        else
            erfc2Internal();
        if (s != 0) {
            neg();
            add(TWO);
        }
    }
    /**
     * Calculates the inverse complementary error function for this
     * <code>Real</code>.
     * Replaces the contents of this <code>Real</code> with the result.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <i>none</i>
     * </td></tr><tr><td><i>Approximate&nbsp;error&nbsp;bound:</i></td><td>
     *     2<sup>19</sup> ULPs
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     240-5100
     * </td></tr></table>
     */
    public void inverfc() {
        if ((this.exponent < 0 && this.mantissa != 0) || (this.sign!=0) || this.greaterThan(TWO)) {
            makeNan();
            return;
        }
        if ((this.exponent == 0 && this.mantissa == 0)) {
            makeInfinity(0);
            return;
        }
        if (this.equalTo(TWO)) {
            makeInfinity(1);
            return;
        }
        int sign = ONE.compare(this);
        if (sign==0) {
            makeZero();
            return;
        }
        if (sign<0) {
            neg();
            add(TWO);
        }
        // Using invphi to calculate inverfc, like this
        // inverfc(x) = -invphi(x/2)/(sqrt(2))
        scalbn(-1);
        // Inverse Phi Algorithm (phi(Z)=P, so invphi(P)=Z)
        // ------------------------------------------------
        // Part 1: Numerical Approximation Method for Inverse Phi
        // This accepts input of P and outputs approximate Z as Y
        // Source:Odeh & Evans. 1974. AS 70. Applied Statistics.
        // R = sqrt(Ln(1/(Q²)))
        { tmp1.mantissa = this.mantissa; tmp1.exponent = this.exponent; tmp1.sign = this.sign; };
        tmp1.ln();
        tmp1.mul(-2);
        tmp1.sqrt();
        // Y = -(R+((((P4*R+P3)*R+P2)*R+P1)*R+P0)/((((Q4*R+Q3)*R*Q2)*R+Q1)*R+Q0))
        { tmp2.sign=(byte)1; tmp2.exponent=0x3ffffff1; tmp2.mantissa=0x5f22bb0fb4698674L; }; // P4=-0.0000453642210148
        tmp2.mul(tmp1);
        { tmp3.sign=(byte)1; tmp3.exponent=0x3ffffffa; tmp3.mantissa=0x53a731ce1ea0be15L; }; // P3=-0.0204231210245
        tmp2.add(tmp3);
        tmp2.mul(tmp1);
        { tmp3.sign=(byte)1; tmp3.exponent=0x3ffffffe; tmp3.mantissa=0x579d2d719fc517f3L; }; // P2=-0.342242088547
        tmp2.add(tmp3);
        tmp2.mul(tmp1);
        tmp2.add(-1); // P1=-1
        tmp2.mul(tmp1);
        { tmp3.sign=(byte)1; tmp3.exponent=0x3ffffffe; tmp3.mantissa=0x527dd3193bc8dd4cL; }; // P0=-0.322232431088
        tmp2.add(tmp3);
        { tmp3.sign=(byte)0; tmp3.exponent=0x3ffffff7; tmp3.mantissa=0x7e5b0f681d161e7dL; }; // Q4=0.0038560700634
        tmp3.mul(tmp1);
        { tmp4.sign=(byte)0; tmp4.exponent=0x3ffffffc; tmp4.mantissa=0x6a05ccf9917da0a8L; }; // Q3=0.103537752850
        tmp3.add(tmp4);
        tmp3.mul(tmp1);
        { tmp4.sign=(byte)0; tmp4.exponent=0x3fffffff; tmp4.mantissa=0x43fb32c0d3c14ec4L; }; // Q2=0.531103462366
        tmp3.add(tmp4);
        tmp3.mul(tmp1);
        { tmp4.sign=(byte)0; tmp4.exponent=0x3fffffff; tmp4.mantissa=0x4b56a41226f4ba95L; }; // Q1=0.588581570495
        tmp3.add(tmp4);
        tmp3.mul(tmp1);
        { tmp4.sign=(byte)0; tmp4.exponent=0x3ffffffc; tmp4.mantissa=0x65bb9a7733dd5062L; }; // Q0=0.0993484626060
        tmp3.add(tmp4);
        tmp2.div(tmp3);
        tmp1.add(tmp2);
        tmp1.neg();
        { sqrtTmp.mantissa = tmp1.mantissa; sqrtTmp.exponent = tmp1.exponent; sqrtTmp.sign = tmp1.sign; }; // sqrtTmp and tmp5 not used by erfc() and exp()
        // Part 2: Refine to accuracy of erfc Function
        // This accepts inputs Y and P (from above) and outputs Z
        // (Using Halley's third order method for finding roots of equations)
        // Q = erfc(-Y/sqrt(2))/2-P
        { tmp5.mantissa = sqrtTmp.mantissa; tmp5.exponent = sqrtTmp.exponent; tmp5.sign = sqrtTmp.sign; };
        tmp5.mul(SQRT1_2);
        tmp5.neg();
        tmp5.erfc();
        tmp5.scalbn(-1);
        tmp5.sub(this);
        // R = Q*sqrt(2*pi)*e^(Y²/2)
        { tmp3.mantissa = sqrtTmp.mantissa; tmp3.exponent = sqrtTmp.exponent; tmp3.sign = sqrtTmp.sign; };
        tmp3.sqr();
        tmp3.scalbn(-1);
        tmp3.exp();
        tmp5.mul(tmp3);
        { tmp3.sign=(byte)0; tmp3.exponent=0x40000001; tmp3.mantissa=0x50364c7fd89c1659L; }; // sqrt(2*pi)
        tmp5.mul(tmp3);
        // Z = Y-R/(1+R*Y/2)
        { this.mantissa = sqrtTmp.mantissa; this.exponent = sqrtTmp.exponent; this.sign = sqrtTmp.sign; };
        mul(tmp5);
        scalbn(-1);
        add(ONE);
        rdiv(tmp5);
        neg();
        add(sqrtTmp);
        // calculate inverfc(x) = -invphi(x/2)/(sqrt(2))
        mul(SQRT1_2);
        if (sign>0)
            neg();
    }
    //*************************************************************************
    // Calendar conversions taken from
    // http://www.fourmilab.ch/documents/calendar/
    private static int floorDiv(int a, int b) {
        if (a>=0)
            return a/b;
        return -((-a+b-1)/b);
    }
    private static int floorMod(int a, int b) {
        if (a>=0)
            return a%b;
        return a+((-a+b-1)/b)*b;
    }
    private static boolean leap_gregorian(int year) {
        return ((year % 4) == 0) &&
            (!(((year % 100) == 0) && ((year % 400) != 0)));
    }
    // GREGORIAN_TO_JD -- Determine Julian day number from Gregorian
    // calendar date -- Except that we use 1/1-0 as day 0
    private static int gregorian_to_jd(int year, int month, int day) {
        return ((366 - 1) +
                (365 * (year - 1)) +
                (floorDiv(year - 1, 4)) +
                (-floorDiv(year - 1, 100)) +
                (floorDiv(year - 1, 400)) +
                ((((367 * month) - 362) / 12) +
                 ((month <= 2) ? 0 : (leap_gregorian(year) ? -1 : -2)) + day));
    }
    // JD_TO_GREGORIAN -- Calculate Gregorian calendar date from Julian
    // day -- Except that we use 1/1-0 as day 0
    private static int jd_to_gregorian(int jd) {
        int wjd, depoch, quadricent, dqc, cent, dcent, quad, dquad,
            yindex, year, yearday, leapadj, month, day;
        wjd = jd;
        depoch = wjd - 366;
        quadricent = floorDiv(depoch, 146097);
        dqc = floorMod(depoch, 146097);
        cent = floorDiv(dqc, 36524);
        dcent = floorMod(dqc, 36524);
        quad = floorDiv(dcent, 1461);
        dquad = floorMod(dcent, 1461);
        yindex = floorDiv(dquad, 365);
        year = (quadricent * 400) + (cent * 100) + (quad * 4) + yindex;
        if (!((cent == 4) || (yindex == 4)))
            year++;
        yearday = wjd - gregorian_to_jd(year, 1, 1);
        leapadj = ((wjd < gregorian_to_jd(year, 3, 1)) ? 0
                   : (leap_gregorian(year) ? 1 : 2));
        month = floorDiv(((yearday + leapadj) * 12) + 373, 367);
        day = (wjd - gregorian_to_jd(year, month, 1)) + 1;
        return (year*100+month)*100+day;
    }
    /**
     * Converts this <code>Real</code> from "hours" to "days, hours,
     * minutes and seconds".
     * Replaces the contents of this <code>Real</code> with the result.
     *
     * <p>The format converted to is encoded into the digits of the
     * number (in decimal form):
     * "<code>DDDDhh.mmss</code>". Here "<code>DDDD</code>," is number
     * of days, "<code>hh</code>" is hours (0-23), "<code>mm</code>" is
     * minutes (0-59) and "<code>ss</code>" is seconds
     * (0-59). Additional digits represent fractions of a second.
     *
     * <p>If the number of hours of the input is greater or equal to
     * 8784 (number of hours in year <code>0</code>), the format
     * converted to is instead "<code>YYYYMMDDhh.mmss</code>". Here
     * "<code>YYYY</code>" is the number of years since the imaginary
     * year <code>0</code> in the Gregorian calendar, extrapolated back
     * from year 1582. "<code>MM</code>" is the month (1-12) and
     * "<code>DD</code>" is the day of the month (1-31). See a thorough
     * discussion of date calculations <a
     * href="http://midp-calc.sourceforge.net/Calc.html#DateNote">here</a>.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <i>none</i>
     * </td></tr><tr><td><i>Approximate&nbsp;error&nbsp;bound:</i></td><td>
     *     ?
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     19
     * </td></tr></table>
     */
    public void toDHMS() {
        if (!(this.exponent >= 0 && this.mantissa != 0))
            return;
        boolean negative = (this.sign!=0);
        abs();
        int D,m;
        long h;
        h = toLong();
        frac();
        tmp1.assign(60);
        mul(tmp1);
        m = toInteger();
        frac();
        mul(tmp1);
        // MAGIC ROUNDING: Check if we are 2**-16 sec short of a whole minute
        // i.e. "seconds" > 59.999985
        { tmp2.mantissa = ONE.mantissa; tmp2.exponent = ONE.exponent; tmp2.sign = ONE.sign; };
        tmp2.scalbn(-16);
        add(tmp2);
        if (this.compare(tmp1) >= 0) {
            // Yes. So set zero secs instead and carry over to mins and hours
            { this.mantissa = ZERO.mantissa; this.exponent = ZERO.exponent; this.sign = ZERO.sign; };
            m++;
            if (m >= 60) {
                m -= 60;
                h++;
            }
            // Phew! That was close. From now on it is integer arithmetic...
        } else {
            // Nope. So try to undo the damage...
            sub(tmp2);
        }
        D = (int)(h/24);
        h %= 24;
        if (D >= 366)
            D = jd_to_gregorian(D);
        add(m*100);
        div(10000);
        tmp1.assign(D*100L+h);
        add(tmp1);
        if (negative)
            neg();
    }
    /**
     * Converts this <code>Real</code> from "days, hours, minutes and
     * seconds" to "hours".
     * Replaces the contents of this <code>Real</code> with the result.
     *
     * <p>The format converted from is encoded into the digits of the
     * number (in decimal form):
     * "<code>DDDDhh.mmss</code>". Here "<code>DDDD</code>" is number of
     * days, "<code>hh</code>" is hours (0-23), "<code>mm</code>" is
     * minutes (0-59) and "<code>ss</code>" is seconds
     * (0-59). Additional digits represent fractions of a second.
     *
     * <p>If the number of days in the input is greater than or equal to
     * 10000, the format converted from is instead
     * "<code>YYYYMMDDhh.mmss</code>". Here "<code>YYYY</code>" is the
     * number of years since the imaginary year <code>0</code> in the
     * Gregorian calendar, extrapolated back from year
     * 1582. "<code>MM</code>" is the month (1-12) and
     * "<code>DD</code>" is the day of the month (1-31). If month or day
     * is 0 it is treated as 1. See a thorough discussion of date
     * calculations <a
     * href="http://midp-calc.sourceforge.net/Calc.html#DateNote">here</a>.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <i>none</i>
     * </td></tr><tr><td><i>Approximate&nbsp;error&nbsp;bound:</i></td><td>
     *     ?
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     19
     * </td></tr></table>
     */
    public void fromDHMS() {
        if (!(this.exponent >= 0 && this.mantissa != 0))
            return;
        boolean negative = (this.sign!=0);
        abs();
        int Y,M,D,m;
        long h;
        h = toLong();
        frac();
        tmp1.assign(100);
        mul(tmp1);
        m = toInteger();
        frac();
        mul(tmp1);
        // MAGIC ROUNDING: Check if we are 2**-10 second short of 100 seconds
        // i.e. "seconds" > 99.999
        { tmp2.mantissa = ONE.mantissa; tmp2.exponent = ONE.exponent; tmp2.sign = ONE.sign; };
        tmp2.scalbn(-10);
        add(tmp2);
        if (this.compare(tmp1) >= 0) {
            // Yes. So set zero secs instead and carry over to mins and hours
            { this.mantissa = ZERO.mantissa; this.exponent = ZERO.exponent; this.sign = ZERO.sign; };
            m++;
            if (m >= 100) {
                m -= 100;
                h++;
            }
            // Phew! That was close. From now on it is integer arithmetic...
        } else {
            // Nope. So try to undo the damage...
            sub(tmp2);
        }
        D = (int)(h/100);
        h %= 100;
        if (D>=10000) {
            M = D/100;
            D %= 100;
            if (D==0) D=1;
            Y = M/100;
            M %= 100;
            if (M==0) M=1;
            D = gregorian_to_jd(Y,M,D);
        }
        add(m*60);
        div(3600);
        tmp1.assign(D*24L+h);
        add(tmp1);
        if (negative)
            neg();
    }
    /**
     * Assigns this <code>Real</code> the current time. The time is
     * encoded into the digits of the number (in decimal form), using the
     * format "<code>hh.mmss</code>", where "<code>hh</code>" is hours,
     * "<code>mm</code>" is minutes and "code>ss</code>" is seconds.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <i>none</i>
     * </td></tr><tr><td><i>Error&nbsp;bound:</i></td><td>
     *     ½ ULPs
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     8.9
     * </td></tr></table>
     */
    public void time() {
        long now = System.currentTimeMillis();
        int h,m,s;
        now /= 1000;
        s = (int)(now % 60);
        now /= 60;
        m = (int)(now % 60);
        now /= 60;
        h = (int)(now % 24);
        assign((h*100+m)*100+s);
        div(10000);
    }
    /**
     * Assigns this <code>Real</code> the current date. The date is
     * encoded into the digits of the number (in decimal form), using
     * the format "<code>YYYYMMDD00</code>", where "<code>YYYY</code>"
     * is the year, "<code>MM</code>" is the month (1-12) and
     * "<code>DD</code>" is the day of the month (1-31). The
     * "<code>00</code>" in this format is a sort of padding to make it
     * compatible with the format used by {@link #toDHMS()} and {@link
     * #fromDHMS()}.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <i>none</i>
     * </td></tr><tr><td><i>Error&nbsp;bound:</i></td><td>
     *     0 ULPs
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     30
     * </td></tr></table>
     */
    public void date() {
        long now = System.currentTimeMillis();
        now /= 86400000; // days
        now *= 24; // hours
        assign(now);
        add(719528*24); // 1970-01-01 era
        toDHMS();
    }
    //*************************************************************************
    /**
     * The seed of the first 64-bit CRC generator of the random
     * routine. Set this value to control the generated sequence of random
     * numbers. Should never be set to 0. See {@link #random()}.
     * Initialized to mantissa of pi.
     */
    public static long randSeedA = 0x6487ed5110b4611aL;
    /**
     * The seed of the second 64-bit CRC generator of the random
     * routine. Set this value to control the generated sequence of random
     * numbers. Should never be set to 0. See {@link #random()}.
     * Initialized to mantissa of e.
     */
    public static long randSeedB = 0x56fc2a2c515da54dL;
    // 64 Bit CRC Generators
    //
    // The generators used here are not cryptographically secure, but
    // two weak generators are combined into one strong generator by
    // skipping bits from one generator whenever the other generator
    // produces a 0-bit.
    private static void advanceBit() {
        randSeedA = (randSeedA<<1)^(randSeedA<0?0x1b:0);
        randSeedB = (randSeedB<<1)^(randSeedB<0?0xb000000000000001L:0);
    }
    // Get next bits from the pseudo-random sequence
    private static long nextBits(int bits) {
        long answer = 0;
        while (bits-- > 0) {
            while (randSeedA >= 0)
                advanceBit();
            answer = (answer<<1) + (randSeedB < 0 ? 1 : 0);
            advanceBit();
        }
        return answer;
    }
    /**
     * Accumulate more randomness into the random number generator, to
     * decrease the predictability of the output from {@link
     * #random()}. The input should contain data with some form of
     * inherent randomness e.g. System.currentTimeMillis().
     *
     * @param seed some extra randomness for the random number generator.
     */
    public static void accumulateRandomness(long seed) {
        randSeedA ^= seed & 0x5555555555555555L;
        randSeedB ^= seed & 0xaaaaaaaaaaaaaaaaL;
        nextBits(63);
    }
    /**
     * Calculates a pseudorandom number in the range [0,&nbsp;1).
     * Replaces the contents of this <code>Real</code> with the result.
     *
     * <p>The algorithm used is believed to be cryptographically secure,
     * combining two relatively weak 64-bit CRC generators into a strong
     * generator by skipping bits from one generator whenever the other
     * generator produces a 0-bit. The algorithm passes the <a
     * href="http://www.fourmilab.ch/random/">ent</a> test.
     *
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <code>this = Math.{@link Math#random() random}();</code>
     * </td></tr><tr><td><i>Approximate&nbsp;error&nbsp;bound:</i></td><td>
     *     -
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     81
     * </td></tr></table>
     */
    public void random() {
        sign = 0;
        exponent = 0x3fffffff;
        while (nextBits(1) == 0)
            exponent--;
        mantissa = 0x4000000000000000L+nextBits(62);
    }
    //*************************************************************************
    private int digit(char a, int base, boolean twosComplement) {
        int digit = -1;
        if (a>='0' && a<='9')
            digit = a-'0';
        else if (a>='A' && a<='F')
            digit = a-'A'+10;
        if (digit >= base)
            return -1;
        if (twosComplement)
            digit ^= base-1;
        return digit;
    }
    private void shiftUp(int base) {
        if (base==2)
            scalbn(1);
        else if (base==8)
            scalbn(3);
        else if (base==16)
            scalbn(4);
        else
            mul10();
    }
    private void atof(String a, int base) {
        makeZero();
        int length = a.length();
        int index = 0;
        byte tmpSign = 0;
        boolean compl = false;
        while (index<length && a.charAt(index)==' ')
            index++;
        if (index<length && a.charAt(index)=='-') {
            tmpSign=1;
            index++;
        } else if (index<length && a.charAt(index)=='+') {
            index++;
        } else if (index<length && a.charAt(index)=='/') {
            // Input is twos complemented negative number
            compl=true;
            tmpSign=1;
            index++;
        }
        int d;
        while (index<length && (d=digit(a.charAt(index),base,compl))>=0) {
            shiftUp(base);
            add(d);
            index++;
        }
        int exp=0;
        if (index<length && (a.charAt(index)=='.' || a.charAt(index)==',')) {
            index++;
            while (index<length && (d=digit(a.charAt(index),base,compl))>=0) {
                shiftUp(base);
                add(d);
                exp--;
                index++;
            }
        }
        if (compl)
            add(ONE);
        while (index<length && a.charAt(index)==' ')
            index++;
        if (index<length && (a.charAt(index)=='e' || a.charAt(index)=='E')) {
            index++;
            int exp2 = 0;
            boolean expNeg = false;
            if (index<length && a.charAt(index)=='-') {
                expNeg = true;
                index++;
            } else if (index<length && a.charAt(index)=='+') {
                index++;
            }
            while (index<length && a.charAt(index)>='0' &&
                   a.charAt(index)<='9')
            {
                // This takes care of overflows and makes inf or 0
                if (exp2 < 400000000)
                    exp2 = exp2*10 + a.charAt(index) - '0';
                index++;
            }
            if (expNeg)
                exp2 = -exp2;
            exp += exp2;
        }
        if (base==2)
            scalbn(exp);
        else if (base==8)
            scalbn(exp*3);
        else if (base==16)
            scalbn(exp*4);
        else {
            if (exp > 300000000 || exp < -300000000) {
                // Kludge to be able to enter very large and very small
                // numbers without causing over/underflows
                { tmp1.mantissa = TEN.mantissa; tmp1.exponent = TEN.exponent; tmp1.sign = TEN.sign; };
                if (exp<0) {
                    tmp1.pow(-exp/2);
                    div(tmp1);
                } else {
                    tmp1.pow(exp/2);
                    mul(tmp1);
                }
                exp -= exp/2;
            }
            { tmp1.mantissa = TEN.mantissa; tmp1.exponent = TEN.exponent; tmp1.sign = TEN.sign; };
            if (exp<0) {
                tmp1.pow(-exp);
                div(tmp1);
            } else if (exp>0) {
                tmp1.pow(exp);
                mul(tmp1);
            }
        }
        sign = tmpSign;
    }
    //*************************************************************************
    private void normalizeDigits(byte[] digits, int nDigits, int base) {
        byte carry = 0;
        boolean isZero = true;
        for (int i=nDigits-1; i>=0; i--) {
            if (digits[i] != 0)
                isZero = false;
            digits[i] += carry;
            carry = 0;
            if (digits[i] >= base) {
                digits[i] -= base;
                carry = 1;
            }
        }
        if (isZero) {
            exponent = 0;
            return;
        }
        if (carry != 0) {
            if (digits[nDigits-1] >= base/2)
                digits[nDigits-2] ++; // Rounding, may be inaccurate
            System.arraycopy(digits, 0, digits, 1, nDigits-1);
            digits[0] = carry;
            exponent++;
            if (digits[nDigits-1] >= base) {
                // Oh, no, not again!
                normalizeDigits(digits, nDigits, base);
            }
        }
        while (digits[0] == 0) {
            System.arraycopy(digits, 1, digits, 0, nDigits-1);
            digits[nDigits-1] = 0;
            exponent--;
        }
    }
    private int getDigits(byte[] digits, int base) {
        if (base == 10)
        {
            { tmp1.mantissa = this.mantissa; tmp1.exponent = this.exponent; tmp1.sign = this.sign; };
            tmp1.abs();
            { tmp2.mantissa = tmp1.mantissa; tmp2.exponent = tmp1.exponent; tmp2.sign = tmp1.sign; };
            int exp = exponent = tmp1.lowPow10();
            exp -= 18;
            boolean exp_neg = exp <= 0;
            exp = Math.abs(exp);
            if (exp > 300000000) {
                // Kludge to be able to print very large and very small numbers
                // without causing over/underflows
                { tmp1.mantissa = TEN.mantissa; tmp1.exponent = TEN.exponent; tmp1.sign = TEN.sign; };
                tmp1.pow(exp/2); // So, divide twice by not-so-extreme numbers
                if (exp_neg)
                    tmp2.mul(tmp1);
                else
                    tmp2.div(tmp1);
                { tmp1.mantissa = TEN.mantissa; tmp1.exponent = TEN.exponent; tmp1.sign = TEN.sign; };
                tmp1.pow(exp-(exp/2));
            } else {
                { tmp1.mantissa = TEN.mantissa; tmp1.exponent = TEN.exponent; tmp1.sign = TEN.sign; };
                tmp1.pow(exp);
            }
            if (exp_neg)
                tmp2.mul(tmp1);
            else
                tmp2.div(tmp1);
            long a;
            if (tmp2.exponent > 0x4000003e) {
                tmp2.exponent--;
                tmp2.round();
                a = tmp2.toLong();
                if (a >= 5000000000000000000L) { // Rounding up gave 20 digits
                    exponent++;
                    a /= 5;
                    digits[18] = (byte)(a%10);
                    a /= 10;
                } else {
                    digits[18] = (byte)((a%5)*2);
                    a /= 5;
                }
            } else {
                tmp2.round();
                a = tmp2.toLong();
                digits[18] = (byte)(a%10);
                a /= 10;
            }
            for (int i=17; i>=0; i--) {
                digits[i] = (byte)(a%10);
                a /= 10;
            }
            digits[19] = 0;
            return 19;
        }
        int accurateBits = 64;
        int bitsPerDigit = base == 2 ? 1 : base == 8 ? 3 : 4;
        if ((this.exponent == 0 && this.mantissa == 0)) {
            sign = 0; // Two's complement cannot display -0
        } else {
            if ((this.sign!=0)) {
                mantissa = -mantissa;
                if (((mantissa >> 62)&3) == 3) {
                    mantissa <<= 1;
                    exponent--;
                    accurateBits--; // ?
                }
            }
            exponent -= 0x40000000-1;
            int shift = bitsPerDigit-1 -
                floorMod(exponent, bitsPerDigit);
            exponent = floorDiv(exponent, bitsPerDigit);
            if (shift == bitsPerDigit-1) {
                // More accurate to shift up instead
                mantissa <<= 1;
                exponent--;
                accurateBits--;
            }
            else if (shift>0) {
                mantissa = (mantissa+(1L<<(shift-1)))>>>shift;
                if ((this.sign!=0)) {
                    // Need to fill in some 1's at the top
                    // (">>", not ">>>")
                    mantissa |= 0x8000000000000000L>>(shift-1);
                }
            }
        }
        int accurateDigits = (accurateBits+bitsPerDigit-1)/bitsPerDigit;
        for (int i=0; i<accurateDigits; i++) {
            digits[i] = (byte)(mantissa>>>(64-bitsPerDigit));
            mantissa <<= bitsPerDigit;
        }
        digits[accurateDigits] = 0;
        return accurateDigits;
    }
    private boolean carryWhenRounded(byte[] digits, int nDigits, int base) {
        if (digits[nDigits] < base/2)
            return false; // no rounding up, no carry
        for (int i=nDigits-1; i>=0; i--)
            if (digits[i] < base-1)
                return false; // carry would not propagate
        exponent++;
        digits[0] = 1;
        for (int i=1; i<digits.length; i++)
            digits[i] = 0;
        return true;
    }
    private void round(byte[] digits, int nDigits, int base) {
        if (digits[nDigits] >= base/2) {
            digits[nDigits-1]++;
            normalizeDigits(digits, nDigits, base);
        }
    }
    /**
     * The number format used to convert <code>Real</code> values to
     * <code>String</code> using {@link Real#toString(Real.NumberFormat)
     * Real.toString()}. The default number format uses base-10, maximum
     * precision, removal of trailing zeros and '.' as radix point.
     *
     * <p>Note that the fields of <code>NumberFormat</code> are not
     * protected in any way, the user is responsible for setting the
     * correct values to get a correct result.
     */
    public static class NumberFormat
    {
        /**
         * The number base of the conversion. The default value is 10,
         * valid options are 2, 8, 10 and 16. See {@link Real#and(Real)
         * Real.and()} for an explanation of the interpretation of a
         * <code>Real</code> in base 2, 8 and 16.
         *
         * <p>Negative numbers output in base-2, base-8 and base-16 are
         * shown in two's complement form. This form guarantees that a
         * negative number starts with at least one digit that is the
         * maximum digit for that base, i.e. '1', '7', and 'F',
         * respectively. A positive number is guaranteed to start with at
         * least one '0'. Both positive and negative numbers are extended
         * to the left using this digit, until {@link #maxwidth} is
         * reached.
         */
        public int base = 10;
        /**
         * Maximum width of the converted string. The default value is 30.
         * If the conversion of a <code>Real</code> with a given {@link
         * #precision} would produce a string wider than
         * <code>maxwidth</code>, <code>precision</code> is reduced until
         * the number fits within the given width. If
         * <code>maxwidth</code> is too small to hold the number with its
         * sign, exponent and a <code>precision</code> of 1 digit, the
         * string may become wider than <code>maxwidth</code>.
         *
         * <p>If <code>align</code> is set to anything but
         * <code>ALIGN_NONE</code> and the converted string is shorter
         * than <code>maxwidth</code>, the resulting string is padded with
         * spaces to the specified width according to the alignment.
         */
        public int maxwidth = 30;
        /**
         * The precision, or number of digits after the radix point in the
         * converted string when using the <i>FIX</i>, <i>SCI</i> or
         * <i>ENG</i> format (see {@link #fse}). The default value is 16,
         * valid values are 0-16 for base-10 and base-16 conversion, 0-21
         * for base-8 conversion, and 0-63 for base-2 conversion.
         *
         * <p>The <code>precision</code> may be reduced to make the number
         * fit within {@link #maxwidth}. The <code>precision</code> is
         * also reduced if it is set higher than the actual numbers of
         * significant digits in a <code>Real</code>. When
         * <code>fse</code> is set to <code>FSE_NONE</code>, i.e. "normal"
         * output, the precision is always at maximum, but trailing zeros
         * are removed.
         */
        public int precision = 16;
        /**
         * The special output formats <i>FIX</i>, <i>SCI</i> or <i>ENG</i>
         * are enabled with this field. The default value is
         * <code>FSE_NONE</code>. Valid options are listed below.
         *
         * <p>Numbers are output in one of two main forms, according to
         * this setting. The normal form has an optional sign, one or more
         * digits before the radix point, and zero or more digits after the
         * radix point, for example like this:<br>
         * <code>&nbsp;&nbsp;&nbsp;3.14159</code><br>
         * The exponent form is like the normal form, followed by an
         * exponent marker 'e', an optional sign and one or more exponent
         * digits, for example like this:<br>
         * <code>&nbsp;&nbsp;&nbsp;-3.4753e-13</code>
         *
         * <p><dl>
         *   <dt>{@link #FSE_NONE}
         *   <dd>Normal output. Numbers are output with maximum precision,
         *   trailing zeros are removed. The format is changed to
         *   exponent form if the number is larger than the number of
         *   significant digits allows, or if the resulting string would
         *   exceed <code>maxwidth</code> without the exponent form.
         *
         *   <dt>{@link #FSE_FIX}
         *   <dd>Like normal output, but the numbers are output with a
         *   fixed number of digits after the radix point, according to
         *   {@link #precision}. Trailing zeros are not removed.
         *
         *   <dt>{@link #FSE_SCI}
         *   <dd>The numbers are always output in the exponent form, with
         *   one digit before the radix point, and a fixed number of
         *   digits after the radix point, according to
         *   <code>precision</code>. Trailing zeros are not removed.
         *
         *   <dt>{@link #FSE_ENG}
         *   <dd>Like the <i>SCI</i> format, but the output shows one to
         *   three digits before the radix point, so that the exponent is
         *   always divisible by 3.
         * </dl>
         */
        public int fse = FSE_NONE;
        /**
         * The character used as the radix point. The default value is
         * <code>'.'</code>. Theoretcally any character that does not
         * otherwise occur in the output can be used, such as
         * <code>','</code>.
         *
         * <p>Note that setting this to anything but <code>'.'</code> and
         * <code>','</code> is not supported by any conversion method from
         * <code>String</code> back to <code>Real</code>.
         */
        public char point = '.';
        /**
         * Set to <code>true</code> to remove the radix point if this is
         * the last character in the converted string. This is the
         * default.
         */
        public boolean removePoint = true;
        /**
         * The character used as the thousands separator. The default
         * value is the character code <code>0</code>, which disables
         * thousands-separation. Theoretcally any character that does not
         * otherwise occur in the output can be used, such as
         * <code>','</code> or <code>' '</code>.
         *
         * <p>When <code>thousand!=0</code>, this character is inserted
         * between every 3rd digit to the left of the radix point in
         * base-10 conversion. In base-16 conversion, the separator is
         * inserted between every 4th digit, and in base-2 conversion the
         * separator is inserted between every 8th digit. In base-8
         * conversion, no separator is ever inserted.
         *
         * <p>Note that tousands separators are not supported by any
         * conversion method from <code>String</code> back to
         * <code>Real</code>, so use of a thousands separator is meant
         * only for the presentation of numbers.
         */
        public char thousand = 0;
        /**
         * The alignment of the output string within a field of {@link
         * #maxwidth} characters. The default value is
         * <code>ALIGN_NONE</code>. Valid options are defined as follows:
         *
         * <p><dl>
         *   <dt>{@link #ALIGN_NONE}
         *   <dd>The resulting string is not padded with spaces.
         *
         *   <dt>{@link #ALIGN_LEFT}
         *   <dd>The resulting string is padded with spaces on the right side
         *   until a width of <code>maxwidth</code> is reached, making the
         *   number left-aligned within the field.
         *
         *   <dt>{@link #ALIGN_RIGHT}
         *   <dd>The resulting string is padded with spaces on the left side
         *   until a width of <code>maxwidth</code> is reached, making the
         *   number right-aligned within the field.
         *
         *   <dt>{@link #ALIGN_CENTER}
         *   <dd>The resulting string is padded with spaces on both sides
         *   until a width of <code>maxwidth</code> is reached, making the
         *   number center-aligned within the field.
         * </dl>
         */
        public int align = ALIGN_NONE;
        /** Normal output {@linkplain #fse format} */
        public static final int FSE_NONE = 0;
        /** <i>FIX</i> output {@linkplain #fse format} */
        public static final int FSE_FIX = 1;
        /** <i>SCI</i> output {@linkplain #fse format} */
        public static final int FSE_SCI = 2;
        /** <i>ENG</i> output {@linkplain #fse format} */
        public static final int FSE_ENG = 3;
        /** No {@linkplain #align alignment} */
        public static final int ALIGN_NONE = 0;
        /** Left {@linkplain #align alignment} */
        public static final int ALIGN_LEFT = 1;
        /** Right {@linkplain #align alignment} */
        public static final int ALIGN_RIGHT = 2;
        /** Center {@linkplain #align alignment} */
        public static final int ALIGN_CENTER = 3;
    }
    private String align(StringBuffer s, NumberFormat format) {
        if (format.align == NumberFormat.ALIGN_LEFT) {
            while (s.length()<format.maxwidth)
                s.append(' ');
        } else if (format.align == NumberFormat.ALIGN_RIGHT) {
            while (s.length()<format.maxwidth)
                s.insert(0,' ');
        } else if (format.align == NumberFormat.ALIGN_CENTER) {
            while (s.length()<format.maxwidth) {
                s.append(' ');
                if (s.length()<format.maxwidth)
                    s.insert(0,' ');
            }
        }
        return s.toString();
    }
    private static byte[] ftoaDigits = new byte[65];
    private static StringBuffer ftoaBuf = new StringBuffer(40);
    private static StringBuffer ftoaExp = new StringBuffer(15);
    /**
     * This string holds the only valid characters to use in hexadecimal
     * numbers. Equals <code>"0123456789ABCDEF"</code>.
     * See {@link #assign(String,int)}.
     */
    public static final String hexChar = "0123456789ABCDEF";
    private String ftoa(NumberFormat format) {
        ftoaBuf.setLength(0);
        if ((this.exponent < 0 && this.mantissa != 0)) {
            ftoaBuf.append("nan");
            return align(ftoaBuf,format);
        }
        if ((this.exponent < 0 && this.mantissa == 0)) {
            ftoaBuf.append((this.sign!=0) ? "-inf":"inf");
            return align(ftoaBuf,format);
        }
        int digitsPerThousand;
        switch (format.base) {
            case 2:
                digitsPerThousand = 8;
                break;
            case 8:
                digitsPerThousand = 1000; // Disable thousands separator
                break;
            case 16:
                digitsPerThousand = 4;
                break;
            case 10:
            default:
                digitsPerThousand = 3;
                break;
        }
        if (format.thousand == 0)
            digitsPerThousand = 1000; // Disable thousands separator
        { tmp4.mantissa = this.mantissa; tmp4.exponent = this.exponent; tmp4.sign = this.sign; };
        int accurateDigits = tmp4.getDigits(ftoaDigits, format.base);
        if (format.base == 10 && (exponent > 0x4000003e || !isIntegral()))
            accurateDigits = 16; // Only display 16 digits for non-integers
        int precision;
        int pointPos = 0;
        do
        {
            int width = format.maxwidth-1; // subtract 1 for decimal point
            int prefix = 0;
            if (format.base != 10)
                prefix = 1; // want room for at least one "0" or "f/7/1"
            else if ((tmp4.sign!=0))
                width--; // subtract 1 for sign
            boolean useExp = false;
            switch (format.fse) {
                case NumberFormat.FSE_SCI:
                    precision = format.precision+1;
                    useExp = true;
                    break;
                case NumberFormat.FSE_ENG:
                    pointPos = floorMod(tmp4.exponent,3);
                    precision = format.precision+1+pointPos;
                    useExp = true;
                    break;
                case NumberFormat.FSE_FIX:
                case NumberFormat.FSE_NONE:
                default:
                    precision = 1000;
                    if (format.fse == NumberFormat.FSE_FIX)
                        precision = format.precision+1;
                    if (tmp4.exponent+1 >
                        width-(tmp4.exponent+prefix)/digitsPerThousand-prefix+
                        (format.removePoint ? 1:0) ||
                        tmp4.exponent+1 > accurateDigits ||
                        -tmp4.exponent >= width ||
                        -tmp4.exponent >= precision)
                    {
                        useExp = true;
                    } else {
                        pointPos = tmp4.exponent;
                        precision += tmp4.exponent;
                        if (tmp4.exponent > 0)
                            width -= (tmp4.exponent+prefix)/digitsPerThousand;
                        if (format.removePoint && tmp4.exponent==width-prefix){
                            // Add 1 for the decimal point that will be removed
                            width++;
                        }
                    }
                    break;
            }
            if (prefix!=0 && pointPos>=0)
                width -= prefix;
            ftoaExp.setLength(0);
            if (useExp) {
                ftoaExp.append('e');
                ftoaExp.append(tmp4.exponent-pointPos);
                width -= ftoaExp.length();
            }
            if (precision > accurateDigits)
                precision = accurateDigits;
            if (precision > width)
                precision = width;
            if (precision > width+pointPos) // In case of negative pointPos
                precision = width+pointPos;
            if (precision <= 0)
                precision = 1;
        }
        while (tmp4.carryWhenRounded(ftoaDigits,precision,format.base));
        tmp4.round(ftoaDigits,precision,format.base);
        // Start generating the string. First the sign
        if ((tmp4.sign!=0) && format.base == 10)
            ftoaBuf.append('-');
        // Save pointPos for hex/oct/bin prefixing with thousands-sep
        int pointPos2 = pointPos < 0 ? 0 : pointPos;
        // Add leading zeros (or f/7/1)
        char prefixChar = (format.base==10 || (tmp4.sign==0)) ? '0' :
            hexChar.charAt(format.base-1);
        if (pointPos < 0) {
            ftoaBuf.append(prefixChar);
            ftoaBuf.append(format.point);
            while (pointPos < -1) {
                ftoaBuf.append(prefixChar);
                pointPos++;
            }
        }
        // Add fractional part
        for (int i=0; i<precision; i++) {
            ftoaBuf.append(hexChar.charAt(ftoaDigits[i]));
            if (pointPos>0 && pointPos%digitsPerThousand==0)
                ftoaBuf.append(format.thousand);
            if (pointPos == 0)
                ftoaBuf.append(format.point);
            pointPos--;
        }
        if (format.fse == NumberFormat.FSE_NONE) {
            // Remove trailing zeros
            while (ftoaBuf.charAt(ftoaBuf.length()-1) == '0')
                ftoaBuf.setLength(ftoaBuf.length()-1);
        }
        if (format.removePoint) {
            // Remove trailing point
            if (ftoaBuf.charAt(ftoaBuf.length()-1) == format.point)
                ftoaBuf.setLength(ftoaBuf.length()-1);
        }
        // Add exponent
        ftoaBuf.append(ftoaExp.toString());
        // In case hex/oct/bin number, prefix with 0's or f/7/1's
        if (format.base!=10) {
            while (ftoaBuf.length()<format.maxwidth) {
                pointPos2++;
                if (pointPos2>0 && pointPos2%digitsPerThousand==0)
                    ftoaBuf.insert(0,format.thousand);
                if (ftoaBuf.length()<format.maxwidth)
                    ftoaBuf.insert(0,prefixChar);
            }
            if (ftoaBuf.charAt(0) == format.thousand)
                ftoaBuf.deleteCharAt(0);
        }
        return align(ftoaBuf,format);
    }
    private static NumberFormat tmpFormat = new NumberFormat();
    /**
     * Converts this <code>Real</code> to a <code>String</code> using
     * the default <code>NumberFormat</code>.
     *
     * <p>See {@link Real.NumberFormat NumberFormat} for a description
     * of the default way that numbers are formatted.
     * 
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td><td>
     *     <code>this.toString()
     * </td></tr><tr><td><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;
     * </i></td><td>
     *     130
     * </td></tr></table>
     *
     * @return a <code>String</code> representation of this <code>Real</code>.
     */
    public String toString() {
        tmpFormat.base = 10;
        return ftoa(tmpFormat);
    }
    /**
     * Converts this <code>Real</code> to a <code>String</code> using
     * the default <code>NumberFormat</code> with <code>base</code> set
     * according to the argument.
     *
     * <p>See {@link Real.NumberFormat NumberFormat} for a description
     * of the default way that numbers are formatted.
     * 
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td>
     * <td colspan="2">
     *     <code>this.toString()  // Works only for base-10</code>
     * </td></tr><tr><td rowspan="4" valign="top"><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;</i>
     * </td><td width="1%">base-2</td><td>
     *     120
     * </td></tr><tr><td>base-8</td><td>
     *     110
     * </td></tr><tr><td>base-10</td><td>
     *     130
     * </td></tr><tr><td>base-16&nbsp;&nbsp;</td><td>
     *     120
     * </td></tr></table>
     *
     * @param base the base for the conversion. Valid base values are
     *     2, 8, 10 and 16.
     * @return a <code>String</code> representation of this <code>Real</code>.
     */
    public String toString(int base) {
        tmpFormat.base = base;
        return ftoa(tmpFormat);
    }
    /**
     * Converts this <code>Real</code> to a <code>String</code> using
     * the given <code>NumberFormat</code>.
     *
     * <p>See {@link Real.NumberFormat NumberFormat} for a description of the
     * various ways that numbers may be formatted.
     * 
     * <p><table border="1" width="100%" cellpadding="3" cellspacing="0"
     * bgcolor="#e8d0ff"><tr><td width="1%"><i>
     * Equivalent&nbsp;</i><code>double</code><i>&nbsp;code:</i></td>
     * <td colspan="2">
     *     <code>String.format("%...g",this);  // Works only for base-10</code>
     * </td></tr><tr><td rowspan="4" valign="top"><i>
     * Execution&nbsp;time&nbsp;relative&nbsp;to&nbsp;add:&nbsp;&nbsp;</i>
     * </td><td width="1%">base-2</td><td>
     *     120
     * </td></tr><tr><td>base-8</td><td>
     *     110
     * </td></tr><tr><td>base-10</td><td>
     *     130
     * </td></tr><tr><td>base-16&nbsp;&nbsp;</td><td>
     *     120
     * </td></tr></table>
     *
     * @param format the number format to use in the conversion.
     * @return a <code>String</code> representation of this <code>Real</code>.
     */
    public String toString(NumberFormat format) {
        return ftoa(format);
    }
}
