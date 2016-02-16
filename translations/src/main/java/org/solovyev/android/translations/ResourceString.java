package org.solovyev.android.translations;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

@SuppressWarnings("unused")
@Root(name = "string", strict = false)
public class ResourceString {
    @Attribute
    public String name;
    @Text(required = false)
    public String value;

    public ResourceString() {
    }

    ResourceString(String name, String value) {
        this.name = name;
        this.value = value;
    }
}
