package com.yakindu.sct.se.model;

import org.yakindu.base.types.PrimitiveType;
import org.yakindu.base.types.Type;
import org.yakindu.base.types.TypesFactory;

/**
 * Converts Yakindu Variable Types to Types used in this engine
 * @author jwielage
 *
 */
public enum VarType {
	INT, BOOL, STRING;

	
	public static VarType ofYSCT(Type type) {
		return ofYSCT(type.getName());
	}

	public static VarType ofYSCT(String name) {
		switch(name) {
		case "integer": return INT;
		case "boolean": return BOOL;
		case "string": return STRING;
		}
		return null;
	}
	

	public Type convertToType() {
		String name = null;
		switch(toString()) {
		case "INT": name = "integer"; break;
		case "BOOL": name = "boolean"; break;
		case "STRING": name = "string"; break;
		}
		PrimitiveType result = TypesFactory.eINSTANCE.createPrimitiveType();
		result.setName(name);
		return result;
	}
	
	public String smtLibFormat() {
		String result = this.name().substring(0, 1) + this.name().substring(1).toLowerCase();
		return result;
	}
	

}
