package jscl.math;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import jscl.AngleUnit;
import jscl.JsclMathEngine;
import jscl.MathEngine;
import jscl.NumeralBase;
import jscl.math.function.Constant;
import jscl.math.function.ExtendedConstant;
import jscl.math.function.IConstant;
import jscl.text.ParseException;
import midpcalc.Real;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ExpressionTest {

    private static final String expressions = "-24.37581129610191-((2699.798527427213-4032.781981216783)*√(4657.120529143301)/6202.47137988087-ln(4435.662292261872)*sin(5134.044125137488)-sin(5150.617980207194)+sin(1416.6029070906816))\n" +
            "1.6796699432963022E11-((5709.375015847543-√(9582.238699996864))*3622.6983393324695*8262.5649407951-4677.148654973858*ln(7443.120012194502)*ln(8771.583007058995)-7796.8909039525515)\n" +
            "73260.62134636212-(((8211.239143650871+9653.120869092472/7201.080677271473-3675.134705789929/sin(7383.23886608315))+sin(8201.936690357508)/9797.420229466312/4487.554672699577))\n" +
            "7835770.323315129-(3053.1562785415554*2564.6140313677965+ln(3376.462881190876)/2722.807595157415+cos(1654.053577173823)/4481.384989253306+cos(8539.28578313432)+5603.074520175994)\n" +
            "7.219848990044144E11-((((9742.199604684844*8637.637793906879)-8613.230244786755+√(1026.016931180783))*8580.654028379886+ln(391.54269092744664)/√(4341.52889100337))+sin(508.2338437131828))\n" +
            "6685.424634765305-((3746.8598111083793*sin(4784.284503822155)-ln(2218.167104685851)-sin(4794.102351616163))+3063.0457850324233-9545.89841181986/7482.886158430515/√(4001.7788453452417))\n" +
            "4108.351107289166-((4102.493099763215-cos(5125.896955144614))+cos(3540.5378825149537)/5495.082697662915-8681.097948569084/cos(8032.923414105565)/4501.859274666647-cos(2711.854814853617))\n" +
            "-1.0620650024203222-(((750.111466515082-9102.643276012855+3065.780766976849+2861.8661641038534)*3536.5716528042535/1106.4220238862831/7308.354645022433/sin(1173.0557272435349)))\n" +
            "76379.44709543366-(((9932.156860771898+ln(7185.939808298219)*7687.141207402175)+cos(8185.971595673607)+ln(3977.781005305916)+cos(2376.681088176604)*2201.8644681719-√(3135.5682620873513)))\n" +
            "635.3559760598341-((8075.628923197531/8255.66812901165+√(2936.433021287237)*sin(7502.632251185349)*sin(3225.272171990918)+613.2028126347367+ln(8485.99141046724))-cos(8518.190742544848))\n" +
            "-7.51034737891574E7-(4640.543920377892/√(4363.843503017953)*√(7152.285785189239)*sin(7908.617128515873)*√(6906.317696310425)*6562.864387786373/ln(4988.784292770342)-sin(5488.826440303076))\n" +
            "-5932.595870545627-(((3010.4402565484047-3218.3878293708044)+sin(9074.010686307622))/cos(7656.587621759453)/cos(1187.7115449548426)+cos(2207.5981975517957)/sin(7170.633198376899)+cos(129.16231777575283))\n" +
            "14603.51285508874-((1505.6670065700584-ln(7760.688872668162)-cos(1521.0119520475184)+5874.745001223881+sin(5672.757849045151)*sin(9740.028947007728)+7239.645067283123)-ln(1198.788813287901))\n" +
            "13789.681143529104-(4837.182498312745-sin(8683.238702053257)+9725.382455542274-ln(6866.318581911774)*√(7639.899860231787)-cos(8486.508690243441)/√(3325.7578426126165)/sin(5655.089763857597))\n" +
            "5.9142041337333955E7-((((6945.350108837433-6875.255304556105-5503.241468583639*ln(4882.916231493727)-8221.764146581652)*4816.727562192865)-47.13141200212378)*sin(7032.925165237175))\n" +
            "5.307098467139001E7-((((1472.5507104204128-2244.0144093640956)/337.94074333738934-6119.909773145814/4030.814210676087)+7955.59068044787)*6674.093078737379+cos(1072.8762639281485))\n" +
            "2.4791276864495695E8-(((4650.104984872984*5990.69176729321*ln(7326.221240600894)-√(4166.293207980269)-cos(2930.9607978551735))+4892.051672831694)-√(4643.4262014756005)*ln(4322.391733256239))\n" +
            "1.000473116354486E7-((1856.1678375267843*3375.5973472957558+8102.216834762455*460.5133278219642)+ln(1077.2976545272872)+9836.94091820254/cos(561.8742170542756)*sin(9587.941076809435))\n" +
            "1.043950271691602E7-((((3594.0668967195334*2903.435684617801-ln(431.72508853349336)+√(2631.9717706394795)+4315.178672680215)+sin(1034.406679999502)/cos(7200.345388541185))+sin(8030.470700471927)))\n" +
            "-4612.867091103858-(((117.31001770566519/6314.371065466436/5793.914918630644*1016.2707467350263*8539.984705173652)/3647.0016733225143*8871.091071924995-4680.559579608435))";

    public static void main(String[] args) {
        System.out.println("Result: " + getWolframAlphaResult("APP_ID", "-24.37581129610191-((2699.798527427213-4032.781981216783)*√(4657.120529143301)/6202.47137988087-ln(4435.662292261872)*sin(5134.044125137488)-sin(5150.617980207194)+sin(1416.6029070906816))"));
        /*final StringTokenizer st = new StringTokenizer(expressions, "\n");
          if ( st.hasMoreTokens() ) {
              final String expression = st.nextToken();
              final String result = getWolframAlphaResult("APP_ID", expression);
              System.out.println("Expression: " + expression);
              System.out.println("Result: " + result);
              try {
                  final Double value = Double.valueOf(result);
              } catch (NumberFormatException e) {
                  e.printStackTrace();
              }
          }*/
    }

    @Nullable
    public static String getWolframAlphaResult(@Nonnull String appId, @Nonnull String expression) {
        String result = null;

        URL wolframAlphaUrl;
        try {
            wolframAlphaUrl = new URL("http://api.wolframalpha.com/v2/query?input=" + expression + "&appid=" + appId + "&format=plaintext&podtitle=Decimal+approximation");

            final URLConnection connection = wolframAlphaUrl.openConnection();
            BufferedReader in = null;
            try {
                in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                String line;
                while ((line = in.readLine()) != null) {
                    System.out.println(line);
                    if (line.contains("<plaintext>")) {
                        result = line.replace("<plaintext>", "").replace("</plaintext>", "").replace("...", "").trim();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (in != null) {
                    in.close();
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Test
    public void testImag() throws Exception {
        assertEquals("-i", Expression.valueOf("i^3").numeric().toString());
    }

    @Test
    public void testConstants() throws Exception {
        assertTrue(Expression.valueOf("3+4").getConstants().isEmpty());

        Set<? extends Constant> constants = Expression.valueOf("3+4*t").getConstants();
        assertTrue(constants.size() == 1);
        assertTrue(constants.contains(new Constant("t")));

        IConstant constant = null;

        final JsclMathEngine me = JsclMathEngine.getInstance();
        try {
            final ExtendedConstant.Builder t_0 = new ExtendedConstant.Builder(new Constant("t_0"), 1d);
            constant = me.getConstantsRegistry().addOrUpdate(t_0.create());

            constants = Expression.valueOf("3+4*t_0+t_0+t_1").getConstants();
            assertTrue(constants.size() == 2);
            assertTrue(constants.contains(new Constant("t_0")));
            assertTrue(constants.contains(new Constant("t_1")));

            final Expression expression = Expression.valueOf("2*t_0+5*t_1");

            assertEquals("7", expression.substitute(new Constant("t_1"), Expression.valueOf(1.0)).numeric().toString());
            assertEquals("12", expression.substitute(new Constant("t_1"), Expression.valueOf(2.0)).numeric().toString());
            assertEquals("27", expression.substitute(new Constant("t_1"), Expression.valueOf(5.0)).numeric().toString());

        } finally {
            if (constant != null) {
                final ExtendedConstant.Builder jBuilder = new ExtendedConstant.Builder(new Constant(constant.getName()), (String) null);
                me.getConstantsRegistry().addOrUpdate(jBuilder.create());
            }
        }
    }

    @Test
    public void testExpressions() throws Exception {
        assertEquals("3", Expression.valueOf("3").numeric().toString());
        assertEquals("0.6931471805599453", Expression.valueOf("ln(2)").numeric().toString());
        assertEquals("1", Expression.valueOf("lg(10)").numeric().toString());
        assertEquals("0", Expression.valueOf("eq(0, 1)").numeric().toString());
        assertEquals("1", Expression.valueOf("eq(1, 1)").numeric().toString());

        assertEquals("24", Expression.valueOf("4!").numeric().toString());
        try {
            Expression.valueOf("(-3+2)!").numeric().toString();
            fail();
        } catch (ArithmeticException e) {

        }
        assertEquals("24", Expression.valueOf("(2+2)!").numeric().toString());
        assertEquals("120", Expression.valueOf("(2+2+1)!").numeric().toString());
        assertEquals("24", Expression.valueOf("(2.0+2.0)!").numeric().toString());
        assertEquals("24", Expression.valueOf("4.0!").numeric().toString());
        assertEquals("48", Expression.valueOf("2*4.0!").numeric().toString());
        assertEquals("40320", Expression.valueOf("(2*4.0)!").numeric().toString());

        final JsclMathEngine me = JsclMathEngine.getInstance();
        final AngleUnit angleUnits = me.getAngleUnits();
        try {
            me.setAngleUnits(AngleUnit.rad);
            assertEquals("-0.9055783620066238", Expression.valueOf("sin(4!)").numeric().toString());
        } finally {
            me.setAngleUnits(angleUnits);
        }
        assertEquals("1", Expression.valueOf("(3.14/3.14)!").numeric().toString());
        assertEquals("1", Expression.valueOf("2/2!").numeric().toString());
        try {
            assertEquals("3.141592653589793!", Expression.valueOf("3.141592653589793!").numeric().toString());
            fail();
        } catch (NotIntegerException e) {

        }
        assertEquals("0.5235987755982988", Expression.valueOf("3.141592653589793/3!").numeric().toString());
        try {
            assertEquals("3.141592653589793/3.141592653589793!", Expression.valueOf("3.141592653589793/3.141592653589793!").numeric().toString());
            fail();
        } catch (ArithmeticException e) {

        }
        try {
            assertEquals("7.2!", Expression.valueOf("7.2!").numeric().toString());
            fail();
        } catch (NotIntegerException e) {
        }

        try {
            assertEquals("ln(7.2!)", Expression.valueOf("ln(7.2!)").numeric().toString());
            fail();
        } catch (NotIntegerException e) {
        }

        assertEquals("ln(7.2!)", Expression.valueOf("ln(7.2!)").simplify().toString());


        assertEquals("36", Expression.valueOf("3!^2").numeric().toString());
        assertEquals("1", Expression.valueOf("(π/π)!").numeric().toString());
        assertEquals("720", Expression.valueOf("(3!)!").numeric().toString());
        assertEquals("36", Expression.valueOf("3!*3!").numeric().toString());

        assertEquals("100", Expression.valueOf("0.1E3").numeric().toString());

        final AngleUnit defaultAngleUnits = me.getAngleUnits();
        try {
            me.setAngleUnits(AngleUnit.rad);
            assertEquals("0.0174532925199433", Expression.valueOf("1°").numeric().toString());
            assertEquals("0.0349065850398866", Expression.valueOf("2°").numeric().toString());
            assertEquals("0.0523598775598299", Expression.valueOf("3°").numeric().toString());
            assertEquals("0.2617993877991495", Expression.valueOf("3°*5").numeric().toString());
            assertEquals("0.0027415567780804", Expression.valueOf("3°^2").numeric().toString());
            assertEquals("0.0109662271123215", Expression.valueOf("3!°^2").numeric().toString());
            assertEquals("0.0009138522593601", Expression.valueOf("3°°").numeric().toString());
            assertEquals("0.0872664625997165", Expression.valueOf("5°").numeric().toString());
            assertEquals("2.05235987755983", Expression.valueOf("2+3°").numeric().toString());
        } finally {
            me.setAngleUnits(defaultAngleUnits);
        }

        try {
            me.setAngleUnits(AngleUnit.deg);
            assertEquals("1", Expression.valueOf("1°").numeric().toString());
            assertEquals("2", Expression.valueOf("2°").numeric().toString());
            assertEquals("3", Expression.valueOf("3°").numeric().toString());
            assertEquals("15", Expression.valueOf("3°*5").numeric().toString());
            assertEquals("9", Expression.valueOf("3°^2").numeric().toString());
            assertEquals("36", Expression.valueOf("3!°^2").numeric().toString());
            assertEquals("3", Expression.valueOf("3°°").numeric().toString());
            assertEquals("5", Expression.valueOf("5°").numeric().toString());
            assertEquals("5", Expression.valueOf("2+3°").numeric().toString());
        } finally {
            me.setAngleUnits(defaultAngleUnits);
        }

        assertEquals("6", Expression.valueOf("2*∂(3*x,x)").expand().toString());
        assertEquals("3", Expression.valueOf("∂(3*x,x)").expand().toString());
        assertEquals("12", Expression.valueOf("∂(x^3,x,2)").expand().toString());
        assertEquals("3*a", Expression.valueOf("∂(3*x*a,x)").expand().toString());
        assertEquals("0", Expression.valueOf("∂(3*x*a,x,0.011,2)").expand().toString());
        assertEquals("0", Expression.valueOf("2*∂(3*x*a,x,0.011,2)").expand().toString());
        assertEquals("ln(8)+lg(8)*ln(8)", Expression.valueOf("ln(8)*lg(8)+ln(8)").expand().toString());
        assertEquals("3.957364376505986", Expression.valueOf("ln(8)*lg(8)+ln(8)").numeric().toString());

        assertEquals("4!", Expression.valueOf("4.0!").simplify().toString());
        assertEquals("4°", Expression.valueOf("4.0°").simplify().toString());
        assertEquals("30°", Expression.valueOf("30°").simplify().toString());


        assertEquals("1", Expression.valueOf("abs(1)").numeric().toString());
        assertEquals("0", Expression.valueOf("abs(0)").numeric().toString());
        assertEquals("0", Expression.valueOf("abs(-0)").numeric().toString());
        assertEquals("1", Expression.valueOf("abs(-1)").numeric().toString());
        assertEquals("∞", Expression.valueOf("abs(-∞)").numeric().toString());

        assertEquals("1", Expression.valueOf("abs(i)").numeric().toString());
        assertEquals("0", Expression.valueOf("abs(0+0*i)").numeric().toString());
        assertEquals("1", Expression.valueOf("abs(-i)").numeric().toString());
        assertEquals("2.23606797749979", Expression.valueOf("abs(2-i)").numeric().toString());
        assertEquals("2.23606797749979", Expression.valueOf("abs(2+i)").numeric().toString());
        assertEquals("2.82842712474619", Expression.valueOf("abs(2+2*i)").numeric().toString());
        assertEquals("2.82842712474619", Expression.valueOf("abs(2-2*i)").numeric().toString());

        try {
            final ExtendedConstant.Builder k = new ExtendedConstant.Builder(new Constant("k"), 2.8284271247461903);
            me.getConstantsRegistry().addOrUpdate(k.create());
            assertEquals("k", Expression.valueOf("k").numeric().toString());
            assertEquals("k", Expression.valueOf("k").simplify().toString());
            assertEquals("k", Expression.valueOf("k").simplify().toString());
            assertEquals("k^3", Expression.valueOf("k*k*k").simplify().toString());
            assertEquals("22.62741699796953", Expression.valueOf("k*k*k").numeric().toString());
        } finally {
            final ExtendedConstant.Builder k = new ExtendedConstant.Builder(new Constant("k"), (String) null);
            me.getConstantsRegistry().addOrUpdate(k.create());
        }

        try {
            final ExtendedConstant.Builder k_1 = new ExtendedConstant.Builder(new Constant("k_1"), 3d);
            me.getConstantsRegistry().addOrUpdate(k_1.create());
            assertEquals("k_1", Expression.valueOf("k_1").numeric().toString());
            assertEquals("k_1", Expression.valueOf("k_1[0]").numeric().toString());
            assertEquals("k_1", Expression.valueOf("k_1[2]").numeric().toString());
        } finally {
            final ExtendedConstant.Builder k_1 = new ExtendedConstant.Builder(new Constant("k_1"), (String) null);
            me.getConstantsRegistry().addOrUpdate(k_1.create());
        }

        Generic expression = me.simplifyGeneric("cos(t)+∂(cos(t),t)");
        Generic substituted = expression.substitute(new Constant("t"), Expression.valueOf(100d));
        assertEquals("-1.158455930679138", substituted.numeric().toString());

        expression = me.simplifyGeneric("abs(t)^2+2!");
        substituted = expression.substitute(new Constant("t"), Expression.valueOf(10d));
        assertEquals("102", substituted.numeric().toString());


        expression = me.simplifyGeneric("abs(t)^2+10%");
        substituted = expression.substitute(new Constant("t"), Expression.valueOf(10d));
        assertEquals("110", substituted.numeric().toString());

        expression = me.simplifyGeneric("abs(t)^2-10%");
        substituted = expression.substitute(new Constant("t"), Expression.valueOf(10d));
        assertEquals("90", substituted.numeric().toString());

        expression = me.simplifyGeneric("(abs(t)^2)*10%");
        substituted = expression.substitute(new Constant("t"), Expression.valueOf(10d));
        assertEquals("10", substituted.numeric().toString());

        expression = me.simplifyGeneric("(abs(t)^2)/10%");
        substituted = expression.substitute(new Constant("t"), Expression.valueOf(10d));
        assertEquals("1000", substituted.numeric().toString());

        expression = me.simplifyGeneric("abs(t)^2+t%");
        substituted = expression.substitute(new Constant("t"), Expression.valueOf(10d));
        assertEquals("110", substituted.numeric().toString());

        expression = me.simplifyGeneric("abs(t)^2-t%");
        substituted = expression.substitute(new Constant("t"), Expression.valueOf(10d));
        assertEquals("90", substituted.numeric().toString());

        expression = me.simplifyGeneric("(abs(t)^2)*t%");
        substituted = expression.substitute(new Constant("t"), Expression.valueOf(10d));
        assertEquals("10", substituted.numeric().toString());

        expression = me.simplifyGeneric("(abs(t)^2)/t%");
        substituted = expression.substitute(new Constant("t"), Expression.valueOf(10d));
        assertEquals("1000", substituted.numeric().toString());

        expression = me.simplifyGeneric("Σ(t, t, 0, 10)");
        assertEquals("55", expression.numeric().toString());

        expression = me.simplifyGeneric("Σ(t, t, 0, 10)");
        substituted = expression.substitute(new Constant("t"), Expression.valueOf(10d));
        assertEquals("55", substituted.numeric().toString());

        expression = me.simplifyGeneric("10*Σ(t, t, 0, 10)");
        substituted = expression.substitute(new Constant("t"), Expression.valueOf(10d));
        assertEquals("550", substituted.numeric().toString());

        expression = me.simplifyGeneric("t*Σ(t, t, 0, 10)");
        substituted = expression.substitute(new Constant("t"), Expression.valueOf(10d));
        assertEquals("550", substituted.numeric().toString());

        expression = me.simplifyGeneric("t*Σ(t+100%, t, 0, 10)");
        substituted = expression.substitute(new Constant("t"), Expression.valueOf(10d));
        assertEquals("1100", substituted.numeric().toString());

        assertEquals("i*t", Expression.valueOf("i*t").expand().simplify().toString());
        assertEquals("t", Expression.valueOf("t").simplify().toString());
        assertEquals("t^3", Expression.valueOf("t*t*t").simplify().toString());

        try {
            Expression.valueOf("t").numeric();
            fail();
        } catch (ArithmeticException e) {
        }

        final ExtendedConstant.Builder t = new ExtendedConstant.Builder(new Constant("t"), (String) null);
        me.getConstantsRegistry().addOrUpdate(t.create());
        try {
            Expression.valueOf("t").numeric();
            fail();
        } catch (ArithmeticException e) {
        }

        assertEquals("√(1+t)/(1+t)", Expression.valueOf("1/√(1+t)").simplify().toString());

        assertEquals("t", Expression.valueOf("t").simplify().toString());
        assertEquals("t^3", Expression.valueOf("t*t*t").simplify().toString());

        try {
            me.setAngleUnits(AngleUnit.rad);
            assertEquals("0.6931471805599453+Π*i", Expression.valueOf("ln(-2)").numeric().toString());
        } finally {
            me.setAngleUnits(AngleUnit.deg);
        }
        assertEquals("-2/57", Expression.valueOf("1/(-57/2)").simplify().toString());
        assertEquals("sin(30)", Expression.valueOf("sin(30)").expand().toString());
        assertEquals("sin(n)", Expression.valueOf("sin(n)").expand().toString());
        assertEquals("sin(n!)", Expression.valueOf("sin(n!)").expand().toString());
        assertEquals("sin(n°)", Expression.valueOf("sin(n°)").expand().toString());
        assertEquals("sin(30°)", Expression.valueOf("sin(30°)").expand().toString());
        assertEquals("0.4999999999999999", Expression.valueOf("sin(30°)").expand().numeric().toString());
        assertEquals("sin(2!)", Expression.valueOf("sin(2!)").expand().toString());

        assertEquals("12", Expression.valueOf("3*(3+1)").expand().toString());
        assertEquals("114.5915590261647", Expression.valueOf("deg(2)").numeric().toString());
        try {
            assertEquals("-0.1425465430742778", Expression.valueOf("∏(tan(3))").numeric().toString());
            fail();
        } catch (ParseException e) {
        }
        try {
            assertEquals("-0.14255", Expression.valueOf("sin(2,2)").expand().numeric().toString());
            fail();
        } catch (ParseException e) {
        }
        try {
            assertEquals("114.59155902616465", Expression.valueOf("deg(2,2)").numeric().toString());
            fail();
        } catch (ParseException e) {
        }

        assertEquals("0.4999999999999999", Expression.valueOf("sin(30°)").numeric().toString());
        assertEquals("π", Expression.valueOf("√(π)^2").simplify().toString());
        assertEquals("π", Expression.valueOf("√(π^2)").simplify().toString());
        assertEquals("π^2", Expression.valueOf("√(π^2*π^2)").simplify().toString());
        assertEquals("π^3", Expression.valueOf("√(π^4*π^2)").simplify().toString());
        assertEquals("e*π^2", Expression.valueOf("√(π^4*e^2)").simplify().toString());

        assertEquals("1", Expression.valueOf("(π/π)!").numeric().toString());

        // in deg mode π=180 and factorial of 180 is calculating
        assertEquals("0", Expression.valueOf("Π/Π!").numeric().toString());

        assertEquals("0.0000000000000001*i", Expression.valueOf("exp((Π*i))+1").numeric().toString());
        assertEquals("20*x^3", Expression.valueOf("∂(5*x^4, x)").expand().simplify().toString());
        assertEquals("25*x", Expression.valueOf("5*x*5").expand().simplify().toString());
        assertEquals("20*x", Expression.valueOf("5*x*4").expand().simplify().toString());

        try {
            me.evaluate("0b:π");
            fail();
        } catch (ParseException e) {
            // ok
        }

        try {
            me.evaluate("0b:10π");
            fail();
        } catch (ParseException e) {
            // ok
        }

        try {
            me.setNumeralBase(NumeralBase.hex);

            assertEquals("0.EEEEEEEEEEEEEC88", me.evaluate("0x:E/0x:F"));
            assertEquals("E/F", me.simplify("0x:E/0x:F"));

            assertEquals("0.EEEEEEEEEEEEEC88", me.evaluate("E/F"));
            assertEquals("E/F", me.simplify("E/F"));

        } finally {
            me.setNumeralBase(NumeralBase.dec);
        }

        try {
            me.setAngleUnits(AngleUnit.rad);
            assertEquals("-1.570796326794897+2.993222846126381*i", me.evaluate("asin(-10)"));
            assertEquals("-1.570796326794897+1.316957896924817*i", me.evaluate("asin(-2)"));
            assertEquals("-1.570796326794897", me.evaluate("asin(-1)"));
            assertEquals("0", me.evaluate("asin(0)"));
            assertEquals("1.570796326794897", me.evaluate("asin(1)"));
            assertEquals("1.570796326794897-1.316957896924817*i", me.evaluate("asin(2)"));
            assertEquals("1.570796326794897-2.993222846126381*i", me.evaluate("asin(10)"));

            assertEquals("Π-2.993222846126379*i", me.evaluate("acos(-10)"));
            assertEquals("Π-1.316957896924816*i", me.evaluate("acos(-2)"));
            assertEquals("Π", me.evaluate("acos(-1)"));
            assertEquals("1.570796326794897", me.evaluate("acos(0)"));
            assertEquals("0", me.evaluate("acos(1)"));
            assertEquals("1.316957896924816*i", me.evaluate("acos(2)"));
            assertEquals("2.993222846126379*i", me.evaluate("acos(10)"));

            assertEquals("-1.471127674303735", me.evaluate("atan(-10)"));
            assertEquals("-1.10714871779409", me.evaluate("atan(-2)"));
            assertEquals("-0.7853981633974483", me.evaluate("atan(-1)"));
            assertEquals("0", me.evaluate("atan(0)"));
            assertEquals("0.7853981633974483", me.evaluate("atan(1)"));
            assertEquals("1.10714871779409", me.evaluate("atan(2)"));
            assertEquals("1.471127674303735", me.evaluate("atan(10)"));

            for (int i = -10; i < 10; i++) {
                assertEquals(me.evaluate("3.14159265358979323846/2 - atan(" + i + ")"), me.evaluate("acot(" + i + ")"));
            }

            assertEquals("3.041924001098631", me.evaluate("3.14159265358979323846/2 - atan(-10)"));
            assertEquals("3.041924001098631", me.evaluate("acot(-10)"));
            assertEquals("1.570796326794897", me.evaluate("acot(0)"));
            assertEquals("2.677945044588987", me.evaluate("acot(-2)"));
            assertEquals("2.356194490192345", me.evaluate("acot(-1)"));
            assertEquals("0.7853981633974483", me.evaluate("acot(1)"));
            assertEquals("0.4636476090008062", me.evaluate("acot(2)"));
            assertEquals("0.0996686524911619", me.evaluate("acot(10)"));

            assertEquals("Π", me.evaluate("π"));
            assertEquals("Π", me.evaluate("3.14159265358979323846"));
        } finally {
            me.setAngleUnits(AngleUnit.deg);
        }

        assertEquals("180", me.evaluate("Π"));
        assertEquals("180", me.evaluate("200-10%"));

        assertEquals("∞", me.evaluate("1/0"));
        assertEquals("-∞", me.evaluate("-1/0"));
        assertEquals("-∞", me.evaluate("-1/0"));
        assertEquals("∞", me.evaluate("(1 + 2) / (5 - 3 - 2)"));
        assertEquals("∞", me.evaluate("(1 + 2) / (5.1 - 3.1 - 2.0 )"));
        assertEquals("∞", me.evaluate("1/0"));
    }

    @Test
    public void testAngleUnits() throws Exception {
        final MathEngine mathEngine = JsclMathEngine.getInstance();

        final AngleUnit defaultAngleUnits = mathEngine.getAngleUnits();

        for (AngleUnit angleUnits : AngleUnit.values()) {
            try {
                mathEngine.setAngleUnits(angleUnits);
                mathEngine.evaluate("sin(2)");
                mathEngine.evaluate("asin(2)");
            } finally {
                mathEngine.setAngleUnits(defaultAngleUnits);
            }
        }

        try {
            mathEngine.setAngleUnits(AngleUnit.rad);
            assertEquals("Π", mathEngine.evaluate("π"));
            assertEquals("π/2", mathEngine.simplify("π/2"));
            assertEquals(mathEngine.evaluate("0.9092974268256816953960198659117448427022549714478902683789"), mathEngine.evaluate("sin(2)"));
            assertEquals(mathEngine.evaluate("0.1411200080598672221007448028081102798469332642522655841518"), mathEngine.evaluate("sin(3)"));
            assertEquals(mathEngine.evaluate("0"), mathEngine.evaluate("sin(0)"));

            assertEquals(mathEngine.evaluate("1"), mathEngine.evaluate("cos(0)"));
            assertEquals(mathEngine.evaluate("0.8623188722876839341019385139508425355100840085355108292801"), mathEngine.evaluate("cos(100)"));
            assertEquals(mathEngine.evaluate("-0.416146836547142386997568229500762189766000771075544890755"), mathEngine.evaluate("cos(2)"));

            assertEquals(mathEngine.evaluate("-2.185039863261518991643306102313682543432017746227663164562"), mathEngine.evaluate("tan(2)"));
            assertEquals(mathEngine.evaluate("-0.142546543074277805295635410533913493226092284901804647633"), mathEngine.evaluate("tan(3)"));
            assertEquals(mathEngine.evaluate("0.6483608274590872"), mathEngine.evaluate("tan(10)"));

            assertEquals(mathEngine.evaluate("0.6420926159343306"), mathEngine.evaluate("cot(1)"));
            assertEquals(mathEngine.evaluate("-0.457657554360285763750277410432047276428486329231674329641"), mathEngine.evaluate("cot(2)"));
            assertEquals(mathEngine.evaluate("-7.015252551434533469428551379526476578293103352096353838156"), mathEngine.evaluate("cot(3)"));
        } finally {
            mathEngine.setAngleUnits(defaultAngleUnits);
        }

        try {
            mathEngine.setAngleUnits(AngleUnit.deg);
            assertEquals(mathEngine.evaluate("0.9092974268256816953960198659117448427022549714478902683789"), mathEngine.evaluate("sin(deg(2))"));
            assertEquals(mathEngine.evaluate("0.1411200080598672221007448028081102798469332642522655841518"), mathEngine.evaluate("sin(deg(3))"));
            assertEquals(mathEngine.evaluate("0"), mathEngine.evaluate("sin(deg(0))"));

            assertEquals(mathEngine.evaluate("1"), mathEngine.evaluate("cos(deg(0))"));
            assertEquals(mathEngine.evaluate("0.8623188722876839341019385139508425355100840085355108292801"), mathEngine.evaluate("cos(deg(100))"));
            assertEquals(mathEngine.evaluate("-0.416146836547142386997568229500762189766000771075544890755"), mathEngine.evaluate("cos(deg(2))"));

            assertEquals(mathEngine.evaluate("-2.185039863261518991643306102313682543432017746227663164562"), mathEngine.evaluate("tan(deg(2))"));
            assertEquals(mathEngine.evaluate("-0.142546543074277805295635410533913493226092284901804647633"), mathEngine.evaluate("tan(deg(3))"));
            assertEquals(mathEngine.evaluate("0.6483608274590872"), mathEngine.evaluate("tan(deg(10))"));

            assertEquals(mathEngine.evaluate("0.6420926159343306"), mathEngine.evaluate("cot(deg(1))"));
            assertEquals(mathEngine.evaluate("-0.457657554360285763750277410432047276428486329231674329641"), mathEngine.evaluate("cot(deg(2))"));
            assertEquals(mathEngine.evaluate("-7.015252551434533469428551379526476578293103352096353838156"), mathEngine.evaluate("cot(deg(3))"));
        } finally {
            mathEngine.setAngleUnits(defaultAngleUnits);
        }

        try {
            mathEngine.setAngleUnits(AngleUnit.rad);
            assertEquals(mathEngine.evaluate("-0.5235987755982989"), mathEngine.evaluate("asin(-0.5)"));
            assertEquals(mathEngine.evaluate("-0.47349551215005636"), mathEngine.evaluate("asin(-0.456)"));
            assertEquals(mathEngine.evaluate("0.32784124364198347"), mathEngine.evaluate("asin(0.322)"));

            assertEquals(mathEngine.evaluate("1.2429550831529133"), mathEngine.evaluate("acos(0.322)"));
            assertEquals(mathEngine.evaluate("1.5587960387762325"), mathEngine.evaluate("acos(0.012)"));
            assertEquals(mathEngine.evaluate("1.6709637479564563"), mathEngine.evaluate("acos(-0.1)"));

            assertEquals(mathEngine.evaluate("0.3805063771123649"), mathEngine.evaluate("atan(0.4)"));
            assertEquals(mathEngine.evaluate("0.09966865249116204"), mathEngine.evaluate("atan(0.1)"));
            assertEquals(mathEngine.evaluate("-0.5404195002705842"), mathEngine.evaluate("atan(-0.6)"));

            assertEquals(mathEngine.evaluate("1.0603080048781206"), mathEngine.evaluate("acot(0.56)"));
            // todo serso: wolfram alpha returns -0.790423 instead of 2.3511694068615325 (-PI)
            assertEquals(mathEngine.evaluate("2.3511694068615325"), mathEngine.evaluate("acot(-0.99)"));
            // todo serso: wolfram alpha returns -1.373401 instead of 1.7681918866447774 (-PI)
            assertEquals(mathEngine.evaluate("1.7681918866447774"), mathEngine.evaluate("acot(-0.2)"));
        } finally {
            mathEngine.setAngleUnits(defaultAngleUnits);
        }

        try {
            mathEngine.setAngleUnits(AngleUnit.deg);
            assertEquals(mathEngine.evaluate("deg(-0.5235987755982989)"), mathEngine.evaluate("asin(-0.5)"));
            assertEquals(mathEngine.evaluate("-27.129294464583623"), mathEngine.evaluate("asin(-0.456)"));
            assertEquals(mathEngine.evaluate("18.783919611005786"), mathEngine.evaluate("asin(0.322)"));

            assertEquals(mathEngine.evaluate("71.21608038899423"), mathEngine.evaluate("acos(0.322)"));
            assertEquals(mathEngine.evaluate("89.31243414358914"), mathEngine.evaluate("acos(0.012)"));
            assertEquals(mathEngine.evaluate("95.73917047726678"), mathEngine.evaluate("acos(-0.1)"));

            assertEquals(mathEngine.evaluate("deg(0.3805063771123649)"), mathEngine.evaluate("atan(0.4)"));
            assertEquals(mathEngine.evaluate("deg(0.09966865249116204)"), mathEngine.evaluate("atan(0.1)"));
            assertEquals(mathEngine.evaluate("deg(-0.5404195002705842)"), mathEngine.evaluate("atan(-0.6)"));

            assertEquals(mathEngine.evaluate("deg(1.0603080048781206)"), mathEngine.evaluate("acot(0.56)"));
            // todo serso: wolfram alpha returns -0.790423 instead of 2.3511694068615325 (-PI)
            assertEquals(mathEngine.evaluate("134.7120839334429"), mathEngine.evaluate("acot(-0.99)"));
            // todo serso: wolfram alpha returns -1.373401 instead of 1.7681918866447774 (-PI)
            assertEquals(mathEngine.evaluate("deg(1.7681918866447774)"), mathEngine.evaluate("acot(-0.2)"));
        } finally {
            mathEngine.setAngleUnits(defaultAngleUnits);
        }

        try {
            mathEngine.setAngleUnits(AngleUnit.deg);
            assertEquals(mathEngine.evaluate("0.0348994967025009716459951816253329373548245760432968714250"), mathEngine.evaluate("(sin(2))"));
            assertEquals(mathEngine.evaluate("0.0523359562429438327221186296090784187310182539401649204835"), mathEngine.evaluate("(sin(3))"));
            assertEquals(mathEngine.evaluate("0"), mathEngine.evaluate("sin(0)"));

            assertEquals(mathEngine.evaluate("1"), mathEngine.evaluate("cos(0)"));
            assertEquals(mathEngine.evaluate("-0.1736481776669303"), mathEngine.evaluate("(cos(100))"));
            assertEquals(mathEngine.evaluate("0.9993908270190958"), mathEngine.evaluate("(cos(2))"));

            assertEquals(mathEngine.evaluate("0.03492076949174773"), mathEngine.evaluate("(tan(2))"));
            assertEquals(mathEngine.evaluate("0.05240777928304121"), mathEngine.evaluate("(tan(3))"));
            assertEquals(mathEngine.evaluate("0.17632698070846498"), mathEngine.evaluate("(tan(10))"));

            assertEquals(mathEngine.evaluate("57.28996163075943"), mathEngine.evaluate("(cot(1))"));
            assertEquals(mathEngine.evaluate("28.636253282915604"), mathEngine.evaluate("(cot(2))"));
            assertEquals(mathEngine.evaluate("19.081136687728208"), mathEngine.evaluate("(cot(3))"));
        } finally {
            mathEngine.setAngleUnits(defaultAngleUnits);
        }

        try {
            mathEngine.setAngleUnits(AngleUnit.rad);
            testSinEqualsToSinh(mathEngine, 0d);
            testSinEqualsToSinh(mathEngine, 1d, "0.8414709848078965");
            testSinEqualsToSinh(mathEngine, 3d, "0.1411200080598672");
            testSinEqualsToSinh(mathEngine, 6d);
            testSinEqualsToSinh(mathEngine, -1d, "-0.8414709848078965");
            testSinEqualsToSinh(mathEngine, -3.3d, "0.1577456941432482");
            testSinEqualsToSinh(mathEngine, -232.2d, "0.2742948637368958");
        } finally {
            mathEngine.setAngleUnits(defaultAngleUnits);
        }

        try {
            mathEngine.setAngleUnits(AngleUnit.deg);
            testSinEqualsToSinh(mathEngine, 0d);
            testSinEqualsToSinh(mathEngine, 1d, "0.0174524064372835");
            testSinEqualsToSinh(mathEngine, 3d, "0.0523359562429438");
            testSinEqualsToSinh(mathEngine, 6d, "0.1045284632676535");
            testSinEqualsToSinh(mathEngine, -1d, "-0.0174524064372835");
            testSinEqualsToSinh(mathEngine, -3.3d, "-0.0575640269595673");
            testSinEqualsToSinh(mathEngine, -232.2d, "0.7901550123756904");
            assertEquals("Π/2", mathEngine.simplify("Π/2"));
        } finally {
            mathEngine.setAngleUnits(defaultAngleUnits);
        }

        try {
            mathEngine.setAngleUnits(AngleUnit.rad);
            assertEquals(mathEngine.evaluate("1.5707963267948966-0.8813735870195429*i"), mathEngine.evaluate("acos(i)"));
            assertEquals(mathEngine.evaluate("0.9045568943023814-1.0612750619050357*i"), mathEngine.evaluate("acos(1+i)"));
            assertEquals(mathEngine.evaluate("0.9999999999999999-0.9999999999999998*i"), mathEngine.evaluate("cos(acos(1-i))"));
            assertEquals(mathEngine.evaluate("-0.9045568943023814-1.0612750619050355*i"), mathEngine.evaluate("-acos(1-i)"));
        } finally {
            mathEngine.setAngleUnits(defaultAngleUnits);
        }

    }

    private void testSinEqualsToSinh(@Nonnull MathEngine mathEngine, @Nonnull Double x) throws ParseException {
        testSinEqualsToSinh(mathEngine, x, null);
    }

    private void testSinEqualsToSinh(@Nonnull MathEngine mathEngine, @Nonnull Double x, @Nullable String expected) throws ParseException {
        if (expected == null) {
            assertEquals(mathEngine.evaluate("sinh(i*" + x + ")/i"), mathEngine.evaluate("sin(" + x + ")"));
//			Assert.assertEquals(mathEngine.evaluate("exp("+x+")-sinh(" + x + ")"), mathEngine.evaluate("cosh(" + x + ")"));
        } else {
            assertEquals(expected, mathEngine.evaluate("sin(" + x + ")"));
            assertEquals(expected, mathEngine.evaluate("(exp(i * " + x + ") - cos(" + x + "))/i"));
            assertEquals(expected, mathEngine.evaluate("(exp(i * " + x + ") - cos(" + x + "))/i"));
        }
    }

    @Test
    public void testName() throws Exception {
        Expression.valueOf("a*c+b*sin(c)").toString();
    }

    @Test
    public void testIntegrals() throws Exception {
        assertEquals("50", Expression.valueOf("∫ab(x, x, 0, 10)").expand().numeric().toString());
        assertEquals("1/2*a^2", Expression.valueOf("∫ab(x, x, 0, a)").expand().toString());
        try {
            assertEquals("∫ab(x, x, 0)", Expression.valueOf("∫ab(x, x, 0)").expand().toString());
            fail();
        } catch (ParseException e) {
        }
        try {
            assertEquals("∫ab(x, x)", Expression.valueOf("∫ab(x, x)").expand().simplify().toString());
            fail();
        } catch (ParseException e) {
        }
        assertEquals("x^2/2", Expression.valueOf("∫(x, x)").expand().simplify().toString());
        try {
            assertEquals("x^2/2", Expression.valueOf("∫(x, x)").expand().numeric().toString());
            fail();
        } catch (ArithmeticException e) {
        }

        assertEquals("x^2/2", Expression.valueOf("∫(x, x)").expand().simplify().toString());
        assertEquals("ln(x)", Expression.valueOf("∫(1/x, x)").expand().simplify().toString());
        try {
            JsclMathEngine.getInstance().setAngleUnits(AngleUnit.rad);
            assertEquals("2*ln(2)+ln(cosh(x))", Expression.valueOf("∫(tanh(x), x)").expand().simplify().toString());
            assertEquals("2*ln(2)+ln(sin(x))", Expression.valueOf("∫(cot(x), x)").expand().simplify().toString());
            assertEquals("-2*ln(2)-ln(cos(x))", Expression.valueOf("∫(tan(x), x)").expand().simplify().toString());
        } finally {
            JsclMathEngine.getInstance().setAngleUnits(AngleUnit.deg);
        }
    }

    @Test
    public void testDerivations() throws Exception {
        final AngleUnit defaultAngleUnits = JsclMathEngine.getInstance().getAngleUnits();
        try {
            JsclMathEngine.getInstance().setAngleUnits(AngleUnit.rad);
            assertEquals("-0.9092974268256817", Expression.valueOf("∂(cos(t),t,2)").numeric().toString());
            assertEquals("∂(cos(t), t, 2, 1)", Expression.valueOf("∂(cos(t),t,2)").simplify().toString());
            assertEquals("-2.234741690198506", Expression.valueOf("∂(t*cos(t),t,2)").numeric().toString());
            assertEquals("-4.469483380397012", Expression.valueOf("2*∂(t*cos(t),t,2)").numeric().toString());
            assertEquals("-sin(2)", Expression.valueOf("∂(cos(t),t,2)").expand().toString());
            assertEquals("-sin(t)", Expression.valueOf("∂(cos(t),t)").expand().toString());
            assertEquals("-sin(t)", Expression.valueOf("∂(cos(t),t,t,1)").expand().simplify().toString());
            assertEquals("∂(cos(t), t, t, 1°)", Expression.valueOf("∂(cos(t),t,t,1°)").expand().simplify().toString());
        } finally {
            JsclMathEngine.getInstance().setAngleUnits(defaultAngleUnits);
        }

        assertEquals("∂(cos(t), t, t, 1°)", Expression.valueOf("∂(cos(t),t,t,1°)").expand().numeric().toString());
    }

    @Test
    public void testSum() throws Exception {
        assertEquals("3", Expression.valueOf("Σ(n,n,1,2)").expand().toString());
        assertEquals("200", Expression.valueOf("Σ(n/n,n,1,200)").expand().toString());
        assertEquals("1/3", Expression.valueOf("Σ((n-1)/(n+1),n,1,2)").expand().toString());
        assertEquals("sin(1)", Expression.valueOf("Σ(sin(n),n,1,1)").expand().toString());
        assertEquals("1/1!", Expression.valueOf("Σ(n/n!,n,1,1)").expand().toString());
        assertEquals("2", Expression.valueOf("Σ(n/n!,n,1,2)").expand().numeric().toString());
        assertEquals("2.718281828459046", Expression.valueOf("Σ(n/n!,n,1,200)").expand().numeric().toString());
        assertEquals("2.718281828459046", Expression.valueOf("Σ(n/(2*n/2)!,n,1,200)").expand().numeric().toString());
        assertEquals(Expression.valueOf("3").numeric().toString(), Expression.valueOf("Σ(n°,n,1,2)").expand().numeric().toString());
        assertEquals("200", Expression.valueOf("Σ(n°/n°,n,1,200)").expand().numeric().toString());
        assertEquals("-sin(1)-sin(2)", Expression.valueOf("Σ(∂(cos(t),t,n),n,1,2)").expand().toString());
        assertEquals("-0.0523519031397845", Expression.valueOf("Σ(∂(cos(t),t,n),n,1,2)").expand().numeric().toString());
    }

    @Test
    public void testNumeralBases() throws Exception {
        final JsclMathEngine me = JsclMathEngine.getInstance();
        //final NumeralBase defaultNumeralBase = me.getDefaultNumeralBase();
        try {
            //me.setDefaultNumeralBase(NumeralBase.bin);
            assertEquals("10", me.evaluate("0b:01010"));
            assertEquals("10", me.evaluate("0b:1010"));
            assertEquals("520", me.evaluate("0o:1010"));
            assertEquals("1010", me.evaluate("1010"));
            assertEquals("1010.1", me.evaluate("1010.1"));
        } finally {
            //me.setDefaultNumeralBase(defaultNumeralBase);
        }

        try {
            me.setNumeralBase(NumeralBase.hex);
            assertEquals("22F", me.evaluate("22F*exp(F)/exp(F)"));
            assertEquals("E", me.evaluate("E"));
        } finally {
            me.setNumeralBase(NumeralBase.dec);

        }
    }

    @Test
    public void testFormat() throws Exception {
        final JsclMathEngine me = JsclMathEngine.getInstance();
        try {
            me.setGroupingSeparator(' ');
            assertEquals("123 456.7891011", Expression.valueOf("123456.7891011").numeric().toString());
            assertEquals("123 456.7891011", Expression.valueOf("123456.7891011").simplify().toString());
            assertEquals("123 456.7891011123", Expression.valueOf("123456.7891011123123123123123").simplify().toString());
            assertEquals("0.000001222", Expression.valueOf("1222/(10^9)").numeric().toString());
            assertEquals("12 345", JsclInteger.valueOf(12345L).toString());

            me.setNotation(Real.NumberFormat.FSE_SCI);
            assertEquals("0", Expression.valueOf("0.0").simplify().toString());
            assertEquals("1", Expression.valueOf("1.0").simplify().toString());
            assertEquals("100", Expression.valueOf("100.0").simplify().toString());

            me.setNotation(Real.NumberFormat.FSE_NONE);
            me.setRoundResult(true);
            me.setPrecision(5);
            assertEquals("0", Expression.valueOf("1222/(10^9)").numeric().toString());

            me.setNotation(Real.NumberFormat.FSE_SCI);
            me.setRoundResult(true);
            me.setPrecision(5);
            assertEquals("1.222E-6", Expression.valueOf("1222/(10^9)").numeric().toString());

            me.setRoundResult(true);
            me.setPrecision(10);
            assertEquals("1.222E-6", Expression.valueOf("1222/(10^9)").numeric().toString());

            me.setRoundResult(false);
            assertEquals("1.222E-6", Expression.valueOf("1222/(10^9)").numeric().toString());

            me.setNotation(Real.NumberFormat.FSE_NONE);
            assertEquals("0.3333333333333333", Expression.valueOf("1/3").numeric().toString());

            me.setNotation(Real.NumberFormat.FSE_SCI);
            assertEquals("0.3333333333333333", Expression.valueOf("1/3").numeric().toString());

            me.setRoundResult(true);
            me.setPrecision(10);
            assertEquals("0.3333333333", Expression.valueOf("1/3").numeric().toString());

            me.setNotation(Real.NumberFormat.FSE_NONE);
            me.setRoundResult(true);
            me.setPrecision(10);
            assertEquals("0.3333333333", Expression.valueOf("1/3").numeric().toString());

        } finally {
            me.setGroupingSeparator(JsclMathEngine.GROUPING_SEPARATOR_NO);
            me.setNotation(Real.NumberFormat.FSE_NONE);
            me.setRoundResult(false);
        }
    }
}
