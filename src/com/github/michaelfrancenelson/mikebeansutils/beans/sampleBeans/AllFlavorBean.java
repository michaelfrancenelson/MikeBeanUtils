package com.github.michaelfrancenelson.mikebeansutils.beans.sampleBeans;

import java.util.Random;

import com.github.michaelfrancenelson.mikebeansutils.beans.builder.AnnotatedBeanReader.InitializedField;
import com.github.michaelfrancenelson.mikebeansutils.beans.builder.AnnotatedBeanReader.ParsedField;
import com.github.michaelfrancenelson.mikebeansutils.beans.memberState.SimpleFieldWatcher.DisplayName;

public class AllFlavorBean
{

	Random r;
	int notParsed;
	
	@ParsedField @InitializedField @DisplayName(name = "Primitive int member") private int     intPrim;
	@ParsedField @InitializedField @DisplayName(name = "Primitive short member") private short   shortPrim;
	@ParsedField @InitializedField private long    longPrim;
	@ParsedField @InitializedField private byte    bytePrim;
	@ParsedField @InitializedField private double  doublePrim;
	@ParsedField @InitializedField @DisplayName(name = "Primitive float member") private float   floatPrim;
	@ParsedField @InitializedField private boolean boolPrim;
	@ParsedField @InitializedField private char    charPrim;
	
	@ParsedField @InitializedField @DisplayName(name = "Autoboxed int member") private Integer   intBox;
	@ParsedField @InitializedField @DisplayName(name = "Autoboxed short member")private Short     shortBox;
	@ParsedField @InitializedField private Long      longBox;
	@ParsedField @InitializedField private Byte      byteBox;
	@ParsedField @InitializedField private Double    doubleBox;
	@ParsedField @InitializedField @DisplayName(name = "Autoboxed float member") private Float     floatBox;
	@ParsedField @InitializedField private Boolean   boolBox;
	@ParsedField @InitializedField private Character charBox;
	
	@ParsedField @InitializedField private String strng;

	public Random getR() {
		return r;
	}

	public void setR(Random r) {
		this.r = r;
	}

	public int getNotParsed() {
		return notParsed;
	}

	public void setNotParsed(int notParsed) {
		this.notParsed = notParsed;
	}

	public int getIntPrim() {
		return intPrim;
	}

	public void setIntPrim(int intPrim) {
		this.intPrim = intPrim;
	}

	public short getShortPrim() {
		return shortPrim;
	}

	public void setShortPrim(short shortPrim) {
		this.shortPrim = shortPrim;
	}

	public long getLongPrim() {
		return longPrim;
	}

	public void setLongPrim(long longPrim) {
		this.longPrim = longPrim;
	}

	public byte getBytePrim() {
		return bytePrim;
	}

	public void setBytePrim(byte bytePrim) {
		this.bytePrim = bytePrim;
	}

	public double getDoublePrim() {
		return doublePrim;
	}

	public void setDoublePrim(double doublePrim) {
		this.doublePrim = doublePrim;
	}

	public float getFloatPrim() {
		return floatPrim;
	}

	public void setFloatPrim(float floatPrim) {
		this.floatPrim = floatPrim;
	}

	public boolean isBoolPrim() {
		return boolPrim;
	}

	public void setBoolPrim(boolean boolPrim) {
		this.boolPrim = boolPrim;
	}

	public char getCharPrim() {
		return charPrim;
	}

	public void setCharPrim(char charPrim) {
		this.charPrim = charPrim;
	}

	public Integer getIntBox() {
		return intBox;
	}

	public void setIntBox(Integer intBox) {
		this.intBox = intBox;
	}

	public Short getShortBox() {
		return shortBox;
	}

	public void setShortBox(Short shortBox) {
		this.shortBox = shortBox;
	}

	public Long getLongBox() {
		return longBox;
	}

	public void setLongBox(Long longBox) {
		this.longBox = longBox;
	}

	public Byte getByteBox() {
		return byteBox;
	}

	public void setByteBox(Byte byteBox) {
		this.byteBox = byteBox;
	}

	public Double getDoubleBox() {
		return doubleBox;
	}

	public void setDoubleBox(Double doubleBox) {
		this.doubleBox = doubleBox;
	}

	public Float getFloatBox() {
		return floatBox;
	}

	public void setFloatBox(Float floatBox) {
		this.floatBox = floatBox;
	}

	public Boolean getBoolBox() {
		return boolBox;
	}

	public void setBoolBox(Boolean boolBox) {
		this.boolBox = boolBox;
	}

	public Character getCharBox() {
		return charBox;
	}

	public void setCharBox(Character charBox) {
		this.charBox = charBox;
	}

	public String getStrng() {
		return strng;
	}

	public void setStrng(String strng) {
		this.strng = strng;
	}
	
	
	
	
	
}
