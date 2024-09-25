package com.cut.order.plan.pro.data;


public class SizeData {
	public String sizeName;
	public int quantity;
	public int endBit;
	
	private int cuttable;
	private int contigency;
	private int contiPercent;
	
	public void setContigency(int c){
		contiPercent = c;
		recalcCut();
	}
	public void recalcCut(){
		contigency = (quantity * contiPercent) / 100;
		cuttable = (contigency + quantity) - endBit;
	}
	public int getCuttable(){
		return cuttable;
	}
	public int getContigency(){
		return contigency;
	}
	public SizeData copy(){
		SizeData clone = new SizeData();
		clone.sizeName = sizeName;
		clone.quantity = quantity;
		clone.endBit = endBit;
		clone.cuttable = cuttable;
		clone.contigency = contigency;
		clone.contiPercent = contiPercent;
		return clone;
	}

}
