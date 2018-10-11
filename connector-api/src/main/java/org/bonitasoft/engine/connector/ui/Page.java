package org.bonitasoft.engine.connector.ui;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Documented
public @interface Page {

    String value();
	int position() default -1;
	
}
