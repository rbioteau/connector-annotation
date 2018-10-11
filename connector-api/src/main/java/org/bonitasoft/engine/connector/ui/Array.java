package org.bonitasoft.engine.connector.ui;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
public @interface Array {
	
	int nbColumns();
	String[] columnNames();
	boolean fixedCols() default true;
	int nbRows() default 0;
	boolean fixedRows() default false;
	
}
