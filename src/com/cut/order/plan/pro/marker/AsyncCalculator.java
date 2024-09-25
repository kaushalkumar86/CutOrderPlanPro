package com.cut.order.plan.pro.marker;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.AsyncTask;

import com.cut.order.plan.pro.MainApp;
import com.cut.order.plan.pro.MarkerResultActivity;
import com.cut.order.plan.pro.data.StyleData;


public class AsyncCalculator extends AsyncTask<String, Void, MarkerResult> implements OnCancelListener {
	
	private final ProgressDialog dialog;
	private final Context context;
	
	public AsyncCalculator(Context context, String style) {
		this.context = context;
		dialog = ProgressDialog.show(context, "Calculating Result", "Please wait. It may take upto a few minutes.",
			true, true, this);
		execute(style);
	}
	

	@Override
	public void onCancel(DialogInterface arg0) {
		this.cancel(true);
		
	}

	@Override
	protected MarkerResult doInBackground(String... params) {
		String styleName = params[0];
		StyleData styleData = MainApp.getDB().getStyleData(styleName);
		
		MarkerResult result = new MarkerResult();
		result.styleName = styleData.styleName;
		result.maxGarment = styleData.maxGarment;
		result.maxPlies = styleData.maxPlies;
		result.name = new ArrayList<String>();
		result.quant = new ArrayList<Row>();
		result.temp_quant = new ArrayList<Row>();
		int countX = styleData.fabrics.get(0).sizes.size();
		result.sizeCount = countX;
		result.position = new Row(countX);
		int countY = styleData.fabrics.size();
		result.fabCount = countY;
		for(int y = 0; y < countY; y++){
			Row q = new Row(countX);
			for(int x = 0; x < countX; x++){
				q.cells[x] = styleData.fabrics.get(y).sizes.get(x).getCuttable();
				
			}
			result.quant.add(q);
			result.temp_quant.add(q);
		}
		for(int x = 0; x < countX; x++){
			result.name.add(styleData.fabrics.get(0).sizes.get(x).sizeName);
			result.position.cells[x] = x;
		}
		result.beginCalc();
		
		return result;
	}
	
	@Override
	protected void onPostExecute(MarkerResult result) {
		MarkerResult.LAST_CALCULATED_RESULT = result;
		Intent resultIntent = new Intent(context, MarkerResultActivity.class);
		context.startActivity(resultIntent);
		dialog.dismiss();
	}

}
