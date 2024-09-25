package com.cut.order.plan.pro.dialog;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cut.order.plan.pro.FabricDetailActivity;
import com.cut.order.plan.pro.MainApp;
import com.cut.order.plan.pro.R;
import com.cut.order.plan.pro.data.SizeData;

public class SizeDataDialog implements OnFocusChangeListener, DialogInterface.OnClickListener {
	
	private final SizeData editData;
	private final SizeData orgData;

	private final EditText sizeName;
	private final EditText quantity;
	private final EditText endBit;
	private final TextView cuttable;
	private final TextView contigency;
	private final boolean editMode;
	private final FabricDetailActivity main;
	
	private final AlertDialog dialog;
	
	public SizeDataDialog(SizeData data, FabricDetailActivity context, boolean editMode){
		this.editMode = editMode;
		this.orgData = data;
		this.editData = data.copy();
		this.main = context;
		
		View dialogView =  LayoutInflater.from(context).inflate(R.layout.add_size, null);
		sizeName = (EditText)dialogView.findViewById(R.id.size_name);
		quantity = (EditText)dialogView.findViewById(R.id.size_quantity);
		endBit = (EditText)dialogView.findViewById(R.id.end_bit);
		cuttable = (TextView)dialogView.findViewById(R.id.cuttable);
		contigency = (TextView)dialogView.findViewById(R.id.contigency_quan);
		AlertDialog.Builder builder = new AlertDialog.Builder(context)
				.setTitle(editMode?R.string.edit_size : R.string.new_size)
				.setView(dialogView)
				.setPositiveButton(R.string.done_size, this);
		if(editMode){
			builder.setNeutralButton(R.string.delete_size, this);
		}
		
		dialog = builder.create();
		
		endBit.setOnFocusChangeListener(this);
		quantity.setOnFocusChangeListener(this);
		
	}
	
	public void show(){
		updateUi(orgData);
		dialog.show();
	}

	private void updateUi(SizeData data) {
		sizeName.setText(data.sizeName);
		quantity.setText((data.quantity == 0)? "" : (data.quantity + ""));
		endBit.setText((data.endBit == 0)? "" : (data.endBit + ""));
		cuttable.setText(data.getCuttable() + "");
		contigency.setText(data.getContigency() + "");
	}
	
	private void updateSizeData(SizeData data) {
		data.sizeName = sizeName.getText().toString();
		data.quantity = parseInt(quantity);
		data.endBit = parseInt(endBit);
		data.recalcCut();
	}
	
	private int parseInt(EditText input) {
		try {
			int val = Integer.parseInt(input.getText().toString());
			return val > 0 ? val : 0;
		} catch (Exception e) {
			return 0;
		}
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if(which == AlertDialog.BUTTON_POSITIVE){
			String sname = sizeName.getText().toString();
			if(sname.equals("")){
				Toast.makeText(main, "Name Cannot be empty.", Toast.LENGTH_SHORT).show();
				return;
			}
			Pattern p = Pattern.compile("[^a-z0-9]", Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(sizeName.getText().toString());
			boolean b = m.find();
			if (b){
				Toast.makeText(main, "Invalid characters in size name.", Toast.LENGTH_SHORT).show();
				return;
			}
			
			Pattern s = Pattern.compile("[^0-9]", Pattern.CASE_INSENSITIVE);
			
			Matcher mt = s.matcher(quantity.getText().toString());
			boolean c = mt.find();
			int squan = parseInt(quantity);
			if(squan < 0){
				Toast.makeText(main, "Quantity cannot be less than zero.", Toast.LENGTH_SHORT).show();
				return;
			}
			if (c){
				Toast.makeText(main, "Invalid characters in quantity.", Toast.LENGTH_SHORT).show();
				return;
			}
			
			mt = s.matcher(endBit.getText().toString());
			c = false;
			c = mt.find();
			if (c){
				Toast.makeText(main, "Invalid characters in end-bit.", Toast.LENGTH_SHORT).show();
				return;
			}
			int endquan = parseInt(endBit);
			if(squan < endquan){
				Toast.makeText(main, "Quantity should be greater than or equal to End-Bits.", Toast.LENGTH_SHORT).show();
				return;
			}else{
			
			
				String oldName = orgData.sizeName;
				//Log.v("Testing", sname);
				if(oldName == null){
					oldName = " ";
				}
				if (sname.compareTo(oldName) != 0){
					//Log.v("Testing", "oldname not equal");
					SizeData check = new SizeData();
					String tabName = main.style_Name + main.fabricData.fabricName;
					check = MainApp.getDB().getSizeData(tabName, sname);
					if(check.sizeName != null){
						Toast.makeText(main, "Size Name already exists.", Toast.LENGTH_SHORT).show();
						return;
					}
				}
				updateSizeData(orgData);
				
				if (editMode) {
					main.adaptor.notifyDataSetChanged();
					MainApp.getDB().addUpdateSize(main.style_Name, main.fabricData.fabricName, orgData, oldName);
					
				} else {
					main.adaptor.add(orgData);
					
					//Log.v("Testing", main.fabricData.fabricName);
					
					MainApp.getDB().addUpdateSize(main.style_Name, main.fabricData.fabricName, orgData, orgData.sizeName);
				}
			}
			
		} else if (which == AlertDialog.BUTTON_NEUTRAL) {
			
			AlertDialog.Builder builder = new AlertDialog.Builder(main);
        	builder.setMessage("Deleting size will delete this particular size from all fabrics.")
        			.setTitle("Confirm")
        	       .setCancelable(false)
        	       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
        	           public void onClick(DialogInterface dialog, int id) {
        	        	   main.adaptor.remove(orgData);
        	   				MainApp.getDB().addUpdateSize(main.style_Name, main.fabricData.fabricName, null, orgData.sizeName);
        		           return;
        	           }
        	       })
        	       .setNegativeButton("No", new DialogInterface.OnClickListener() {
        	           public void onClick(DialogInterface dialog, int id) {
        	                dialog.cancel();
        	                return;
        	           }
        	       });
        	AlertDialog alert = builder.create();
        	alert.show();
			
			
		}
		main.setTotal();
		
	}

	@Override
	public void onFocusChange(View arg0, boolean arg1) {
		updateSizeData(editData);
		updateUi(editData);
		
	}

}
