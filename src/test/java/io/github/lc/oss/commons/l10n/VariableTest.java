package io.github.lc.oss.commons.l10n;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.lc.oss.commons.testing.AbstractMockTest;

public class VariableTest extends AbstractMockTest {
    @Test
    public void test_constructor_nulls() {
        Variable var = new Variable(null, null);

        Assertions.assertNull(var.getKey());
        Assertions.assertEquals("%null%", var.getKeyId());
        Assertions.assertNull(var.getValue());
        Assertions.assertEquals("null=null", var.toString());
    }

    @Test
    public void test_replace() {
        Variable var = new Variable("key", "value");

        Assertions.assertEquals("key", var.getKey());
        Assertions.assertEquals("%key%", var.getKeyId());
        Assertions.assertEquals("value", var.getValue());
        Assertions.assertEquals("key=value", var.toString());

        String result = Variable.replace("%a% %key% key #key# a", var);
        Assertions.assertEquals("%a% value key #key# a", result);

        result = Variable.replace("%a% %key% key #key# a", var);
        Assertions.assertEquals("%a% value key #key# a", result);
    }

    @Test
    public void test_replace_blanks() {
        Variable var = new Variable("key", "value");

        String input = null;
        String result = Variable.replace(input, (Variable[]) null);
        Assertions.assertNull(result);
        result = var.replace(input);
        Assertions.assertNull(result);

        input = "";
        result = Variable.replace(input, (Variable[]) null);
        Assertions.assertSame(input, result);
        result = var.replace(input);
        Assertions.assertSame(input, result);

        input = " ";
        result = Variable.replace(input, (Variable[]) null);
        Assertions.assertSame(input, result);
        result = var.replace(input);
        Assertions.assertSame(input, result);

        input = " \t \r \n \t ";
        result = Variable.replace(input, (Variable[]) null);
        Assertions.assertSame(input, result);
        result = var.replace(input);
        Assertions.assertSame(input, result);

        input = "test";
        result = Variable.replace(input, (Variable[]) null);
        Assertions.assertSame(input, result);
        result = var.replace(input);
        Assertions.assertSame(input, result);

        input = "test";
        result = Variable.replace(input, (Variable) null);
        Assertions.assertSame(input, result);
    }

    @Test
    public void test_replace_nulls() {
        Variable nullKey = new Variable(null, "value");
        Variable nullValue = new Variable("key", null);
        Variable nulls = new Variable(null, null);

        String input = "Text=key";
        String result = nullKey.replace(input);
        Assertions.assertSame(input, result);

        input = "Text=key";
        result = nullValue.replace(input);
        Assertions.assertSame(input, result);

        input = "Text=key";
        result = nulls.replace(input);
        Assertions.assertSame(input, result);
    }
}
